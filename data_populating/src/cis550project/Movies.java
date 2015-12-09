package cis550project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class Movies {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://trivia.crqsulbfdc9h.us-west-2.rds.amazonaws.com/movie";
	static final String USERNAME = "fanglinlu";
	static final String PASSWORD = "lfl383LFL";
	static final String BUILD_PATH = "/Users/fanglinlu/Desktop/movies.csv";
		
	public static void main(String[] args) throws SQLException,ClassNotFoundException, IOException, ParseException {
		Connection conn = null;
		Statement stmt = null;
		Class.forName(JDBC_DRIVER);
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
		stmt = conn.createStatement();
		System.out.println("Creating statement...");
		
		BufferedReader br = new BufferedReader(new FileReader(BUILD_PATH));
		String line;
		br.readLine();
		StringBuilder sql = new StringBuilder();
		stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
		stmt.executeUpdate("TRUNCATE movies");
		stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
		while ((line = br.readLine()) != null) {
			sql = new StringBuilder();
			
			sql.append("INSERT INTO movies VALUES (");
			String[] str = line.split(",");
			String rating = str[2];
			if (rating.length() < 4) {
				rating = "null";
			}
			String mid = str[0];
			if (mid.equals("0")) {
				continue;
			}
			
			sql.append(str[0] + "," + "'" + str[1].trim().replace("\"", "").replace("'", "\\'") + "'" + "," + rating + "," + "'" + str[3].trim().replace("'", "\\'") + "')");
			System.out.println(sql);
			
			stmt.executeUpdate(new String(sql));
		}
		System.out.println("import done");
		
		
		br.close();
		stmt.close();
		conn.close();
		
	}

}
