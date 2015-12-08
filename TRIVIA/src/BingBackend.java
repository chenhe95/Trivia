import java.util.ArrayList;
import java.util.List;

import net.billylieurance.azuresearch.AzureSearchResultSet;
import net.billylieurance.azuresearch.AzureSearchWebQuery;
import net.billylieurance.azuresearch.AzureSearchWebResult;


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
	 * In order to use the webresults, it has getTitle(), getDescription(), getDisplayUrl(), getUrl().
	 * @param keyword the word to be queried by bing
	 * @param size the size of the response
	 * @return a list of results.  
	 */
	public static List<AzureSearchWebResult> getQueryResult(String keyword, int size){
		List<AzureSearchWebResult> answer = new ArrayList<>((size+50));
		AzureSearchWebQuery aq = new AzureSearchWebQuery();
		aq.setAppid(APP_KEY);
		aq.setQuery(keyword);
		
		for (int i = 1; i <= size/50+1; i++){
			AzureSearchResultSet<AzureSearchWebResult> ars = aq.getQueryResult();
			for (AzureSearchWebResult anr : ars) {
				answer.add(anr);
			}
		}
		return answer;
	}
}
