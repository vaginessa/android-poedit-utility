package com.app.ztrel.android.poedit_utility;

import com.app.ztrel.android.poedit_utility.creators.POTFilesCreator;
import com.app.ztrel.android.poedit_utility.creators.XMLFilesCreator;

import java.io.File;
import java.net.URLDecoder;

public class Main {

    /**
     * Utility automatically goes through all directories, creates (or updates) single android_strings_template.pot in -jar file directory
     * and creates corresponding strings.xml from .po files, which exists in -jar file directory.
     *
     * @param args - utility need only one arg - path to app folder.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("Error! You need to provide path to app directory.");
        }

        String filepath = args[0];
        File appDir = new File(filepath);
        if (!appDir.exists()) {
            throw new RuntimeException("Error! You need to provide path to existing app directory.");
        }

        File jarDir = getJarDir();
        POTFilesCreator.potFileCreating(jarDir, appDir);
        XMLFilesCreator.createXMLFiles(jarDir, appDir);
    }

    private static File getJarDir() throws Exception {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return new File(URLDecoder.decode(path, "UTF-8")).getParentFile();
    }

}