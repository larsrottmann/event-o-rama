package com.eventorama;
/**
 * Contains HTTP Status code which gets passed to client 
 * @author renard
 *
 */
public class AppRequestException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1611888691916079260L;
	private final int httpResponseCode;

	public AppRequestException(int code) {
		this.httpResponseCode = code;
	}

	public AppRequestException(int code, Exception parentException) {
		super(parentException);
		this.httpResponseCode = code;
	}
	
	public int getHttpResponseCode(){
		return httpResponseCode;
	}

}
