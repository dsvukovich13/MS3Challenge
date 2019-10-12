package dbmaker;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import dbmaker.BlankEntryException;
import dbmaker.ReadFile;
import dbmaker.WriteFile;
import dbmaker.DBTools;

/* Title: Database Maker Class
 * Author: Devon Vukovich
 * Email: dsvukovich13@gmail.com
 * 
 * This class establishes methods to:
 * -retrieve a specified line from a file
 * -convert a string to an array using commas to mark entries and check for blank entries
 * -get/set a list of column titles
 * -convert the specific string with exactly one interior double quoted entry that may contain commas
 * -copy the complete records of a .csv file into a SQLite database 
 * 		while writing the incomplete records to a .csv file
 * -write a .log file with # of records received, successful and failed
 * -run the complete .csv to SQLite database process
 */

public class DBMaker {
	
	private ReadFile reader;
	private WriteFile writer;
	private DBTools toolbox;
	private File file;
	private String name;
	private ArrayList<String> headerNames = new ArrayList<String>();
	private int recordCount = 0;
	private int goodCount = 0;
	private int badCount = 0;
	private int columnCount = 0;
	
	public DBMaker() {
	
		
		
	}
	
	// Retrieve the string literal of line number x
	public String getLineString(int x) {
		
		String line = null;
		
		try {
			Scanner scanner = new Scanner(file);
			
			for (int i = 0; i <= x; i++) {
				line = scanner.nextLine();
			}
			
			scanner.close();	
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return line;
	}
	
	// Create an array from the comma separated entries of a given line. Error for blank entries.
	// Has the option to replace white spaces in the entries with '_'
	public ArrayList<String> linetoList (String line, boolean cleanWS) throws BlankEntryException{
		
		String tempString = line;
		String tempEntry;
		boolean replaceWhitespace = cleanWS;
		ArrayList<String> entryList = new ArrayList<String>();
		int index = tempString.indexOf(',');
		
		while (true) {
			
			//if the string begins with a comma, then error
			if (index == 0) {
				throw new BlankEntryException();
			}
			
			//if there is a comma and it is not the first character
			else if (index > 0) {
				
				//if the comma was the last character of the line, then error
				if (index + 1 == tempString.length()) {
					throw new BlankEntryException();
				}
				
				tempEntry = tempString.substring(0, index);
				
				if (tempEntry.isBlank()) {
					throw new BlankEntryException();
				}
				
				//otherwise clean the entry, add to array and increment
				else {
					tempEntry = tempEntry.trim();
				
					if (replaceWhitespace) {
						tempEntry = tempEntry.replace(' ', '_');
					}
					
					entryList.add(tempEntry);						
					tempString = tempString.substring(index+1);
					index = tempString.indexOf(',');
				}
			}
			
			//if there is no comma
			else {
				
				if (tempString.isBlank()) {
					throw new BlankEntryException();
				}
				
				//use the entire string as an entry, clean, add to array and end the loop
				else {
					tempEntry = tempString.trim();
					
					if (replaceWhitespace) {
						tempEntry = tempEntry.replace(' ', '_');
					}
					
					entryList.add(tempEntry);
					break;
				}
			}
		}
		return entryList;
	}
	
	public void createHeaderList() throws BlankEntryException {
		
		String headerString = getLineString(0);
		
		try {
			headerNames = linetoList(headerString, false);
			columnCount = getHeaderList().size();
		} catch (BlankEntryException e) {
			System.out.println("File has at least one blank header name. "
					+ "Please correct and try again.");
			throw e;
		}
	}
	
	public ArrayList<String> getHeaderList() {
		
		return headerNames;
	}
	
	// Converts a record line into a record array checking for blank entries.
	// If a blank entry is found, the line is counted, written to a bad line file,
	// and an empty array is returned.
	public ArrayList<String> recordCheckConvert(String line) {
		
		String recordLine = line;
		ArrayList<String> recordArray = new ArrayList<String> ();
		
		int quoteStart = recordLine.indexOf(",\"");
		int quoteEnd = recordLine.indexOf("\",");
		
		//if double quotation exists and the string does not begin or end in a comma
		if (quoteStart > 0 && quoteEnd  > quoteStart+1 && quoteEnd < recordLine.length()-2) {
			
			String frontString = recordLine.substring(0, quoteStart);
			String quoteString = recordLine.substring(quoteStart+1, quoteEnd+1);
			String rearString = recordLine.substring(quoteEnd+2);
			
			try {
				//convert strings to arrays checking for blank entries
				ArrayList<String> frontArray = linetoList(frontString, false);
				ArrayList<String> rearArray = linetoList(rearString, false);
				
				//create record array
				recordArray.addAll(frontArray);
				recordArray.add(quoteString);
				recordArray.addAll(rearArray);
			 
			//if a blank entry is found, count the line and write in bad file
			} catch (BlankEntryException e) {
				badCount ++;
				writer.writetoFile(recordLine);
			}
		}
		//if there is no quoted entry, count the line and write in bad file
		else {
			badCount ++;
			writer.writetoFile(recordLine);
		}
		return recordArray;
	}
	
	// Copies the good records from the CSV file into the database
	public void copyFile() throws Exception {
		
		try {
			Scanner scanner = new Scanner(file);
			
			//write the header line to the bad file
			String header = scanner.nextLine();
			writer.writetoFile(header);
			
			//count and check/convert each line
			while (scanner.hasNextLine()) {
				
				recordCount ++;
				String line = scanner.nextLine();
				ArrayList<String> record = recordCheckConvert(line);
				int entryCount = record.size();
				
				//count and insert each good record into the database
				if (entryCount == columnCount) {
					goodCount ++;
					toolbox.insert(record);
				}
				//count and write each uncounted bad record to bad file
				else if (entryCount != 0) {
					badCount ++;
					writer.writetoFile(line);
				}	
			}
			
			scanner.close();	
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void makeLog () {
		
		writer.writetoFile(recordCount + " records received.");
		writer.writetoFile(goodCount + " records successful.");
		writer.writetoFile(badCount + " records failed.");
	}
	
	public void run() {
		
		try {
			reader = new ReadFile();
			reader.setFilePath();
			reader.checkFilePath();
			file = reader.getFile();
			name = reader.getFileName();
			createHeaderList();
			
			String newPath = WriteFile.checkDir(file.getPath());
			toolbox = new DBTools (newPath + name);
			toolbox.createTable(name, getHeaderList());
			
			writer = new WriteFile (newPath + name + "-bad.csv", true);
			copyFile();
			toolbox.close();
			
			writer = new WriteFile (newPath + name + ".log", true);
			makeLog();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
