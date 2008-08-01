/*
 * Created on 28.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package multimonster.converter.plugin;

import java.lang.reflect.Field;

import multimonster.common.Codec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sihovelk
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestTranscodeMagic extends TestCase{

	/**
	 * 
	 */
	public TestTranscodeMagic() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TestTranscodeMagic(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void getCodecTest(){
		
		Field[] fields = null;
		
		fields = TranscodeMagic.class.getFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().startsWith("TC_CODEC_")){
					Codec codec = TranscodeMagic.getCodec(fields[i].getLong(TranscodeMagic.class));
					System.out.println(codec);
				}
			}
		} catch (Exception e) {
		}
		
	}
	
	public static Test suite(){
		
		TestSuite suite = new TestSuite("TranscodeMagic Test");
		
		suite.addTest(new TestTranscodeMagic("getCodecTest"));
		
		return suite;
	}
}
