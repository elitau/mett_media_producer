/*
 * Created on 07.02.2004
 */
package mmonster.webfrontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import multimonster.common.AuthData;
import multimonster.common.ConnectionAddress;
import multimonster.common.Format;
import multimonster.common.FormatId;
import multimonster.common.InputOption;
import multimonster.common.OutputOption;
import multimonster.common.Protocol;
import multimonster.common.ProtocolId;
import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.UserIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MetaData;
import multimonster.controller.exceptions.ControllerException;
import multimonster.controller.exceptions.InvalidAuthDataException;
import multimonster.controller.interfaces.ControllerImpl;
import multimonster.controller.interfaces.ControllerImplHome;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;

import org.apache.log4j.Logger;

/**
  * 
 * A web-UserInterface for the MultiMonster-Server.
 * Some standard-operations are implemented (login, insert, search, play, logout).
 * 
 * @author jrgmei
 * 
 * @web.servlet name = "ControllerDispachterServlet" display-name =
 * "ControllerDispachter Servlet" description = "Servlet that calls
 * MMonster-Controller-Methods"
 * 
 * @web.servlet-mapping url-pattern = "/ControllerDispachter"
 * 
 * @web.env-entry name = "Title" type = "java.lang.String" value =
 * "ControllerDispachterServlet" description = ""
 * 
 * @web.ejb-ref name = "multimonster/controller/Controller" type = "Session"
 * home = "multimonster.controller.interfaces.ControllerImplHome" remote =
 * "multimonster.controller.interfaces.ControllerImpl" description = ""
 * 
 * @jboss.ejb-ref-jndi ref-name = "multimonster/controller/Controller"
 * jndi-name = "ejb/ControllerFacade"
 *  
 */
public class ControllerDispachterServlet extends HttpServlet {

	private Logger log;

	private String title;

	private ControllerImplHome controllerHome;

	private MediaProxyImplHome proxyHome;
	
	private String httpSessionID;
	
	private ControllerImpl controller = null;
	

	public ControllerDispachterServlet() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
	
		Context context;
	
		log = Logger.getLogger(this.getClass());
		log.debug("ControllerDispatcher-Servlet init...");
	
