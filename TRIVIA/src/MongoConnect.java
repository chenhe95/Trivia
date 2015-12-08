import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class MongoConnect {
	MongoClientURI uri;
	MongoClient client;
	
	public MongoConnect() {
		uri = new MongoClientURI("mongodb://jayjung:jungshi12@ds061954.mongolab.com:61954/trivia");
		client = new MongoClient(uri);
	}
	
	public DB getDatabase() {
		@SuppressWarnings("deprecation")
		DB database = client.getDB("trivia");

		return database;
	}
}
