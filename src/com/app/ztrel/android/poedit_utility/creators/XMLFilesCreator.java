package com.app.ztrel.android.poedit_utility.creators;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.regex.Matcher;

import static com.app.ztrel.android.poedit_utility.Constants.*;

/**
 * Utility class for creating string resources XML-files from PO-files, provided by POEditor.
 */
public final class XMLFilesCreator {

    private static File baseFile;
    private static int createdFilesCount;

    private XMLFilesCreator() {
        // utility class.
    }

    public static void createXMLFiles(@NotNull final File startFile) throws Exception {
        long startTime = System.currentTimeMillis();
        createdFilesCount = 0;

        handleFile(startFile);
        writeTotalLog(startTime, createdFilesCount);
    }

    private static void handleFile(@NotNull final File startFile) throws Exception {
        if (startFile.isDirectory()) {
            if (baseFile == null) {
                baseFile = startFile;
            }
            File[] directoryFiles = startFile.listFiles();
            if (directoryFiles != null && directoryFiles.length != 0) {
                for (File directoryFile : directoryFiles) {
                    handleFile(directoryFile);
                }
            }
        } else {
            if (startFile.getName().endsWith(".po")) {
                handlePOFile(startFile);
            }
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    private static void handlePOFile(File poFile) throws Exception {
        BufferedWriter bw = null;
        try (BufferedReader br = new BufferedReader(new FileReader(poFile))) {
            String line;
            StringBuilder commentBuilder = new StringBuilder("");
            while ((line = br.readLine()) != null) {
                Matcher commentMatcher = PO_COMMENT_PATTERN.matcher(line);
                if (commentMatcher.matches()) {
                    commentBuilder.append(" ").append(commentMatcher.group(1));
                } else {
                    Matcher messageIdMatcher = PO_MSGID_PATTERN.matcher(line);
                    if (!messageIdMatcher.matches()) {
                        continue;
                    }
                    if (commentBuilder.toString().isEmpty()) {
                        continue;
                    }
                    String[] valuesCommentArray = getCommentValuesArray(commentBuilder.toString());
                    if (valuesCommentArray[0] == null) {
                        if (bw == null) {
                            throw new IllegalStateException();
                        }
                        handleValuesCommentArray(br, bw, valuesCommentArray);
                    } else {
                        if (bw != null) {
                            writeEndOfStringsXMLFile(bw);
                            bw.close();
                            bw = null;
                            ++createdFilesCount;
                        }
                        bw = getBufferedWriter(valuesCommentArray[0], getValuesDirNamePostfix(poFile.getName()));
                        writeStartOfXMLFile(bw);
                        handleValuesCommentArray(br, bw, valuesCommentArray);
                    }

                    commentBuilder.setLength(0);
                }
            }
        } finally {
            if (bw != null) {
                writeEndOfStringsXMLFile(bw);
                bw.close();
                ++createdFilesCount;
            }
        }
    }

    private static void handleValuesCommentArray(BufferedReader br,
                                                 BufferedWriter bw,
                                                 String[] valuesCommentArray) throws Exception {
        if ("true".equals(valuesCommentArray[3])) {
            bw.newLine();
        }
        if (valuesCommentArray[1] != null) {
            writeCommentString(bw, valuesCommentArray[1]);
        }

        String key = valuesCommentArray[2];
        String value = readValue(br);

        writeStringTagString(bw, key, value);
    }

    private static String readValue(BufferedReader br) throws Exception {
        StringBuilder valueBuilder = new StringBuilder("");
        String line;
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

                return valueBuilder.toString();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static BufferedWriter getBufferedWriter(String path, String valuesDirPostfix) throws Exception {
        File xmlFile = new File(baseFile, path);
        File dir = xmlFile.getParentFile().getParentFile();
        String dirPath = dir.getAbsolutePath() + "/" + "values-" + valuesDirPostfix + "/";
        new File(dirPath).mkdirs();
        String filepath = dirPath + xmlFile.getName();
        File myFile = new File(filepath);
        return new BufferedWriter(new FileWriter(myFile));
    }

    private static String[] getCommentValuesArray(String fullComment) {
        Matcher fullCommentMatcher = UTILITY_COMMENT_PATTERN.matcher(fullComment);
        if (!fullCommentMatcher.matches()) {
            throw new IllegalStateException();
        }
        return new String[]{
                fullCommentMatcher.group(3),
                fullCommentMatcher.group(6),
                fullCommentMatcher.group(8),
                String.valueOf(fullCommentMatcher.group(1) != null
                        || fullCommentMatcher.group(4) != null
                        || fullCommentMatcher.group(7) != null)
        };
    }

    private static String getValuesDirNamePostfix(String filename) {
        Matcher poFilenameMatcher = PO_FILENAME_PATTERN.matcher(filename);
        if (poFilenameMatcher.matches()) {
            return poFilenameMatcher.group(1);
        } else {
            throw new IllegalStateException();
        }
    }

    private static void writeStringTagString(BufferedWriter bw, String key, String value) throws Exception {
        bw.write(TAB + keyString(key) + value + CLOSE_STRING_TAG + NEW_LINE);
    }

    private static String keyString(String key) {
        return OPEN_STRING_TAG + STRING_TAG_KEY_ATTRIBUTE + QUOTE + key + QUOTE + CLOSE_TAG;
    }

    private static void writeCommentString(BufferedWriter bw, String comment) throws Exception {
        bw.write(TAB + OPEN_XML_COMMENT_TAG + comment + CLOSE_XML_COMMENT_TAG + NEW_LINE);
    }

    private static void writeStartOfXMLFile(BufferedWriter bw) throws Exception {
        bw.write(XML_SCHEME_START + NEW_LINE + OPEN_RESOURCES_TAG + NEW_LINE);
    }

    private static void writeEndOfStringsXMLFile(BufferedWriter bw) throws Exception {
        bw.write(NEW_LINE + CLOSE_RESOURCES_TAG);
    }

    private static void writeTotalLog(long startTime, int createdFilesCount) {
        System.out.println("XML files creation successfully complete.\n " +
                "\tCount of created files with strings values: " + createdFilesCount + ". " +
                "\tTotal time: " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}