		try {
			context = new InitialContext();
			title = (String) context.lookup("java:/comp/env/Title");
	
			Object ref = context.lookup(ControllerImplHome.JNDI_NAME);
			controllerHome =
				(ControllerImplHome) PortableRemoteObject.narrow(
					ref,
					ControllerImplHome.class);
	
			ref = context.lookup(MediaProxyImplHome.JNDI_NAME);
			proxyHome =
				(MediaProxyImplHome) PortableRemoteObject.narrow(
					ref,
					MediaProxyImplHome.class);
	
		} catch (NamingException e) {
			log.error("Couldn't get Controller: " + e.getMessage());
	
		} catch (Exception e) {
			log.error("Couldn't get Controller: " + e.getMessage());
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse response)
		throws ServletException, IOException {
	
		log.warn(
			"ControllerDispatcher-Servlet directly called (HTTP-Method GET).");
	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>");
		out.println(title);
		out.println("</title></head>");
		out.println("<body>");
		out.println("Please use HTML-websites to start application.");
		out.println("</body></html>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException {

		String function = null;
		Object controller_from_httpSession;		
		
		// besorge HTTP-Session aus Request-Obejekt,
		// falls keine Session vorhanden, wird diese angelegt
		HttpSession httpSession = req.getSession(true);
		httpSessionID = httpSession.getId();
		
		// controller aus der Session holen, falls vorhanden:
		controller_from_httpSession = httpSession.getAttribute("controller");
		
		if (controller_from_httpSession == null || (!(controller_from_httpSession instanceof ControllerImpl))){

			// wenn kein controller da, dann erzeugen und in Session ablegen:
			try {
				controller = controllerHome.create();
				httpSession.setAttribute("controller", controller);

			} catch (RemoteException e) {
				log.error("Error calling Controller: " + e.getMessage());

			} catch (CreateException e) {
				log.error("Error calling Controller: " + e.getMessage());
			}			
			
		} else {
			controller = (ControllerImpl) controller_from_httpSession;
			
		}		
		

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println( printHTMLHeader() );
		out.println("<body>");
//		out.println("<center>HTTP-Session-ID <b>" +httpSessionID +"<b></center><p/>");
		out.println("<center>");

		try {
			if (controller != null) {

				function = req.getParameter("function");
				if (function.equals("login")) {
					login(out, req);
					
				} else if (function.equals("logout")) {
					logout(out, req);
					
				} else if (function.equals("search_result")) {
					searchResult(out, req);
					
				} else if (function.equals("handleSearchResult")) {
					String deleteCheckbox = null;					
					deleteCheckbox = req.getParameter("del");
					log.debug("del-checkbox: " +deleteCheckbox);
					if (deleteCheckbox != null && deleteCheckbox.equals("1")){
						// moid should be deleted
						delete(out, req);						
					} else {
						// normal play
						play1(out, req);
					}
					
				} else if (function.equals("play2")) {
					play2(out, req);
					
				} else if (function.equals("insert1")) {
					insert1(out, req);
					
				} else if (function.equals("insert2")) {
					insert2(out, req);
					
				} else {
					log.warn(
						"unknown function called: '"
							+ req.getParameter("function")
							+ "'");
				}
			} else {
				/* no controllerHome */
				log.error("No connection to Controller, can't call anything.");
				out.println("No connection to Controller, can't call anything.");
			}

		} catch (RemoteException e) {
			log.error("Error calling Controller: " + e.getMessage());

		} catch (CreateException e) {
			log.error("Error calling Controller: " + e.getMessage());

		} finally {
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	/**
	 * schreibt HTML-Header für Servlet-Antwort
	 */
	private String printHTMLHeader() {
		String ret = "";
		
		ret += "<head>";
		ret += "<title>" + title + "</title>";
		ret += "<link rel=\"stylesheet\" media=\"all\" href=\"style.css\">";
		ret += "<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">";
		ret += "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">";
		ret += "</head>";
		
		return ret;
	}

	/**
	 *  
	 */
	private void login(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException {
	
		boolean loginSuccessful = false;
		AuthData auth = null;
	
		UserIdentifier uid = new UserIdentifier(req.getParameter("username"));
		auth = new AuthData(uid, req.getParameter("password"));
	
		try {
			loginSuccessful = controller.login(auth);
	
		} catch (InvalidAuthDataException e) {
			out.println("Login failed.<br>Please retry.");
		
		} catch (ControllerException e1) {
			out.println("<b>The following exception occured:</b></br>");
			out.println(e1.getMessage());
		}
		
		out.println("<p>");
		if (loginSuccessful) {
			out.println(
				"User <b>"
					+ auth.getUid().getUid()
					+ "</b> successfully logged in.<br>");
			
		} 

		out.println("</p>");
	}

	/**
	 * @param controller2
	 * @param out
	 * @param req
	 * @throws RemoteException
	 */
	private void logout(PrintWriter out, HttpServletRequest req) throws RemoteException {
		
		try {
			controller.logout();
			out.println("You are now logged out.");
			
		} catch (ControllerException e) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e.getMessage());
			out.println("</p>");
		}
		
	}

	/**
	 *  
	 */
	private void searchResult(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException {
	
		log.debug("Viewing searchresult.");
	
		out.println(
			"<form action=\"ControllerDispachter\" method=\"POST\" >"
				+ "<input type=\"hidden\" name=\"function\" value=\"handleSearchResult\">"
				+ "<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">");
	
		SearchCriteria crit = new SearchCriteria();
		SearchResult[] result = null;
	
		crit.setTitle(req.getParameter("title_search_string"));
		try {
			result = controller.search(crit);
	
			if ((result != null) && (result.length > 0)) {
				for (int i = 0; i < result.length; i++) {
					int moid =
						result[i].getMediaObject().getMOId().getMoNumber();
					out.println(
						"<tr><td><input type=\"radio\" name=\"mo\" value=\""
							+ moid
							+ "\"");
					if (i == 0)
						out.println(" checked=\"checked\"");
					out.println(">");
					out.println(
						result[i].getMediaObject().getMetaData().getTitle());
					out.println("</td></tr>");
				}
				out.println("<tr><td></td></tr>");
				out.println("<tr><td><input type=\"checkbox\" name=\"del\" value=\"1\">delete selected media-object</td></tr>");
				out.println(
					"<tr><td><input type=\"submit\" name=\"button\" value=\"Process selection...\"></td></tr>");
			} else {
				out.println("<tr><td>Nothing found</td></tr>");
			}
			out.println("</table></form>");
		} catch (ControllerException e) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e.getMessage());
			out.println("</p>");
		}
	
	}

	private void delete(PrintWriter out,
		HttpServletRequest req)
		throws RemoteException {
	
		boolean delSuccessful = false;
		
		log.debug("Delete.");
		
		// get mOId from user selection:
		MOIdentifier moid =
			new MOIdentifier(new Integer(req.getParameter("mo")).intValue());
		
		try {
			delSuccessful = controller.deleteMediaObject(moid);
			
			if (delSuccessful) {
				out.println("Deleted MediaObject<br>" +moid.getMoNumber() +".");
			} else {
				out.println("<b>Could not delete MediaObject:<br> " +moid +".</b>");
			}
			
		} catch (ControllerException e) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e.getMessage());
			out.println("</br></p>");
		}



		
	}

