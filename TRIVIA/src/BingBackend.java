import java.util.List;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

/**
 * 
 */

/**
 * @author Jason
 *
 */
public class BingBackend {

	public static final String APP_KEY = "0AZbBkuj8OHRk+UduutcFfxRbHSY2Aimi8nP5CK8gNU=";
	
	/**
	 * In order to use the webresults, it has getTitle(), getDescription(), getUrl(), and getDateTime().
	 * @param keyword the word to be queried by bing
	 * @return a list of results.  
	 */
	public static List<WebResult> getQueryResult(String keyword){
		BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();
		BingSearchClient client = factory.createBingSearchClient();

		SearchRequestBuilder builder = client.newSearchRequestBuilder();
		builder.withAppId(APP_KEY);
		builder.withQuery(keyword);
		builder.withSourceType(SourceType.WEB);
		builder.withVersion("2.0");
		builder.withMarket("en-us");
		builder.withAdultOption(AdultOption.MODERATE);
		builder.withSearchOption(SearchOption.ENABLE_HIGHLIGHTING);

		builder.withWebRequestCount(10L);
		builder.withWebRequestOffset(0L);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_QUERY_ALTERATIONS);

		SearchResponse response = client.search(builder.getResult());
		return response.getWeb().getResults();
	}
	
}
