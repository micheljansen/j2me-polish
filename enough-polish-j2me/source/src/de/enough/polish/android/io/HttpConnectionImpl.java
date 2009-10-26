//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class HttpConnectionImpl implements HttpConnection {
	
	private static final int STATE_SETUP = 0;
	
	private static final int STATE_CONNECTED = 1;
	
	private int state = STATE_SETUP;
		
	private String urlString = null;
	private String requestMethod = GET;
		
	private URL url;
	private HttpURLConnection theConnection = null;
	private InputStream theInput = null;
	private OutputStream theOutput = null;

		
	protected HttpConnectionImpl(String url) {
		if(url == null) {
			throw new NullPointerException();
		}
		checkIsHttpUrl(url);
		this.urlString = url;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid URL");
		} 
	}
	
	HttpConnectionImpl(String url, int mode) {
		this(url);
	}

	
	private void checkIsHttpUrl(String url2) {
		if (url2.indexOf(Connector.HTTP_PREFIX)!=0 || 
				!(url2.length()>Connector.HTTP_PREFIX.length()) ) {
			throw new IllegalArgumentException("invalid URL");
		}
	}
	
	public void close() throws IOException {
		if(this.theInput != null) {
			this.theInput.close();
		}
		if(this.theOutput != null) {
			this.theOutput.close();
		}
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		if(this.theOutput != null) {
			throw new IOException("already opened");
		}
		connect();
		this.theConnection.setDoOutput(true);
		this.theOutput = this.theConnection.getOutputStream();
		return this.theOutput;
	}
	
	
	//only in setup state
	public void setRequestMethod(String requestMethod) throws IOException {
		if (this.state == STATE_SETUP) {
			if (requestMethod.equals(GET)) {
				this.requestMethod = requestMethod;
			} else if (requestMethod.equals(POST)) {
				this.requestMethod = requestMethod;
			} else {
				throw new IllegalArgumentException("illegal request method");
			}
		} else {
			throw new IOException("already connected");
		}
	}
	
	public void setRequestProperty(String key, String value) throws IOException {
		connect();
		this.theConnection.setRequestProperty(key, value);
	}
	
	//invoke at any time
	public String getRequestMethod() {
		return this.requestMethod;
	}
	
	public String getRequestProperty(String key) {
		try {
			connect();
		} catch (IOException e) {
			return "";
		}
		return this.theConnection.getRequestProperty(key);
	}
	
	public String getURL() {
		return this.urlString;
	}
	
	public String getQuery() {
		return this.url.getQuery();
	}
	
	public int getPort() {
		return this.url.getPort();
	}
	
	public String getHost() {
		return this.url.getHost();
	}
	
	public String getProtocol() {
		return this.url.getProtocol();
	}
		
	public String getFile() {
		return this.url.getFile();
	}
		
	public String getRef() {
		return this.url.getRef();
	}
	
	//these calls force transition to connected state
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public InputStream openInputStream() throws IOException {
		connect();
		this.theInput = this.theConnection.getInputStream();
		return this.theInput;
	}
	
	public long getLength() {
		try {
			connect();
		} catch (IOException ex) {
			return 0;
		}
		return this.theConnection.getHeaderFieldInt("Content-Length", 0);
	}
	
	public String getType() {
		try {
			connect();
		} catch (IOException ex) {
			return "";
		}
		return this.theConnection.getContentType();
	}
	
	public String getEncoding() {
		try {
			connect();
		} catch (IOException ex) {
			return "";
		}
		return this.theConnection.getContentEncoding();
	}
	
	public String getHeaderField(String name) throws IOException {
		connect();
		return this.theConnection.getHeaderField(name);
	}
	
	public String getHeaderField(int n) throws IOException {
		connect();
		return this.theConnection.getHeaderField(n);
	}
	
	public int getResponseCode()  throws IOException {
		connect();
		return this.theConnection.getResponseCode();
	}
	
	public String getResponseMessage() throws IOException {
		connect();
		return this.theConnection.getResponseMessage();
	}
	
	public int getHeaderFieldInt(String name, int def) throws IOException {
		connect();
		return this.theConnection.getHeaderFieldInt(name, def);
	}

	public long getHeaderFieldDate(String name,	long def) throws IOException {
		connect();
		return this.theConnection.getHeaderFieldDate(name, def);
	}

	public String getHeaderFieldKey(int n) throws IOException {
		connect();
		return this.theConnection.getHeaderFieldKey(n);
	}
	
	public long getDate() throws IOException {
		connect();
		return this.theConnection.getDate();
	}
	
	public long getExpiration() throws IOException {
		connect();
		return this.theConnection.getExpiration();
	}
	
	public long getLastModified() throws IOException {
		connect();
		return this.theConnection.getLastModified();
	}
	
	private synchronized void connect() throws IOException {
		if (this.state == STATE_CONNECTED) {
			if(this.theConnection == null) {
				throw new IOException("Invalid State. No connection in state STATE_CONNECTED");
			}
			return;
		} else {
			this.state = STATE_CONNECTED;
		}
		this.theConnection = (HttpURLConnection)this.url.openConnection();
		this.theConnection.setRequestMethod(this.requestMethod);
	}
	
}	
