package aca.com.hris.Util;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class UtilException {

	private String arrayIndexOutOfBoundException  = "No data found";
	private String ioException  = "Unable connect to server";
	private String socketTimeoutException  = "Connection timeout";
	private String xmlParserException  = "Failed to load data";
	private String exception  = "An unknown error occured";
	private String nullPointerException  = "Failed to load data";
	public UtilException() {
	}
	
	public String getException (Exception ex) {
		String errorMessage = "";
		
		if (ex instanceof ArrayIndexOutOfBoundsException)
			errorMessage = arrayIndexOutOfBoundException;
		
		else if (ex instanceof NullPointerException)
			errorMessage = nullPointerException;		

		else if (ex instanceof SocketTimeoutException)
			errorMessage = socketTimeoutException;
				
		else if (ex instanceof IOException)
			errorMessage = ioException;	
		
		else if (ex instanceof XmlPullParserException)
			errorMessage = xmlParserException;
		
		else if (ex instanceof Exception)
			errorMessage = exception;

		return errorMessage;
	}


}
