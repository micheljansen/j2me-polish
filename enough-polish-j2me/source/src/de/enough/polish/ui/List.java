//#condition polish.usePolishGui
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:45 CET 2003
/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui;


import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.Locale;


/**
 * A Screen containing list of choices.
 * 
 * The dynamic CSS selector is "list".
 * 
 * A <code>Screen</code> containing list of choices. Most of its
 * behavior is common with
 * class <A HREF="../../../javax/microedition/lcdui/ChoiceGroup.html"><CODE>ChoiceGroup</CODE></A>, and their common API. The
 * different <code>List</code> types in particular, are defined in
 * interface <A HREF="../../../javax/microedition/lcdui/Choice.html"><CODE>Choice</CODE></A>.  When a <code>List</code> is present on the display, the
 * user can interact with
 * it by selecting elements and possibly by traversing and scrolling among
 * them.  Traversing and scrolling operations do not cause application-visible
 * events. The system notifies the application only when a <A HREF="../../../javax/microedition/lcdui/Command.html"><CODE>Command</CODE></A> is invoked by notifying its <A HREF="../../../javax/microedition/lcdui/CommandListener.html"><CODE>CommandListener</CODE></A>.  The
 * <code>List</code>
 * class also supports a select command that may be invoked specially
 * depending upon the capabilities of the device.
 * 
 * <p>The notion of a <em>select</em> operation on a <code>List</code>
 * element is central
 * to the user's interaction with the <code>List</code>.  On devices
 * that have a dedicated
 * hardware &quot;select&quot; or &quot;go&quot; key, the select
 * operation is implemented with
 * that key.  Devices that do not have a dedicated key must provide another
 * means to do the select operation, for example, using a soft key.  The
 * behavior of the select operation within the different types of lists is
 * described in the following sections.</p>
 * 
 * <p><code>List</code> objects may be created with <code>Choice</code> types of
 * <A HREF="../../../javax/microedition/lcdui/Choice.html#EXCLUSIVE"><CODE>Choice.EXCLUSIVE</CODE></A>, <A HREF="../../../javax/microedition/lcdui/Choice.html#MULTIPLE"><CODE>Choice.MULTIPLE</CODE></A>, and
 * <A HREF="../../../javax/microedition/lcdui/Choice.html#IMPLICIT"><CODE>Choice.IMPLICIT</CODE></A>.  The <code>Choice</code> type <A HREF="../../../javax/microedition/lcdui/Choice.html#POPUP"><CODE>Choice.POPUP</CODE></A>
 * is not allowed on <code>List</code> objects.</p>
 * 
 * <h3>Selection in <code>EXCLUSIVE</code> and <code>MULTIPLE</code> Lists</h3>
 * 
 * <p>The select operation is not associated with a
 * <code>Command</code> object, so the
 * application has no means of setting a label for it or being notified when
 * the operation is performed.  In <code>Lists</code> of type
 * <code>EXCLUSIVE</code>, the select
 * operation selects the target element and deselects the previously selected
 * element.  In <code>Lists</code> of type <code>MULTIPLE</code>, the
 * select operation toggles the
 * selected state of the target element, leaving the selected state of other
 * elements unchanged.  Devices that implement the select operation using a
 * soft key will need to provide a label for it.  The label should be something
 * similar to &quot;Select&quot; for <code>Lists</code> of type
 * <code>EXCLUSIVE</code>, and it should be something
 * similar to &quot;Mark&quot; or &quot;Unmark&quot; for
 * <code>Lists</code> of type <code>MULTIPLE</code>.</p>
 * 
 * <h3>Selection in <code>IMPLICIT</code> Lists</h3>
 * 
 * <p>The select operation is associated with a <code>Command</code>
 * object referred to as
 * the <em>select command</em>.  When the user performs the select operation,
 * the system will invoke the select command by notifying the
 * <code>List's</code> <CODE>CommandListener</CODE>.  The default select command is the
 * system-provided command <code>SELECT_COMMAND</code>.  The select
 * command may be modified
 * by the application through use of the <A HREF="../../../javax/microedition/lcdui/List.html#setSelectCommand(javax.microedition.lcdui.Command)"><CODE>setSelectCommand</CODE></A> method.  Devices that implement the select
 * operation using a soft key will use the label from the select command.  If
 * the select command is <code>SELECT_COMMAND</code>, the device may
 * choose to provide its
 * own label instead of using the label attribute of
 * <code>SELECT_COMMAND</code>.
 * Applications should generally provide their own select command to replace
 * <code>SELECT_COMMAND</code>.  This allows applications to provide a
 * meaningful label,
 * instead of relying on the one provided by the system for
 * <code>SELECT_COMMAND</code>.
 * The implementation must <em>not</em> invoke the select command if there are
 * no elements in the <code>List</code>, because if the
 * <code>List</code> is empty the selection does
 * not exist.  In this case the implementation should remove or disable the
 * select command if it would appear explicitly on a soft button or in a menu.
 * Other commands can be invoked normally when the <code>List</code>
 * is empty.</p>
 * 
 * <h3>Use of <code>IMPLICIT</code> Lists</h3>
 * 
 * <p> <code>IMPLICIT</code> <code>Lists</code> can be used to
 * construct menus by providing operations
 * as <code>List</code> elements.  The application provides a
 * <code>Command</code> that is used to
 * select a <code>List</code> element and then defines this
 * <code>Command</code> to be used as the
 * select command.  The application must also register a
 * <code>CommandListener</code> that
 * is called when the user selects or activates the <code>Command</code>:</p>
 * 
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 * <pre><code>
 * String[] elements = { ... }; //Menu items as List elements
 * List menuList = new List("Menu", List.IMPLICIT, elements, null);
 * Command selectCommand = new Command("Open", Command.ITEM, 1);
 * menuList.setSelectCommand(selectCommand);
 * menuList.setCommandListener(...);     </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * 
 * <p>The listener can query the <code>List</code> to determine which
 * element is selected
 * and then perform the corresponding action.  Note that setting a command as
 * the select command adds it to the <code>List</code> as a side effect.</p>
 * 
 * <p> The select command should be considered as a <em>default operation</em>
 * that takes place when a select key is pressed.  For example, a
 * <code>List</code>
 * displaying email headers might have three operations: read, reply, and
 * delete. Read is considered to be the default operation.  </p>
 * 
 * <pre><code>
 * List list = new List("Email", List.IMPLICIT, headers);
 * readCommand = new Command("Read", Command.ITEM, 1);
 * replyCommand = new Command("Reply", Command.ITEM, 2);
 * deleteCommand = new Command("Delete", Command.ITEM, 3);
 * list.setSelectCommand(readCommand);
 * list.addCommand(replyCommand);
 * list.addCommand(deleteCommand);
 * list.setCommandListener(...);     
 * </code></pre>
 * 
 * <p>On a device with a dedicated select key, pressing this key will invoke
 * <code>readCommand</code>.  On a device without a select key, the user is
 * still able to invoke the read command, since it is also provided as an
 * ordinary <code>Command</code>.</p>
 * 
 * <p> It should be noted that this kind of default operation must be used
 * carefully, and the usability of the resulting user interface must always
 * kept in mind. The default operation should always be the most intuitive
 * operation on a particular List.  </p>
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class List extends Screen implements Choice
{
	/**
	 * The default select command for <code>IMPLICIT</code> <code>Lists</code>.
	 * Applications using an <code>IMPLICIT</code> <code>List</code>
	 * should set their own select command
	 * using
	 * <A HREF="../../../javax/microedition/lcdui/List.html#setSelectCommand(javax.microedition.lcdui.Command)"><CODE>setSelectCommand</CODE></A>.
	 * 
	 * 
	 * <p><code>SELECT_COMMAND</code> is treated as an ordinary
	 * <code>Command</code> if it is used with other <code>Displayable</code>
	 * types.</p>
	 */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command SELECT_COMMAND = new Command( Locale.get("polish.command.select"), Command.ITEM, 3 );
	//#elifdef polish.command.select:defined
		//#= public static final Command SELECT_COMMAND = new Command("${polish.command.select}", Command.ITEM, 3 );
	//#else
		//# public static final Command SELECT_COMMAND = new Command( "Select", Command.ITEM, 3 );
	//#endif

	private Command selectCommand = SELECT_COMMAND;
	protected int listType;
	protected ChoiceGroup choiceGroup;
	//#ifdef polish.css.show-text-in-title
		private boolean showTextInTitle;
	//#endif

	/**
	 * Creates a new, empty <code>List</code>, specifying its title
	 * and the type of the list.
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice
	 */
	public List( String title, int listType)
	{
		this( title, listType, new String[0], null, null );
	}

	/**
	 * Creates a new, empty <code>List</code>, specifying its title
	 * and the type of the
	 * list.
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param style the style of this list
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice
	 */
	public List( String title, int listType, Style style)
	{
		this( title, listType, new String[0], new Image[0], style );
	}

	/**
	 * Creates a new <code>List</code>, specifying its title, the type
	 * of the <code>List</code>, and
	 * an array of <code>Strings</code> and <code>Images</code> to be
	 * used as its initial contents.
	 * 
	 * <p>The <code>stringElements</code> array must be non-null and
	 * every array element
	 * must also be non-null.  The length of the
	 * <code>stringElements</code> array
	 * determines the number of elements in the <code>List</code>.
	 * The <code>imageElements</code> array
	 * may be <code>null</code> to indicate that the <code>List</code>
	 * elements have no images.  If the
	 * <code>imageElements</code> array is non-null, it must be the
	 * same length as the
	 * <code>stringElements</code> array.  Individual elements of the
	 * <code>imageElements</code> array
	 * may be <code>null</code> in order to indicate the absence of an
	 * image for the
	 * corresponding <code>List</code> element. Non-null elements of the
	 * <code>imageElements</code> array may refer to mutable or
	 * immutable images.</p>
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param stringElements set of strings specifying the string parts of the List elements
	 * @param imageElements set of images specifying the image parts of the List elements
	 * @throws NullPointerException if stringElements is null 
	 * 		   or if the stringElements array contains any null elements
	 * @throws IllegalArgumentException if the imageElements array is non-null and has a different length from the stringElements array
	 *         or if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice#EXCLUSIVE
	 * @see Choice#MULTIPLE
	 * @see Choice#IMPLICIT
	 */
	public List( String title, int listType, String[] stringElements, Image[] imageElements)
	{
		this( title, listType, stringElements, imageElements, null );
	}

	/**
	 * Creates a new <code>List</code>, specifying its title, the type
	 * of the <code>List</code>, and
	 * an array of <code>Strings</code> and <code>Images</code> to be
	 * used as its initial contents.
	 * 
	 * <p>The <code>stringElements</code> array must be non-null and
	 * every array element
	 * must also be non-null.  The length of the
	 * <code>stringElements</code> array
	 * determines the number of elements in the <code>List</code>.
	 * The <code>imageElements</code> array
	 * may be <code>null</code> to indicate that the <code>List</code>
	 * elements have no images.  If the
	 * <code>imageElements</code> array is non-null, it must be the
	 * same length as the
	 * <code>stringElements</code> array.  Individual elements of the
	 * <code>imageElements</code> array
	 * may be <code>null</code> in order to indicate the absence of an
	 * image for the
	 * corresponding <code>List</code> element. Non-null elements of the
	 * <code>imageElements</code> array may refer to mutable or
	 * immutable images.</p>
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param stringElements set of strings specifying the string parts of the List elements
	 * @param imageElements set of images specifying the image parts of the List elements
	 * @param style the style of this list
	 * @throws NullPointerException if stringElements is null 
	 * 		   or if the stringElements array contains any null elements
	 * @throws IllegalArgumentException if the imageElements array is non-null and has a different length from the stringElements array
	 *         or if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice#EXCLUSIVE
	 * @see Choice#MULTIPLE
	 * @see Choice#IMPLICIT
	 */
	public List( String title, int listType, String[] stringElements, Image[] imageElements, Style style)
	{
		this( title, listType, ChoiceGroup.buildChoiceItems(stringElements, imageElements, listType, style), style);
//		super( title, style, false );
//		//#ifndef polish.skipArgumentCheck
//			if (listType != Choice.EXCLUSIVE && listType != Choice.MULTIPLE && listType != Choice.IMPLICIT ) {
//				//#ifdef polish.debugVerbose
//					throw new IllegalArgumentException("invalid list-type: " + listType );
//				//#else
//					//# throw new IllegalArgumentException();
//				//#endif
//			}		
//		//#endif
//		this.listType = listType;
//		
//		this.choiceGroup = new ChoiceGroup( null, this.listType, stringElements, imageElements, style, true  );
//		this.choiceGroup.autoFocusEnabled = true;
//		this.choiceGroup.screen = this;
//		this.choiceGroup.isFocused = true;
//		this.container = this.choiceGroup;
	}
		
	/**
	 * Creates a new <code>List</code>, specifying its title, the type
	 * the type of the
	 * <code>List</code>, and an array of <code>ChoiceItem</code>s
	 * to be used as its initial contents.
	 * 
	 * <p>The <code>items</code>s array must be non-null and
	 * every <code>ChoiceItem</code> must have its text be a non-null
	 * <code>String</code>.
	 * The length of the <code>items</code> array
	 * determines the number of elements in the <code>ChoiceGroup</code>.</p>
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param items set of <code>ChoiceItem</code>s specifying the ChoiceGroup elements
	 * @throws NullPointerException if <code>items</code> is null 
	 *         or if getText() for one of the <code>ChoiceItem</code> in the array 
	 *         retuns a null <code>String</code>.
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice#EXCLUSIVE
	 * @see Choice#MULTIPLE
	 * @see Choice#IMPLICIT
	 */
	public List( String title, int listType, ChoiceItem[] items)
	{
		this( title, listType, items, null ); 
	}
		
	/**
	 * Creates a new <code>List</code>, specifying its title, the type
	 * the type of the
	 * <code>List</code>, and an array of <code>ChoiceItem</code>s
	 * to be used as its initial contents.
	 * 
	 * <p>The <code>items</code>s array must be non-null and
	 * every <code>ChoiceItem</code> must have its text be a non-null
	 * <code>String</code>.
	 * The length of the <code>items</code> array
	 * determines the number of elements in the <code>ChoiceGroup</code>.</p>
	 * 
	 * @param title the screen's title (see Displayable)
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param items set of <code>ChoiceItem</code>s specifying the ChoiceGroup elements
	 * @param style the style of this list
	 * @throws NullPointerException if <code>items</code> is null 
	 *         or if getText() for one of the <code>ChoiceItem</code> in the array 
	 *         retuns a null <code>String</code>.
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @see Choice#EXCLUSIVE
	 * @see Choice#MULTIPLE
	 * @see Choice#IMPLICIT
	 */
	public List( String title, int listType, ChoiceItem[] items, Style style)
	{
		super( title, style, false );
		//#ifndef polish.skipArgumentCheck
			if (listType != Choice.EXCLUSIVE && listType != Choice.MULTIPLE && listType != Choice.IMPLICIT ) {
				//#ifdef polish.debugVerbose
					throw new IllegalArgumentException("invalid list-type: " + listType );
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}		
		//#endif
		this.listType = listType;
		
		//#ifdef polish.i18n.useDynamicTranslations
			String selectLabel = Locale.get("polish.command.select");
			if ( selectLabel != SELECT_COMMAND.getLabel()) {
				SELECT_COMMAND = new Command( selectLabel, Command.ITEM, 3 );
				this.selectCommand = SELECT_COMMAND;
			}
		//#endif

		this.choiceGroup = new ChoiceGroup( null, this.listType, items, style, true  );
		this.choiceGroup.autoFocusEnabled = true;
		this.choiceGroup.screen = this;
		this.choiceGroup.isFocused = true;
		this.container = this.choiceGroup;
	}
		
	/**
	 * Gets the number of elements in the <code>List</code>.
	 * 
	 * @return the number of elements in the List
	 * @see Choice#size() in interface Choice
	 */
	public int size()
	{
		return this.container.size();
	}

	/**
	 * Gets the <code>String</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * 
	 * @param elementNum - the index of the element to be queried
	 * @return the string part of the element
	 * @throws IndexOutOfBoundsException - if elementNum is invalid
	 * @see Choice#getString(int) in interface Choice
	 * @see #getImage(int)
	 */
	public String getString(int elementNum)
	{
		return getItem(elementNum).getText();
	}

	/**
	 * Gets the <code>Image</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * 
	 * @param elementNum the number of the element to be queried
	 * @return the image part of the element, or null if there is no image
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#getImage(int) in interface Choice
	 * @see #getString(int)
	 */
	public Image getImage( int elementNum )
	{
		return getItem(elementNum).getImage();
	}

	/**
	 * Gets the <code>ChoiceItem</code> of the element referenced by
	 * <code>elementNum</code>.
	 *
	 * @param elementNum the number of the element to be queried
	 * @return the ChoiceItem of the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 */
	public ChoiceItem getItem( int elementNum )
	{
		return (ChoiceItem)this.choiceGroup.get( elementNum );
	}

	/**
	 * Appends an element to the <code>List</code>.
	 * 
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added, or null if there is no image part
	 * @return the assigned index of the element
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#append(String, Image) in interface Choice
	 */
	public int append( String stringPart, Image imagePart)
	{
		return append( stringPart, imagePart, null );
	}
	/**
	 * Appends an element to the <code>List</code>.
	 * 
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added, or null if there is no image part
	 * @param elementStyle the style for the new list element.
	 * @return the assigned index of the element
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#append(String, Image) in interface Choice
	 */
	public int append( String stringPart, Image imagePart, Style elementStyle )
	{
		return append( new ChoiceItem( stringPart, imagePart, this.listType, elementStyle ));
	}
	
	/**
	 * Appends a <code>ChoiceItem</code> to the <code>List</code>.
	 *
	 * @param item ChoiceItem to be added
	 * @return the assigned index of the element
	 */
	public int append( ChoiceItem item )
	{
		//#ifdef polish.css.show-text-in-title
			if (this.showTextInTitle){
				item.setTextVisible(false);
				String stringPart = item.getText();
				if (this.choiceGroup.size() == 0) {
					setTitle( stringPart );
				}
			}
		//#endif
		int number = this.choiceGroup.append( item );
		//#if polish.List.suppressCommands != true
			if (number == 0) {
				// the first item has been inserted:
				if (this.listType == Choice.IMPLICIT && this.selectCommand != null ) {
					//#if polish.List.suppressSelectCommand != true
						addCommand( this.selectCommand );
					//#endif
				} else {
					//#if polish.List.suppressMarkCommands != true
						setItemCommands(this.choiceGroup.commands, this.choiceGroup);
					//#endif
				}
			}
		//#endif
		return number;
	}
	
	/**
	 * Inserts an element into the <code>List</code> just prior to the element specified.
	 * 
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#insert(int, String, Image) in interface Choice
	 */
	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		insert( elementNum, stringPart, imagePart, null );
	}
	
	/**
	 * Inserts an element into the <code>List</code> just prior to the element specified.
	 * 
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @param elementStyle the style for the new list element.
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#insert(int, String, Image) in interface Choice
	 */
	public void insert(int elementNum, String stringPart, Image imagePart, Style elementStyle )
	{
		insert( elementNum, new ChoiceItem( stringPart, imagePart, this.listType, elementStyle ) );
	}

	/**
	 * Inserts an element into the <code>List</code> just prior to the element specified.
	 * 
	 * @param elementNum the index of the element where insertion is to occur
	 * @param item ChoiceItem of the element to be inserted
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 */
	public void insert(int elementNum, ChoiceItem item)
	{
		//#ifdef polish.css.show-text-in-title
			if (this.showTextInTitle){
				item.setTextVisible(false);
				String stringPart = item.getText();
				if (this.choiceGroup.size() == 0) {
					setTitle( stringPart );
				}
			}
		//#endif
		this.choiceGroup.insert( elementNum, item );
		//#if polish.List.suppressCommands != true
			if (this.choiceGroup.size() == 0) {
				// the first item has been inserted:
				if (this.listType == Choice.IMPLICIT && this.selectCommand != null ) {
					//#if polish.List.suppressSelectCommand != true
						addCommand( this.selectCommand );
					//#endif
				} else {
					//#if polish.List.suppressMarkCommands != true
						setItemCommands(this.choiceGroup.commands, this.choiceGroup);
					//#endif
				}
			}
		//#endif
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or null if there is no image part
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#set(int, String, Image) in interface Choice
	 */
	public void set(int elementNum, String stringPart, Image imagePart)
	{
		set( elementNum, stringPart, imagePart, null );
	}

	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or null if there is no image part
	 * @param elementStyle the style for the new list element.
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#set(int, String, Image) in interface Choice
	 */
	public void set(int elementNum, String stringPart, Image imagePart, Style elementStyle )
	{
		set(elementNum, new ChoiceItem(stringPart, imagePart, this.listType, elementStyle));
	}

	/**
	 * Sets the <code>ChoiceItem</code> of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous one.
	 * 
	 * @param elementNum the index of the element to be set
	 * @param item ChoiceItem of the new element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 */
	public void set(int elementNum, ChoiceItem item)
	{
		this.choiceGroup.set( elementNum, item );
		//#ifdef polish.css.show-text-in-title
			if (this.showTextInTitle){
				item.setTextVisible(false);
				String stringPart = item.getText();
				if (elementNum == this.choiceGroup.getFocusedIndex()) {
					setTitle( stringPart );
				}
			}
		//#endif
	}

	/**
	 * Deletes the element referenced by <code>elementNum</code>.
	 * 
	 * @param elementNum the index of the element to be deleted
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#delete(int) in interface Choice
	 */
	public void delete(int elementNum)
	{
		this.choiceGroup.delete(elementNum);
		if (this.choiceGroup.size() == 0 ) {
			if (this.listType == Choice.IMPLICIT && this.selectCommand != null ) {
				super.removeCommand( this.selectCommand );
			} else {
				removeItemCommands(this.choiceGroup);
			}
		}
	}

	/**
	 * Deletes all elements from this List.
	 * 
	 * @see Choice#deleteAll() in interface Choice
	 */
	public void deleteAll()
	{
		this.choiceGroup.deleteAll();
		if (this.listType == Choice.IMPLICIT && this.selectCommand != null) {
			super.removeCommand( this.selectCommand );
		} else {
			removeItemCommands(this.choiceGroup);
		}
	}

	/**
	 * Gets a boolean value indicating whether this element is selected.
	 * 
	 * @param elementNum index to element to be queried
	 * @return selection state of the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#isSelected(int) in interface Choice
	 */
	public boolean isSelected(int elementNum)
	{
		return this.choiceGroup.isSelected(elementNum);
	}

	/**
	 * Returns the index number of an element in the <code>List</code>
	 * that is selected.
	 * 
	 * @return index of selected element, or -1 if none is selected
	 * @see Choice#getSelectedIndex() in interface Choice
	 * @see #setSelectedIndex(int, boolean)
	 */
	public int getSelectedIndex()
	{
		return this.choiceGroup.getSelectedIndex();
	}

	/**
	 * Queries the state of a <code>List</code> and returns the
	 * state of all elements in the boolean array
	 * <code>selectedArray_return</code>.
	 * 
	 * @param selectedArray_return array to contain the results
	 * @return the number of selected elements in the Choice
	 * @throws IllegalArgumentException if selectedArray_return is shorter than the size of the List
	 * @throws NullPointerException if selectedArray_return is null
	 * @see Choice#getSelectedFlags(boolean[]) in interface Choice
	 * @see #setSelectedFlags(boolean[])
	 */
	public int getSelectedFlags(boolean[] selectedArray_return)
	{
		return this.choiceGroup.getSelectedFlags(selectedArray_return);
	}

	/**
	 * Sets the selected state of an element.
	 * 
	 * @param elementNum the index of the element, starting from zero
	 * @param selected the state of the element, where true means selected and false means not selected
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see #setSelectedIndex(int, boolean) in interface Choice
	 * @see #getSelectedIndex()
	 */
	public void setSelectedIndex(int elementNum, boolean selected)
	{
		this.choiceGroup.setSelectedIndex(elementNum, selected);
	}

	/**
	 * Sets the selected state of all elements of the <code>List</code>.
	 * 
	 * @param selectedArray an array in which the method collect the selection status
	 * @throws IllegalArgumentException if selectedArray is shorter than the size of the List
	 * @throws NullPointerException if selectedArray is null
	 * @see #setSelectedFlags(boolean[]) in interface Choice
	 * @see #getSelectedFlags(boolean[])
	 */
	public void setSelectedFlags(boolean[] selectedArray)
	{
		this.choiceGroup.setSelectedFlags(selectedArray);
	}

	/**
	 * The same as <A HREF="../../../javax/microedition/lcdui/Displayable.html#removeCommand(javax.microedition.lcdui.Command)"><CODE>Displayable.removeCommand</CODE></A>
	 * but with the following additional semantics.
	 * 
	 * <p>If the command to be removed happens to be the select command, the
	 * <code>List</code> is set to have no select command, and the command is
	 * removed from the <code>List</code>.</p>
	 * 
	 * <p>The following code: </P>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * // Command c is the select command on List list
	 * list.removeCommand(c);     </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <P>
	 * is equivalent to the following code: </P>
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * // Command c is the select command on List list
	 * list.setSelectCommand(null);
	 * list.removeCommand(c);     </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * 
	 * @param cmd - the command to be removed
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 * @since  MIDP 2.0
	 */
	public void removeCommand( Command cmd)
	{
		if (cmd == this.selectCommand) {
			this.selectCommand = null;
		}
		super.removeCommand(cmd);
	}

	/**
	 * Sets the <code>Command</code> to be used for an
	 * <code>IMPLICIT</code> <code>List</code> selection
	 * action.
	 * By default, an implicit selection of a List will result in the
	 * predefined <code>List.SELECT_COMMAND</code> being used. This
	 * behavior may be
	 * overridden by calling the <code>List.setSelectCommand()</code>
	 * method with an
	 * appropriate parameter value.  If a <code>null</code> reference
	 * is passed, this
	 * indicates that no &quot;select&quot; action is appropriate for
	 * the contents
	 * of this <code>List</code>.
	 * 
	 * <p> If a reference to a command object is passed, and
	 * it is not the special command <code>List.SELECT_COMMAND</code>, and
	 * it is not currently present on this <code>List</code> object,
	 * the command object is added to this <code>List</code> as if
	 * <code>addCommand(command)</code> had been called
	 * prior to the command being made the select command.  This
	 * indicates that this command
	 * is to be invoked when the user performs the &quot;select&quot;
	 * on an element of
	 * this <code>List</code>. </p>
	 * 
	 * <p> The select command should have a command type of
	 * <code>ITEM</code> to indicate
	 * that it operates on the currently selected object.  It is not an error
	 * if the command is of some other type.
	 * (<code>List.SELECT_COMMAND</code> has a type
	 * of <code>SCREEN</code> for historical purposes.)  For purposes
	 * of presentation and
	 * placement within its user interface, the implementation is allowed to
	 * treat the select command as if it were of type <code>ITEM</code>. </p>
	 * 
	 * <p> If the select command is later removed from the <code>List</code>
	 * with <code>removeCommand()</code>, the <code>List</code> is set to have
	 * no select command as if <code>List.setSelectCommand(null)</code> had
	 * been called.</p>
	 * 
	 * <p> The default behavior can be reestablished explicitly by calling
	 * <code>setSelectCommand()</code> with an argument of
	 * <code>List.SELECT_COMMAND</code>.</p>
	 * 
	 * <p> This method has no effect if the type of the
	 * <code>List</code> is not <code>IMPLICIT</code>. </p>
	 * 
	 * @param command the command to be used for an IMPLICIT list selection action, or null if there is none
	 * @since  MIDP 2.0
	 */
	public void setSelectCommand( Command command)
	{
		if (this.listType == Choice.IMPLICIT) {
			if (this.selectCommand != null) {
				super.removeCommand( this.selectCommand );
			}
			this.selectCommand = command;
			if (this.choiceGroup.size() > 0 && command != null) {
				addCommand( command );
			}
			this.choiceGroup.setSelectCommand( command );
		}
	}

	/**
	 * Sets the application's preferred policy for fitting
	 * <code>Choice</code> element
	 * contents to the available screen space. The set policy applies for all
	 * elements of the <code>Choice</code> object.  Valid values are
	 * <CODE>Choice.TEXT_WRAP_DEFAULT</CODE>, <CODE>Choice.TEXT_WRAP_ON</CODE>,
	 * and <CODE>Choice.TEXT_WRAP_OFF</CODE>. Fit policy is a hint, and the
	 * implementation may disregard the application's preferred policy.
	 * 
	 * @param fitPolicy preferred content fit policy for choice elements
	 * @throws IllegalArgumentException if fitPolicy is invalid
	 * @see Choice#setFitPolicy(int) in interface Choice
	 * @see #getFitPolicy()
	 * @since MIDP 2.0
	 */
	public void setFitPolicy(int fitPolicy)
	{
		this.choiceGroup.setFitPolicy(fitPolicy);
	}

	/**
	 * Gets the application's preferred policy for fitting
	 * <code>Choice</code> element
	 * contents to the available screen space.  The value returned is the
	 * policy that had been set by the application, even if that value had
	 * been disregarded by the implementation.
	 * 
	 * @return one of Choice.TEXT_WRAP_DEFAULT, Choice.TEXT_WRAP_ON, or Choice.TEXT_WRAP_OFF
	 * @see Choice#getFitPolicy() in interface Choice
	 * @see #setFitPolicy(int)
	 * @since  MIDP 2.0
	 */
	public int getFitPolicy()
	{
		return this.choiceGroup.getFitPolicy();
	}

	/**
	 * Sets the application's preferred font for
	 * rendering the specified element of this <code>Choice</code>.
	 * An element's font is a hint, and the implementation may disregard
	 * the application's preferred font.
	 * 
	 * <p> The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.</p>
	 * 
	 * <p> The <code>font</code> parameter must be a valid <code>Font</code>
	 * object or <code>null</code>. If the <code>font</code> parameter is
	 * <code>null</code>, the implementation must use its default font
	 * to render the element.</p>
	 * 
	 * @param elementNum the index of the element, starting from zero
	 * @param font the preferred font to use to render the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#setFont(int, Font) in interface Choice
	 * @see #getFont(int)
	 * @since  MIDP 2.0
	 */
	public void setFont(int elementNum, Font font)
	{
		getItem(elementNum).setFont(font);
	}

	/**
	 * Gets the application's preferred font for
	 * rendering the specified element of this <code>Choice</code>. The
	 * value returned is the font that had been set by the application,
	 * even if that value had been disregarded by the implementation.
	 * If no font had been set by the application, or if the application
	 * explicitly set the font to <code>null</code>, the value is the default
	 * font chosen by the implementation.
	 * 
	 * <p> The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.</p>
	 * 
	 * @param elementNum the index of the element, starting from zero
	 * @return the preferred font to use to render the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#getFont(int) in interface Choice
	 * @see #setFont(int elementNum, Font font)
	 * @since  MIDP 2.0
	 */
	public Font getFont(int elementNum)
	{
		return getItem(elementNum).getFont();
	}


	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCssSelector()
	 */
	protected String createCssSelector() {
		return "list";
	}
	//#endif	
	
	//#ifdef polish.css.show-text-in-title
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		Boolean showTextInTitleBool = style.getBooleanProperty("show-text-in-title");
		if (showTextInTitleBool != null) { 
			this.showTextInTitle = showTextInTitleBool.booleanValue();  
			if (this.showTextInTitle ) {
				// now remove all texts from the embedded items:
				Item[] items = this.choiceGroup.getItems();
				for (int i = 0; i < items.length; i++) {
					ChoiceItem item = (ChoiceItem) items[i];
					item.setTextVisible(false);
				}
				if (this.choiceGroup.getFocusedIndex() !=  -1) {
					setTitle( ((ChoiceItem)(items[this.choiceGroup.getFocusedIndex()])).getText() );
				} else if (items.length > 0) {
					setTitle( ((ChoiceItem)(items[0])).getText() );
				}
			}
		}
	}
	//#endif
	
	
	//#ifdef polish.css.show-text-in-title
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean processed = this.choiceGroup.handleKeyPressed(keyCode, gameAction);
		if (processed && this.showTextInTitle) {
			int selectedIndex = this.choiceGroup.getSelectedIndex();
			if (selectedIndex != -1) {
				setTitle( getString( selectedIndex ) ); 
			}
		}
		return processed;
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getDefaultCommand(de.enough.polish.ui.Item)
	 */
	protected Command getDefaultCommand(Item item)
	{
		if (this.choiceGroup.choiceType != Choice.MULTIPLE && this.selectCommand != null) {
			return this.selectCommand;
		}
		return super.getDefaultCommand(item);
	}

	
}
