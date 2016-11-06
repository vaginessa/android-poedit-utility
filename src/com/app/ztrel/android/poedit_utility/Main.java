package com.app.ztrel.android.poedit_utility;

import com.app.ztrel.android.poedit_utility.creators.POTFilesCreator;
import com.app.ztrel.android.poedit_utility.creators.XMLFilesCreator;

import java.io.File;
import java.net.URLDecoder;

public class Main {

    /**
     * Utility automatically goes through all directories, creates single android_strings_template.pot and
     * creates corresponding strings.xml from .po files.
     *
     * @param args - utility need only one arg - path to app folder.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("Error! You need to provide path to file or directory.");
        }

        String filepath = args[0];
        File startFile = new File(filepath);
        if (!startFile.exists()) {
            throw new RuntimeException("Error! You need to provide path to existing file or directory.");
        }

        POTFilesCreator.potFileCreating(getPathToJARDirectory(), startFile);
        XMLFilesCreator.createXMLFiles(startFile);
    }

    private static File getPathToJARDirectory() throws Exception {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File dir = new File(URLDecoder.decode(path, "UTF-8"));
        return dir.getParentFile();
    }

}