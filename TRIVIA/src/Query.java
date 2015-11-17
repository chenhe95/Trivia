
public class Query {

	private String query = null;

	public Query(String select, String from, String where, String suffixes) {
		query = "select " + select + " from " + from + " where " + where + " " + suffixes;
	}

	public String getQuery() {
		return query;
	}

	@Override
	public int hashCode() {
		return query.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Query) {
			return getQuery().equals(((Query) o).getQuery());
		}
		return false;
	}
}
