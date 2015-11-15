
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author He
 */
public class DBConnect {

    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	String testQuery = "select * from movie.movies limit 25";
        DBConnect db = new DBConnect();
        Connection connection = db.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery(testQuery);
        ResultSet result = statement.getResultSet();
        while(result.next()) {
        	int mid = result.getInt("mid");
        	double rating = result.getDouble("rating");
        	System.out.println("mid: " + mid + " rating: " + rating);
        }
    }

    private String dbms = "mysql";
    private String dbname = "movie";
    private String username = "fanglinlu";
    private String password = "lfl383LFL";
    private int portNumber = 3306;
    private String servername = "trivia.crqsulbfdc9h.us-west-2.rds.amazonaws.com";

    public Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);
        
        String url = "jdbc:" + dbms + "://"
                + servername
                + ":" + portNumber + "/" + dbname;
        
        
        System.out.println("Connection to: " + url);
        conn = DriverManager.getConnection(url, connectionProps);
        if (conn != null) {
            System.out.println("Connected to database");
        }
        return conn;
    }

}
