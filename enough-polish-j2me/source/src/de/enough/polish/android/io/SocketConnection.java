//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketConnection extends StreamConnection {

	public static final byte DELAY = 0;
	
	public static final byte KEEPALIVE = 2;
	
	public static final byte LINGER = 1;
	
	public static final byte RCVBUF = 3;
	
	public static final byte SNDBUF = 4;
	       
	public void close() throws IOException;
	
	public InputStream openInputStream() throws IOException;
	
	public OutputStream openOutputStream() throws IOException;
	
	public DataInputStream openDataInputStream() throws IOException;

	public DataOutputStream openDataOutputStream() throws IOException;
	
	public String getAddress();
	
	public String getLocalAddress();
	
	public int getLocalPort();
	
	public int getPort();
	
	public void setSocketOption(byte option, int value) throws IOException;
		
	public int getSocketOption(byte option) throws IOException;
}
