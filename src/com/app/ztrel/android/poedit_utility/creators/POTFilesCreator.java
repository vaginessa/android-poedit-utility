package com.app.ztrel.android.poedit_utility.creators;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

import static com.app.ztrel.android.poedit_utility.Constants.*;

/**
 * Utility class for creating POT-files: PO-templates for POEditor.
 */
public final class POTFilesCreator {

    private static Path basePath;
    private static int handledStringsFilesCount;

    private POTFilesCreator() {
        // utility class.
    }

    /**
     * Main method for creating single POT-template for further work.
     *
     * @param appDir - app directory for start. We will try to search any .xml files in it.
     * @throws Exception
     */
    public static void potFileCreating(@NotNull final File jarDir, @NotNull final File appDir) throws Exception {
        long startTime = System.currentTimeMillis();
        basePath = null;
        handledStringsFilesCount = 0;

        File potTemplateFile = new File(jarDir, "android_strings_template.pot");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(potTemplateFile))) {
            handleFile(bw, appDir);
        }
        writeTotalLog(startTime, handledStringsFilesCount);
    }

    private static void handleFile(@NotNull BufferedWriter bw, @NotNull final File startFile) throws Exception {
        if (startFile.isDirectory()) {
            if (basePath == null) {
                basePath = Paths.get(startFile.getAbsolutePath());
            }
            File[] directoryFiles = startFile.listFiles();
            if (directoryFiles != null && directoryFiles.length != 0) {
                for (File directoryFile : directoryFiles) {
                    handleFile(bw, directoryFile);
                }
            }
        } else {
            String parentName = startFile.getParentFile().getName();
            if (startFile.getName().endsWith(".xml") && parentName.equals("values")) {
                handleXMLFile(bw, startFile);
            }
        }
    }

    private static void handleXMLFile(@NotNull BufferedWriter bw, @NotNull File xmlFile) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            boolean wasResourcesTag = false;
            boolean makePathComment = false;
            int emptyLinesCounter = 0;
            StringBuilder startCommentBuilder = new StringBuilder("");
            while ((line = br.readLine()) != null) {
                if (!wasResourcesTag) {
                    if (RESOURCES_TAG_PATTERN.matcher(line).matches()) {
                        wasResourcesTag = true;
                    }
                    continue;
                }

                if (line.isEmpty()) {
                    if (makePathComment) {
                        ++emptyLinesCounter;
                    }
                    continue;
                }

                Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                if (commentMatcher.matches()) {
                    if (makePathComment) {
                        writeCommentLine(bw, UTILITY_COMMENT_KEY, commentMatcher.group(1));
                    } else {
                        startCommentBuilder.append(commentMatcher.group(1));
                    }
                    continue;
                }

                Matcher translatableStringResourceMatcher = TRANSLATABLE_STRING_RESOURCE_PATTERN.matcher(line);
                if (translatableStringResourceMatcher.matches()) {
                    boolean needTranslate = translatableStringResourceMatcher.group(2) == null ||
                            translatableStringResourceMatcher.group(2).equals("true");
                    if (needTranslate) {
                        handleMatchedLine(bw, xmlFile, makePathComment, emptyLinesCounter, startCommentBuilder);

                        emptyLinesCounter = 0;
                        makePathComment = true;
                        startCommentBuilder = null;

                        String key = translatableStringResourceMatcher.group(1);
                        String value = translatableStringResourceMatcher.group(4);
                        writePOTFormatItem(bw, key, value);
                    }
                } else {
                    Matcher stringResourceMatcher = STRING_RESOURCE_PATTERN.matcher(line);
                    if (stringResourceMatcher.matches()) {
                        handleMatchedLine(bw, xmlFile, makePathComment, emptyLinesCounter, startCommentBuilder);

                        emptyLinesCounter = 0;
                        makePathComment = true;
                        startCommentBuilder = null;

                        String key = stringResourceMatcher.group(1);
                        String value = stringResourceMatcher.group(2);
                        writePOTFormatItem(bw, key, value);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private static void handleMatchedLine(BufferedWriter bw, File xmlFile,
                                          boolean makePathComment, int emptyLinesCounter,
                                          StringBuilder startCommentBuilder) throws Exception {
        writeEmptyLines(bw, emptyLinesCounter);
        if (!makePathComment) {
            ++handledStringsFilesCount;
            if (handledStringsFilesCount == 1) {
                writeStartOfPOTTemplate(bw);
            }
            Path pathRelative = basePath.relativize(Paths.get(xmlFile.getAbsolutePath()));
            writeCommentLine(bw, UTILITY_PATH_KEY, pathRelative.toString());
        }
        if (startCommentBuilder != null && !startCommentBuilder.toString().isEmpty()) {
            writeCommentLine(bw, UTILITY_COMMENT_KEY, startCommentBuilder.toString());
        }
    }

    private static void writeStartOfPOTTemplate(BufferedWriter bw) throws Exception {
        bw.write(POT_MESSAGE_ID_LINE_START + QUOTES + NEW_LINE +
                POT_MESSAGE_STRING_LINE_START + QUOTES + NEW_LINE +
                QUOTE + "Content-Type: text/plain; charset=UTF-8" + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE +
                QUOTE + "Content-Transfer-Encoding: 8bit" + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE +
                QUOTE + "Project-Id-Version: " + NEW_LINE_SLASH_ESCAPED + QUOTE + NEW_LINE
        );
    }

    private static void writeCommentLine(@NotNull BufferedWriter bw, String utilityKey, String comment) throws Exception {
        String delimiterString = "";
        switch (utilityKey) {
            case UTILITY_PATH_KEY:
                delimiterString = "*";
                break;

            case UTILITY_COMMENT_KEY:
                delimiterString = "\"";
                break;
        }
        bw.write(NEW_LINE + POT_COMMENT_LINE_START + utilityKey + delimiterString + comment + delimiterString);
    }

    private static void writePOTFormatItem(@NotNull BufferedWriter bw, String key, String value) throws Exception {
        bw.write(NEW_LINE +
                POT_COMMENT_LINE_START + key + NEW_LINE +
                POT_MESSAGE_ID_LINE_START + QUOTE + value + QUOTE + NEW_LINE +
                POT_MESSAGE_STRING_LINE_START + QUOTES + NEW_LINE
        );
    }

    private static void writeEmptyLines(BufferedWriter bw, int emptyLinesCounter) throws Exception {
        if (emptyLinesCounter == 0) {
            return;
        }
        for (int i = 0; i < emptyLinesCounter; ++i) {
            writeCommentLine(bw, UTILITY_EMPTY_LINE_KEY, "");
        }
    }

    private static void writeTotalLog(long startTime, int handledStringsFilesCount) {
        System.out.println("POT file creation successfully complete.\n " +
                "\tCount of handled files with strings values: " + handledStringsFilesCount + ". " +
                "\tTotal time: " + (System.currentTimeMillis() - startTime) + " ms.");
    }

}