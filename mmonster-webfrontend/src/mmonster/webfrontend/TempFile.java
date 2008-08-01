package mmonster.webfrontend;


import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.io.IOException;

/**
	Borrowed pretty much all the the logic from java.io.File (Java 2)
	@author Jason Pell
	
	@link http://www.geocities.com/jasonpell
*/
public class TempFile
{
	/* -- Temporary files -- */
    private static final Object tmpFileLock = new Object();
    private static int counter = -1; /* Protected by tmpFileLock */

	/**
		@param prefix		Must be at least 3 characters long
		@param suffix		The file extension (minus the extension)
		@param directory	Where do you want this temporary file saved.
	*/
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException
    {
		if (prefix == null) throw new NullPointerException();
		if (prefix.length() < 3)
	    	throw new IllegalArgumentException("Prefix string too short");

        String extension = (suffix == null) ? "tmp" : suffix;
		synchronized(tmpFileLock)
		{
		    SecurityManager sm = System.getSecurityManager();
		    File f;
	    	while(true)
			{
				f = generateFile(prefix, extension, directory);
				if(!f.exists())
				{
					try{
						// Create the file.
						FileWriter writer = new FileWriter(f);
						writer.close();
						break;//break out of while loop!
					}catch(Exception e){}
				}
            }
		    return f;
		}
    }

	/**
		This method is used to generate the file name.
	*/
    private static File generateFile(String prefix, String extension, File dir) throws IOException
    {
		if (counter == -1)
		    counter = new Random().nextInt() & 0xffff;
		counter++;

		return new File(dir, prefix + Integer.toString(counter) + "."+extension);
    }
}
