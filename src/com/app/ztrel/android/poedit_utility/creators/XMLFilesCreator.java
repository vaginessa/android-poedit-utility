package com.app.ztrel.android.poedit_utility.creators;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for creating string resources XML-files from PO-files, provided by POEditor.
 */
public final class XMLFilesCreator {

    private static final String NEW_LINE = "\n";
    private static final String TAB = "\t";
    private static final String CLOSE_TAG = ">";
    private static final String QUOTE = "\"";
    private static final String OPEN_STRING_TAG = "<string ";
    private static final String STRING_TAG_KEY_ATTRIBUTE = "key=";
    private static final String CLOSE_STRING_TAG = "</string>";
    private static final String OPEN_RESOURCES_TAG = "<resources>";
    private static final String CLOSE_RESOURCES_TAG = "</resources>";
    private static final String OPEN_XML_COMMENT_TAG = "<!--";
    private static final String CLOSE_XML_COMMENT_TAG = "-->";
    private static final String UTILITY_EMPTY_LINE_COMMENT = "_empty_";

    private static final Pattern PO_KEY_PATTERN = Pattern.compile("#: \"(.+)\"");
    private static final Pattern PO_VALUE_PATTERN = Pattern.compile("msgstr \"(.*)\"");
    private static final Pattern START_PO_VALUE_PATTERN = Pattern.compile("\"(.+)\"");
    private static final Pattern PO_EMPTY_LINE_PATTERN = Pattern.compile("");
    private static final Pattern UTILITY_COMMENT_KEY_PATTERN = Pattern.compile(".*([ ]_comment_(.+))\"(.+)\"");

    private XMLFilesCreator() {
        // utility class.
    }

    public static void createXMLFromPOFile(final File poFile) throws Exception {
        File stringsXMLFile = new File("strings.xml");
        try (BufferedReader br = new BufferedReader(new FileReader(poFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(stringsXMLFile))) {

            writeStartOfStringsXMLFile(bw);

            String line;
            while ((line = br.readLine()) != null) {
                Matcher poKeyLineMatcher = PO_KEY_PATTERN.matcher(line);
                Matcher poCommentKeyLineMatcher = UTILITY_COMMENT_KEY_PATTERN.matcher(line);

                String key = poCommentKeyLineMatcher.matches() ? QUOTE + poCommentKeyLineMatcher.group(3) + QUOTE :
                        poKeyLineMatcher.matches() ? QUOTE + poKeyLineMatcher.group(1) + QUOTE : null;
                if (key == null) {
                    continue;
                }
                if (poCommentKeyLineMatcher.matches()) {
                    if (line.contains(UTILITY_EMPTY_LINE_COMMENT)) {
                        bw.newLine();
                    }
                    writeCommentString(bw, poCommentKeyLineMatcher.group(2));
                }

                StringBuilder valueBuilder = new StringBuilder("");
                while (true) {
                    line = br.readLine();
                    Matcher valueMatcher = PO_VALUE_PATTERN.matcher(line);
                    if (valueMatcher.matches()) {
                        valueBuilder.append(valueMatcher.group(1));
                        while (true) {
                            line = br.readLine();
                            if (line == null) {
                                break;
                            }
                            Matcher startValueMatcher = START_PO_VALUE_PATTERN.matcher(line);
                            if (startValueMatcher.matches()) {
                                valueBuilder.append(startValueMatcher.group(1));
                                continue;
                            }
                            Matcher emptyMatcher = PO_EMPTY_LINE_PATTERN.matcher(line);
                            if (emptyMatcher.matches()) {
                                break;
                            }
                        }

                        writeStringTagString(bw, key, valueBuilder.toString());
                        break;
                    }
                }
            }

            writeEndOfStringsXMLFile(bw);
        }
    }

    private static void writeStringTagString(BufferedWriter bw, String key, String value) throws Exception {
        bw.write(TAB + OPEN_STRING_TAG + STRING_TAG_KEY_ATTRIBUTE + key + CLOSE_TAG + value + CLOSE_STRING_TAG + NEW_LINE);
    }

    private static void writeCommentString(BufferedWriter bw, String comment) throws Exception {
        bw.write(TAB + OPEN_XML_COMMENT_TAG + comment + CLOSE_XML_COMMENT_TAG + NEW_LINE);
    }

    private static void writeStartOfStringsXMLFile(BufferedWriter bw) throws Exception {
        bw.write(NEW_LINE + OPEN_RESOURCES_TAG + NEW_LINE);
    }

    private static void writeEndOfStringsXMLFile(BufferedWriter bw) throws Exception {
        bw.write(NEW_LINE + CLOSE_RESOURCES_TAG + NEW_LINE);
    }

}