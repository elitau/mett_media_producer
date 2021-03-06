/* Generated by Together */

package multimonster.systemadministration;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import multimonster.exceptions.MultiMonsterException;

import org.apache.log4j.Logger;

public class QueryManager {

	private Logger log;

	//private static String dbUrl =
	// "jdbc:mysql://faui6p11/mmonster?user=inf6&password=multi";
	private static String dbUrl = "jdbc:mysql://127.0.0.1:3306/mmonster?user=monster&password=monster";

	//private static String dbUrl = "jdbc:mysql://localhost/mmonster";

	private Connection conn;

	public QueryManager() {

		log = Logger.getLogger(this.getClass());
		this.conn = null;

	}

	/**
	 * reserves one connection out of the connection pool
	 * 
	 * @return
	 */
	public int reserveConnection() {
		try {
			if (this.conn == null || this.conn.isClosed()) {

				log.debug("Try to Load DB-Driver");
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				log.debug("successful");
				conn = DriverManager.getConnection(dbUrl);
				log.debug("DB-Connection established");
			}
		} catch (InstantiationException e) {
			log.error("Fehler beim Laden des DB-Treibers");
			return -1;
		} catch (IllegalAccessException e) {
			log.error("Zugriff auf DB verweigert");
			return -1;
		} catch (ClassNotFoundException e) {
			log.error("Datenbanktreiber wurde nicht gefunden");
			return -1;
		} catch (SQLException e) {
			log.error("Fehler bei Datenbankverbindung");
			log.error(e);
			return -1;
		}
		return 0;
	}

	/**
	 * deblocks a reserved connection
	 * 
	 * @param connNr
	 */
	public void bringBackConn(int connNr) {
		try {
			if (!(this.conn != null || this.conn.isClosed()))
				this.conn.close();
		} catch (SQLException e) {
			log.error("Fehler bei Datenbankverbindung");
		}
	}

	/**
	 * Executes a Query to the DB Takes in SQL-Statement and returns the
	 * ResultSet for it
	 * 
	 * @param query
	 *            SQL-Query
	 * @return ResultSet
	 */
	public ResultSet dbOpExec(String query, int connNr) {
		ResultSet result = null;
		Statement stmt = null;

		try {
			stmt = this.conn.createStatement();
			//log.debug("Statement was successfully created.");
			result = stmt.executeQuery(query);
			//log.debug("Query was successfully executed.");

		} catch (SQLException e) {
			log.error("Query failed: " + query);
			log.error("SQLException: " +e.getLocalizedMessage());
		}

		return result;
	}

	/**
	 * Executes a Query to the DB Takes in SQL-Statement and returns the
	 * ResultSet for it
	 * 
	 * @param query
	 *            SQL-Query
	 * @return ResultSet
	 */
	public void dbOpInsert(String query, int connNr)
			throws MultiMonsterException {
		Statement stmt = null;

		try {
			stmt = this.conn.createStatement();
			//log.debug("Statement was successfully created.");
			stmt.execute(query);
			//log.debug("Query was successfully executed.");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.debug("Query failed: " + query);
			log.error("SQLException: " +e.getLocalizedMessage());
			throw new MultiMonsterException("Failed Query: " + query);
		}
	}

	/**
	 * Executes a insert Query for the DB containing one or more BLOB's Takes in
	 * SQL-Statement and Array of BLOBS
	 * 
	 * @param query
	 *            SQL-Query
	 * @return ResultSet
	 */
	public void dbOpInsertBLOB(String query, byte[] ba1, byte[] ba2, int connNr)
			throws MultiMonsterException {
		PreparedStatement pstmt = null;

		try {
			//log.debug("l�nge des Bytearrays: " + ba1.length + " " +
			// ba2.length);
			pstmt = this.conn.prepareStatement(query);
			// Byte Array in Stream wandeln der dann in ...
			ByteArrayInputStream bais1 = new ByteArrayInputStream(ba1);
			ByteArrayInputStream bais2 = new ByteArrayInputStream(ba2);
			// ... ein Blob reinfliessen kann
			pstmt.setBinaryStream(1, bais1, ba1.length);
			pstmt.setBinaryStream(2, bais2, ba2.length);

			//log.debug("PreparedStatement was successfully created.");
			pstmt.execute();
			//log.debug("PreparedQuery was successfully executed.");
			pstmt.close();

		} catch (SQLException e) {
			log.debug("Prepared Query failed: " + query);
			throw new MultiMonsterException("Failed PreparedQuery: " + query);
		}
	}

	/**
	 * Executes a update Query for the DB containing one or more BLOB's Takes in
	 * SQL-Statement and Array of BLOBS
	 * 
	 * @param query
	 *            SQL-Query
	 * @return ResultSet
	 */
	public void dbOpUpdateBLOB(String query, byte[] ba1, int connNr)
			throws MultiMonsterException {
		PreparedStatement pstmt = null;

		try {
			//log.debug("l�nge des Bytearrays: " + ba1.length + " " +
			// ba2.length);
			pstmt = this.conn.prepareStatement(query);
			// Byte Array in Stream wandeln der dann in ...
			ByteArrayInputStream bais1 = new ByteArrayInputStream(ba1);
			// ... ein Blob reinfliessen kann
			pstmt.setBinaryStream(1, bais1, ba1.length);

			//log.debug("PreparedStatement was successfully created.");
			pstmt.executeUpdate();
			log.debug("PreparedQueryUpdate was successfully executed.");
			pstmt.close();

		} catch (SQLException e) {
			log.debug("Prepared Query Update failed: " + query);
			throw new MultiMonsterException("Failed PreparedQueryUpdate: "
					+ query);
		}
	}
}