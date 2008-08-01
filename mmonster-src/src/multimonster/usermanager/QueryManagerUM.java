package multimonster.usermanager;

import org.apache.log4j.Logger;

/**
 * implemented as Singelton in order to provide a connection pool
 * 
 * @author Marc Iseler
 */

public class QueryManagerUM {

	private static QueryManagerUM queryMngr = null;
	private Logger log;

	public QueryManagerUM() {
		log = Logger.getLogger(this.getClass());
	}

	public void dbQuery(String query) {
		log.debug("dbQuery() - " + query);
	}

	public static QueryManagerUM getInstance() {
		if (QueryManagerUM.queryMngr == null) {
			return new QueryManagerUM();
		} else {
			return QueryManagerUM.queryMngr;
		}
	}

}
