package mett.producer;

/**
 * @author elitau
 *
 */

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;

/**
 * Annotations:
 * This is a Tutorial from http://www.javabeat.net/articles/40-creating-webservice-using-jboss-and-eclipse-europa-2.html 
 */

/**
 * This is a webservice class exposing a method called hello which takes a
 * input parameter and greets the parameter with hello.
 *
 * @author dhanago and ede
 * http://www.javabeat.net/articles/40-creating-webservice-using-jboss-and-eclipse-europa-1.html
 */

/*
 * @WebService indicates that this is webservice interface and the name
 * indicates the webservice name.
 */

@WebService(name = "MonsterWebService")

/*
 * @SOAPBinding indicates binding information of soap messages. Here we have
 * document-literal style of webservice and the parameter style is wrapped.
 */
@SOAPBinding
   (
         style = SOAPBinding.Style.DOCUMENT,
         use = SOAPBinding.Use.LITERAL,
         parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
    )


public class MonsterWebService {
	
	private MonsterAdapter monsterAdapter;
	/**
	 * This method accepts a string and prepends it with "Hello ".
	 * @param name
	 * @return String
	 * 
	 */
	@WebMethod
	public String getMedia( @WebParam(name = "key") String key ){
		monsterAdapter = getMonsterAdapter();
		return "Hello " + key + ", my bam ID: " + monsterAdapter.hashCode();
	}
	
	private MonsterAdapter getMonsterAdapter() {
		return new MonsterAdapter();
	}
	
}
