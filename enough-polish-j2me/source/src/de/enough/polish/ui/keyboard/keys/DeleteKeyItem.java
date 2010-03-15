//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.ui.keyboard.view.KeyboardView;

/**
 * A special key item implementation to delete the last character of
 * the value of a KeyboardView
 * @author Andre
 *
 */
public class DeleteKeyItem extends KeyItem {
	
	/**
	 * Creates a new DeleteKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 */
	public DeleteKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	/**
	 * Creates a new DeleteKeyItem instance
	 * @param keyboard the keyboard
	 * @param position the position
	 * @param style the style
	 */
	public DeleteKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "DEL", style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.keyboard.KeyItem#apply(boolean)
	 */
	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		KeyboardView view = keyboard.getKeyboardView();
		String text = view.getText();
		
		if(text.length() > 0)
		{
			view.setText(text.substring(0, text.length() - 1));
		}
	}
}
