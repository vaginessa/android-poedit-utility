package com.app.ztrel.android.poedit_utility;

import java.util.regex.Pattern;

/**
 * Common set of strings.
 */
public final class Constants {

    public static final String TAB = "\t";
    public static final String CLOSE_TAG = ">";
    public static final String QUOTE = "\"";
    public static final String QUOTES = "\"\"";
    public static final String NEW_LINE = "\n";
    public static final String NEW_LINE_SLASH_ESCAPED = "\\n";

    public static final String POT_COMMENT_LINE_START = "#: ";
    public static final String POT_MESSAGE_ID_LINE_START = "msgid ";
    public static final String POT_MESSAGE_STRING_LINE_START = "msgstr ";

    public static final String UTILITY_COMMENT_KEY = "_comment_";
    public static final String UTILITY_EMPTY_LINE_KEY = "_empty_";
    public static final String UTILITY_PATH_KEY = "_path_";

    public static final String XML_SCHEME_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    public static final String OPEN_RESOURCES_TAG = "<resources>";
    public static final String CLOSE_RESOURCES_TAG = "</resources>";
    public static final String OPEN_STRING_TAG = "<string ";
    public static final String STRING_TAG_KEY_ATTRIBUTE = "key=";
    public static final String CLOSE_STRING_TAG = "</string>";
    public static final String OPEN_XML_COMMENT_TAG = "<!--";
    public static final String CLOSE_XML_COMMENT_TAG = "-->";

    public static final Pattern RESOURCES_TAG_PATTERN = Pattern.compile("^<resources>$");
    public static final Pattern TRANSLATABLE_STRING_RESOURCE_PATTERN = Pattern.compile(".*<string name=(\".+\")([ ]+translatable=\"(.+)\")?>(.+)</string>.*");
    public static final Pattern COMMENT_PATTERN = Pattern.compile(".*<!--(.+)-->.*");

    public static final Pattern PO_VALUE_PATTERN = Pattern.compile("msgstr \"(.*)\"");
    public static final Pattern START_PO_VALUE_PATTERN = Pattern.compile("\"(.+)\"");
    public static final Pattern PO_EMPTY_LINE_PATTERN = Pattern.compile("");

    public static final Pattern PO_FILENAME_PATTERN = Pattern.compile("(.+)\\.po");
    public static final Pattern PO_COMMENT_PATTERN = Pattern.compile("#: (.+)");
    public static final Pattern PO_MSGID_PATTERN = Pattern.compile("msgid (.+)");
    public static final Pattern UTILITY_COMMENT_PATTERN =
            Pattern.compile("([ ]?_empty_ )*([ ]?_path_\\*(.+)\\* )?([ ]?_empty_ )*([ ]?_comment_\\\"(.+)\\\" )?(_empty_ )*[ ]?\\\"(.+)\\\"");

    private Constants() {
        // no instances.
    }

}