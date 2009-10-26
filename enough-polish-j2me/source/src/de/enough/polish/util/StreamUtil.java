package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Provides some useful Stream methods.</p>
 *
 * <p>Copyright Enough Software 2008</p>

 * <pre>
 * history
 *        10-Jul-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */

public class StreamUtil {
	/**
	 * the default length for the temporary buffer is 8 kb
	 */
	public static final int DEFAULT_BUFFER = 8 * 1024;
	
	/**
	 * Returns the lines of a InputStream as an ArrayList of Strings
	 * @param in the stream to read
	 * @param encoding the encoding to use
	 * @param bufferLength the length of the used temporary buffer
	 * @return the lines as an ArrayList
	 * @throws IOException when reading fails
	 */
	public static String[] getLines(InputStream in, String encoding, int bufferLength) 
	throws IOException
	{
		String allLines = getString(in,encoding,bufferLength);
		
		String[] lines = TextUtil.split(allLines, '\n');
		
		return lines;
	}
	
	/**
	 * Returns the content of a stream as a String
	 * @param in the stream to read
	 * @param encoding the encoding to use
	 * @param bufferLength the length of the used temporary buffer
	 * @return the resulting String
	 * @throws IOException when reading fails
	 */	
	public static String getString(InputStream in, String encoding, int bufferLength) 
	throws IOException
	{
		
		byte[] buffer = readFully( in, bufferLength );
		if (encoding != null) {
			return new String( buffer, 0, buffer.length, encoding );
		} else {
			return new String( buffer, 0, buffer.length );				
		}
	}

	/**
	 * Reads the complete input stream into a byte array using a 8kb temporary buffer
	 * @param in the input stream
	 * @return the read data
	 * @throws IOException when reading fails
	 */
	public static byte[] readFully(InputStream in)
	throws IOException 
	{
		return readFully( in, DEFAULT_BUFFER );
	}

	/**
	 * Reads the complete input stream into a byte array using a 8kb temporary buffer
	 * @param in the input stream
	 * @param bufferLength the length of the used temporary buffer
	 * @return the read data
	 * @throws IOException when reading fails
	 */
	public static byte[] readFully(InputStream in, int bufferLength)
	throws IOException 
	{
		byte[] buffer = new byte[ bufferLength ];
		int read;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = in.read(buffer, 0, bufferLength))  != -1)
		{
			out.write( buffer, 0, read );
		}
		return out.toByteArray();
	}
}
