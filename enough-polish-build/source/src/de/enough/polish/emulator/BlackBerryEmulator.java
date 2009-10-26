package de.enough.polish.emulator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.FileUtil;

/**
 * Invokes a specific BlackBerry simulator.
 * @author Robert Virkus
 */
public class BlackBerryEmulator extends Emulator {

	private File blackberryHome;
	private File executionDir;
	private String[] arguments;

	public boolean init( Device dev, EmulatorSetting setting,
			Environment env ) 
	{
		String blackberryHomeStr =  env.getVariable("blackberry.emulator.home");
		if (blackberryHomeStr == null){
			blackberryHomeStr = env.getVariable("blackberry.home");
		}
		if (blackberryHomeStr == null) {
			File file = new File( "C:\\Program Files\\Research In Motion");
			if (file.exists()) {
				blackberryHomeStr = "C:\\Program Files\\Research In Motion";
			} else {
				System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" is not set." );
				return false;
			}
		}
		this.blackberryHome = new File( blackberryHomeStr );
		if ( !this.blackberryHome.exists() ) {
			System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" points to an invalid directory: " + this.blackberryHome.getAbsolutePath()  );
			return false;
		}		
		File home = new File( this.blackberryHome, "simulator" );
		File executable = getExecutable(home, dev, env);
		if ( !executable.exists() ) {
			// search for "BlackBerry Device Simulators" folders:
			File parent;
			if (this.blackberryHome.getName().indexOf("JDE") == -1) {
				parent = this.blackberryHome;
			} else {
				parent = this.blackberryHome.getParentFile();
			}
			File[] children = parent.listFiles( new DirectoryFilter("BlackBerry Device Simulators") );
			Arrays.sort( children );
			for (int i = children.length - 1; i >= 0; i--) {
				File deviceSimulatorsFolder = children[i];
				File[] deviceSimlatorsChildren = deviceSimulatorsFolder.listFiles(new DirectoryFilter("Device Simulators"));
				for (int j = deviceSimlatorsChildren.length -1; j >= 0; j--) {
					home = deviceSimlatorsChildren[j];
					executable = getExecutable(home, dev, env);
					if (executable.exists()) {
						break;
					}
				}
				if (executable.exists()) {
					break;
				}
			}
			if ( !executable.exists() ) {
				// check all JDEs as well:
				children = parent.listFiles( new DirectoryFilter("BlackBerry JDE") );
				Arrays.sort( children );
				for (int i = children.length - 1; i >= 0; i--) {
					File deviceSimulatorsFolder = children[i];
					home = new File( deviceSimulatorsFolder, "simulator" );
					executable = getExecutable(home, dev, env);
					if (executable.exists()) {
						break;
					}
				}
			}
			if ( !executable.exists() ) {
				System.err.println("Unable to start blackberry simulator: simulator not found: " + executable.getAbsolutePath()  );
				return false;
			}
		}
		this.executionDir = executable.getParentFile();
		
		ArrayList argumentsList = new ArrayList();
		if (File.separatorChar == '/') { // this is a unix environment, try wine:
			argumentsList.add("wine");
			argumentsList.add( executable.getAbsolutePath() );
			argumentsList.add("--");
		} else {
			argumentsList.add( executable.getAbsolutePath() );
		}
		this.arguments = (String[]) argumentsList.toArray( new String[ argumentsList.size() ] );
		
		// now copy the jar, cod, alx and jad files to the simulator's home directory:
		File targetDir = executable.getParentFile();
		File file = new File( env.getVariable("polish.jadPath") );
		try {
			//FileUtil.copy( file, targetDir );
			//file = new File( env.getVariable("polish.jarPath") );
			//FileUtil.copy( file, targetDir );
			String baseName = file.getAbsolutePath();
			baseName = baseName.substring( 0, baseName.length() - ".jar".length() );
			file = new File( baseName + ".cod" );
			FileUtil.copy( file, targetDir );
			file = new File( baseName + ".alx" );
			FileUtil.copy( file, targetDir );
		} catch ( IOException e ) {
			e.printStackTrace();
			System.err.println("Unable to copy BlackBerry resources to the simulator directory: " + e.toString() );
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the executable
	 * 
	 * @param dev the device
	 * @param env the environment
	 * @return the executable file, which might not exist
	 */
	private File getExecutable(File home, Device dev, Environment env) {
		File executable = new File( home, dev.getName() + ".bat" );
		if ( !executable.exists() ) {
			String alternativeName = env.getVariable("polish.Emulator.Skin");
			if (alternativeName != null) {
				executable = new File(home, alternativeName + ".bat" );
			}
		}
		return executable;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getExecutionDir()
	 */
	protected File getExecutionDir() {
		return this.executionDir;
	}
	
	

	class DirectoryFilter implements FileFilter {
		private final String requiredName;
		/**
		 * @param requiredName the name of the dir
		 */
		public DirectoryFilter( String requiredName ) {
			this.requiredName = requiredName;
		}
		public boolean accept(File file) {
			return file.isDirectory() 
				&& file.getName().startsWith( this.requiredName );
		}
	}
	
	
}
