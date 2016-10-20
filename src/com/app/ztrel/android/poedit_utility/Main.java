package com.app.ztrel.android.poedit_utility;

import com.app.ztrel.android.poedit_utility.creators.POTFilesCreator;
import com.app.ztrel.android.poedit_utility.creators.XMLFilesCreator;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new RuntimeException("Error! You need to provide path to .xml or .po file.");
        }

        String filePath = args[0];
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("Error! File not exists.");
        }

        if (filePath.endsWith(".xml")) {
            POTFilesCreator.createPOTFileFromStringsXML(file);
        } else if (filePath.endsWith(".po")) {
            XMLFilesCreator.createXMLFromPOFile(file);
        }
    }

}