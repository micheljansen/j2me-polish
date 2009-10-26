/*
 * Created on Jun 5, 2008 at 9:58:54 AM.
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
package de.enough.polish.devices;

import de.enough.polish.Device;

/**
 * <p>Receives information about new devices that have been resolved or that were unable to become resolved using the UserAgent.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface UserAgentStorage
{
	
	
	/**
	 * Is called whenever a new device has been resolved.
	 * @param userAgent the user agent
	 * @param device the corresponding found device
	 */
	public void notifyDeviceResolved( String userAgent, Device device );
	
	/**
	 * Is called whenever a device could not be resolved.
	 * @param userAgent the user agent
	 */
	public void notifyDeviceUnresolved( String userAgent );

}
