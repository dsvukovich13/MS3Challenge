# MS3Challenge
This repository is Devon Vukovich's submission to the coding challenge for Mountain State Software Solutions.

The contained application's purpose is to consume a source CSV file, validate the data and insert complete records into a SQLite database. Incomplete records will be written to a CSV file, and a log will be generated 

To run the application:
1) Use the command line call the dvukovich_dbmaker.jar file from the MS3_Code_Challenge directory
  (on Mac the command is "java -jar path/to/directory/MS3_Code_Challenge/dvukovich_dbmaker.jar"
   on Windows the command is "java -jar c:\path\to\directory\MS3_Code_Challenge\dvukovich_dbmaker.jar")

2) When prompted, enter the full file path to the source CSV file for the SQLite database. If the file path provided is incorrect, the user will be prompted to enter a correct file path.

3) The application will run and complete the following steps:
    a) Read in the source file's header and check that it is properly formatted.
    b) Create a directory named <source-filename> in the same directory as the source file. If such a directory already 
        exists, the directory name will be indexed with the first positive integer for which such a directory does not 
        already exist.
    c) Create a database named <source-filename>.db in the above directory.
    d) Create a table named <source-filename> in the new database using the source file's header.
    e) Create a CSV file named <source-filename>-bad.csv in the above directory with the same header as the source file.
    f) Validate each line of the source file inserting the complete records into <source-filename>.db and copying the
        incomplete records to <source-filename>-bad.csv.
    g) Close the database.
    h) Create a log file named <source-filename>.log in the above directory with the # of records received, successful, and
        failed

This application was designed to provided additional flexibility. It can be run on a file with more or less than 10 columns.
It trims whitespace around entries (excepting the double quoted entry), and has the ability to replace interior whitespace with underscores (turned off by default). For speed efficiency, records are only fully parsed if they are complete. Incomplete records are flagged first by the absence of the double quoted entry, and then by the first occurance of an empty entry starting from the left. For resource efficiency, the database and the file writer are only opened when in use. For robustness, empty entries are classified not only by concurrent commas, but also by commas with only whitespace between. 

This application assumes that:
  1) if a valid file path is provided, then the file path leads to a CSV file
  2) a properly formatted CSV file has:
      a) a header as its first line
      b) records separated by newlines with entries separated by commas
      c) records that should contain exactly one entry contained in double quotations where:
          i) the double quoted entry is not the first or last entry
          ii) there is no whitespace between commas and double quotations
          

This program was written in Eclipse version 2019-06 (4.12.0) utilizing
- JDK version 12.0.2
- SQLite JBDC Driver version 3.27.2.1
