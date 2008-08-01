/*
 * Created on 10.02.2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package mmonster.webfrontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MetaData;
import multimonster.common.resource.QueueTime;
import multimonster.edit.exceptions.EditException;
import multimonster.edit.interfaces.EditImpl;
import multimonster.edit.interfaces.EditImplHome;

/**
 * 
 * Does some calls at the EditFacade to test the Edit component.
 *  
 * @author Holger Velke
 * 
 * @web.servlet				name = "EditTestServlet"
 * 							display-name = "Edit Test Servlet"
 * 							description = "Servlet for Testing the Edit Component"
 * 
 * @web.servlet-mapping 	url-pattern="/test/edit"
 * 
 * @web.ejb-ref 			name = "multimonster/edit/EditFacade"
 * 							type = "Session"
 * 							home = "multimonster.edit.interfaces.EditImplHome"
 * 							remote = "multimonster.edit.interfaces.EditImpl"
 * 							description = "Reference to the Edit Facade Bean"
 * 
 * @jboss.ejb-ref-jndi 		ref-name = "multimonster/edit/EditFacade"
 * 							jndi-name = "multimonster/edit/EditFacade"
 */
public class TestEditServlet extends HttpServlet {

	private EditImplHome editHome;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
		throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Edit Test v0.0.1</title></head>");
		out.println("<body>");
		out.println("<h1>TestEditServlet Called</h1>");
		try {
			EditImpl editFacade = editHome.create();
			String method = request.getParameter("method");
			if (method != null) {
				out.println("EditFacade."+method+"()<br/>");
				if (method.equals("getJob")) {
					editFacade.getJob(new UserIdentifier("servlet EditTest user"), new MOIdentifier(42));
				} else if (method.equals("addTaskToJob")) {
					editFacade.addTaskToJob(
						new EditJobIdentifier(),
						new FilterPlugInIdentifier("multimonster.edit.plugin.MMThreadPlugIn"),
						null);
				} else if (method.equals("abortJob")) {
					editFacade.abortJob(new EditJobIdentifier());
				} else if (method.equals("finishJob")) {
					editFacade.finishJob(new EditJobIdentifier(), new MetaData(), new QueueTime());
				}else if (method.equals("getJobList")) {
					editFacade.getJobList(new UserIdentifier("servlet EditTest user"));
				}else if (method.equals("removeTask")) {
					editFacade.removeTask(new EditTaskIdentifier(new EditJobIdentifier()));
				}else if (method.equals("complexTest")){
					doComplexTest(editFacade, out);
				} else {
					out.println("Unbekannter Request");
				}
			} else {
				out.println("Unbekannter Request");
			}
			editFacade.remove();
		} catch (Exception e) {
			out.println("Es ist ein Fehler aufgetreten.</br>");
			out.println(e);
		} finally {
			out.println("</body></html>");
			out.close();
		}

	}

	/**
	 * 
	 */
	private void doComplexTest(EditImpl editFacade, PrintWriter out) throws RemoteException, EditException {
		
		UserIdentifier uId = new UserIdentifier("servlet EditTest user");
		MOIdentifier mOId = new MOIdentifier(42);
		
		out.println("getJob()<br/>");
		EditJobIdentifier jobId = editFacade.getJob(uId, mOId);
		
		out.println("addTaskToJob()<br/>");
		EditTaskIdentifier taskId1 = editFacade.addTaskToJob(
				jobId, 
				new FilterPlugInIdentifier("multimonster.edit.plugin.MMThreadPlugIn"), 
				null);
		out.println("addTaskToJob()<br/>");
		EditTaskIdentifier taskId2 = editFacade.addTaskToJob(
				jobId, 
				new FilterPlugInIdentifier("multimonster.edit.plugin.MMThreadPlugIn"),
				null);
		out.println("addTaskToJob()<br/>");
		EditTaskIdentifier taskId3 = editFacade.addTaskToJob(jobId,
				new FilterPlugInIdentifier("multimonster.edit.plugin.MMThreadPlugIn"), 
				null);
		
		out.println("removeTask()<br/>");
		editFacade.removeTask(taskId2);
		
		out.println("getJob()<br/>");
		EditJobIdentifier jobId1 = editFacade.getJob(uId, mOId);
		out.println("getJob()<br/>");
		EditJobIdentifier jobId2 = editFacade.getJob(uId, mOId);
		out.println("getJob()<br/>");
		EditJobIdentifier jobId3 = editFacade.getJob(uId, mOId);
		out.println("getJobList() - ");
		EditJobIdentifier[] list = editFacade.getJobList(uId);
		if (list.length == 4){
			out.println("JobList.length OK!<br/>");
		}
		out.println("abortJob()<br/>");
		editFacade.abortJob(jobId1);
		out.println("getJobList() - ");
		list = editFacade.getJobList(uId);
		if (list.length == 3){
			out.println("JobList.length OK!<br/>");
		}
		
		MetaData metaData = new MetaData("Title","Outline");
		QueueTime maxQT = new QueueTime();
		
		out.println("finishJob()<br/>");
		editFacade.finishJob(jobId, metaData, maxQT);
		
		out.println("<br/>SUCCESS<br/>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		try {
			Context context = new InitialContext();
			Object ref = context.lookup(EditImplHome.JNDI_NAME);
			editHome =
				(EditImplHome) PortableRemoteObject.narrow(
					ref,
					EditImplHome.class);
		} catch (Exception e) {
			throw new ServletException("Lookup of java:/comp/env/ failed");
		}
		super.init();
	}

}
