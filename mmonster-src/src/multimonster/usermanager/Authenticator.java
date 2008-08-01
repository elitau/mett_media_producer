package multimonster.usermanager;

import java.sql.ResultSet;
import java.sql.SQLException;

import multimonster.common.AuthData;
import multimonster.systemadministration.QueryManager;
import org.apache.log4j.Logger;

/**
 * Offers Authentication service for MMonster Usermanager currently the Database
 * is used for authentication, but any other Authentication method could be
 * implemented
 * 
 * @author Marc Iseler
 */
public class Authenticator {

	private static Logger log = Logger.getLogger(Authenticator.class);

	/**
	 * creates new Instance
	 */
	public Authenticator() {
		super();
	}

	/**
	 * Password check for User Login and User authentication
	 */
	public boolean checkUserAuth(AuthData authData) {

		log.debug("checkUserAuth()");
		boolean isAuth = false;

		String uid = authData.getUid().getUid();

		QueryManager query = new QueryManager();
		int connNr = query.reserveConnection();
		ResultSet result = query.dbOpExec(
				"select passwd from mmUser where username = '" + uid + "';",
				connNr);

		try {
			if (result.next()) {
				String passw = result.getString("passwd");
				if (authData.check(passw)) {
					isAuth = true;
				}

			} else {
				log.error("Benutzer mit diesem Benutzernamen existiert nicht!");
			}
		} catch (SQLException e) {
			log.error("Fehler beim Auslesen des Passworts zu User " + uid);
		}
		
		query.bringBackConn(connNr);
		return isAuth;

	}

}