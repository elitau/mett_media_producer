package mmonster.webfrontend;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
	Just a Wrapper for MultipartRequest
	@author Jason Pell
	
	@link http://www.geocities.com/jasonpell
*/
public class ServletMultipartRequest extends MultipartRequest
{
	/** 
	 * Constructor.
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param strSaveDirectory		The temporary directory to save the file from where they can then be moved to wherever by the
	 * 								calling process.  <b>If you specify <u>null</u> for this parameter, then any files uploaded
	 *								will be silently ignored.</B>
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, String strSaveDirectory) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			strSaveDirectory,
			MultipartRequest.MAX_READ_BYTES);
	}

	/** 
	 * Constructor.
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param strSaveDirectory		The temporary directory to save the file from where they can then be moved to wherever by the
	 * 								calling process.  <b>If you specify <u>null</u> for this parameter, then any files uploaded
	 *								will be silently ignored.</B>
	 * @param intMaxReadBytes		Overrides the MAX_BYTES_READ value, to allow arbitrarily long files.
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, String strSaveDirectory, int intMaxReadBytes) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			strSaveDirectory,
			intMaxReadBytes);
	}

	/** 
	 * Constructor - load into memory constructor
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param intMaxReadBytes		Overrides the MAX_BYTES_READ value, to allow arbitrarily long files.
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, int intMaxReadBytes) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			intMaxReadBytes);
	}
}