/*
 * Created on 18-Aug-2005 at 15:58:25.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.android.precompiler;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.propertyfunctions.VersionFunction;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Creates the R.java and Manifest.java</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ResourcesPreCompiler extends PreCompiler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File,
	 * de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
			throws BuildException 
	{
		Environment env = device.getEnvironment();

		System.out.println("aapt: Copying resources to " + ArgumentHelper.getRaw(env) + "...");		
		try {
			copyResources(device, env);
		} catch (IOException e) {
			throw new BuildException("Unable to copy resources: " + e);
		}

		String aapt = ArgumentHelper.aapt(env);
		if (aapt != null) {
			ArrayList arguments = getDefaultArguments(aapt,env);
			try {
				
				System.out.println("aapt: Generating R.java / AndroidManifest.xml from the resources...");
				
				int result = ProcessUtil.exec( arguments, "aapt: ", true, null, null);
				if (result != 0) {
					throw new BuildException("Unable to execute aapt - got result: " + result);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to execute aapt - got exception: " + e);
			}
		}
		String manifestPath = ArgumentHelper.getActivity(env) + "/AndroidManifest.xml";
		SAXBuilder builder = new SAXBuilder();
		Document document;
		File manifestFile = new File(manifestPath);
		try {
			document = builder.build(manifestFile);
		} catch (JDOMException e) {
			throw new BuildException("Could not parse file '"+manifestPath+"'",e);
		} catch (IOException e) {
			throw new BuildException("Could not parse file '"+manifestPath+"'",e);
		}
		Element rootElement = document.getRootElement();
		Namespace namespace = rootElement.getNamespace("android");

		Element permissionElement;
		permissionElement = new Element("uses-permission");
		permissionElement.setAttribute("name","android.permission.INTERNET",namespace);
		rootElement.addContent(permissionElement);
		//TODO: use actual permissions:
		String[] permissions = new String[] {
				"android.permission.ACCESS_FINE_LOCATION"
				,"android.permission.ACCESS_MOCK_LOCATION"
				,"android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"
				,"android.permission.ACCESS_COARSE_LOCATION"
				,"android.permission.RECEIVE_SMS"
				,"android.permission.SEND_SMS"
				,"android.permission.READ_CONTACTS"
				,"android.permission.WRITE_CONTACTS"
				,"android.permission.READ_PHONE_STATE"
				,"android.permission.WAKE_LOCK"
		};
		
		for (int i = 0; i < permissions.length; i++) {
			permissionElement = new Element("uses-permission");
			String permission = permissions[i];
			permissionElement.setAttribute("name",permission,namespace);
			rootElement.addContent(permissionElement);
		}
		
		Element usesSdkElement = new Element("uses-sdk");
		usesSdkElement.setAttribute("minSdkVersion", "3",namespace);
		rootElement.addContent(usesSdkElement);
		
		String version = env.getVariable("MIDlet-Version");
		if(version == null || version.length() == 0) {
			version = "1";
		}
		int versionCodeNumber = computeVersionCode(version);
		String versionCode = String.valueOf(versionCodeNumber);
		rootElement.setAttribute("versionCode", versionCode,namespace);
		
		String versionName = version;
		rootElement.setAttribute("versionName", versionName,namespace);
		
		String midletName = env.getBuildSetting().getMidlets(env)[0].name;

		Element applicationElement = rootElement.getChild("application");
		applicationElement.setAttribute("label",midletName,namespace);
		
		Element activityElement = applicationElement.getChild("activity");
		activityElement.setAttribute("configChanges","mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|fontScale",namespace);
		activityElement.setAttribute("label",midletName,namespace);
		
		String iconUrl = env.getVariable("MIDlet-Icon");
		if(iconUrl != null) {
			int indexOfPoint = iconUrl.indexOf(".");
			if(indexOfPoint != -1) {
				// Remove the affix as android needs only the name of the resource.
				iconUrl = iconUrl.substring(0,indexOfPoint);
			}
			if(iconUrl.length() != 0) {
				applicationElement.setAttribute("icon","@raw"+iconUrl,namespace);
				activityElement.setAttribute("icon","@raw"+iconUrl,namespace);
			}
		} else {
			System.err.println("No icon was defined in this build. You will not be able to deploy this application in the Android Market. Please define an icon with the attribte 'icon' in the 'info' tag of the build.xml file.");
		}
		
		// TODO: This does not work. Instead we need to alter the file res/values/string.xml, add the description as a string resource
		// and reference this resource as value '@string/mystring' to this property.
//		String description = env.getVariable("MIDlet-Description");
//		if(description == null || description.length() == 0) {
//			description = "";
//		}
//		applicationElement.setAttribute("description",description,namespace);
		
		XMLOutputter xmlOutputter = new XMLOutputter();
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(manifestFile);
		} catch (IOException e) {
			throw new BuildException("Could not create FileWriter for file '"+manifestPath+"'",e);
		}
		try {
			xmlOutputter.output(document,fileWriter);
		} catch (IOException e) {
			throw new BuildException("Could not write to file file '"+manifestPath+"'",e);
		}
		
	}

	/**
	 * This method sums all numerical version components in the version string. It is helpful to compare version strings with a
	 * simple ordering. It will break with versions like 1.0.1-preview1 and 1.0.1 as the former has a higher numerical score but
	 * is 'lower' as a version.
	 * @param version
	 * @return
	 */
	private int computeVersionCode(String version) {
		if(version == null || version.length() == 0) {
			version = "1";
		}
		String versionCodeString = VersionFunction.process(version);
		int versionCode = Integer.parseInt(versionCodeString);
		return versionCode;
	}
	
	/**
	 * Returns the default arguments for executable
	 * @param executable the executable
	 * @param env the environment
	 * @return the ArrayList
	 */
	static ArrayList getDefaultArguments(String executable, Environment env)
	{
		String androidJar = ArgumentHelper.getAndroidJar(env); 
		
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		arguments.add("package");
		arguments.add("-m");
		arguments.add("-J");
		arguments.add(ArgumentHelper.getSrc(env));
		arguments.add("-M");
		arguments.add(ArgumentHelper.getActivity(env) + "/AndroidManifest.xml");
		arguments.add("-S");
		arguments.add(ArgumentHelper.getRes(env));
		arguments.add("-I");
		arguments.add(androidJar);
		return arguments;
	}
	
	/**
	 * Copies the resources in lower case
	 * @param device the device
	 * @param env the environment
	 * @throws IOException if an error occurs during the copying
	 */
	void copyResources(Device device, Environment env) throws IOException
	{
		FilenameFilter filenameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(".svn".equals(name)) {
					return false;
				}
				return true;
			}
			
		};
		String[] fileNames = FileUtil.filterDirectory(device.getResourceDir(), null, filenameFilter, true);
		String resourceDir = device.getResourceDir().getAbsolutePath();		
		String rawDirectory = ArgumentHelper.getRaw(env);
		
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			File srcFile = new File(resourceDir + File.separator + fileName);
			String targetFileName = fileName.toLowerCase();
			int pathSeparatorIndex = targetFileName.lastIndexOf(File.separatorChar );
			if (pathSeparatorIndex != -1) {
				targetFileName = targetFileName.substring( pathSeparatorIndex + 1 );
			}
			String cleanedTargetFileName = cleanResourceName(targetFileName);
			File destFile = new File(rawDirectory + File.separator + cleanedTargetFileName);
			
			FileUtil.copy(srcFile, destFile);
		}
	}
	
	/**
	 * Cleans the filename. Android does not allow the minus sign in the name of a resource file.
	 * The path is also flattened as android does not allow resources in subfolders.
	 * TODO: This method must be equal to the one in ResourceHelper. Its hard to take the same class as
	 * the classpath is different for building and at runtime.
	 * @param resourceName Must not be null
	 * @return Never null.
	 */
	private String cleanResourceName(String resourceName) {
		String cleanedName = resourceName.replace('-', '_');
		int lastIndexOfSlash = cleanedName.lastIndexOf(File.separator);
		if(lastIndexOfSlash > -0) {
			cleanedName = cleanedName.substring(lastIndexOfSlash+1);
		}
		return cleanedName;
	}
}
