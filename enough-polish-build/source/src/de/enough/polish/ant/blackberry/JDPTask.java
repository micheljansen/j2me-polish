package de.enough.polish.ant.blackberry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import de.enough.polish.util.StringUtil;

public class JDPTask extends Task {
	private static final String TEMPLATE = 
		"## RIM Java Development Environment\n" +
		"# RIM Project file\n" + 
		"AlwaysBuild=0\n" + 
		"[AlxImports\n" + 
		"]\n" + 
		"AutoRestart=0\n" + 
		"[ClassProtection\n" + 
		"]\n" + 
		"[CustomBuildFiles\n" + 
		"]\n" + 
		"[CustomBuildRules\n" + 
		"]\n" + 
		"[DefFiles\n" + 
		"]\n" + 
		"[DependsOn\n" + 
		"]\n" + 
		"ExcludeFromBuildAll=0\n" +
		"[Files\n" +
		"@@FILES@@\n" +
		"]\n" +
		"HaveAlxImports=0\n" +
		"HaveDefs=0\n" +
		"HaveImports=0\n" +
		"[Icons\n" +
		"]\n" +
		"[ImplicitRules\n" +
		"]\n" +
		"[Imports\n" +
		"]\n" +
		"Listing=0\n" +
		"Options=-quiet\n" +
		"OutputFileName=@@NAME@@\n" +
		"[PackageProtection\n" +
		"]\n" +
		"RibbonPosition=0\n" +
		"RunOnStartup=0\n" +
		"StartupTier=7\n" +
		"SystemModule=0\n" +
		"Type=0\n";
	
	
	private static final String FILES = "@@FILES@@";
	private static final String NAME = "@@NAME@@";

	String name;
	
	String path;

	String template;

	String sources;
	
	public void execute() throws BuildException {
		String sourceDir = this.sources.trim() + File.separatorChar + "source";

		String resourceDir = this.sources.trim() +File.separatorChar+ "classes";

		try {
			System.out.println("jdp: Reading template...");
			
			String fullPath = this.path.trim() + File.separatorChar + this.name + ".jdp";

			String content;
			
			if(this.template != null)
			{
				content = readFile(new File(this.template)).trim();
			}
			else
			{
				content = TEMPLATE;
			}

			System.out.println("jdp: Collecting java classes from " + sourceDir
					+ "...");

			List sourceFiles = FileListing.getFileListing(new File(sourceDir),
					"java", true);

			System.out.println("jdp: Collecting resources from " + resourceDir
					+ "...");

			List resourceFiles = FileListing.getFileListing(new File(
					resourceDir), null, false);

			System.out.println("jdp: Writing files to project " + fullPath
					+ "...");

			String list = getFileList(sourceFiles, resourceFiles).trim();
			
			content = StringUtil.replace(content, FILES, list);
			content = StringUtil.replace(content, NAME, this.name);
			
			writeFile(new File(fullPath),content);
			
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		int len = stream.available();
		StringBuffer sb = new StringBuffer();
		sb.append(" ");

		for (int i = 1; i <= len; i++) {
			sb.append((char) stream.read());
		}

		stream.close();
		return sb.toString();
	}
	
	private void writeFile(File file, String content) throws IOException
	{
		if(!file.exists())
		{
			file.createNewFile();
		}
		
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}

	private String getFileList(List classes, List resources) {
		String filename;
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < classes.size(); i++) {
			File file = (File) classes.get(i);
			if (file.getName().charAt(0) != '.') {
				filename = file.getAbsolutePath().replace("\\", "\\\\");
				sb.append(filename);
				sb.append("\r\n");
			}
		}

		for (int i = 0; i < resources.size(); i++) {
			File file = (File) resources.get(i);
			if (file.getName().charAt(0) != '.' && !file.getName().endsWith(".class")) {
				filename = file.getAbsolutePath().replace("\\", "\\\\");
				sb.append(filename);
				sb.append("\r\n");
			}
		}

		return sb.toString();
	}

	public String getproject() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSources() {
		return this.sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name.trim().replace(' ', '_');
	}

}
