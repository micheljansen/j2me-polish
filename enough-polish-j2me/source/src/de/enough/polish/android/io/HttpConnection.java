//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HttpConnection extends ContentConnection {

	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final String POST = "POST";
	public static final int HTTP_ACCEPTED = 202;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_BAD_METHOD = 405;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_CLIENT_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_CREATED = 201;
	public static final int	HTTP_ENTITY_TOO_LARGE = 413;
	public static final int HTTP_EXPECT_FAILED = 417;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_GONE = 410;
	public static final int HTTP_INTERNAL_ERROR = 500;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_MOVED_TEMP = 302;
	public static final int HTTP_MULT_CHOICE = 300;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_NOT_ACCEPTABLE = 406;
	public static final int HTTP_NOT_AUTHORITATIVE = 203;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int	HTTP_NOT_IMPLEMENTED = 501;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_OK = 200;
	public static final int HTTP_PARTIAL = 206;
	public static final int HTTP_PAYMENT_REQUIRED = 402;
	public static final int HTTP_PRECON_FAILED = 412;
	public static final int HTTP_PROXY_AUTH = 407;
	public static final int HTTP_REQ_TOO_LONG = 414;
	public static final int HTTP_RESET = 205;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_TEMP_REDIRECT = 307;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int	HTTP_UNAVAILABLE = 503;
	public static final int	HTTP_UNSUPPORTED_RANGE = 416;
	public static final int HTTP_UNSUPPORTED_TYPE = 415;
	public static final int HTTP_USE_PROXY = 305;
	public static final int	HTTP_VERSION = 505;

	public void close() throws IOException;

	public DataOutputStream openDataOutputStream() throws IOException;

	public OutputStream openOutputStream() throws IOException;
	
	//only in setup state
	public void setRequestMethod(String method) throws IOException;
	
	public void setRequestProperty(String key, String value) throws IOException;
	
	//invoke at any time
	public String getRequestMethod();
	
	public String getRequestProperty(String key);
	
	public String getURL();
	
	public String getQuery();
	
	public int getPort();
	
	public String getHost();
	
	public String getProtocol();
		
	public String getFile();
		
	public String getRef();
	
	//these calls force transition to connected state
	public DataInputStream openDataInputStream() throws IOException;

	public InputStream openInputStream() throws IOException;
	
	public long getLength();
	
	public String getType();
	
	public String getEncoding();
	
	public String getHeaderField(String name) throws IOException;
	
	public String getHeaderField(int n) throws IOException;
	
	public int getResponseCode()  throws IOException;
	
	public String getResponseMessage() throws IOException;
	
	public int getHeaderFieldInt(String name, int def) throws IOException;
	
	public long getHeaderFieldDate(String name,
			long def) throws IOException;

	public String getHeaderFieldKey(int n) throws IOException;
	
	public long getDate() throws IOException;
	
	public long getExpiration() throws IOException;
	
	public long getLastModified() throws IOException;

}
