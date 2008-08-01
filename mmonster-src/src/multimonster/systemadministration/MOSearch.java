package multimonster.systemadministration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.media.Duration;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaObject;
import multimonster.common.media.MetaData;
import multimonster.exceptions.DBNotAvailableException;

public class MOSearch {
	
	static {
		MOSearch.log = Logger.getLogger(MOSearch.class);
	}

	private static Logger log;
	
	private static SettingProxy proxy;

	public MOSearch() {
	}

	public SearchResult[] search(SearchCriteria criteria)
		throws DBNotAvailableException {
		SearchResult searchResult = null;
		Vector vec = null;

		try {
			String titleSearch = criteria.getTitle();

			QueryManager qmngr = new QueryManager();
			// demand connection
			int connNr = qmngr.reserveConnection();

			if (connNr == -1) {
				throw new DBNotAvailableException("DB not available!");
			}
			// got connection
			ResultSet result =
				qmngr.dbOpExec(
					"select mo.id, mo.title, mo.outline, mo.duration " +
					"from mediaobject mo, mediainstance mi " +
					"where mo.id = mi.moid and " +
					"mo.title like '%"
						+ titleSearch
						+ "%' " +
					"group by mo.id, mo.title, mo.outline, mo.duration",
					connNr);

			if (result != null) {
				vec = new Vector();

				while (result.next()) {
					searchResult = extractData_search(result);
					vec.addElement(searchResult);
				}

				result.close();
				// release Connection
				qmngr.bringBackConn(connNr);

				// Ausgabe bauen
				SearchResult[] ret = new SearchResult[vec.size()];
				ret = (SearchResult[]) vec.toArray(ret);

				return ret;

			} else {
				// release Connection
				qmngr.bringBackConn(connNr);
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * delivers a SearchCriteria-Object filled with possible Value-Domains for
	 * the attributes
	 * 
	 * @return SearchCriteria
	 */
	public SearchCriteria prepareSearch() {
		return new SearchCriteria();
	}

	/**
	 * delivers a SearchCriteria-Object filled with possible Value-Domains for
	 * the attributes
	 * 
	 * @return SearchCriteria
	 */
	private SearchResult extractData_search(ResultSet result) {
		try {
			SearchResult searchResult = null;
			MediaObject mo = null;
			MetaData meta = null;
			MOIdentifier moid = null;

			int monr = result.getInt(1);
			String title = result.getString(2);
			String outline = result.getString(3);
			String duration = result.getString(4);
			//log.debug("Titel des Films: " + title + " Dauer: " + duration);
			meta = new MetaData();
			meta.setTitle(title);
			meta.setOutline(outline);
			meta.setDuration(new Duration(duration));
			moid = new MOIdentifier(monr);
			mo = new MediaObject(moid, meta);
			searchResult = new SearchResult(mo);
			return searchResult;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
