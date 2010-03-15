//#condition polish.usePolishGui && polish.blackberry

/*
 * Created on Jul 7, 2008 at 2:16:30 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.blackberry.ui;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import net.rim.device.api.ui.MenuItem;

/**
 * <p>Maps from a command to a menuitem</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandMenuItem extends MenuItem {

	final Command cmd;
	private final Displayable displayable;

	/**
	 * @param cmd 
	 * @param displayable 
	 */
	public CommandMenuItem(Command cmd, Displayable displayable) {
		super(cmd.getLabel(), cmd.getPriority(), cmd.getPriority() );
		this.cmd = cmd;
		this.displayable = displayable;
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (this.displayable instanceof Screen) {
			UiAccess.handleCommand((Screen)this.displayable, this.cmd);
		} else if (this.displayable instanceof CommandListener) {
			((CommandListener)this.displayable).commandAction(this.cmd, this.displayable);
//		} else {
//			Display.getInstance().commandAction(this.cmd, this.displayable);
		}
	}

}
