//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

/**
 * A special key item implementation for a blank key item
 * which does nothing and can be used as a blank filling in 
 * a keyboard layout 
 * @author Andre
 *
 */
public class BlankItem extends KeyItem {

	/**
	 * Creates a new BlankItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public BlankItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new BlankItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public BlankItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, " ", style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.keyboard.KeyItem#apply(boolean)
	 */
	protected void apply(boolean doubleclick) {
		// do nothing
	}
}
