//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

/**
 * A special key item implementation to switch between the modes
 * of a keyboard
 * @author Andre
 *
 */
public class ModeKeyItem extends KeyItem {
	
	/**
	 * Creates a new ModeKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public ModeKeyItem(Keyboard keyboard, String position) {
		this(keyboard,position, null);
	}
	
	/**
	 * Creates a new ModeKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ModeKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "M", style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		
		keyboard.setNextMode();
	}
}
