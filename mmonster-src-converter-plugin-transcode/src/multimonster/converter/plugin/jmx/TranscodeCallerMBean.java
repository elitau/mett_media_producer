/*
 * Generated file - Do not edit!
 */
package multimonster.converter.plugin.jmx;

/**
 * MBean interface.
 * @author XDoclet (autogenerated)
 */
public interface TranscodeCallerMBean extends org.jboss.system.ServiceMBean {

  java.lang.String getTRANSCODE_PATH() ;

  void setTRANSCODE_PATH(java.lang.String transcode_path) ;

   /**
    * this can only be used within the same VM.
    */
  multimonster.converter.plugin.jmx.TranscodeCaller returnThis() ;

}