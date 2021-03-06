//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Jan 21 22:12:19 CET 2009

package de.enough.polish.android.io.file;

/**
 * This class is used for receiving status notification when adding or removing a
 * file system root. This can be achieved by inserting or removing a card from a
 * device or by mounting or unmounting file systems to a device.
 * <DD>
 * FileConnection 1.0 <DT><B>See Also:</B><DD><A HREF="../../../../de/enough/polish/android/io/file/FileConnection.html"><CODE>FileConnection</CODE></A></DD></DL>
 */
public interface FileSystemListener
{
	/**
	 * 
	 * Constant indicating that a file system root has been added to the device.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int ROOT_ADDED = 0;

	/**
	 * 
	 * Constant indicating that a file system root has been removed from the device.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int ROOT_REMOVED = 1;

	/**
	 * 
	 * This method is invoked when a root on the device has changed state.
	 * <P></P>
	 * 
	 * 
	 * @param state - int representing the state change that has happened to the root.
	 * @param rootName - the String name of the root, following the root naming conventions detailed in FileConnection.
	 * @throws java.lang.IllegalArgumentException - if state has a negative value or is not one of the legal acceptable constants.
	 * @throws java.lang.NullPointerException - if rootName is null.
	 * @see FileConnection
	 */
	public void rootChanged(int state, java.lang.String rootName);


}
