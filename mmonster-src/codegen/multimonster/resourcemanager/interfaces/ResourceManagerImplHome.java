/*
 * Generated by XDoclet - Do not edit!
 */
package multimonster.resourcemanager.interfaces;

/**
 * Home interface for ResourceManagerImpl.
 * @see multimonster.resourcemanager.ResourceManagerFacade
 * @author Holger Velke (sihovelk)
 */
public interface ResourceManagerImplHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/ResourceManagerImpl";
   public static final String JNDI_NAME="multimonster/edit/ResourceManagerFacade";

   public multimonster.resourcemanager.interfaces.ResourceManagerImpl create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}