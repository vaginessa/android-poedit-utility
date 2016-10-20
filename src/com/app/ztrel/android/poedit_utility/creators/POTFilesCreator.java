package com.app.ztrel.android.poedit_utility.creators;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for creating POT-files: PO-templates for POEditor.
 */
public final class POTFilesCreator {

    private static final String QUOTE = "\"";
    private static final String QUOTES = "\"\"";
    private static final String NEW_LINE = "\n";
    private static final String NEW_LINE_SLASH_ESCAPED = "\\n";
    private static final String POT_COMMENT_LINE_START = "#: ";
    private static final String POT_MESSAGE_ID_LINE_START = "msgid ";
    private static final String POT_MESSAGE_STRING_LINE_START = "msgstr ";
    private static final String UTILITY_COMMENT_KEY = "_comment_";
    private static final String UTILITY_EMPTY_LINE_KEY = "[]";

    private static final Pattern RESOURCES_TAG_PATTERN = Pattern.compile("^<resources>$");
    private static final Pattern RESOURCES_END_TAG_PATTERN = Pattern.compile("^</resources>$");
    private static final Pattern STRING_RESOURCE_PATTERN = Pattern.compile(".*<string name=(\".+\")>(.+)</string>.*");
    private static final Pattern COMMENT_PATTERN = Pattern.compile(".*<!--(.+)-->.*");

    private POTFilesCreator() {
        // utility class.
    }

    public static void createPOTFileFromStringsXML(final File stringsXMLFile) throws Exception {
        File potTemplateFile = new File("template.pot");
        try (BufferedReader br = new BufferedReader(new FileReader(stringsXMLFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(potTemplateFile))) {

            writeStartOfPOTTemplate(bw);

            String line;
            while ((line = br.readLine()) != null) {
                Matcher stringResourceMatcher = STRING_RESOURCE_PATTERN.matcher(line);
                if (stringResourceMatcher.matches()) {
                    writePOTFormatItem(bw, stringResourceMatcher);
                } else {
                    Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                    if (commentMatcher.matches()) {
                        writeCommentString(bw, commentMatcher.group(1));
                    } else if (!RESOURCES_TAG_PATTERN.matcher(line).matches() && !RESOURCES_END_TAG_PATTERN.matcher(line).matches()) {
                        writeCommentString(bw, "");
                    }
                }
            }
        }
    }

    private static void writePOTFormatItem(BufferedWriter bw, Matcher stringResourceMatcher) throws Exception {
        String key = stringResourceMatcher.group(1);
        String value = stringResourceMatcher.group(2);
        System.out.println();
        System.out.println("key: " + key);
        System.out.println("value: " + value);
        System.out.println();

        bw.write(NEW_LINE +
                POT_COMMENT_LINE_START + key + NEW_LINE +
                POT_MESSAGE_ID_LINE_START + QUOTE + value + QUOTE + NEW_LINE +
                POT_MESSAGE_STRING_LINE_START + QUOTES + NEW_LINE
        );
    }

    private static void writeCommentString(BufferedWriter bw, String comment) throws Exception {
        bw.write(NEW_LINE +
                POT_COMMENT_LINE_START + UTILITY_COMMENT_KEY + (comment.isEmpty() ? UTILITY_EMPTY_LINE_KEY : comment)
        );
    }

    private static void writeStartOfPOTTemplate(BufferedWriter bw) throws Exception {
        bw.write(
                POT_MESSAGE_ID_LINE_START + QUOTES + NEW_LINE +
                        POT_MESSAGE_STRING_LINE_START + QUOTES + NEW_LINE +
                        QUOTE + "Content-Type: text/plain; charset=UTF-8" + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE +
                        QUOTE + "Content-Transfer-Encoding: 8bit" + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE +
                        QUOTE + "Project-Id-Version: " + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE
        );
    }

}