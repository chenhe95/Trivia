import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MongoTest {
	
	public static void main(String args[]) throws ParseException {
		MongoConnect mongo = new MongoConnect();
		DB db = mongo.getDatabase();
		
		DBObject query = new BasicDBObject();
		query.put("Info.Username", "JayJung");
		DBObject fields = new BasicDBObject();
		String d = "Beginner";
		DBCollection collection = db.getCollection("Users");
		
		DBCursor cursor = collection.find(query,fields);
		String json = cursor.next().toString();
		JSONParser parser = new JSONParser();
		JSONObject doc = (JSONObject) parser.parse(json);
		
        JSONObject diffStruct = (JSONObject) doc.get(d);
        int userCorrect = Integer.parseInt(diffStruct.get("Correct").toString());
        int userTotal = Integer.parseInt(diffStruct.get("Correct").toString());
        
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject(d, new BasicDBObject("Total", 0).append("Correct", 0)));
        collection.update(new BasicDBObject().append("Info.Username", "JayJung"), newDocument);

	}
}
