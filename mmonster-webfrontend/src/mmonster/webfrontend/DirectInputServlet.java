/*
 * Created on 23.04.2004
 * 
*/
package mmonster.webfrontend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * HelperServlet for Input data to the MultiMonster Server.
 * Parses the HTTP-Request, gets out the uploaded file and sends the file
 * directly to the MediaProxy by useing a socket transfer.
 *  
 * 
 * @web.servlet name = "DirectInputServlet" display-name = "DirectInput
 * Servlet" description = "Servlet that calls MMonster-Controller-Methods"
 * 
 * @web.servlet-mapping url-pattern = "/DirectInput"
 * 
 * @web.ejb-ref name = "multimonster/mediaproxy/MediaProxy" type = "Session"
 * home = "multimonster.mediaproxy.interfaces.MediaProxyImplHome" remote =
 * "multimonster.mediaproxy.interfaces.MediaProxyImpl" description = ""
 * 
 * @jboss.ejb-ref-jndi ref-name = "multimonster/mediaproxy/MediaProxy"
 * jndi-name = "ejb/MediaProxyFacade"
 */
public class DirectInputServlet extends HttpServlet {

	private Logger log = Logger.getLogger(this.getClass());
	
	/** default block size to transfer data to server*/
	private static int transferBlockSize = 512;

	/**
	 *  
	 */
	public DirectInputServlet() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {

		Context context;

		log.debug("DirectInput-Servlet init...");

		try {
			context = new InitialContext();


		} catch (Exception e) {
			log.error("Error init(): " + e.getMessage());
		}
	}

	/**
	 * Parses HTTP-Request, gets out uploaded data and transmits it to the server
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException {

		String serverSocketHost = null;
		String serverSocketPort_String = null;
		int serverSocketPort = 0;
		Socket socketToServer;
		DataOutputStream dataStreamToServer = null;
		DataInputStream dataStreamFromServer = null;
		String serverAntwort = "";
		int uploadedFileSize = 0;
		
		PrintWriter out = null;
		
		
		response.setContentType("text/html");
		out = response.getWriter();

		out.println("<html><head>");
		out.println(
		"<link rel=\"stylesheet\" media=\"all\" href=\"style.css\">");
		out.println("</head>");

		out.println("<body>");
		out.println("<center>");
		
		

		try {

			/* ---------------GET DATA OUT OF HTTP STREAM --------------------- */
			MultipartRequest parser =
				new ServletMultipartRequest(req, 1024 * 1024 * 1024);

			if (null == parser) {
				log.error("parser is null");
				out.println("<p>server-problem, retry upload</p>");
				return;
			}

			serverSocketHost = parser.getURLParameter("serverSocketHost");
			serverSocketPort_String = parser.getURLParameter("serverSocketPort");
			try {
				serverSocketPort = (new Integer(serverSocketPort_String)).intValue();
			} catch (Exception e) {
				log.error("Couldn't parse port.");
			}		
			
			if ((serverSocketHost != null) && (serverSocketPort != 0)) {
				log.debug("read serverSocketEndPoint: " + serverSocketHost +":" +serverSocketPort);
			} else {
				log.debug("serverSocket couldn't be read completly.");
				out.println("<p>no input-link found</p>");
				return;
			}

			// get file out of HTTP-Request
			if (null == parser.getFileContents("mediafile")) {
				log.debug("unable to parse file");
			}
			InputStream fileIn = parser.getFileContents("mediafile");
			
			if (fileIn == null){
				log.error("user sent no file");
				out.println("<p>no file selected</p>");
				return;
			}
			
			uploadedFileSize = fileIn.available();
			log.debug("read file with size:" + uploadedFileSize);
		

			/* ---------------TRANSFER TO SERVER --------------------- */
			
			//open socket to server and send file			
			try {
				//open socket
				socketToServer = new Socket(serverSocketHost, serverSocketPort);
				log.error("Socket created!");
				dataStreamToServer = new DataOutputStream(socketToServer.getOutputStream());
				dataStreamFromServer = new DataInputStream(socketToServer.getInputStream());
				
				
				//transfer data
				int nextBlockSize = Math.min(512, fileIn.available());
				
				while (nextBlockSize > 0){
					byte[] data = new byte[nextBlockSize];
					
					fileIn.read(data, 0, nextBlockSize);
					
					dataStreamToServer.write(data);
					
					nextBlockSize = Math.min(512, fileIn.available());
					
				}				
				
				//close
				fileIn.close();
				dataStreamFromServer.close();
				dataStreamToServer.close();
				
			}
			catch (IOException e) {
				log.error(e);
			    e.printStackTrace();
			    out.println("<p>An error occured:" +e.getMessage() +"</p>");
			    return;
			    
			}

			out.println("<p>input finished.</p>");
			
		} catch (Exception e) {
			log.error("input-problem: ",e);
			
		} finally{
			out.println("</body>");
			out.println("</html>");			
		}

	}

}
