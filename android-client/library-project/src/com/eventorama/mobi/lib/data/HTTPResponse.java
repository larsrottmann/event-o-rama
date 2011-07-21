package com.eventorama.mobi.lib.data;

import org.apache.http.Header;

public class HTTPResponse {

	private final String body;
	private final int respCode;
	private final Header[] headers;
	private final byte[] binBody;

	public HTTPResponse(int respCode, String body, Header[] headers) {
		this.respCode = respCode;
		this.body = body;
		this.headers = headers;
		this.binBody = null;
	}
	
	public HTTPResponse(int respCode, byte[] body, Header[] headers) {
		this.respCode = respCode;
		this.binBody = body;
		this.headers = headers;
		this.body = null;
	}

	public byte[] getBinaryBody() {
		return binBody;
	}
	public String getBody() {
		return body;
	}
	public int getRespCode() {
		return respCode;
	}		
	public Header[] getHeader()
	{ 
		return headers;
	}
	public Header getHeader(String name)
	{
		for (int i = 0; i < headers.length; i++) {
			if(headers[i].getName().equalsIgnoreCase(name))
				return headers[i];
		}
		return null;
	}
}