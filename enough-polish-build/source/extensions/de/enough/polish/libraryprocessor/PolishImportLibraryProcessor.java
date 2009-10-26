/*
 * Created on Feb 4, 2009 at 3:08:12 PM.
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
package de.enough.polish.libraryprocessor;

import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;

/**
 * <p>Converts MIDP imports to J2ME Polish ones</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishImportLibraryProcessor extends ImportLibraryProcessor
{

	/* (non-Javadoc)
	 * @see de.enough.polish.libraryprocessor.ImportLibraryProcessor#addImportConversions(de.enough.polish.libraryprocessor.ImportConversionMap, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	protected void addImportConversions(ImportConversionMap conversions,
			Device device, Locale locale, Environment env)
	{
		conversions.addConversion("javax.microedition.lcdui.Alert", 				"de.enough.polish.ui.Alert");
		conversions.addConversion("javax.microedition.lcdui.AlertType", 			"de.enough.polish.ui.AlertType");
		conversions.addConversion("javax.microedition.lcdui.Canvas", 				"de.enough.polish.ui.Canvas");
		conversions.addConversion("javax.microedition.lcdui.Choice", 				"de.enough.polish.ui.Choice");
		conversions.addConversion("javax.microedition.lcdui.ChoiceGroup", 			"de.enough.polish.ui.ChoiceGroup");
		conversions.addConversion("javax.microedition.lcdui.Command", 				"de.enough.polish.ui.Command");
		conversions.addConversion("javax.microedition.lcdui.CommandListener", 		"de.enough.polish.ui.CommandListener");
		conversions.addConversion("javax.microedition.lcdui.CustomItem",			"de.enough.polish.ui.CustomItem");
		conversions.addConversion("javax.microedition.lcdui.DateField", 			"de.enough.polish.ui.DateField");
		conversions.addConversion("javax.microedition.lcdui.Display", 				"de.enough.polish.ui.Display");
		conversions.addConversion("javax.microedition.lcdui.Displayable", 			"de.enough.polish.ui.Displayable", true);
		conversions.addConversion("javax.microedition.lcdui.Form",					"de.enough.polish.ui.Form");
		conversions.addConversion("javax.microedition.lcdui.Gauge",					"de.enough.polish.ui.Gauge");
		conversions.addConversion("javax.microedition.lcdui.ImageItem",				"de.enough.polish.ui.ImageItem");
		conversions.addConversion("javax.microedition.lcdui.Item",					"de.enough.polish.ui.Item");
		conversions.addConversion("javax.microedition.lcdui.ItemCommandListener",	"de.enough.polish.ui.ItemCommandListener");
		conversions.addConversion("javax.microedition.lcdui.List",					"de.enough.polish.ui.List");
		conversions.addConversion("javax.microedition.lcdui.Screen",				"de.enough.polish.ui.Screen");
		conversions.addConversion("javax.microedition.lcdui.Spacer",				"de.enough.polish.ui.Spacer");
		conversions.addConversion("javax.microedition.lcdui.StringItem",			"de.enough.polish.ui.StringItem");
		conversions.addConversion("javax.microedition.lcdui.TextBox",				"de.enough.polish.ui.TextBox");
		conversions.addConversion("javax.microedition.lcdui.TextField",				"de.enough.polish.ui.TextField");
		conversions.addConversion("javax.microedition.lcdui.Ticker",				"de.enough.polish.ui.Ticker");
	}

}
