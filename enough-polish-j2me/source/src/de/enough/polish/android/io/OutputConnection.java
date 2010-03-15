//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface OutputConnection extends Connection {

	public OutputStream openOutputStream() throws IOException;
	
	public DataOutputStream openDataOutputStream() throws IOException;
	
}
