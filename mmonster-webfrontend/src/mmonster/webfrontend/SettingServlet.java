/*
 * Created on 2004-10-18
 */
package mmonster.webfrontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import multimonster.common.setting.Setting;
import multimonster.common.setting.SettingID;
import multimonster.common.setting.SettingValue;
import multimonster.systemadministration.exceptions.SettingNotExistsException;
import multimonster.systemadministration.exceptions.SettingOutOfDomainException;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;

/**
 * 
 * For Viewing and Modifying Settings of the MultiMonster.
 * Displays all available Settings in a table and allows to change single
 * setting-values.
 * 
 * @author sijomeie
 * 
 * @web.servlet				name = "SettingServlet"
 * 							display-name = "Setting  Servlet"
 * 							description = "Servlet for Viewing and Modifying Settings"
 * 
 * @web.servlet-mapping 	url-pattern="/setting-admin"
 * 
 * @web.ejb-ref 			name = "multimonster/systemadministration/SystemAdministrationFacade"
 * 							type = "Session"
 * 							home = "multimonster.systemadministration.interfaces.SystemAdministrationImplHome"
 * 							remote = "multimonster.systemadministration.interfaces.SystemAdministrationImpl"
 * 							description = "Reference to the Sysadmin Facade Bean"
 * 
 * @jboss.ejb-ref-jndi 		ref-name = "multimonster/systemadministration/SystemAdministrationFacade"
 * 							jndi-name = "multimonster/systemadministration/SystemAdministrationFacade"
 */
public class SettingServlet extends HttpServlet {

	private SystemAdministrationImplHome sysHome;
	private Logger log;	
	String title = "";

	public void init() throws ServletException {
		log = Logger.getLogger(this.getClass());
		try {
			Context context = new InitialContext();
			
			Object ref = context.lookup(SystemAdministrationImplHome.JNDI_NAME);
			sysHome =
				(SystemAdministrationImplHome) PortableRemoteObject.narrow(
					ref,
					SystemAdministrationImplHome.class);
		} catch (Exception e) {
			throw new ServletException("Error in init(): " +e.getMessage());
		}
		title = "MultiMonster - Setting-Admin";
		super.init();
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		handleRequest(request, response);

	}

	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		
		handleRequest(request, response);
	}
	

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		SystemAdministrationImpl sysadminFacade = null;
		String answerToClient = "";
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println( printHTMLHeader() );
		out.println("<body>");
		out.println("<center>");
		out.println("<h2>Setting Administration</h2>");

		
		try {
			sysadminFacade = sysHome.create();
			String method = request.getParameter("method");

			if (method != null) {			

				if (method.equals("setvalue")) {
					
					answerToClient = changeSetting(	request.getParameter("sId"),
													request.getParameter("sValue"),
													sysadminFacade
													);
					out.println(answerToClient);
					
					
				} else {
					log.warn("Unbekannter Request");
				}
			}
			
			answerToClient = writeForm();
			out.println(answerToClient);
			
			answerToClient = writeSettingTable(sysadminFacade);
			out.println(answerToClient);
			
		} catch (Exception e) {
			log.error("Fehler: " +e.getMessage());
			out.println("Es ist ein Fehler aufgetreten:</br>");
			out.println(e);
			
		} finally {
			if (sysadminFacade != null)
				try {
						sysadminFacade.remove();
					}
				  catch (RemoteException e1) {
				} catch (RemoveException e1) {
				}
			out.println("</center></body></html>");
			out.close();
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
	 * @return
	 */
	private String writeSettingTable(SystemAdministrationImpl sysadminFacade) {
		String ret = "";
		String tableRow = "";
		Setting[] settings = null;
		Setting s = null;
		
		try {
			settings = sysadminFacade.getAllSettings();
			if (settings != null || settings.length > 0){
				
				ret += "<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">";			
				ret += "<tr>" +
						"<td><b>ID</b></td>" +
						"<td><b>Name</b></td>" +
						"<td><b>Value</b></td>" +
						"<td><b>Description</b></td>" +
						"<td><b>Domain</b></td>" +
						"</tr>";
			
				for(int i=0; i<settings.length; i++){
					s = settings[i];
					
					tableRow = "<tr>";
					tableRow += "<td>" +s.getId().getId() +"</td>";
					tableRow += "<td>" +s.getName() +"</td>";
					tableRow += "<td>" +s.getValue().getValueCont() +"</td>";
					tableRow += "<td>" +s.getDescription() +"</td>";
					tableRow += "<td>" +s.getDomain().getLowerLimit() +" - " +s.getDomain().getUpperLimit() +"</td>";
					tableRow += "<tr>";
					
					ret += tableRow;					
				}	
				
				ret += "</table>";
			}
			
			
		} catch (RemoteException e) {
			log.error("Couldn't get Settings from sysadmin: " +e.getMessage());
		}
		
		return ret;
	}


	/**
	 * @return
	 */
	private String writeForm() {
		String ret = "";
		
		ret += "<form action=\"setting-admin\" method=\"POST\" >";
		ret += "<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">";

		ret += "<tr><td>SettingID</td><td colspan=\"2\">";
		ret += "<input type=\"text\" name=\"sId\" value=\"\">";
		ret += "</td></tr>";

		ret += "<tr><td>SettingValue</td><td colspan=\"2\">" +
				"<input type=\"text\" name=\"sValue\" value=\"\">";
		ret += "</td></tr>";

		ret += "<tr><td></td><td><input type=\"submit\" name=\"method\" value=\"setvalue\">";
		ret += "</td></tr>";
		
		ret += "</table></form>";
		  
		return ret;
	}


	/**
	 * @param parameter
	 * @param parameter2
	 * @param sysadminFacade
	 * @return
	 */
	private String changeSetting(String id, String value, SystemAdministrationImpl sysadminFacade) {
		
		String ret = "";
		String s_sId = null;
		int sId = 0;
		String s_sValue = null;
		int sValue = 0;	
		
		try {
			if ((s_sId = id) != null){
				sId = Integer.parseInt( s_sId );
			}
			if ((s_sValue = value) != null){
				sValue = Integer.parseInt( s_sValue );			
			}

			SettingID settingId = new SettingID(sId);
			SettingValue settingValue = new SettingValue(sValue);
			
			try {
				sysadminFacade.setSettingValue(settingId, settingValue);
				
				ret = "<p>Changed setting " +sId +" to value " +sValue +"</p>";
				
			} catch (SettingNotExistsException e1) {
				log.error(e1.getMessage());
			} catch (SettingOutOfDomainException e1) {
				log.error(e1.getMessage());
			} catch (RemoteException e1) {
				log.error(e1.getMessage());
			}					

		
		} catch (NumberFormatException e) {
			log.warn("Couldn't parse parameters to int (id: " +s_sId +", value: " +s_sValue +"): " +e.getMessage());
		}	
		
		
		return ret;
	}


}
