# android-poedit-utility
Small utility for converting Android "strings.xml" into "template.pot" and "translate.po" into "strings.xml"

# Usage

1. Create .jar from this sources
2. Use jar in the following way:

For creating "template.pot" from your "strings.xml" write in command line:
> java -jar \<your_jar\> \<path_to_your_strings_xml\>
  
After this in your jar folder you will see "template.pot" file, that can be used in POEdit (https://poedit.net/) for creating translates on other languages.

For creating "strings.xml" from your translated ".po" files write in command line:
> java -jar \<your_jar\> \<path_to_your_po_file\>
    
After this in your jar folder you will see "strings.xml" with correct keys and values. This file can be copied into corresponding res-folder.

# Some features

This utility supports one-line xml-comments and single empty lines in xml. It is very conveniently, to see comments in other languages strings.

# Features to do

- Support multi-line empty strings
- Support multi-line comments
- Support path to Android project to get all strings.xml and creating corresponding folder structure
