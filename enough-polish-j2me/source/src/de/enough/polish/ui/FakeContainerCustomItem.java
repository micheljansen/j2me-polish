//#condition polish.LibraryBuild
/*
 * Created on 01-Mar-2004 at 09:45:32.
 *
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

import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;

/**
 * <p>Contains a number of items.</p>
 * <p>Main purpose is to manage all items of a Form or similiar canvases.</p>
 * <p>Containers support following additional CSS attributes:
 * </p>
 * <ul>
 * 		<li><b>columns</b>: The number of columns. If defined a table will be drawn.</li>
 * 		<li><b>columns-width</b>: The width of the columns. "equals" for an equal width
 * 				of each column, "normal" for a column width which depends on
 * 			    the items. One can also specify the used widths directly with
 * 				a comma separated list of integers, e.g.
 * 				<pre>
 * 					columns: 2;
 * 					columns-width: 15,5;
 * 				</pre>
 * 				</li>
 * 		<li><b>scroll-mode</b>: Either "smooth" (=default) or "normal".</li>
 * </ul>
 * <p>Copyright Enough Software 2004 - 2007 - 2009</p>

 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FakeContainerCustomItem extends FakeCustomItem {
	/** constant for normal scrolling (0) */
	public static final int SCROLL_DEFAULT = 0;
	/** constant for smooth scrolling (1) */
	public static final int SCROLL_SMOOTH = 1;
	
	protected ArrayList itemsList;
	//protected Item[] items;
	protected boolean autoFocusEnabled;
	protected int autoFocusIndex;
	protected Style itemStyle;
	protected Item focusedItem;
	/** the index of the currently focused item - please use only for reading, not for setting, unless you know what you are doing */
	public int focusedIndex = -1;
	protected boolean enableScrolling;
	//#if polish.Container.allowCycling != false
		/** specifies whether this container is allowed to cycle to the beginning when the last item has been reached */
		public boolean allowCycling = true;
	//#else
		//#	public boolean allowCycling = false;
	//#endif
	protected int yOffset;
	protected int targetYOffset;
	private int focusedTopMargin;
	//#if polish.css.view-type || polish.css.columns
		//#define tmp.supportViewType 
		protected ContainerView containerView;
	//#endif
	//#ifdef polish.css.scroll-mode
		protected boolean scrollSmooth = true;
	//#endif
	//#if polish.css.expand-items
		protected boolean isExpandItems;
	//#endif
	//#ifdef polish.hasPointerEvents
		/** vertical pointer position when it was pressed the last time */ 
		protected int lastPointerPressY;
	//#endif
	//#if polish.css.focused-style-first
		protected Style focusedStyleFirst;
	//#endif
	//#if polish.css.focused-style-last
		protected Style focusedStyleLast;
	//#endif
	private boolean isScrollRequired;
	/** The height available for scrolling, ignore when set to -1 */
	protected int scrollHeight = -1;
	private Item[] containerItems;
	private boolean showCommandsHasBeenCalled;
	private Item scrollItem;
	protected Style plainStyle;
	private int availableContentWidth;

	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public FakeContainerCustomItem( boolean focusFirstElement ) {
		this( null, focusFirstElement, null, -1 );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public FakeContainerCustomItem(boolean focusFirstElement, Style style) {
		this( null, focusFirstElement, style, -1  );
	}

	/**
	 * Creates a new empty container.
	 * 
	 * @param label the label of this container
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 * @param height the vertical space available for this container, set to -1 when scrolling should not be activated
	 * @see #setScrollHeight( int ) 
	 */
	public FakeContainerCustomItem(String label, boolean focusFirstElement, Style style, int height ) {
		super( label, LAYOUT_DEFAULT, INTERACTIVE, style );
		this.itemsList = new ArrayList();
		this.autoFocusEnabled = focusFirstElement;
		this.layout |= Item.LAYOUT_NEWLINE_BEFORE;
		setScrollHeight( height );
	}
	
	/**
	 * Sets the height available for scrolling of this item.
	 * 
	 * @param height available height for this item including label, padding, margin and border, -1 when scrolling should not be done.
	 */
	public void setScrollHeight( int height ) {
		this.scrollHeight = height;
		this.enableScrolling = (height != -1);
		Item item = this.scrollItem != null ? this.scrollItem : this.focusedItem;
		if (this.isInitialized && this.enableScrolling && item != null) {
			//#debug
			System.out.println("setScrollHeight(): scrolling to item=" + item + " with y=" + item.relativeY + ", height=" + height);
			scroll( 0, item, true);
			this.isScrollRequired = false;
		}
	}
	
	/**
	 * Returns the available height for scrolling eiter from this container or from it's parent container.
	 * Note that the height available for this container might differ from the returned value.
	 * 
	 * @return the available vertical space or -1 when it is not known.
	 * @see #getContentScrollHeight()
	 */
	public int getScrollHeight() {
		if (this.scrollHeight == -1 && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollHeight();
		} else {
			return this.scrollHeight;
		}
	}
	
	/**
	 * Returns the available height for scrolling eiter from this container or from it's parent container.
	 * 
	 * @return the available vertical space for this container or -1 when it is not known.
	 * @see #getContentScrollHeight()
	 */
	public int getRelativeScrollHeight() {
		if (this.scrollHeight == -1 && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollHeight() - this.relativeY;
		} else {
			return this.scrollHeight;
		}
	}

	
	/**
	 * Retrieves the available height available for the content of this container
	 *  
	 * @return the available vertical space minus paddings/margins etc or -1 when it is not known.
	 * @see #getScrollHeight()
	 */
	int getContentScrollHeight() {
		return getScrollHeight() - (this.contentY + 0 + this.paddingBottom + this.marginBottom ); 
	}
	
	/**
	 * Adds an StringItem with the given text to this container.
	 * 
	 * @param text the text
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add(String text)
	{
		add(new StringItem(null,text));
	}
	
	/**
	 * Adds an StringItem with the given text to this container.
	 * 
	 * @param text the text
	 * @param textAddStyle the style for the text
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add(String text,Style textAddStyle)
	{
		add(new StringItem(null,text),textAddStyle);
	}

	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item ) {
		synchronized (this.itemsList) {
			item.relativeY =  0;
			item.internalX = Item.NO_POSITION_SET;
			this.itemsList.add( item );
			this.isInitialized = false;
			if (this.parent != null) {
				this.parent.isInitialized = false;
			}
			if (this.isShown) {
				item.showNotify();
			}
		}
		repaint();
	}
	

	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @param itemAddStyle the style for the item
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item, Style itemAddStyle ) {
		add( item );
		if (itemAddStyle != null) {
			item.setStyle( itemAddStyle );
		}
	}

	/**
	 * Inserts the given item at the defined position.
	 * Any following elements are shifted one position to the back.
	 * 
	 * @param index the position at which the element should be inserted, 
	 * 					 use 0 when the element should be inserted in the front of this list.
	 * @param item the item which should be inserted
	 * @throws IllegalArgumentException when the given item is null
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public void add( int index, Item item ) {
		synchronized (this.itemsList) {
			item.relativeY = 0;
			item.internalX = NO_POSITION_SET;
			this.itemsList.add( index, item );
			if (index <= this.focusedIndex) {
				this.focusedIndex++;
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex = this.focusedIndex;
					}
				//#endif
			}
			requestInit();
			// set following items to relativeY=0, so that they will be scrolled correctly:
			for (int i= index + 1; i < this.itemsList.size(); i++ ) {
				Item followingItem = (Item) this.itemsList.get(i);
				followingItem.relativeY = 0;
			}
			if (this.isShown) {
				item.showNotify();
			}
		}
		repaint();
	}
	
	//#if polish.LibraryBuild
	public void add( javax.microedition.lcdui.Item item ) {
		// ignore
	}
	public void add( int index, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	/**
	 * Replaces an item
	 * @param index the index
	 * @param item the item to be added
	 */
	public void set( int index, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif
	
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item ) {
		return set( index, item, null );
	}
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @param itemStyle the new style for the item
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item, Style itemStyle ) {
		//#debug
		System.out.println("Container: setting item " + index + " " + item.toString() );
		if (itemStyle != null) {
			item.setStyle(itemStyle);
		}
		Item last = (Item) this.itemsList.set( index, item );
		if (index == this.focusedIndex) {
			last.defocus(this.itemStyle);
			if ( item.appearanceMode != PLAIN ) {
				if (this.isFocused) {
					focusChild( index, item, 0 , true);
				} else {
					this.focusedItem = item;
				}
			} else {
				focusChild( -1 );
			}
		}
		if (this.focusedIndex == -1 || index <= this.focusedIndex ) {
			int offset = getScrollYOffset() + last.itemHeight;
			if (offset > 0) {
				offset = 0;
			}
			setScrollYOffset(offset);
		}
		requestInit();
		// set following items to relativeY=0, so that they will be scrolled correctly:
		for (int i= index + 1; i < this.itemsList.size(); i++ ) {
			Item followingItem = (Item) this.itemsList.get(i);
			followingItem.relativeY = 0;
		}
		requestInit();
		repaint();
		return last;
	}
	
	/**
	 * Returns the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item get( int index ) {
		return (Item) this.itemsList.get( index );
	}
	
	/**
	 * Removes the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item remove( int index ) {
		Item removedItem = null;
		synchronized (this.itemsList) {
			removedItem = (Item) this.itemsList.remove(index);
			if (removedItem == this.scrollItem) {
				this.scrollItem = null;
			}
			//#debug
			System.out.println("Container: removing item " + index + " " + removedItem.toString()  );
			// adjust y-positions of following items:
			//this.items = null;
			Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
			int removedItemHeight = removedItem.itemHeight + this.paddingVertical;
			//#if tmp.supportViewType
				if (this.containerView == null) {
			//#endif
					for (int i = index; i < myItems.length; i++) {
						Item item = myItems[i];
						item.relativeY -= removedItemHeight;
					}
			//#if tmp.supportViewType
				}
			//#endif
			// check if the currenlty focused item has been removed:
			if (index == this.focusedIndex) {
				this.focusedItem = null;
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex = -1;
						this.containerView.focusedItem = null;
					}
				//#endif
				// remove any item commands:
				Screen scr = getScreen();
				if (scr != null) {
					scr.removeItemCommands(removedItem);
				}
				// focus the first possible item:
				if (index >= myItems.length) {
					index = myItems.length - 1;
				}
				if (index != -1) { 
					Item item = myItems[ index ];
					if (item.appearanceMode != PLAIN) {
						focusChild( index, item, Canvas.DOWN, true );
					} else {
						focusClosestItem(index, myItems);
					}
				} else {
					this.autoFocusEnabled = true;
					this.autoFocusIndex = 0;
				}
			} else if (index < this.focusedIndex) {
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex--;
					} else {
				//#endif
						int offset = getScrollYOffset() + removedItemHeight;
						//System.out.println("new container offset: from " + this.yOffset + " to " + (offset > 0 ? 0 : offset));
						setScrollYOffset( offset > 0 ? 0 : offset, false );
				//#if tmp.supportViewType
					}
				//#endif
				this.focusedIndex--;
			}
			this.isInitialized = false;
			if (this.parent != null) {
				this.parent.isInitialized = false;
			}
			if (this.isShown) {
				removedItem.hideNotify();
			}
		}
		repaint();
		return removedItem;
	}
	
	/**
	 * Focuses the next focussable item starting at the specified index + 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItemAbove( int index) {
		//#debug
		System.out.println("focusClosestItemAbove(" + index + ")");
		Item[] myItems = getItems();
		Item newFocusedItem = null;
		int newFocusedIndex = -1;
		for (int i = index -1; i >= 0; i--) {
			Item item = myItems[i];
			if (item.appearanceMode != PLAIN) {
				newFocusedIndex = i;
				newFocusedItem = item;
				break;
			}
		}
		if (newFocusedItem == null) {
			for (int i = index + 1; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.appearanceMode != PLAIN) {
					newFocusedIndex = i;
					newFocusedItem = item;
					break;
				}
			}			
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (newFocusedIndex < index) {
				direction = Canvas.UP;
			}
			focusChild( newFocusedIndex, newFocusedItem, direction, true );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
	}

	/**
	 * Focuses the next focussable item starting at the specified index +/- 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItem( int index) {
		return focusClosestItem( index, getItems() );
	}

	/**
	 * Focuses the next focussable item starting at the specified index +/- 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @param myItems the items that should be used for the search
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	protected boolean focusClosestItem( int index, Item[] myItems ) {
		//#debug
		System.out.println("focusClosestItem(" + index + ")");
		int i = 1;
		Item newFocusedItem = null;
		Item item;
		boolean continueFocus = true;
		while (continueFocus) {
			continueFocus = false;
			int testIndex = index + i;
			if (testIndex < myItems.length) {
				item = myItems[ testIndex ];
				if (item.appearanceMode != Item.PLAIN) {
					newFocusedItem = item;
					i = testIndex;
					break;
				}
				continueFocus = true;
			}
			testIndex = index - i;
			if (testIndex >= 0) {
				item = myItems[ testIndex ];
				if (item.appearanceMode != Item.PLAIN) {
					i = testIndex;
					newFocusedItem = item;
					break;
				}
				continueFocus = true;
			}
			i++;
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (i < index) {
				direction = Canvas.UP;
			}
			focusChild( i, newFocusedItem, direction, true );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
	}
	
	/**
	 * Removes the given item.
	 * 
	 * @param item the item which should be removed.
	 * @return true when the item was found in this list.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public boolean remove( Item item ) {
		int index = this.itemsList.indexOf(item);
		if (index != -1) {
			remove( index );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all items from this container.
	 */
	public void clear() {
		//System.out.println("CLEARING CONTAINER " + this);
		synchronized (this.itemsList) {
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			//System.out.println("clearing container - focusedItem=" + this.focusedItem + ", isFocused="  + this.isFocused + ", focusedIndex=" + this.focusedIndex + ",  size=" + this.size() + ", itemStyle=" + this.itemStyle );
			this.scrollItem = null;
			if (this.isShown) {
				Object[] myItems = this.itemsList.getInternalArray();
				for (int i = 0; i < myItems.length; i++) {
					Item item = (Item) myItems[i];
					if (item == null) {
						break;
					}
					item.hideNotify();
				}
			}
			this.itemsList.clear();
			this.containerItems = new Item[0];
			//this.items = new Item[0];
			if (this.focusedIndex != -1) {
				this.autoFocusEnabled = this.isFocused;
				//#if polish.Container.clearResetsFocus != false
					this.autoFocusIndex = 0;
				//#else
					this.autoFocusIndex = this.focusedIndex;
				//#endif			
				this.focusedIndex = -1;
				if (this.focusedItem != null) {
					if (this.itemStyle != null) {
						//System.out.println("Container.clear(): defocusing current item " + this.focusedItem);
						this.focusedItem.defocus(this.itemStyle);
					} 
					if (this.focusedItem.commands != null) {
						Screen scr = getScreen();
						if (scr != null) {
							scr.removeItemCommands(this.focusedItem);
						}
					}
				}
				this.focusedItem = null;
			}
			this.yOffset = 0;
			this.targetYOffset = 0;
			if (this.internalX != NO_POSITION_SET) {
				this.internalX = NO_POSITION_SET;
				this.internalY = 0;
			}
				// adjust scrolling:
				if ( this.isFocused && this.parent instanceof Container ) {
					Container parentContainer = (Container) this.parent;
					int scrollOffset = - parentContainer.getScrollYOffset();
					if (scrollOffset > this.relativeY) {
						int diff = scrollOffset - this.relativeY;
						parentContainer.setScrollYOffset( diff - scrollOffset,  false );
					}
				}
			//}
			this.contentHeight = 0;
			this.contentWidth = 0;
			this.itemHeight = this.marginTop + this.paddingTop + this.paddingBottom + this.marginBottom;
			this.itemWidth = this.marginLeft + this.paddingLeft + this.paddingRight + this.marginRight;
			if (this.isInitialized) {
				this.isInitialized = false;
				//this.yBottom = this.yTop = 0;
				repaint();
			}
		}
	}
	
	/**
	 * Retrieves the number of items stored in this container.
	 * 
	 * @return The number of items stored in this container.
	 */
	public int size() {
		return this.itemsList.size();
	}
	
	/**
	 * Retrieves all items which this container holds.
	 * The items might not have been intialised.
	 * 
	 * @return an array of all items, can be empty but not null.
	 */
	public Item[] getItems() {
		if (!this.isInitialized || this.containerItems == null) {
			this.containerItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		}
		return this.containerItems;
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item. The first item has the index 0, 
	 * 		when -1 is given, the focus will be removed altogether 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focusable.
	 */
	public boolean focusChild(int index) {
		if (index == -1) {
			this.focusedIndex = -1;
			Item item = this.focusedItem; 
			if (item != null && this.itemStyle != null && item.isFocused) {
				item.defocus( this.itemStyle );
			}
			this.focusedItem = null;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			return true;
		}
		if (!this.isFocused) {
			this.autoFocusEnabled = true;
		}
		Item item = (Item) this.itemsList.get(index );
		if (item.appearanceMode != Item.PLAIN) {
			int direction = 0;
			if (this.isFocused) {
				if (this.focusedIndex == -1) {
					// nothing
				} else if (this.focusedIndex < index ) {
					direction = Canvas.DOWN;
				} else if (this.focusedIndex > index) {
					direction = Canvas.UP;
				}
			
			}
			focusChild( index, item, direction, true );			
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the focus to the given item.
	 * 
	 * @param index the position
	 * @param item the item which should be focused
	 * @param direction the direction, either Canvas.DOWN, Canvas.RIGHT, Canvas.UP, Canvas.LEFT or 0.
	 */
	public void focusChild( int index, Item item, int direction, boolean force ) {
		//#debug
		System.out.println("Container (" + this + "): Focusing item " + index + " (" + item + "), isInitialized=" + this.isInitialized + ", autoFocusEnabled=" + this.autoFocusEnabled );
		//System.out.println("focus: yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset + ", enableScrolling=" + this.enableScrolling + ", isInitialized=" + this.isInitialized );
		
		if (!this.isInitialized && this.autoFocusEnabled) {
			// setting the index for automatically focusing the appropriate item
			// during the initialisation:
			//#debug
			System.out.println("Container: Setting autofocus-index to " + index );
			this.autoFocusIndex = index;
		} 
		//#if polish.blackberry
			Display.getInstance().notifyFocusSet( item );
		//#endif
		
		if (index == this.focusedIndex && item.isFocused && item == this.focusedItem) {
			//#debug
			System.out.println("Container: ignoring focusing of item " + index );
			// ignore the focusing of the same element:
			return;
		}
		// indicating if either the former focusedItem or the new focusedItem has changed it's size or it's layout by losing/gaining the focus, 
		// of course this can only work if this container is already initialized:
		boolean isReinitializationRequired = false;
		// first defocus the last focused item:
		if (this.focusedItem != null) {
			Item fItem = this.focusedItem;
			int wBefore = fItem.itemWidth;
			int hBefore = fItem.itemHeight;
			int layoutBefore = fItem.layout;
			if (this.itemStyle != null) {
				fItem.defocus(this.itemStyle);
			} else {
				//#debug error
				System.out.println("Container: Unable to defocus item - no previous style found.");
				fItem.defocus( StyleSheet.defaultStyle );
			}
			if (this.isInitialized) {
			}
		}
		int wBefore = item.itemWidth;
		int hBefore = item.itemHeight;
		int layoutBefore = item.layout;
		Style newStyle = getFocusedStyle( index, item);
		boolean isDownwards = (direction == Canvas.DOWN) || (direction == Canvas.RIGHT) || (direction == 0 &&  index > this.focusedIndex);
		int previousIndex = this.focusedIndex; // need to determine whether the user has scrolled from the bottom to the top
		this.focusedIndex = index;
		this.focusedItem = item;
		//#if tmp.supportViewType
			if ( this.containerView != null ) {
				this.itemStyle =  this.containerView.focusItem( index, item, direction, newStyle );
			} else {
		//#endif
				this.itemStyle = item.focus( newStyle, direction );
		//#if tmp.supportViewType
			} 
		//#endif
		//#ifdef polish.debug.error
			if (this.itemStyle == null) {
				//#debug error 
				System.out.println("Container: Unable to retrieve style of item " + item.getClass().getName() );
			}
		//#endif
		//System.out.println("focus - still initialzed=" + this.isInitialized + " for " + this);
		if  (this.isInitialized) {
			// this container has been initialised already,
			// so the dimensions are known.
			//System.out.println("focus: contentWidth=" + this.contentWidth + ", of container " + this);

			if (item.internalX != NO_POSITION_SET) {
				this.internalX =  item.relativeX + item.contentX + item.internalX;
				this.internalY = item.relativeY + item.contentY + item.internalY;
				this.internalWidth = item.internalWidth;
				this.internalHeight = item.internalHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): internal area found in item " + item + ": setting internalY=" + this.internalY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight + ", item.isInitialized=" + item.isInitialized + ", item.isStyleInitialized=" + item.isStyleInitialised);
			} else {
				this.internalX = item.relativeX;
				this.internalY = item.relativeY;
				this.internalWidth = item.itemWidth;
				this.internalHeight = item.itemHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): NO internal area found in item " + item + ": setting internalY=" + this.internalY + ", internalHeight=" + this.internalHeight + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight + ", getScrollHeight()=" + getScrollHeight());
			}
			if (getScrollHeight() != -1) {	
				// Now adjust the scrolling:			
				Item nextItem;
				if ( isDownwards && index < this.itemsList.size() - 1 ) {
					nextItem = (Item) this.itemsList.get( index + 1 );
					//#debug
					System.out.println("Focusing downwards, nextItem.relativY = [" + nextItem.relativeY + "], focusedItem.relativeY=[" + item.relativeY + "], this.yOffset=" + this.yOffset + ", this.targetYOffset=" + this.targetYOffset);
				} else if ( !isDownwards && index > 0 ) {
					nextItem = (Item) this.itemsList.get( index - 1 );
					//#debug
					System.out.println("Focusing upwards, nextItem.yTopPos = " + nextItem.relativeY + ", focusedItem.relativeY=" + item.relativeY );
				} else {
					//#debug
					System.out.println("Focusing last or first item.");
					nextItem = item;
				}
				
				if ( this.enableScrolling && ((index == 0) || (isDownwards && (index < previousIndex) || (previousIndex == -1))) ) {
					// either the first item or the first selectable item has been focused, so scroll to the very top:
					//#ifdef polish.css.scroll-mode
						if (!this.scrollSmooth) {
							this.yOffset = 0;
						} else {
					//#endif
							this.targetYOffset = 0;
					//#ifdef polish.css.scroll-mode
						}
					//#endif
				} else {
					int itemYTop = isDownwards ? item.relativeY : nextItem.relativeY;
					int itemYBottom = isDownwards ? nextItem.relativeY + nextItem.itemHeight : item.relativeY + item.itemHeight;
                    int availHeight = getRelativeScrollHeight();
                    int height = itemYBottom - itemYTop;
                    if (height > availHeight) {
                        height = availHeight - 5;
                        if (!isDownwards) {
                            itemYTop += (itemYBottom - itemYTop) - height;
                        }
                    }
					scroll( direction, this.relativeX, itemYTop, item.internalWidth, height, force);
				}
			}
		} else if (getScrollHeight() != -1) { // if (this.enableScrolling) {
			//#debug
			System.out.println("focus: postpone scrolling to initContent() for " + this + ", item " + item);
			this.isScrollRequired = true;
		}
		if (this.isInitialized) {
			this.isInitialized = !isReinitializationRequired;
		}
	}
	
	/**
	 * Retrieves the best matching focus style for the given item
	 * @param index the index of the item
	 * @param item the item
	 * @return the matching focus style
	 */
	protected Style getFocusedStyle(int index, Item item)
	{
		if (item.style != null && !item.isStyleInitialised) {
			item.setStyle(  item.style );
		}
		Style newStyle = item.focusedStyle;
		//#if polish.css.focused-style-first
			if (index == 0 && this.focusedStyleFirst != null) {
				newStyle = this.focusedStyleFirst;
			}
		//#endif
		//#if polish.css.focused-style-last
			if (this.focusedStyleLast != null  && index == this.itemsList.size() - 1) {
				newStyle = this.focusedStyleLast;
			}
		//#endif
		if (newStyle == null) {
			newStyle = item.getFocusedStyle();
		}
		return newStyle;
	}

	/**
	 * Scrolls this container so that the (internal) area of the given item is best seen.
	 * This is used when a GUI even has been consumed by the currently focused item.
	 * The call is fowarded to scroll( direction, x, y, w, h ).
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param item the item for which the scrolling should be adjusted
	 * @return true when the container was scrolled
	 */
	public boolean scroll(int direction, Item item, boolean force) {
		//#debug
		System.out.println("scroll: scrolling for item " + item  + ", item.internalX=" + item.internalX +", relativeInternalY=" + ( item.relativeY + item.contentY + item.internalY ) + ", relativeY=" + item.relativeY + ", contentY=" + item.contentY + ", internalY=" + item.internalY);
		if (item.internalX != NO_POSITION_SET 
				&& ( (item.itemHeight > getScrollHeight()) || ((item.internalY + item.internalHeight) > item.contentHeight ) ) ) 
		{
			// use internal position of item for scrolling:
			//System.out.println("using internal area for scrolling");
			int relativeInternalX = item.relativeX + item.contentX + item.internalX;
			int relativeInternalY = item.relativeY + item.contentY + item.internalY;
			return scroll(  direction, relativeInternalX, relativeInternalY, item.internalWidth, item.internalHeight, force );
		} else {
			if (!this.isInitialized && item.relativeY == 0) {
				// defer scrolling to init at a later stage:
				//System.out.println( this + ": setting scrollItem to " + item);
				this.scrollItem = item;
				return true;
			} else {				
				// use item dimensions for scrolling:
				//System.out.println("use item area for scrolling");
				return scroll(  direction, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, force );
			}
		}
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is forwarded to a parent container when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 * @return true when the scroll request changed the internal scroll offsets
	 */
	protected boolean scroll( int direction, int x, int y, int width, int height, boolean force ) {
		if (!this.enableScrolling) {
			if (this.parent instanceof Container) {
				x += this.contentX + this.relativeX;
				y += this.contentY + this.relativeY;
				//#debug
				System.out.println("Forwarding scroll request to parent now with y=" + y);
				return ((Container)this.parent).scroll(direction, x, y, width, height, force );
			}
			return false;
		}
		//#debug
		System.out.println("scroll: direction=" + direction + ", y=" + y + ", availableHeight=" + this.scrollHeight +  ", height=" +  height + ", focusedIndex=" + this.focusedIndex + ", yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset +", numberOfItems=" + this.itemsList.size() );
		if ( height == 0) {
			return false;
		}
		// assume scrolling down when the direction is not known:
		boolean isDownwards = (direction == Canvas.DOWN || direction == Canvas.RIGHT ||  direction == 0);
		boolean isUpwards = (direction == Canvas.UP );
		
		int currentYOffset = this.targetYOffset; // yOffset starts at 0 and grows to -contentHeight + lastItem.itemHeight
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				currentYOffset = this.yOffset;
			}
		//#endif

		int verticalSpace = this.scrollHeight - (this.contentY + this.marginBottom + this.paddingBottom + 0); // the available height for this container
		int yTopAdjust = 0;
		Screen scr = this.screen;
		if ( y + height + currentYOffset + yTopAdjust > verticalSpace ) {
			// the area is too low, so scroll down (= increase the negative yOffset):
			//currentYOffset += verticalSpace - (y + height + currentYOffset + yTopAdjust);
			currentYOffset = verticalSpace - (y + height + yTopAdjust);
			//#debug
			System.out.println("scroll: item too low: verticalSpace=" + verticalSpace + "  y=" + y + ", height=" + height + ", yTopAdjust=" + yTopAdjust + ", yOffset=" + currentYOffset);
			// check if the top of the area is still visible when scrolling downwards:
			if ( isDownwards && y + currentYOffset < 0  && height < verticalSpace) {
				currentYOffset -= (y + currentYOffset);
			}
		} else if ( y + currentYOffset < 0 ) {
			// area is too high, so scroll up (= decrease the negative yOffset):
			currentYOffset -=  y + currentYOffset; 
			//#debug
			System.out.println("scroll: item too high: , y=" + y + ", target=" + currentYOffset ); //+ ", focusedTopMargin=" + this.focusedTopMargin );
			// check if the bottom of the area is still visible when scrolling upwards:
			if (isUpwards && y + height + currentYOffset > verticalSpace  && height < verticalSpace) {
				currentYOffset += verticalSpace - (y + height + currentYOffset);
			}

		} else {
			//#debug
			System.out.println("scroll: do nothing");
			return false;
		}
		setScrollYOffset(currentYOffset, true);
		return true;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		//#debug
		System.out.println("Container: intialising content for " + this + ": autofocus=" + this.autoFocusEnabled + ", autoFocusIndex=" + this.autoFocusIndex + ", firstLineWidth=" + firstLineWidth + ", lineWidth=" + availWidth + ", size=" + this.itemsList.size() );
		this.availableContentWidth = firstLineWidth;
		//#if polish.css.focused-style
			if (this.focusedStyle != null) {
				this.focusedTopMargin = this.focusedStyle.getMarginTop(availWidth) + this.focusedStyle.getPaddingTop(availWidth);
				if (this.focusedStyle.border != null) {
				}
				if (this.focusedStyle.background != null) {
					this.focusedTopMargin += this.focusedStyle.background.borderWidth;
				}
			}
		//#endif
		synchronized (this.itemsList) {
			int myContentWidth = 0;
			int myContentHeight = 0;
			Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
			this.containerItems = myItems;
			if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
				this.autoFocusIndex = 0;
			}
			Item ancestor = this.parent;
			while (this.allowCycling && ancestor != null) {
				if ( (ancestor instanceof Container)  && ((Container)ancestor).getNumberOfInteractiveItems()>1 ) {
					this.allowCycling = false;
					break;
				}
				ancestor = ancestor.parent;
			}
			//#if tmp.supportViewType
				if (this.containerView != null) {
					// additional initialization is necessary when a view is used for this container:
					boolean requireScrolling = this.isScrollRequired && this.isFocused;
	//				System.out.println("ABOUT TO CALL INIT CONTENT - focusedIndex of Container=" + this.focusedIndex);
					this.appearanceMode = this.containerView.appearanceMode;
					if (this.isFocused && this.autoFocusEnabled) {
						//#debug
						System.out.println("Container/View: autofocusing element starting at " + this.autoFocusIndex);
						if (this.autoFocusIndex >= 0 && this.appearanceMode != Item.PLAIN) {
							for (int i = this.autoFocusIndex; i < myItems.length; i++) {
								Item item = myItems[i];
								if (item.appearanceMode != Item.PLAIN) {
									// make sure that the item has applied it's own style first (not needed since it has been initialized by the container view already):
									//item.getItemHeight( firstLineWidth, lineWidth );
									// now focus the item:
									this.autoFocusEnabled = false;
									requireScrolling = (this.autoFocusIndex != 0);
//									int heightBeforeFocus = item.itemHeight;
									focusChild( i, item, 0, true );
									// outcommented on 2008-07-09 because this results in a wrong
									// available width for items with subsequent wrong getAbsoluteX() coordinates
//									int availableWidth = item.itemWidth;
//									if (availableWidth < this.minimumWidth) {
//										availableWidth = this.minimumWidth;
//									}
//									if (item.getItemHeight( availableWidth, availableWidth ) > heightBeforeFocus) {
//										item.isInitialized = false;
//										this.containerView.initContent( this, firstLineWidth, lineWidth);	
//									}
									this.isScrollRequired = this.isScrollRequired && requireScrolling; // override setting in focus()
									//this.containerView.focusedIndex = i; is done within focus(i, item, 0) already
									//this.containerView.focusedItem = item;
									//System.out.println("autofocus: found item " + i );
									break;
								}							
							}
						// when deactivating the auto focus the container won't initialize correctly after it has
						// been cleared and items are added subsequently one after another (e.g. like within the Browser).
	//					} else {
	//						this.autoFocusEnabled = false;
						}
					}
					this.contentWidth = this.containerView.contentWidth;
					this.contentHeight = this.containerView.contentHeight;

					if (requireScrolling && this.focusedItem != null) {
						//#debug
						System.out.println("initContent(): scrolling autofocused or scroll-required item for view, focused=" + this.focusedItem);
						Item item = this.focusedItem;
						scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
					}
					else if (this.scrollItem != null) {
						//System.out.println("initContent(): scrolling scrollItem=" + this.scrollItem);
						boolean  scrolled = scroll( 0, this.scrollItem, true );
						if (scrolled) {
							this.scrollItem = null;
						}
					}
					return;
				}
			//#endif
		
			boolean isLayoutShrink = (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK;
			boolean hasFocusableItem = false;
			int myContentStartX = Integer.MAX_VALUE;
			int myContentEndX = Integer.MIN_VALUE;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				//System.out.println("initalising " + item.getClass().getName() + ":" + i);
				int width = item.getItemWidth( availWidth, availWidth, availHeight );
				int height = item.itemHeight; // no need to call getItemHeight() since the item is now initialised...
				// now the item should have a style, so it can be safely focused
				// without loosing the style information:
				//String toString = item.toString();
				//System.out.println("init of item " + i + ": height=" + height + " of item " + toString.substring( 19, Math.min(120, toString.length() )  ));
				//if (item.isInvisible && height != 0) {
				//	System.out.println("*** item.height != 0 even though it is INVISIBLE - isInitialized=" + item.isInitialized );
				//}
				if (item.appearanceMode != PLAIN) {
					hasFocusableItem = true;
				}
				if (this.isFocused && this.autoFocusEnabled  && (i >= this.autoFocusIndex ) && (item.appearanceMode != Item.PLAIN)) {
					this.autoFocusEnabled = false;
					//System.out.println("Container.initContent: auto-focusing " + i + ": " + item );
					focusChild( i, item, 0, true );
					this.isScrollRequired = (this.isScrollRequired || hasFocusableItem) && (this.autoFocusIndex != 0); // override setting in focus()
					height = item.getItemHeight(availWidth, availWidth, availHeight);
					if (!isLayoutShrink) {
						width = item.itemWidth;  // no need to call getItemWidth() since the item is now initialised...
					} else {
						width = 0;
					}
					if (this.enableScrolling && this.autoFocusIndex != 0) {
						//#debug
						System.out.println("initContent(): scrolling autofocused item, autofocus-index=" + this.autoFocusIndex + ", i=" + i  );
						scroll( 0, 0, myContentHeight, width, height, true );
					}
				} else if (i == this.focusedIndex) {
					if (isLayoutShrink) {
						width = 0;
					}
					if (this.isScrollRequired) {
						//#debug
						System.out.println("initContent(): scroll is required - scrolling to y=" + myContentHeight + ", height=" + height);
						scroll( 0, 0, myContentHeight, width, height, true );
						this.isScrollRequired = false;
	//				} else if (item.internalX != NO_POSITION_SET ) {
	//					// ensure that lines of textfields etc are within the visible area:
	//					scroll(0, item );
					}
				} 
				if (width > myContentWidth) {
					myContentWidth = width; 
				}
				item.relativeY = myContentHeight;
				if  ( (item.layout & LAYOUT_CENTER) == LAYOUT_CENTER) {
					item.relativeX = (availWidth - width) / 2;
				} else if ( (item.layout & LAYOUT_RIGHT) == LAYOUT_RIGHT) {
					item.relativeX = (availWidth - width);
				} else {
					item.relativeX = 0;
				}
				if (item.relativeX < myContentStartX ) {
					myContentStartX = item.relativeX;
				}
				if (item.relativeX + width > myContentEndX ) {
					myContentEndX = item.relativeX + width;
				}
				myContentHeight += height != 0 ? height + this.paddingVertical : 0;
				//System.out.println("item.yTopPos=" + item.yTopPos);
			} // cycling through all items
			
			if (myContentEndX - myContentStartX > myContentWidth) {
				// this can happen when there are different layouts like left and right within the same container:
				myContentWidth = myContentEndX - myContentStartX;
			}
			if (this.minimumWidth != null && this.minimumWidth.getValue(firstLineWidth) > myContentWidth) {
				myContentWidth = this.minimumWidth.getValue(firstLineWidth);
			}
			//#if polish.css.expand-items
				if (this.isExpandItems) {
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = myItems[i];
						if (!item.isLayoutExpand && item.itemWidth < myContentWidth) {
							item.isLayoutExpand = true;
							item.init(myContentWidth, myContentWidth, availHeight);
							item.isLayoutExpand = false;
						}
					}
				}
			//#endif
			if (!hasFocusableItem) {
				this.appearanceMode = PLAIN;
			} else {
				this.appearanceMode = INTERACTIVE;
				if (this.focusedItem != null) {
					Item item = this.focusedItem;
					if (item.internalX != NO_POSITION_SET) {
						this.internalX =  item.relativeX + item.contentX + item.internalX;
						this.internalY = item.relativeY + item.contentY + item.internalY;
						this.internalWidth = item.internalWidth;
						this.internalHeight = item.internalHeight;
						//#debug
						System.out.println("Container (" + getClass().getName() + "): internal area found in item " + item + ": setting internalY=" + this.internalY + ", item.relativeY=" + item.relativeY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight + ", item.isInitialized=" + item.isInitialized);
					} else {
						this.internalX = item.relativeX;
						this.internalY = item.relativeY;
						this.internalWidth = item.itemWidth;
						this.internalHeight = item.itemHeight;
						//#debug
						System.out.println("Container (" + getClass().getName() + "): NO internal area found in item " + item + ": setting internalY=" + this.internalY + ", internalHeight=" + this.internalHeight + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight + ", this.availableHeight=" + this.scrollHeight);
					}
					if (isLayoutShrink) {
						//System.out.println("container has shrinking layout and contains focuse item " + item);
						item.isInitialized = false;
						boolean doExpand = item.isLayoutExpand;
						int width;
						if (doExpand) {
							item.isLayoutExpand = false;
							width = item.getItemWidth( availWidth, availWidth, availHeight );
							item.isInitialized = false;
							item.isLayoutExpand = true;
						} else {
							width = item.itemWidth;
						}
						if (width > myContentWidth) {
							myContentWidth = width;
						}
						if ( this.minimumWidth != null && myContentWidth < this.minimumWidth.getValue(firstLineWidth) ) {
							myContentWidth = this.minimumWidth.getValue(firstLineWidth);
						}
						//myContentHeight += item.getItemHeight( lineWidth, lineWidth );
					}
				}
			}
			if (this.scrollItem != null) {
				boolean scrolled = scroll( 0, this.scrollItem, true );
				//System.out.println( this + ": scrolled scrollItem " + this.scrollItem + ": " + scrolled);
				if (scrolled) {
					this.scrollItem = null;
				}
			}
			this.contentHeight = myContentHeight;
			this.contentWidth = myContentWidth;
			//#debug
			System.out.println("initContent(): Container " + this + " has a content-width of " + this.contentWidth + ", parent=" + this.parent);
		}
	}
	

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		//System.out.println("paintContent, size=" + this.itemsList.size() + ", isInitialized=" + this.isInitialized);
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		// adjust vertical start for scrolling:
		//#if polish.debug.debug
			if (this.enableScrolling) {
//				g.setColor( 0xFFFF00 );
//				g.drawLine( leftBorder, y, rightBorder, y + getContentScrollHeight() );
//				g.drawLine( rightBorder, y, leftBorder, y  + + getContentScrollHeight() );
//				g.drawString( "" + this.availableHeight, x, y, Graphics.TOP | Graphics.LEFT );
				//#debug 
				System.out.println("Container: drawing " + getClass().getName() + " with yOffset=" + this.yOffset );
			}
		//#endif
		boolean setClipping = ( this.enableScrolling && (this.yOffset != 0 || this.itemHeight > this.scrollHeight) ); //( this.yOffset != 0 && (this.marginTop != 0 || this.paddingTop != 0) );
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (setClipping) {
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			//g.clipRect(clipX, y - this.paddingTop, clipWidth, clipHeight - ((y - this.paddingTop) - clipY) );
			g.clipRect(clipX, y, clipWidth, clipHeight - (y - clipY) );
		}
		//x = leftBorder;
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				//#debug
				System.out.println("forwarding paint call to " + this.containerView );
				if (setClipping) {
					g.setClip(clipX, clipY, clipWidth, clipHeight);
				}
			} else {
		//#endif
			Item[] myItems = this.containerItems;
//			if (!(this.isLayoutCenter || this.isLayoutRight)) {
//				// adjust the right border:
//				rightBorder = leftBorder + this.contentWidth;
//			}
			int startY = g.getClipY();
			int endY = startY + g.getClipHeight();
			Item focItem = this.focusedItem;
			int focIndex = this.focusedIndex;
			//int originalY = y;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
				// since after every item a line break will be done. Use view-type: midp2; to place several items into a single row.
				int itemY = y + item.relativeY;
				if (i != focIndex &&  itemY + item.itemHeight >= startY && itemY < endY ){
					//item.paint(x, y, leftBorder, rightBorder, g);
					item.paint(x + item.relativeX, itemY, leftBorder, rightBorder, g);
				}
//				if (item.itemHeight != 0) {
//					y += item.itemHeight + this.paddingVertical;
//				}
			}
			boolean paintFocusedItemOutside = false;
			if (focItem != null) {
				paintFocusedItemOutside = setClipping && (focItem.internalX != NO_POSITION_SET);
				if (!paintFocusedItemOutside) {
					focItem.paint(x + focItem.relativeX, y + focItem.relativeY, leftBorder, rightBorder, g);
				}
			}
	
			if (setClipping) {
				g.setClip(clipX, clipY, clipWidth, clipHeight);
			}
			
			// paint the currently focused item outside of the clipping area when it has an internal area. This is 
			// for example useful for popup items that extend the actual container area.
			if (paintFocusedItemOutside) {
				//System.out.println("Painting focusedItem " + this.focusedItem + " with width=" + this.focusedItem.itemWidth + " and with increased colwidth of " + (focusedRightBorder - focusedX)  );
				focItem.paint(x + focItem.relativeX, y + focItem.relativeY, leftBorder, rightBorder, g);
			}
		//#ifdef tmp.supportViewType
			}
		//#endif

	}
	
	//#if tmp.supportViewType
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#paintBackgroundAndBorder(int, int, int, int, javax.microedition.lcdui.Graphics)
		 */
		protected void paintBackgroundAndBorder(int x, int y, int width, int height, Graphics g) {
			if (this.containerView == null) {
				super.paintBackgroundAndBorder(x, y, width, height, g);
			} else {
				// this is only necessary since ContainerViews are integrated differently from
				// normal ItemViews - we should consider abonding this approach!
				if ( this.background != null ) {
					int bWidth = 0;
					if ( this.border != null ) {
						x += bWidth;
						y += bWidth;
						width -= (bWidth << 1);
						height -= (bWidth << 1);
					}
					this.containerView.paintBackground( this.background, x, y, width, height, g );
					if (this.border != null) {
						x -= bWidth;
						y -= bWidth;
						width += (bWidth << 1);
						height += (bWidth << 1);				
					}
				}
				if ( this.border != null ) {
					this.containerView.paintBorder( this.border, x, y, width, height, g );
				}
			}
		}
	//#endif

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "container";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		//#debug
		System.out.println("handleKeyPressed( " + keyCode + ", " + gameAction + " ) for " + this);
		
		Item item = this.focusedItem;
		if (item != null) {
			if (!item.isInitialized) {
				item.init( this.contentWidth, this.contentWidth, this.contentHeight );
			} else if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
				int startY = getScrollYOffset() + item.relativeY + item.contentY + item.internalY;
				if ( (
					(startY < 0  && gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) 
					||  (startY + item.internalHeight > this.scrollHeight  && gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
					)
					&& (scroll(gameAction, item, false))
				){
					return true;
				}
			}
			int scrollOffset = getScrollYOffset();
			if ( item.handleKeyPressed(keyCode, gameAction) ) {
				//if (item.internalX != NO_POSITION_SET) {
					if (this.enableScrolling) {
						if (getScrollYOffset() == scrollOffset) {
							//#debug
							System.out.println("scrolling focused item that has handled key pressed, item=" + item + ", item.internalY=" + item.internalY);
							scroll(gameAction, item, false);
						}
					} else  {
						if (item.internalX != NO_POSITION_SET) { // && (item.itemHeight > getScrollHeight()  || (item.contentY + item.internalY + item.internalHeight > item.itemHeight) ) ) {
							// adjust internal settings for root container:
							this.internalX = item.relativeX + item.contentX + item.internalX;
							this.internalY = item.relativeY + item.contentY + item.internalY;
							this.internalWidth = item.internalWidth;
							this.internalHeight = item.internalHeight;
							//#debug
							System.out.println(this + ": Adjusted internal area by internal area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
						} else {
							this.internalX = item.relativeX;
							this.internalY = item.relativeY;
							this.internalWidth = item.itemWidth;
							this.internalHeight = item.itemHeight;
							//#debug
							System.out.println(this + ": Adjusted internal area by full area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
						}
					}
				//}
				//#debug
				System.out.println("Container(" + this + "): handleKeyPressed consumed by item " + item.getClass().getName() + "/" + item );
				
				return true;
			}
		}
		
		return handleNavigate(keyCode, gameAction);
	}

	/**
	 * Handles a keyPressed or keyRepeated event for navigating in the container.
	 *  
	 * @param keyCode the code of the keypress/keyrepeat event
	 * @param gameAction the associated game action 
	 * @return true when the key was handled
	 */
	protected boolean handleNavigate(int keyCode, int gameAction) {
		// now allow a navigation within the container:
		boolean processed = false;
		int offset = getRelativeScrollYOffset();
		int availableScrollHeight = getScrollHeight();
		Item focItem = this.focusedItem;
		int y = 0;
		int h = 0;
		if (focItem != null && availableScrollHeight != -1) {
			if (focItem.internalX == NO_POSITION_SET || (focItem.relativeY + focItem.contentY + focItem.internalY + focItem.internalHeight < availableScrollHeight)) {
				y = focItem.relativeY;
				h = focItem.itemHeight;
				//System.out.println("normal item has focus: y=" + y + ", h=" + h + ", item=" + focItem);
			} else {
				y = focItem.relativeY + focItem.contentY + focItem.internalY;
				h = focItem.internalHeight;
				//System.out.println("internal item has focus: y=" + y + ", h=" + h + ", item=" + focItem);
			}
			//System.out.println("offset=" + offset + ", scrollHeight=" + availableScrollHeight + ", offset + y + h=" + (offset + y + h) + ", focusedItem=" + focItem);
		}
		if (
			//#if polish.blackberry && !polish.hasTrackballEvents
				(gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) ||
			//#endif
			   (gameAction == Canvas.DOWN   && keyCode != Canvas.KEY_NUM8)) 
		{
			if (focItem != null 
					&& (availableScrollHeight != -1 && offset + y + h > availableScrollHeight) 
			) {
				//System.out.println("offset=" + offset + ", foc.relativeY=" + this.focusedItem.relativeY + ", foc.height=" + this.focusedItem.itemHeight + ", available=" + this.availableHeight);
				// keep the focus do scroll downwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling down: keeping focus, focusedIndex=" + this.focusedIndex + ", y=" + y + ", h=" + h + ", offset=" + offset );
			} else {
				//#ifdef tmp.supportViewType
					if (this.containerView != null) {
						 processed = this.containerView.handleKeyPressed(keyCode, gameAction);
					} else {
				//#endif
						processed = shiftFocus( true, 0 );
				//#ifdef tmp.supportViewType
					}
				//#endif
			}
			//#debug
			System.out.println("Container(" + this + "): forward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex + ", enableScrolling=" + this.enableScrolling);
			if ((!processed)  
					&& ( 
						(availableScrollHeight != -1 && offset + y + h > availableScrollHeight)
						|| (this.enableScrolling && offset + this.itemHeight > availableScrollHeight)
						)
			) {
				int containerHeight = this.contentHeight;
				int scrollHeight = this.getContentScrollHeight();
				int scrollOffset = this.getScrollYOffset();
				
				// scroll downwards:
				int difference =
				//#if polish.Container.ScrollDelta:defined
					//#=  ${polish.Container.ScrollDelta};
				//#else
					((containerHeight + scrollOffset) - scrollHeight);
				
					if(difference > (scrollHeight / 2))
					{
						difference = scrollHeight / 2;
					}
				//#endif
					
				offset = getScrollYOffset() - difference;
				setScrollYOffset( offset, true );
				processed = true;
				//#debug
				System.out.println("Down/Right: Decreasing (target)YOffset to " + offset);	
			}
		} else if ( 
				//#if polish.blackberry && !polish.hasTrackballEvents
					(gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) ||
				//#endif
				    (gameAction == Canvas.UP    && keyCode != Canvas.KEY_NUM2) ) 
		{
			if (focItem != null 
					&& availableScrollHeight != -1 
					&& offset + focItem.relativeY < 0 ) // this.focusedItem.yTopPos < this.yTop ) 
			{
				// keep the focus do scroll upwards:
				// #debug
				System.out.println("Container(" + this + "): scrolling up: keeping focus, relativeScrollOffset=" + offset + ", scrollHeight=" + availableScrollHeight +  ", focusedIndex=" + this.focusedIndex + ", focusedItem.relativeY=" + this.focusedItem.relativeY + ", this.availableHeight=" + this.scrollHeight + ", targetYOffset=" + this.targetYOffset);
			} else {
				//#ifdef tmp.supportViewType
					if (this.containerView != null) {
						 processed = this.containerView.handleKeyPressed(keyCode, gameAction);
					} else {
				//#endif
						processed = shiftFocus( false, 0 );
				//#ifdef tmp.supportViewType
					}
				//#endif
			}
			//#debug
			System.out.println("Container(" + this + "): upward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) 
					&& ( (this.enableScrolling && offset < 0)
					   || (availableScrollHeight != -1 &&  focItem != null && offset + focItem.relativeY < 0) )
			) {
				// scroll upwards:
				int difference =
				//#if polish.Container.ScrollDelta:defined
					//#= ${polish.Container.ScrollDelta};
				//#else
					getScreen() != null ? getScreen().contentHeight / 2 :  30;
				//#endif
				offset = getScrollYOffset() + difference;
				if (offset > 0) {
					offset = 0;
				}
				setScrollYOffset(offset, true);
				//#debug
				System.out.println("Up/Left: Increasing (target)YOffset to " + offset);	
				processed = true;
			}
		}
		//#ifdef tmp.supportViewType
			else if (this.containerView != null) 
			{
				processed = this.containerView.handleKeyPressed(keyCode, gameAction);
			}
		//#endif
		return processed;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		//#debug
		System.out.println("handleKeyReleased( " + keyCode + ", " + gameAction + " ) for " + this);
		if (this.focusedItem != null) {
			//int scrollOffset= getScrollYOffset();
			Item item = this.focusedItem;
			if ( item.handleKeyReleased( keyCode, gameAction ) ) {
				if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
					scroll(gameAction, item, false);
				}
//				if (this.enableScrolling) {
//					if (getScrollYOffset() == scrollOffset) {
//						// #debug
//						System.out.println("scrolling focused item that has handled key pressed, item=" + item + ", item.internalY=" + item.internalY);
//						scroll(gameAction, item);
//					}
//				} else  {
//					if (item.itemHeight > getScrollHeight()  &&  item.internalX != NO_POSITION_SET) {
//						// adjust internal settings for root container:
//						this.internalX = item.relativeX + item.contentX + item.internalX;
//						this.internalY = item.relativeY + item.contentY + item.internalY;
//						this.internalWidth = item.internalWidth;
//						this.internalHeight = item.internalHeight;
//						// #debug
//						System.out.println(this + ": Adjusted internal area by internal area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
//					} else {
//						this.internalX = item.relativeX;
//						this.internalY = item.relativeY;
//						this.internalWidth = item.itemWidth;
//						this.internalHeight = item.itemHeight;
//						// #debug
//						System.out.println(this + ": Adjusted internal area by full area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
//					}
//				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyReleased consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return super.handleKeyReleased(keyCode, gameAction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		if (this.focusedItem != null) {
			Item item = this.focusedItem;
			if ( item.handleKeyRepeated( keyCode, gameAction ) ) {
				if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
					scroll(gameAction, item, false);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyRepeated consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return handleNavigate(keyCode, gameAction);
		// note: in previous versions a keyRepeat event was just re-asigned to a keyPressed event. However, this resulted
		// in non-logical behavior when an item wants to ignore keyRepeat events and only press "real" keyPressed events.
		// So now events are ignored by containers when they are ignored by their currently focused item...
		//return super.handleKeyRepeated(keyCode, gameAction);
	}

	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @return true when the focus could be moved to either the next or the previous item.
	 */
	private boolean shiftFocus(boolean forwardFocus, int steps ) {
		Item[] items = getItems();
		if ( items == null || items.length == 0) {
			//#debug
			System.out.println("shiftFocus fails: this.items==null");
			return false;
		}
		//System.out.println("|");
		Item focItem = this.focusedItem;
		//#if polish.css.colspan
			int i = this.focusedIndex;
			if (steps != 0) {
				//System.out.println("ShiftFocus: steps=" + steps + ", forward=" + forwardFocus);
				int doneSteps = 0;
				steps = Math.abs( steps ) + 1;
				Item item = items[i];
				while( doneSteps <= steps) {
					doneSteps += item.colSpan;
					if (doneSteps >= steps) {
						//System.out.println("bailing out at too many steps: focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
						break;
					}
					if (forwardFocus) {
						i++;
						if (i == items.length - 1 ) {
							i = items.length - 2;
							break;
						} else if (i == items.length) {
							i = items.length - 1;
							break;
						}
					} else {
						i--; 
						if (i < 0) {
							i = 1;
							break;
						}
					}
					item = items[i];
					//System.out.println("focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
				}
				if (doneSteps >= steps && item.colSpan != 1) {
					if (forwardFocus) {
						i--;
						if (i < 0) {
							i = items.length - 1;
						}
						//System.out.println("forward: Adjusting startIndex to " + i );
					} else {
						i = (i + 1) % items.length;
						//System.out.println("backward: Adjusting startIndex to " + i );
					}
				}
			}
		//#else			
			//# int i = this.focusedIndex + steps;
			if (i > items.length) {
				i = items.length - 2;
			}
			if (i < 0) {
				i = 1;
			}
		//#endif
		Item item = null;
		boolean allowCycle = this.allowCycling;
		if (allowCycle) {
				if (forwardFocus) {
					// when you scroll to the bottom and
					// there is still space, do
					// scroll first before cycling to the
					// first item:
					allowCycle = (getScrollYOffset() + this.itemHeight <= getScrollHeight() + 1);
					//System.out.println("allowCycle-calculation ( forward non-smoothScroll): yOffset=" + this.yOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.yOffset + this.itemHeight));
				} else {
					// when you scroll to the top and
					// there is still space, do
					// scroll first before cycling to the
					// last item:
					allowCycle = (getScrollYOffset() == 0);
				}						
		}
		//#debug
		System.out.println("shiftFocus of " + this + ": allowCycle(local)=" + allowCycle + ", allowCycle(global)=" + this.allowCycling + ", isFoward=" + forwardFocus + ", enableScrolling=" + this.enableScrolling + ", targetYOffset=" + this.targetYOffset + ", yOffset=" + this.yOffset + ", focusedIndex=" + this.focusedIndex + ", start=" + i );
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= items.length) {
					if (allowCycle) {
						allowCycle = false;
						i = 0;
						//#debug
						System.out.println("allowCycle: Restarting at the beginning");
					} else {
						break;
					}
				}
			} else {
				i--;
				if (i < 0) {
					if (allowCycle) {
						allowCycle = false;
						i = items.length - 1;
						//#debug
						System.out.println("allowCycle: Restarting at the end");
					} else {
						break;
					}
				}
			}
			item = items[i];
			if (item.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (item == null || item.appearanceMode == Item.PLAIN || item == focItem) {
			//#debug
			System.out.println("got original focused item: " + (item == focItem) + ", item==null:" + (item == null) + ", mode==PLAIN:" + (item == null ? false:(item.appearanceMode == PLAIN)) );
			
			return false;
		}
		int direction = Canvas.UP;
		if (forwardFocus) {
			direction = Canvas.DOWN;
		}
		focusChild(i, item, direction, true );
		return true;
	}

	/**
	 * Retrieves the index of the item which is currently focused.
	 * 
	 * @return the index of the focused item, -1 when none is focused.
	 */
	public int getFocusedIndex() {
		return this.focusedIndex;
	}
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when there is no focusable item in this container.
	 */
	public Item getFocusedItem() {
		return this.focusedItem;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.debug.debug
		if (this.parent == null) {
			//#debug
			System.out.println("Container.setStyle without boolean parameter for container " + toString() );
		}
		//#endif
		setStyleWithBackground(style, false);
	}
	
	/**
	 * Sets the style of this container.
	 * 
	 * @param style the style
	 * @param ignoreBackground when true is given, the background and border-settings
	 * 		  will be ignored.
	 */
	public void setStyleWithBackground( Style style, boolean ignoreBackground) {
		super.setStyle(style);
		if (ignoreBackground) {
			this.background = null;
			this.border = null;
			this.marginTop = 0;
			this.marginBottom = 0;
			this.marginLeft = 0;
			this.marginRight = 0;
		}
		//#if polish.css.focused-style-first
			Style firstFocusStyleObj = (Style) style.getObjectProperty("focused-style-first");
			if (firstFocusStyleObj != null) {
				this.focusedStyleFirst = firstFocusStyleObj;
			}
		//#endif
		//#if polish.css.focused-style-last
			Style lastFocusStyleObj = (Style) style.getObjectProperty("focused-style-last");
			if (lastFocusStyleObj != null) {
				this.focusedStyleLast = lastFocusStyleObj;
			}
		//#endif
		//#ifdef polish.css.view-type
//			ContainerView viewType =  (ContainerView) style.getObjectProperty("view-type");
//			if (this instanceof ChoiceGroup) {
//				System.out.println("SET.STYLE / CHOICEGROUP: found view-type (1): " + (viewType != null) + " for " + this);
//			}
			if (this.view != null && this.view instanceof ContainerView) {
				ContainerView viewType = (ContainerView) this.view; // (ContainerView) style.getObjectProperty("view-type");
				this.containerView = viewType;
				this.view = null; // set to null so that this container can control the view completely. This is necessary for scrolling, for example.
				viewType.focusFirstElement = this.autoFocusEnabled;
				viewType.allowCycling = this.allowCycling;
			} else if (!this.preserveViewType) {
				this.containerView = null;
			}
		//#endif
		//#ifdef polish.css.columns
			if (this.containerView == null) {
				Integer columns = style.getIntProperty("columns");
				if (columns != null) {
					if (columns.intValue() > 1) {
						//System.out.println("Container: Using default container view for displaying table");
						this.containerView = new ContainerView();  
						this.containerView.focusFirstElement = this.autoFocusEnabled;
						this.containerView.allowCycling = this.allowCycling;
					}
				}
			}
		//#endif

		//#if polish.css.scroll-mode
			Integer scrollModeInt = style.getIntProperty("scroll-mode");
			if ( scrollModeInt != null ) {
				this.scrollSmooth = (scrollModeInt.intValue() == SCROLL_SMOOTH);
			}
		//#endif
		//#if polish.css.expand-items
			Boolean expandItemsBool = style.getBooleanProperty("expand-items");
			if (expandItemsBool != null) {
				this.isExpandItems = expandItemsBool.booleanValue();
			}
		//#endif
			
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.setStyle(style);
			}
		//#endif
	
		//#if polish.css.change-styles
			String changeStyles = style.getProperty("change-styles");
			if (changeStyles != null) {
				int splitPos = changeStyles.indexOf('>');
				if (splitPos != -1) {
					String oldStyle = changeStyles.substring(0, splitPos ).trim();
					String newStyle = changeStyles.substring(splitPos+1).trim();
					try {
						changeChildStyles(oldStyle, newStyle);
					} catch (Exception e) {
						//#debug error
						System.out.println("Unable to apply change-styles \"" + changeStyles + "\"" + e );
					}
				}
			}
		//#endif
	}
	
	
	
	//#ifdef tmp.supportViewType
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style, boolean)
		 */
		public void setStyle(Style style, boolean resetStyle)
		{
			super.setStyle(style, resetStyle);
			if (this.containerView != null) {
				this.containerView.setStyle(style, resetStyle);
			}
		}
	//#endif

	/**
	 * Changes the style of all children that are currently using the specified oldChildStyle with the given newChildStyle.
	 * 
	 * @param oldChildStyleName the name of the style of child items that should be exchanged
	 * @param newChildStyleName the name of the new style for child items that were using the specified oldChildStyle before
	 * @throws IllegalArgumentException if no corresponding newChildStyle could be found
	 * @see StyleSheet#getStyle(String)
	 */
	public void changeChildStyles( String oldChildStyleName, String newChildStyleName) {
		Style newChildStyle = StyleSheet.getStyle(newChildStyleName);
		if (newChildStyle ==  null) {
			throw new IllegalArgumentException("for " + newChildStyleName );
		}
		Style oldChildStyle = StyleSheet.getStyle(oldChildStyleName);
		changeChildStyles(oldChildStyle, newChildStyle);
	}
	
	/**
	 * Changes the style of all children that are currently using the specified oldChildStyle with the given newChildStyle.
	 * 
	 * @param oldChildStyle the style of child items that should be exchanged
	 * @param newChildStyle the new style for child items that were using the specified oldChildStyle before
	 * @throws IllegalArgumentException if newChildStyle is null
	 */
	public void changeChildStyles( Style oldChildStyle, Style newChildStyle) {
		if (newChildStyle == null) {
			throw new IllegalArgumentException();
		}
		Object[] children = this.itemsList.getInternalArray();
		for (int i = 0; i < children.length; i++)
		{
			Item child = (Item) children[i];
			if (child == null) {
				break;
			}
			if (child.style == oldChildStyle) {
				child.setStyle( newChildStyle );
			}
		}
	}

	/**
	 * Parses the given URL and includes the index of the item, when there is an "%INDEX%" within the given url.
	 * @param url the resource URL which might include the substring "%INDEX%"
	 * @param item the item to which the URL belongs to. The item must be 
	 * 		  included in this container.
	 * @return the URL in which the %INDEX% is substituted by the index of the
	 * 		   item in this container. The url "icon%INDEX%.png" is resolved
	 * 		   to "icon1.png" when the item is the second item in this container.
	 * @throws NullPointerException when the given url or item is null
	 */
	public String parseIndexUrl(String url, Item item) {
		int pos = url.indexOf("%INDEX%");
		if (pos != -1) {
			int index = this.itemsList.indexOf( item );
			//TODO rob check if valid, when url ends with %INDEX%
			url = url.substring(0, pos) + index + url.substring( pos + 7 );
		}
		return url;
	}
	/**
	 * Retrieves the position of the specified item.
	 * 
	 * @param item the item
	 * @return the position of the item, or -1 when it is not defined
	 */
	public int getPosition( Item item ) {
		return this.itemsList.indexOf( item );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusStyle, int direction ) {
		this.plainStyle = null;
		if ( this.itemsList.size() == 0) {
			return super.focus(focusStyle, direction );
		} else {
			focusStyle = getFocusedStyle();
			Style result = this.style;
			if ((focusStyle != StyleSheet.focusedStyle && focusStyle != null)  
				//#if polish.css.include-label
				|| this.includeLabel 
				//#endif
			) {
				result = super.focus( focusStyle, direction );
				this.plainStyle = result;
			}
			
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focus(focusStyle, direction);
					//this.isInitialised = false; not required
				}
			//#endif
			this.isFocused = true;
			int newFocusIndex = this.focusedIndex;
			
			//if (this.focusedIndex == -1) {
			//#if tmp.supportViewType
				if ( this.containerView == null || this.containerView.allowsAutoTraversal ) {
			//#endif
					Item[] myItems = getItems();
					if (this.autoFocusEnabled &&  this.autoFocusIndex < myItems.length) {
						//#debug
						System.out.println("focus(Style, direction): autofocusing " + this + ", focusedIndex=" + this.focusedIndex + ", autofocus=" + this.autoFocusIndex);
						newFocusIndex = this.autoFocusIndex;
						this.autoFocusEnabled = false;
					} else {
						// focus the first interactive item...
						if (direction == Canvas.UP || direction == Canvas.LEFT ) {
							//System.out.println("Container: direction UP with " + myItems.length + " items");
							for (int i = myItems.length; --i >= 0; ) {
								Item item = myItems[i];
								if (item.appearanceMode != PLAIN) {
									newFocusIndex = i;
									break;
								}
							}
						} else {
							//System.out.println("Container: direction DOWN");
							for (int i = 0; i < myItems.length; i++) {
								Item item = myItems[i];
								if (item.appearanceMode != PLAIN) {
									newFocusIndex = i;
									break;
								}
							}
						}
					}
				this.focusedIndex = newFocusIndex;
				if (newFocusIndex == -1) {
					//System.out.println("DID NOT FIND SUITEABLE ITEM");
					// this container has only non-focusable items!
					return super.focus( focusStyle, direction );
				}
			//}
			//#if tmp.supportViewType
				} else if (this.focusedIndex == -1) {
					Item[] myItems = getItems();
					//System.out.println("Container: direction DOWN through view type " + this.view);
					for (int i = 0; i < myItems.length; i++) {
						Item item = myItems[i];
						if (item.appearanceMode != PLAIN) {
							newFocusIndex = i;
							break;
						}
					}
					this.focusedIndex = newFocusIndex;
					if (newFocusIndex == -1) {
						//System.out.println("DID NOT FIND SUITEABLE ITEM");
						// this container has only non-focusable items!
						return super.focus( focusStyle, direction );
					}
				}
			//#endif
			Item item = (Item) this.itemsList.get( this.focusedIndex );
//			Style previousStyle = item.style;
//			if (previousStyle == null) {
//				previousStyle = StyleSheet.defaultStyle;
//			}
			this.showCommandsHasBeenCalled = false;
			focusChild( this.focusedIndex, item, direction, true );
			// item command handling is now done within showCommands and handleCommand
			if (!this.showCommandsHasBeenCalled && this.commands != null) {
				showCommands();
			}
//			if (item.commands == null && this.commands != null) {
//				Screen scr = getScreen();
//				if (scr != null) {
//					scr.setItemCommands(this);
//				}
//			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				if (this.label != null) {
					Style labStyle = (Style) focusStyle.getObjectProperty("label-style");
					if (labStyle != null) {
						this.labelStyle = this.label.style;
						this.label.setStyle( labStyle );
					}
				}
			//#endif
			return result;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	public void defocus(Style originalStyle) {
		if ( this.itemsList.size() == 0 || this.focusedIndex == -1 ) {
			super.defocus( originalStyle );
		} else {
			if (this.plainStyle != null) {
				super.defocus( this.plainStyle );
				if (originalStyle == null) {
					originalStyle = this.plainStyle;
				}
				this.plainStyle = null;
			}
			this.isFocused = false;
			Item item = this.focusedItem; //(Item) this.itemsList.get( this.focusedIndex );
			item.defocus( this.itemStyle );
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.defocus( originalStyle );
					this.isInitialized = false;
				}
			//#endif
			this.isFocused = false;
			// now remove any commands which are associated with this item:
			if (item.commands == null && this.commands != null) {
				Screen scr = getScreen();
				if (scr != null) {
				}
			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				Style tmpLabelStyle = null;
				if ( originalStyle != null) {
					tmpLabelStyle = (Style) originalStyle.getObjectProperty("label-style");
				}
				if (tmpLabelStyle == null) {
					tmpLabelStyle = StyleSheet.labelStyle;
				}
				if (this.label != null && tmpLabelStyle != null && this.label.style != tmpLabelStyle) {
					this.label.setStyle( tmpLabelStyle );
				}
			//#endif
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showCommands()
	 */
	public void showCommands() {
		this.showCommandsHasBeenCalled = true;
		super.showCommands();
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleCommand(javax.microedition.lcdui.Command)
	 */
	protected boolean handleCommand(Command cmd) {
		boolean handled = super.handleCommand(cmd);
		if (!handled && this.focusedItem != null) {
			return this.focusedItem.handleCommand(cmd);
		}
		return handled;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		// scroll the container:
		int target = this.targetYOffset;
		int current = this.yOffset;
		if (target != current	
		//#if polish.css.scroll-mode
			&& this.scrollSmooth
		//#endif
		) {
			if (this.scrollHeight != -1 && Math.abs(target - current) > this.scrollHeight) {
				// maximally scroll one page:
				if (current < target) {
					current = target - this.scrollHeight;
				} else {
					current = target + this.scrollHeight;
				}
			}
			int speed = (target - current) / 3;
			
			speed += target > current ? 1 : -1;
			current += speed;
			if ( ( speed > 0 && current > target) || (speed < 0 && current < target ) ) {
				current = target;
			}
			this.yOffset = current;
//			if (this.focusedItem != null && this.focusedItem.backgroundYOffset != 0) {
//				this.focusedItem.backgroundYOffset = (this.targetYOffset - this.yOffset);
//			}
			// # debug
			//System.out.println("animate(): adjusting yOffset to " + this.yOffset );
			int x = getAbsoluteX();
			int y = getAbsoluteY();
			int height = this.itemHeight;
			int width = this.itemWidth;
			Screen scr = getScreen();
			//#if polish.useScrollBar || polish.classes.ScrollBar:defined
				width += scr.getScrollBarWidth();
			//#endif
			if (this.scrollHeight > height) {
				x = scr.contentX;
				y = scr.contentY;
				height = scr.contentHeight;
				width = scr.contentWidth + scr.getScrollBarWidth();
			}
			repaintRegion.addRegion( x, y, width, height );
		}
		
		if (this.focusedItem != null) {
			this.focusedItem.animate(currentTime, repaintRegion);
		}
		
		//#ifdef tmp.supportViewType
			if ( this.containerView != null ) {
				this.containerView.animate(currentTime, repaintRegion);
			}
		//#endif
	}
	
	/**
	 * Called by the system to notify the item that it is now at least
	 * partially visible, when it previously had been completely invisible.
	 * The item may receive <code>paint()</code> calls after
	 * <code>showNotify()</code> has been called.
	 * 
	 * <p>The container implementation calls showNotify() on the embedded items.</p>
	 */
	protected void showNotify()
	{
		super.showNotify();
		if (this.style != null && !this.isStyleInitialised) {
			setStyle( this.style );
		}
		//#ifdef polish.useDynamicStyles
			else if (this.style == null) {
				initStyle();
			}
		//#else
			else if (this.style == null && !this.isStyleInitialised) {
				//#debug
				System.out.println("Setting default style for container " + this  );
				setStyle( StyleSheet.defaultStyle );
			}
		//#endif
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.showNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (item.style != null && !item.isStyleInitialised) {
				item.setStyle( item.style );
			}
			//#ifdef polish.useDynamicStyles
				else if (item.style == null) {
					initStyle();
				}
			//#else
				else if (item.style == null && !item.isStyleInitialised) {
					//#debug
					System.out.println("Setting default style for item " + item );
					item.setStyle( StyleSheet.defaultStyle );
				}
			//#endif
			item.showNotify();
		}
	}

	/**
	 * Called by the system to notify the item that it is now completely
	 * invisible, when it previously had been at least partially visible.  No
	 * further <code>paint()</code> calls will be made on this item
	 * until after a <code>showNotify()</code> has been called again.
	 * 
	 * <p>The container implementation calls hideNotify() on the embedded items.</p>
	 */
	protected void hideNotify()
	{
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.hideNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.hideNotify();
		}
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int relX, int relY) {
		//#debug
		System.out.println("Container.handlePointerPressed(" + relX + ", " + relY + ") for " + this );
		//System.out.println("Container.handlePointerPressed( x=" + x + ", y=" + y + "): adjustedY=" + (y - (this.yOffset  + this.marginTop + this.paddingTop )) );
		// an item within this container was selected:
		this.lastPointerPressY = relY;
		relY -= this.yOffset;
		relX -= this.contentX;
		relY -= this.contentY;
		//System.out.println("Container.handlePointerPressed: adjusted to (" + relX + ", " + relY + ") for " + this );
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			boolean processed = item.handlePointerPressed(relX - item.relativeX, relY - item.relativeY );
			if (processed) {
				//#debug
				System.out.println("pointer event at " + relX + "," + relY + " consumed by focusedItem.");
				return true;
			}
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerPressed(relX,relY) ) {
					return true;
				}
			}
		//#endif
		if (!isInItemArea(relX, relY) || (item != null && item.isInItemArea(relX - item.relativeX, relY - item.relativeY )) ) {
			//System.out.println("Container.handlePointerPressed(): out of range, relativeX=" + this.relativeX + ", relativeY="  + this.relativeY + ", contentHeight=" + this.contentHeight );
			return false;
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			//System.out.println( item + ".relativeX=" + item.relativeX + ", .relativeY=" + item.relativeY + ", pointer event relatively at " + itemRelX + ", " + itemRelY);
			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			//#debug
			System.out.println("Container.handlePointerPressed(" + relX + "," + relY + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
			// only focus the item when it has not been focused already:
			focusChild(i, item, 0, true);
			// let the item also handle the pointer-pressing event:
			item.handlePointerPressed( itemRelX , itemRelY );
			return true;			
		}
		return false;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/**
	 * Allows subclasses to check if a pointer release event is used for scrolling the container.
	 * This method can only be called when polish.hasPointerEvents is true.
	 * 
	 * @param relX the x position of the pointer pressing relative to this item's left position
	 * @param relY the y position of the pointer pressing relative to this item's top position
	 */
	protected boolean handlePointerScrollReleased(int relX, int relY) {
		int yDiff = relY - this.lastPointerPressY;
		int bottomY = Math.max( this.itemHeight, this.internalY + this.internalHeight );
		if (this.focusedItem != null && this.focusedItem.relativeY + this.focusedItem.backgroundHeight > bottomY) {
			bottomY = this.focusedItem.relativeY + this.focusedItem.backgroundHeight;
		}
		if ( this.enableScrolling 
				&& (this.itemHeight > this.scrollHeight || this.yOffset != 0)
				&& ((yDiff < -5 && this.yOffset + bottomY > this.scrollHeight) // scrolling downwards
					|| (yDiff > 5 && this.yOffset != 0) ) // scrolling upwards
			) 
		{
			int offset = this.yOffset + yDiff;
			if (offset > 0) {
				offset = 0;
			}
			//System.out.println("adjusting scrolloffset to " + offset);
			setScrollYOffset(offset, true);
			return true;
		}
		return false;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY) {
		//#debug
		System.out.println("Container.handlePointerReleased(" + relX + ", " + relY + ") for " + this );
		
		if (handlePointerScrollReleased(relX, relY)) {
			return true;
		}
		// an item within this container was selected:
		relY -= this.yOffset;
		relX -= this.contentX;
		relY -= this.contentY;
		//System.out.println("Container.handlePointerReleased: adjusted to (" + relX + ", " + relY + ") for " + this );
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			boolean processed = item.handlePointerReleased(relX - item.relativeX, relY - item.relativeY );
			if (processed) {
				//#debug
				System.out.println("pointer event at " + relX + "," + relY + " consumed by focusedItem.");
				return true;
			} else if ( item.isInItemArea(relX - item.relativeX, relY - item.relativeY )) {
				//#debug
				System.out.println("pointer event not handled by focused item but within that item's area");
				return false;
			}
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerReleased(relX,relY) ) {
					return true;
				}
			}
		//#endif
		if (!isInItemArea(relX, relY)) {
			//System.out.println("Container.handlePointerPressed(): out of range, relativeX=" + this.relativeX + ", relativeY="  + this.relativeY + ", contentHeight=" + this.contentHeight );
			return false;
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			//System.out.println( item + ".relativeX=" + item.relativeX + ", .relativeY=" + item.relativeY + ", pointer event relatively at " + itemRelX + ", " + itemRelY);
			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			//#debug
			System.out.println("Container.handlePointerReleased(" + relX + "," + relY + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
			// only focus the item when it has not been focused already:
			//focus(i, item, 0);
			// let the item also handle the pointer-pressing event:
			item.handlePointerReleased( itemRelX , itemRelY );
			return true;			
		}
		return false;
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAt(int, int)
	 */
	public Item getItemAt(int relX, int relY) {
		relY -= this.yOffset;
		relX -= this.contentX;
		relY -= this.contentY;
		Item item = this.focusedItem;
		if (item != null) {
			int itemRelX = relX - item.relativeX;
			int itemRelY = relY - item.relativeY;
//			if (this.label != null) {
//				System.out.println("itemRelY=" + itemRelY + " of item " + item + ", parent=" + this );
//			}
			if (item.isInItemArea(itemRelX, itemRelY)) {
				return item.getItemAt(itemRelX, itemRelY);
			}
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			if ( i == this.focusedIndex || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			return item.getItemAt(itemRelX, itemRelY);			
		}
		relY += this.yOffset;
		relX += this.contentX;
		relY += this.contentY;
		return super.getItemAt(relX, relY);
	}
	
	/**
	 * Moves the focus away from the specified item.
	 * 
	 * @param item the item that currently has the focus
	 */
	public void requestDefocus( Item item ) {
		if (item == this.focusedItem) {
			boolean success = shiftFocus(true, 1);
			if (!success) {
				defocus(this.itemStyle);
			}
		}
	}

	/**
	 * Requests the initialization of this container and all of its children items.
	 */
	public void requestFullInit() {
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item) this.itemsList.get(i);
			item.isInitialized = false;
			if (item instanceof Container) {
				((Container)item).requestFullInit();
			}
		}
		requestInit();
	}

	/**
	 * Retrieves the vertical scrolling offset of this item.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different.
	 */
	public int getScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollYOffset();
		}
		int offset = this.targetYOffset;
		//#ifdef polish.css.scroll-mode
			if (!this.scrollSmooth) {
				offset = this.yOffset;
			}
		//#endif
		return offset;
	}
	
	/**
	 * Retrieves the vertical scrolling offset of this item relative to the top most container.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different.
	 */
	public int getRelativeScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getRelativeScrollYOffset() + this.relativeY;
		}
		int offset = this.targetYOffset;
		//#ifdef polish.css.scroll-mode
			if (!this.scrollSmooth) {
				offset = this.yOffset;
			}
		//#endif
		return offset;
	}
	
	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 */
	public void setScrollYOffset( int offset) {
		setScrollYOffset( offset, false );
	}

	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 * @param smooth scroll to this new offset smooth if allowed
	 */
	public void setScrollYOffset( int offset, boolean smooth) {
		//#debug
		System.out.println("Setting scrollYOffset to " + offset );
		if (!this.enableScrolling && this.parent instanceof Container) {
			((Container)this.parent).setScrollYOffset(offset, smooth);
			return;
		}
		if (!smooth  
		//#ifdef polish.css.scroll-mode
			|| !this.scrollSmooth
		//#endif
		) {
			this.yOffset = offset;			
		}
		this.targetYOffset = offset;
	}

	/**
	 * Retrieves the index of the specified item.
	 * 
	 * @param item the item
	 * @return the index of the item; -1 when the item is not part of this container
	 */
	public int indexOf(Item item) {
		Object[] myItems = this.itemsList.getInternalArray();
		for (int i = 0; i < myItems.length; i++) {
			Object object = myItems[i];
			if (object == null) {
				break;
			}
			if (object == item) {
				return i;
			}
		}
		return -1;
	}
	
	//#if (polish.debug.error || polish.keepToString) && polish.debug.container.includeChildren
	/**
	 * Generates a String representation of this item.
	 * This method is only implemented when the logging framework is active or the preprocessing variable 
	 * "polish.keepToString" is set to true.
	 * @return a String representation of this item.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( super.toString() ).append( ": { ");
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			//#if polish.supportInvisibleItems || polish.css.visible
				if (item.isInvisible) {
					buffer.append( i ).append(":invis./plain:" + ( item.appearanceMode == PLAIN ) + "=[").append( item.toString() ).append("]");
				} else {
					buffer.append( i ).append("=").append( item.toString() );
				}
			//#else
				buffer.append( i ).append("=").append( item.toString() );
			//#endif
			if (i != myItems.length - 1 ) {
				buffer.append(", ");
			}
		}
		buffer.append( " }");
		return buffer.toString();
	}
	//#endif

	/**
	 * Sets a list of items for this container.
	 * Use this direct access only when you know what you are doing.
	 * 
	 * @param itemsList the list of items to set
	 */
	public void setItemsList(ArrayList itemsList) {
		//System.out.println("Container.setItemsList");
		clear();
		if (this.isFocused) {
			//System.out.println("enabling auto focus for index=" + this.focusedIndex);
			this.autoFocusEnabled = true;
			this.autoFocusIndex = this.focusedIndex;
		}
		this.focusedIndex = -1;
		this.focusedItem = null;
		if (this.enableScrolling) {
			setScrollYOffset(0, false);
		}
		this.itemsList = itemsList;
		this.containerItems = null;
		Object[] myItems = this.itemsList.getInternalArray();
		for (int i = 0; i < myItems.length; i++) {
			Item item = (Item) myItems[i];
			if (item == null) {
				break;
			}
			if (this.isShown) {
				item.showNotify();
			}
		}
		requestInit();
	}
	

	/**
	 * Calculates the number of interactive items included in this container.
	 * @return the number between 0 and size()
	 */
	public int getNumberOfInteractiveItems()
	{
		int number = 0;
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			if (item.appearanceMode != PLAIN) {
				number++;
			}
		}
		return number;
	}

	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		super.releaseResources();
		Item[] items = getItems();
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			item.releaseResources();
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.releaseResources();
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#fireEvent(java.lang.String, java.lang.Object)
	 */
	public void fireEvent(String eventName, Object eventData)
	{
		super.fireEvent(eventName, eventData);
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			item.fireEvent(eventName, eventData);
		}
	}
	
	


//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
