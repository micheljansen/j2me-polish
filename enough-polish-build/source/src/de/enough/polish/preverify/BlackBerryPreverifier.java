/*
 * Created on May 29, 2008 at 5:22:30 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.preverify;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BlackBerryPreverifier extends CldcPreverifier
{

	/* (non-Javadoc)
	 * @see de.enough.polish.preverify.CldcPreverifier#preverify(de.enough.polish.Device, java.io.File, java.io.File, org.apache.tools.ant.types.Path, org.apache.tools.ant.types.Path)
	 */
	public void preverify(Device device, File sourceDir, File targetDir,
			Path bootClassPath, Path classPath) throws IOException
	{
		//this.preverifyExecutable = this.environment.resolveFile("${blackberry.home}/");
		super.preverify(device, sourceDir, targetDir, bootClassPath, classPath);
	}
	
	

}
