//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

/**
 * A special key item implementation to shift the keys of a keyboard
 * @author Andre
 *
 */
public class ShiftKeyItem extends KeyItem {
	
	/**
	 * Creates a new DeleteKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ShiftKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new ShiftKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ShiftKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "S", style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.keyboard.KeyItem#apply(boolean)
	 */
	protected void apply(boolean doubleclick) {
		getKeyboard().shift(!getKeyboard().isShift());
	}
}
