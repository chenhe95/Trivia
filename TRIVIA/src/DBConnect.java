
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

	/*
	 * Connection parameters
	 */
	
	private static final String dbms = "mysql";
	private static final String dbname = "movie";
	private static final String username = "fanglinlu";
	private static final String password = "lfl383LFL";
	private static final int portNumber = 3306;
	private static final String servername = "trivia.crqsulbfdc9h.us-west-2.rds.amazonaws.com";

	public static void main(String[] args) throws SQLException {
		String testQuery;
		String preQuery = "(SELECT * from (SELECT @row := @row +1 as rownum, name as name, mid as mid, rating as rating FROM (SELECT @row := 0) r, movie.movies) ranked WHERE rownum % 4 = 1 AND rating > 3)";
		//testQuery = "select movie.movie_genre.mid as m_id, movie.movie_genre.genre as m_genre from movie.movie_genre inner join movie.movies on movie.movies.mid = movie.movie_genre.mid where movie.movies.rating > 4";
		testQuery = "select movie.movie_genre.genre as m_genre, T.name as m_name from movie.movie_genre inner join " + preQuery + "as T on T.mid = movie.movie_genre.mid";
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		statement.executeQuery(testQuery);
		ResultSet result = statement.getResultSet();
		while (result.next()) {
			//int mid = result.getInt("mid");
			//double rating = result.getDouble("rating");
			System.out.println("" + result.getString("m_name") + " " + result.getString("m_genre"));
			//System.out.println(result.getInt("m_count"));
		}
	}

	public static Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Properties connectionProps = new Properties();
			connectionProps.put("user", username);
			connectionProps.put("password", password);

			String url = "jdbc:" + dbms + "://" + servername + ":" + portNumber + "/" + dbname;

			System.out.println("Attempting to connect to: " + url);
			conn = DriverManager.getConnection(url, connectionProps);
			if (conn != null) {
				System.out.println("Connected to database");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
