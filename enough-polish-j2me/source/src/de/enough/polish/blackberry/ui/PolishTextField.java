//#condition polish.usePolishGui && polish.blackberry

/*
 * Created on Mar 13, 2007 at 12:37:50 PM.
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


import de.enough.polish.ui.Style;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.XYRect;

/**
 * <p>Provides common funcitonality of the PolishEditField and the PolishPasswordEditField.</p>
 *
 * <p>Copyright Enough Software 2007 - 2009</p>
 * <pre>
 * history
 *        Mar 13, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface PolishTextField {
	
	public String getText();
	
	public void setText(String text);
	
	public void focusRemove();
	
	public void setStyle( Style style );
	
	public XYRect getExtent();
	
	public void layout( int width, int height );
	
	public void setPaintPosition( int x, int y );
	
	public void setCursorPosition( int pos );
	
	public int getInsertPositionOffset();
	
	public void setChangeListener( FieldChangeListener listener );

	public int getCursorPosition();

}
