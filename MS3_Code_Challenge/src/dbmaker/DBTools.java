/* Title: Database Toolbox
 * Author: Devon Vukovich
 * Email: dsvukovich13@gmail.com
 * 
 * This class establishes methods to:
 * -create/connect to a SQLite database using the JDBC driver
 * -create a table in the connected database given a list of column titles
 * -insert a record into the most recently created table, and
 * -close the database connection
 * 
 * Last Updated: Oct 11, 2019
 */

package dbmaker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DBTools {
	
	private Connection conn = null;
	private String tableName = null;
	
	public DBTools (String path) throws Exception {
			
		connect (path);
		
	}
	
	public void connect(String name) throws SQLException {
		
		String dbName = name;
		String url = "jdbc:sqlite:" + dbName + ".db";
		
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public void createTable(String name, ArrayList<String> header) throws SQLException {
		
		tableName = name.trim().replace("\'", "\'\'");
		String sql = "CREATE TABLE '" + tableName + "' (\n ";
		ArrayList<String> headerList = header;
		
		for (int i = 0; i < headerList.size(); i++) {
			sql += "'" + headerList.get(i).replace("\'", "\'\'") + "'" + " varchar";
			if (i < headerList.size()-1) {
				sql += ",\n ";
			}
			else {
				sql += ");";
			}
		}
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public void insert(ArrayList<String> record) throws SQLException {
		
		ArrayList<String> recordList = record;
		String sql = "INSERT INTO '" + tableName + "' VALUES(";
		
		for (int i = 0; i < recordList.size(); i++) {
			sql += "'" + recordList.get(i).replace("\'", "\'\'") + "'";
			if (i < recordList.size()-1) {
				sql += ",\n ";
			}
			else {
				sql += ");";
			}
		}
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public void close() throws Exception {
		conn.close();
	}
}
