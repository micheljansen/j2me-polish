//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Jan 21 22:12:19 CET 2009

package de.enough.polish.android.io.file;

/**
 * Represents an exception thrown when a method is invoked requiring a particular
 * security mode (e.g. READ or WRITE), but the connection opened is not in the
 * mode required. The application does pass all security checks, but the
 * connection object is in the wrong mode.
 */
public class IllegalModeException extends java.lang.RuntimeException
{
	/**
	 * 
	 * Constructs a new instance of this class with its stack trace filled in.
	 * <P></P>
	 * 
	 */
	public IllegalModeException()
	{
		super();
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace and message filled
	 * in.
	 * <P></P>
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 */
	public IllegalModeException(java.lang.String detailMessage)
	{
		super(detailMessage);
	}

}
