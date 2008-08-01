package multimonster.common.util;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.TransactionRolledbackException;

import multimonster.controller.interfaces.ControllerImpl;
import multimonster.controller.interfaces.ControllerImplHome;
import multimonster.converter.interfaces.ConverterImpl;
import multimonster.converter.interfaces.ConverterImplHome;
import multimonster.edit.interfaces.EditImpl;
import multimonster.edit.interfaces.EditImplHome;
import multimonster.mediaaccess.interfaces.MediaAccessImpl;
import multimonster.mediaaccess.interfaces.MediaAccessImplHome;
import multimonster.mediaproxy.interfaces.MediaProxyImpl;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;
import multimonster.resourcemanager.interfaces.ResourceManagerImpl;
import multimonster.resourcemanager.interfaces.ResourceManagerImplHome;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;
import multimonster.transporter.interfaces.TransporterImpl;
import multimonster.transporter.interfaces.TransporterImplHome;
import multimonster.usermanager.interfaces.UserManagerImpl;
import multimonster.usermanager.interfaces.UserManagerImplHome;

/**
 * @author Holger Velke (sihovelk)
 */
public class EjbCreator {
	
	public static ConverterImpl createConverter(
		ConverterImplHome home,
		Context context)
		throws RemoteException, CreateException, NamingException {

		ConverterImpl converter = null;

		try {
			converter = home.create();
		} catch (CreateException e) {			
			home = EjbHomeGetter.getConverterHome(context);
			converter = home.create();
		}
		return converter;
	}
	
	public static SystemAdministrationImpl createSystemAdministration(
			SystemAdministrationImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getSystemAdministrationHome(context);
			systemAdministration = home.create();
		}catch (TransactionRolledbackException e){
			
			home = EjbHomeGetter.getSystemAdministrationHome(context);
			systemAdministration = home.create();
		}
		
		return systemAdministration;
	}
	
	public static ResourceManagerImpl createResourceManager(
			ResourceManagerImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		ResourceManagerImpl resourceManager = null;

		try {
			resourceManager = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getResourceManagerHome(context);
			resourceManager = home.create();
		}
		return resourceManager;
	}
	
	public static MediaAccessImpl createMediaAccess(
			MediaAccessImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		MediaAccessImpl mediaAccess = null;

		try {
			mediaAccess = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getMediaAccessHome(context);
			mediaAccess = home.create();
		}
		return mediaAccess;
	}
	
	public static UserManagerImpl createUserManager(
			UserManagerImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		UserManagerImpl userManager = null;

		try {
			userManager = home.create();
		} catch (Exception e) {
			home = EjbHomeGetter.getUserManagerHome(context);
			userManager = home.create();
		}
		return userManager;
	}
	
	public static TransporterImpl createTransporter(
			TransporterImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		TransporterImpl transporter = null;

		try {
			transporter = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getTransporterHome(context);
			transporter = home.create();
		}
		return transporter;
	}
	
	public static EditImpl createEdit(
			EditImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		EditImpl Edit = null;

		try {
			Edit = home.create();
		} catch (CreateException e) {
			
			home = EjbHomeGetter.getEditHome(context);
			Edit = home.create();
		}
		return Edit;
	}

	public static MediaProxyImpl createMediaProxy(
			MediaProxyImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		MediaProxyImpl mediaProxy = null;

		try {
			mediaProxy = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getMediaProxyHome(context);
			mediaProxy = home.create();
		}
		return mediaProxy;
	}
	

	public static ControllerImpl createController(
			ControllerImplHome home,
			Context context)
	throws RemoteException, CreateException, NamingException {

		ControllerImpl controller = null;

		try {
			controller = home.create();
		} catch (CreateException e) {
			home = EjbHomeGetter.getControllerHome(context);
			controller = home.create();
		}
		return controller;
	}
	
}
