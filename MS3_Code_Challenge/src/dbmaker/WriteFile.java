/* Title: File Writer Class
 * Author: Devon Vukovich
 * Email: dsvukovich13@gmail.com
 * 
 * This class establishes methods to:
 * -write a string to a text file (creating the file if necessary), and
 * -create a new directory at the given path (appending an index if necessary)
 * 
 * Last Updated: Oct 11, 2019
 */

package dbmaker;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

public class WriteFile {
	
	private String filePath;
	private boolean appendOn;
	
	public WriteFile(String path, boolean appendtoFile) {
		filePath = path;
		appendOn = appendtoFile;
	}
	
	public void writetoFile(String line) {
		
		try {
			FileWriter writer = new FileWriter(filePath, appendOn);
			PrintWriter printLine = new PrintWriter(writer);
			
			printLine.printf("%s" + "%n", line);
			
			printLine.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String checkDir(String path) {
		
		String sourcePath = path;
		String dirName = sourcePath.substring(0, sourcePath.lastIndexOf('.'));
		String tempName = dirName;
		File tempDir = new File(dirName);
		int index = 0;
		
		while (tempDir.isDirectory()) {
			
			index++;
			dirName = tempName + " " + index;
			tempDir = new File(dirName);
		}
		
		tempDir.mkdir();
		
		return dirName + "/";
	}

}
