package de.enough.polish.blackberry;

import java.io.File;
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.blackberry.JDPTask;
import de.enough.polish.finalize.Finalizer;

/**
 * <p>Creates a Blackberry JDE project file from the preprocessed resources and sources for debugging etc.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        21-Mar-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class JDPFinalizer extends Finalizer{

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env) {
		JDPTask task = new JDPTask();
		
		String name = env.getVariable("MIDlet-Name");
		
		task.setName(name);
		task.setPath(device.getBaseDir());
		task.setSources(device.getBaseDir());
		
		task.execute();
	}

}
