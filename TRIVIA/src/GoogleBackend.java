import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.google.gson.Gson;

public class GoogleBackend {

	public static List<GoogleResults.Result> getQueryResult(String keyword) throws IOException{
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		String charset = "UTF-8";
		
		URL url = new URL(google + URLEncoder.encode(keyword, charset));
		Reader reader = new InputStreamReader(url.openStream(), charset);
	    GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
	    return results.getResponseData().getResults();
	}
}