	/**
	 * view outputpossibilities
	 */
	private void play1(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException {
		
		Format format = null;
		Protocol protocol = null;
	
		log.debug("View Output possibilities");
	
		out.println("<form action=\"ControllerDispachter\" method=\"POST\">");
		out.println(
			"<input type=\"hidden\" name=\"function\" value=\"play2\">");
		out.println("<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">");
	
		out.println(
			"<tr><td></td><td><b>Format</b></td><td><b>Protocol</b></td></tr>");
	
		OutputOption[] oo = null;
	
		// get mOId from user selection:
		MOIdentifier moid =
			new MOIdentifier(new Integer(req.getParameter("mo")).intValue());
	
		try {
			oo = controller.getOutputOptions(moid);
	
			if (oo != null) {
				for (int i = 0; i < oo.length; i++) {
					format = oo[i].getFormat();
					protocol = oo[i].getProtocol();
					if (format == null || protocol == null){
						continue;		
					}
					
					out.println(
						"<tr><td><input type=\"radio\" name=\"mo\" value=\""
							+ req.getParameter("mo")
							+ "."
							+ format.getFormatId().getId()
							+ "."
							+ protocol.getProtocolID().getId()
							+ "\"");
					if (i == 0)
						out.println(" checked=\"checked\"");
					out.println("></td>");
					out.println(
						"<td>" + format.getDescription() + "</td>");
					out.println(
						"<td>"
							+ protocol.getProtocolName()
							+ "</td>");
					out.println("</tr>");
				}
			}
	
			out.println(
				"<tr><td><input type=\"submit\" name=\"button\" value=\"Get it...\"></td></tr>"
					+ "</table></form>");
	
		} catch (ControllerException e1) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e1.getMessage());
			out.println("</br></p>");
		}
	
	}

