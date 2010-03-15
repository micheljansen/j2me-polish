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
 * <pre>
 * history
 *        29-Dec-2009 - David refactored JDE detection 
 *                    - Better linux support, Wine can be specified  by wine.cmd
 *        30-Dec-2009 - David Blackberry Emultors work completely in wine now.
 * </pre>
 * @author Robert Virkus
 * @author David Rubin
 * TODO: filter out wine noise fixme: and error: by config
 */
public class BlackBerryEmulator extends Emulator {

    public static final String WINDOWS_BB_HOME = "C:\\Program Files\\Research In Motion";
    private File blackberryHome;
    private File executionDir;
    private String[] arguments;

    public boolean init(Device dev, EmulatorSetting setting,
            Environment env) {

        File executable = getEmulator(dev, env);
        if (executable!=null && !executable.exists()){
            return false;
        }
        this.executionDir = executable.getParentFile();

        ArrayList argumentsList = new ArrayList();
        if (File.separatorChar == '/') { 
            // this is a unix environment, try wine:
            //wine can NOT execute .bat files so we need to extract the relavent info.
            System.out.println("Extracting params from ["+executable+"] for use in wine.");
            try{
                String [] data =  FileUtil.readTextFile( executable);
                String [] args=new String[0];
                //Only include data from the running application line.
                for (int i =0;i<data.length;i++){
                     if (data[i].trim().startsWith("fledge.exe")){
                        args = data[i].split(" ");
                     }
                }
                if (args.length>0){
                    //
                    String wineBinary=env.getVariable("wine.cmd");
                    if (wineBinary != null && wineBinary.length()>0){
                        argumentsList.add(wineBinary);
                    }else {
                        argumentsList.add("wine");
                    }
                    //Should include the fledge.exe
                    for(int i=0;i<args.length;i++){
                        argumentsList.add(args[i]);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
                //Blackberry don't by default support linux.
                System.err.println("Failed to extract Blackberry params");
                return false;
            }
        } else {
            argumentsList.add(executable.getAbsolutePath());
        }
        this.arguments = (String[]) argumentsList.toArray(new String[argumentsList.size()]);

        // now copy the jar, cod, alx and jad files to the simulator's home directory:
        File targetDir =this.executionDir;
        File file = new File(env.getVariable("polish.jadPath"));
        try {
            //FileUtil.copy( file, targetDir );
            //file = new File( env.getVariable("polish.jarPath") );
            //FileUtil.copy( file, targetDir );
            String baseName = file.getAbsolutePath();
            baseName = baseName.substring(0, baseName.length() - ".jar".length());
            file = new File(baseName + ".cod");
            FileUtil.copy(file, targetDir);
            file = new File(baseName + ".alx");
            FileUtil.copy(file, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to copy BlackBerry resources to the simulator directory: " + e.toString());
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
        File executable = new File(home, dev.getName() + ".bat");
        if (!executable.exists()) {
            String alternativeName = env.getVariable("polish.Emulator.Skin");
            if (alternativeName != null) {
                executable = new File(home, alternativeName + ".bat");
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

    public File getEmulator(Device dev, Environment env) {
        //Try user locations.
        String blackberryHomeStr = env.getVariable("blackberry.emulator.home");
        if (blackberryHomeStr == null) {
            blackberryHomeStr = env.getVariable("blackberry.home");
        }

        //Try guess it windows installs it in a standard place.
        if (blackberryHomeStr == null) {
            File file = new File(WINDOWS_BB_HOME);
            if (file.exists()) {
                blackberryHomeStr = WINDOWS_BB_HOME;
            } else {
                System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" is not set.");
                return null;
            }
        }
        this.blackberryHome = new File(blackberryHomeStr);
        if (!this.blackberryHome.exists()) {
            System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" points to an invalid directory: " + this.blackberryHome.getAbsolutePath());
            return null;
        }
//        Look for simulator in default location
        File home = new File(this.blackberryHome, "simulator");
        File executable = getExecutable(home, dev, env);

        if (!executable.exists()) {
            // search for "*JDE*" folders:
            File parent;
            if (this.blackberryHome.getName().indexOf("JDE") == -1) {
                parent = this.blackberryHome;
            } else {
                parent = this.blackberryHome.getParentFile();
            }
            //Look for jde's in main blackberry.home
            File[] jdes = parent.listFiles(new DirectoryFilter("JDE"));
            //Look in most highest versioned blackberry folders first
            Arrays.sort(jdes);
            for (int i = jdes.length - 1; i >= 0; i--) {
                File jdeFolder = jdes[i];
                //It was BlackBerry Device Simulators, but searching for simulators will also find this value.
                //My install of 4.2 4.7 the folder is called simulators so try match it all.
                File[] simulators = jdeFolder.listFiles(new DirectoryFilter("simulator"));
                for (int j = simulators.length - 1; j >= 0; j--) {
                    home = simulators[j];
                    executable = getExecutable(home, dev, env);
                    if (executable.exists()) {
                        break;
                    }
                }
                if (executable.exists()) {
                    break;
                }
            }
            if (!executable.exists()) {
                System.err.println("Unable to start blackberry simulator: simulator not found: " + executable.getAbsolutePath());
                return executable;
            }
        }
        return executable;
    }

    class DirectoryFilter implements FileFilter {

        private final String requiredName;

        /**
         * @param requiredName the name of the dir
         */
        public DirectoryFilter(String requiredName) {
            this.requiredName = requiredName;
        }

        public boolean accept(File file) {
            //Compare case insensivity
            return file.isDirectory()
                    && file.getName().toLowerCase().indexOf(this.requiredName.toLowerCase())>=0;
        }
    }
}
