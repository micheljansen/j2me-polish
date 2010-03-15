//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.ui.keyboard.view.KeyboardView;

/**
 * A special key item implementation for a clear key
 * to clear the contents of a KeyboardView
 * @author Andre
 *
 */
public class ClearKeyItem extends KeyItem {
	
	/**
	 * Creates a new ClearKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public ClearKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new ClearKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public ClearKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "CLR", style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		KeyboardView view = keyboard.getKeyboardView();
		String text = view.getText();
		
		if(text.length() > 0)
		{
			view.setText("");
		}
	}
}
