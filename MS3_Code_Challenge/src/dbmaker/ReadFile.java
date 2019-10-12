package dbmaker;

import java.io.File;
import java.util.Scanner;

/* Title: File Reader Class
 * Author: Devon Vukovich
 * Email: dsvukovich13@gmail.com
 * 
 * This class establishes the methods to:
 * -retrieve a file path from the user,
 * -get this file path, 
 * -get/set a File object corresponding to this path
 * -check that the file path is valid, and
 * -retrieve the name of the file
 * 
 * Last Updated: Oct 11, 2019
 */

public class ReadFile {
	
	private String filePath;
	private File file;
	
	public ReadFile() {
		
	}
	
	// Query user for a full file path (i.e. /Users/User/Desktop/Example.csv)
	public String setFilePath() throws Exception {
		
		System.out.println("Please enter the full file path for the CSV file to be read.");	
		
		try 
		{
			Scanner scanner = new Scanner(System.in);
			filePath = scanner.nextLine();	
			scanner.close();
			
		} catch (Exception e) {
			throw e;
		}
		return filePath;
	}
	
	public String getFilePath() {
		
		return filePath;
	}
	
	// Check that the file indicated by the path exists. If not, query user for a new path.
	public void checkFilePath() throws Exception {
		
		setFile();
		boolean exists = file.exists();
		
		while (!exists) {
			
			System.out.println("This file path is invalid.");
			try {
				setFilePath();
			} catch (Exception e) {
				throw e;
			}
			setFile();
			exists = getFile().exists();
		}
	}
	
	public void setFile() {
		
		file = new File(getFilePath());
	}
	
	public File getFile() {
		
		return file;
	}
	
	public String getFileName() {
		
		String extName = getFile().getName();
		int i = extName.lastIndexOf('.');
		String onlyName = extName.substring(0, i);
		return onlyName;
	}
}
