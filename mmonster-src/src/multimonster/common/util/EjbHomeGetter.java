package multimonster.common.util;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import multimonster.controller.interfaces.ControllerImplHome;
import multimonster.converter.interfaces.ConverterImplHome;
import multimonster.edit.interfaces.EditImplHome;
import multimonster.mediaaccess.interfaces.MediaAccessImplHome;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;
import multimonster.resourcemanager.interfaces.ResourceManagerImplHome;
import multimonster
	.systemadministration
	.interfaces
	.SystemAdministrationImplHome;
import multimonster.transporter.interfaces.TransporterImplHome;
import multimonster.usermanager.interfaces.UserManagerImplHome;

/**
 * @author Jörg Meier
 */
public class EjbHomeGetter {

	public static ControllerImplHome getControllerHome(Context context)
		throws NamingException {

		ControllerImplHome controllerHome = null;

		/* get controller-Home */
		Object ref = context.lookup(ControllerImplHome.JNDI_NAME);
		controllerHome =
			(ControllerImplHome) PortableRemoteObject.narrow(
				ref,
				ControllerImplHome.class);

		return controllerHome;
	}

	public static MediaProxyImplHome getMediaProxyHome(Context context)
		throws NamingException {

		MediaProxyImplHome mediaProxyHome = null;

		/* get MediaProxy-Home */
		Object ref = context.lookup(MediaProxyImplHome.JNDI_NAME);
		mediaProxyHome =
			(MediaProxyImplHome) PortableRemoteObject.narrow(
				ref,
				MediaProxyImplHome.class);

		return mediaProxyHome;
	}

	public static TransporterImplHome getTransporterHome(Context context)
		throws NamingException {

		TransporterImplHome transporterHome = null;

		/* get transporter-Home */
		Object ref = context.lookup(TransporterImplHome.JNDI_NAME);
		transporterHome =
			(TransporterImplHome) PortableRemoteObject.narrow(
				ref,
				TransporterImplHome.class);

		return transporterHome;

	}

	public static SystemAdministrationImplHome getSystemAdministrationHome(Context context)
		throws NamingException {

		SystemAdministrationImplHome sysadminHome = null;

		/* get SystemAdministrationHome */
		Object ref = context.lookup(SystemAdministrationImplHome.JNDI_NAME);
		sysadminHome =
			(SystemAdministrationImplHome) PortableRemoteObject.narrow(
				ref,
				SystemAdministrationImplHome.class);

		return sysadminHome;
	}

	public static UserManagerImplHome getUserManagerHome(Context context)
		throws NamingException {

		UserManagerImplHome usermngHome = null;

		/* get UserManagerHome */
		Object ref = context.lookup(UserManagerImplHome.JNDI_NAME);
		usermngHome =
			(UserManagerImplHome) PortableRemoteObject.narrow(
				ref,
				UserManagerImplHome.class);

		return usermngHome;
	}

	public static ResourceManagerImplHome getResourceManagerHome(Context context)
		throws NamingException {

		ResourceManagerImplHome resMngHome = null;

		/* get RessourceManagerHome */
		Object ref = context.lookup(ResourceManagerImplHome.JNDI_NAME);
		resMngHome =
			(ResourceManagerImplHome) PortableRemoteObject.narrow(
				ref,
				ResourceManagerImplHome.class);

		return resMngHome;

	}

	public static ConverterImplHome getConverterHome(Context context)
		throws NamingException {

		ConverterImplHome converterHome = null;

		/* get ConverterHome */
		Object ref = context.lookup(ConverterImplHome.JNDI_NAME);
		converterHome =
			(ConverterImplHome) PortableRemoteObject.narrow(
				ref,
				ConverterImplHome.class);

		return converterHome;

	}

	public static MediaAccessImplHome getMediaAccessHome(Context context)
		throws NamingException {

		MediaAccessImplHome mediaAccessHome = null;

		/* get MediaDataHome */
		Object ref = context.lookup(MediaAccessImplHome.JNDI_NAME);
		mediaAccessHome =
			(MediaAccessImplHome) PortableRemoteObject.narrow(
				ref,
				MediaAccessImplHome.class);

		return mediaAccessHome;
	}

	public static EditImplHome getEditHome(Context context) throws NamingException {
		
		EditImplHome editHome = null;
		
		/*get EditHome */
		Object ref = context.lookup(EditImplHome.JNDI_NAME);
		editHome =
			(EditImplHome) PortableRemoteObject.narrow(
					ref,
					EditImplHome.class);
		
		return editHome;
	}

}