	/**
	 * get MediaObject in selected Format, return link to Proxy
	 */
	private void play2(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException {

		ConnectionAddress addr = null;
		StringTokenizer to_parse = null;
		String mOIdString = null;
		String formatString = null;
		String protocolId = null;
		OutputOption oo = null;
		MOIdentifier mOId = null;

		log.debug(
			"User has choosen: "
				+ req.getParameter("mo")
				+ ", presenting the link.");

		// input-form parameter parsing to get mOId, format and
		// protocol

		to_parse = new StringTokenizer(req.getParameter("mo"), ".");

		mOIdString = to_parse.nextToken();
		formatString = to_parse.nextToken();
		protocolId = to_parse.nextToken();

		mOId = new MOIdentifier(new Integer(mOIdString).intValue());
		oo =
			new OutputOption(
				new Format(new FormatId(formatString)),
				new Protocol(new ProtocolId(protocolId)));

		try {
			String link = null;
			out.println("<p>");
			addr = controller.prepareOutput(mOId, oo);
			link = addr.getUrl().toString();
			out.println("<a href=\"" + link + "\">GET IT</a>");
		} catch (ControllerException e1) {
			out.println("<b>The following exception occured:</b></br>");
			out.println(e1.getMessage());
		} finally {
			out.println("</p>");
			out.println("</table></form>");
		}
	}

	/**
	 * getInputOptions
	 */
	private void insert1(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException, CreateException {
	
		log.debug("View Input possibilities");
	
		InputOption[] io = null;
	
		try {
			io = controller.getInputOptions();
	
			if (io != null) {
	
				out.println(
						"<form action=\"ControllerDispachter\" method=\"POST\" >"
							+ "<input type=\"hidden\" name=\"function\" value=\"insert2\">"
							+ "<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">");
					out.println(
						"<tr><td></td><td><b>Protocol</b></td></tr>");
				
				for (int i = 0; i < io.length; i++) {
					if (io[i].getProtocol() != null) {
						out.println(
							"<tr><td><input type=\"radio\" name=\"io\" value=\""
								+ io[i].getProtocol().getProtocolID().getId()
								+ "\"");
						if (i == 0)
							out.println(" checked=\"checked\"");
						out.println("></td>");
						out.println(
							"<td>"
								+ io[i].getProtocol().getProtocolID().getId()
								+ "</td>");
						out.println("</tr>");
					}
				}
				out.println("<tr></tr>" +
				"<tr><td>Title</td><td><input type=\"text\" name=\"title_string\" value=\"\"></td></tr>");
				out.println(
				"<tr><td>Outline</td><td><input type=\"text\" name=\"outline_string\" value=\"\"></td></tr>");
				out.println(
				"<tr><td></td><td><input type=\"submit\" name=\"button\" value=\"Insert it...\"></td></tr>"
					+ "</table></form>");
	
			} else {
				out.println("<p><b>No InputOptions have been returned.</b><br>");
			}
	
	
		} catch (ControllerException e1) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e1.getMessage());
			out.println("</br></p>");
		}
	}

	/**
	 * prepare insert, view connectionAddress to insert
	 */
	private void insert2(
		PrintWriter out,
		HttpServletRequest req)
		throws RemoteException, CreateException {
	
		log.debug("User has choosen: " + req.getParameter("io"));
		
		// get protocol, format
		String protocol_string = req.getParameter("io");
	
		ProtocolId protocolId = new ProtocolId(protocol_string);
		MetaData metaData =
			new MetaData(
				req.getParameter("title_string"),
				req.getParameter("outline_string"));
	
		ConnectionAddress conn = null;
	
		try {
			conn =
				controller.prepareInsert(
					protocolId,
					metaData);
	
			if (conn != null){
				String serverSocketHost = conn.getUrl().getHost();
				int serverSocketPort = conn.getUrl().getPort();
				
				//use a helper servlet, that gets the data out of the http-stream
				String directInputServletPath = "DirectInput";
		
				out.println(
						"<b>Title </b>" + req.getParameter("title_string") + " <br>");
				out.println(
						"<b>Outline </b>" + req.getParameter("outline_string") + " <br>");
				
				//send the post-http-request with the file to the helper servlet
				out.println(
					"<form action=\"" +directInputServletPath +"\" enctype=\"multipart/form-data\" method=\"POST\">"
						+ "<input type=\"hidden\" name=\"serverSocketHost\" value=\"" + serverSocketHost + "\">"
						+ "<input type=\"hidden\" name=\"serverSocketPort\" value=\"" + serverSocketPort + "\">"
						+ "<table>"
						+ "<tr><td>File: </td>"
						+ "<td><input type=\"file\" name=\"mediafile\" size=40 maxlength=255></tr>"
						+ "<tr><td></td><td><input type=\"submit\" value=\"Upload\"></td></tr>"
						+ "</table>"
						+ "</form>");
				out.println("<p/>");
			
			} else {
				//controller returned no conn
				out.println("<p><b>No ConnectionAdress has been returned.</b><br>");
			}
		} catch (ControllerException e) {
			out.println("<p><b>The following exception occured:</b><br>");
			out.println(e.getMessage());
			out.println("</p>");
		}
	}

}
