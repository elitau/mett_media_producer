/*
 * Generated by XDoclet - Do not edit!
 */
package multimonster.transporter.interfaces;

/**
 * Home interface for TransporterImpl.
 * @author J�rg Meier
 */
public interface TransporterImplHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/TransporterImpl";
   public static final String JNDI_NAME="multimonster/transporter/TransporterFacade";

   public multimonster.transporter.interfaces.TransporterImpl create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
