//#condition polish.usePolishGui
/*
 * Created on 15-Aug-2007 at 00:41:51.
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;


/**
 * <p>Shows  the available items of a Container in a horizontal list.</p>
 * <p>Apply this view by specifying "view-type: horizontal;" in your polish.css file.</p>
 *
 * <p>Copyright Enough Software 2007 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HorizontalContainerView extends ContainerView {
	
	
	protected int targetXOffset;
	private boolean allowRoundTrip;
	private boolean isExpandRightLayout;
	//#if polish.css.horizontalview-align-heights
		private boolean isAlignHeights;
	//#endif
	//#if polish.css.show-text-in-title
		private boolean isShowTextInTitle;
		private String[] labels;
	//#endif
	private boolean isClippingRequired;
	private boolean isPointerPressedHandled;
	private int pointerPressedX;
	private int completeContentWidth;

	/**
	 * Creates a new view
	 */
	public HorizontalContainerView() {
		super();
		this.allowsAutoTraversal = false;
		this.isHorizontal = true;
		this.isVertical = false;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		int target = this.targetXOffset;
		int current = this.xOffset;
		if (target != current) {
			int diff = Math.abs( target - current );
			int delta = diff / 3;
			if (delta < 2) {
				delta = 2;
			}
			if (target < current) {
				current -= delta;
				if (current < target) {
					current = target;
				}
			} else {
				current += delta;
				if (current > target) {
					current = target;
				}
			}
			this.xOffset = current;
			addFullRepaintRegion(this.parentItem, repaintRegion);
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth,
			int availWidth, int availHeight) 
	{
		Container parent = (Container) parentItm;
		//#debug
		System.out.println("Initalizing HorizontalContainerView with focusedIndex=" + parent.getFocusedIndex() + " for parent " + parent);
		
		int selectedItemIndex = parent.getFocusedIndex();
		int maxHeight = 0;
		int completeWidth = 0;
		Item[] items = parent.getItems();
		//#if polish.css.show-text-in-title
			if (this.isShowTextInTitle && (this.labels == null || this.labels.length != items.length)) {
				this.labels = new String[ items.length ];
			}
		//#endif

		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			//#if polish.css.show-text-in-title
				if (this.isShowTextInTitle) {
					String text = item.getLabel();
					if (text != null) {
						this.labels[i] = text;
						item.setLabel( null );
					} else if ( item instanceof IconItem) {
						IconItem iconItem = (IconItem) item;
						text = iconItem.getText();
						if (text != null) {						
							this.labels[i] = text;
							iconItem.setTextVisible(false);
						}
					}
				}
			//#endif
			int itemHeight = item.getItemHeight(availWidth, availWidth, availHeight);
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			boolean isLast = i == items.length - 1;
			if ( isLast && item.isLayoutRight() && (completeWidth  + item.itemWidth < availWidth) ) {
				completeWidth = availWidth - item.itemWidth;
			}
			int startX = completeWidth;
			item.relativeX = completeWidth;
			item.relativeY = 0;
			completeWidth += itemWidth + (isLast ? 0 :  this.paddingHorizontal);
			if ( i == selectedItemIndex) {
				if ( startX + this.xOffset < 0 ) {
					this.targetXOffset = -startX; 
				} else if ( completeWidth + this.targetXOffset > availWidth ) {
					this.targetXOffset = availWidth - completeWidth;
				}
				//System.out.println("initContent: xOffset=" + xOffset);
				this.focusedItem = item;
			}
			if (item.appearanceMode != Item.PLAIN) {
				this.appearanceMode = Item.INTERACTIVE;
			}
		}
		//#if polish.css.horizontalview-align-heights
			if (this.isAlignHeights) {
				for (int i = 0; i < items.length; i++) {
					Item item = items[i];
					item.setItemHeight( maxHeight );
				}
			}
		//#endif
		this.contentHeight = maxHeight;
		if (completeWidth > availWidth) {
			this.isClippingRequired = true;
			this.contentWidth = availWidth;
		} else {
			this.isClippingRequired = false;
			this.contentWidth = completeWidth;
		}
		this.completeContentWidth = completeWidth;
		
    	if (
    			((parent.getLayout() & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT)
    		&&	((parent.getLayout() & Item.LAYOUT_EXPAND) == Item.LAYOUT_EXPAND)
    		) 
    	{
    		this.isExpandRightLayout = true;
    	} else {
    		this.isExpandRightLayout = false;
    	}
    	//System.out.println("init of horizontal: " + this.contentWidth + "x" + this.contentHeight);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		//#if polish.css.show-text-in-title
		if (this.isShowTextInTitle) {
			Screen scr = getScreen();
			if (scr != null) {
				scr.setTitle( this.labels[ focIndex ] );
			}
		}
		//#endif
		if (this.isClippingRequired && item != null) {
			if (this.targetXOffset + item.relativeX < 0) {
				this.targetXOffset = -item.relativeX;
			} else if (this.targetXOffset + item.relativeX + item.itemWidth > this.contentWidth) {
				this.targetXOffset = this.contentWidth - item.relativeX - item.itemWidth;
			}
		}
		return super.focusItem(focIndex, item, direction, focStyle);
	}


	protected void setStyle(Style style) {
		super.setStyle(style);

		//#ifdef polish.css.horizontalview-roundtrip
			Boolean allowRoundTripBool = style.getBooleanProperty("horizontalview-roundtrip");
			if (allowRoundTripBool != null) {
				this.allowRoundTrip = allowRoundTripBool.booleanValue();
			}
		//#endif
		//#if polish.css.horizontalview-align-heights
			Boolean alignHeightsBools = style.getBooleanProperty("horizontalview-align-heights");
			if (alignHeightsBools != null) {
				this.isAlignHeights = alignHeightsBools.booleanValue();
			}
		//#endif
		//#if polish.css.show-text-in-title
			Boolean showTextInTitleBool = style.getBooleanProperty("show-text-in-title");
			if (showTextInTitleBool != null) {
				this.isShowTextInTitle = showTextInTitleBool.booleanValue();
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#debug
		System.out.println("paint " + this + " at " + x + ", " + y + ", with xOffset " + this.xOffset + ", clipping req=" + this.isClippingRequired + ", expandRightLayout=" + this.isExpandRightLayout);
		
    	if (this.isExpandRightLayout) {
    		x = rightBorder - this.contentWidth;
    	}
    	if (this.isClippingRequired) {
    		g.clipRect( x, y, this.contentWidth + 1, this.contentHeight + 1 );
    	}
		x += this.xOffset;
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX,
				clipY, clipWidth, clipHeight, g);
		if (this.isClippingRequired) {
			g.setClip(clipX, clipY, clipWidth, clipHeight);
		}
	}


	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int)
	 */
	public boolean handlePointerDragged(int x, int y) {
		if (this.isPointerPressedHandled && this.isClippingRequired) {
			int offset = this.targetXOffset + (x - this.pointerPressedX);
			if (offset + this.completeContentWidth < this.contentWidth) {
				offset = this.contentWidth - this.completeContentWidth;
			} else if (offset > 0) {
				offset = 0;
			}
			this.xOffset = offset;
			this.targetXOffset = offset;
			this.pointerPressedX = x;
			return true;
		}
		return super.handlePointerDragged(x, y);
	}
	//#endif


	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y) {
		if (this.isClippingRequired && this.parentContainer.isInItemArea(x, y)) {
			this.isPointerPressedHandled = true;
			this.pointerPressedX = x;
		}
		return super.handlePointerPressed(x, y);
	}
	//#endif


//	//#ifdef polish.hasPointerEvents
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.ItemView#handlePointerReleased(int, int)
//	 */
//	public boolean handlePointerReleased(int x, int y) {
//		// TODO Auto-generated method stub
//		return super.handlePointerReleased(x, y);
//	}
//	//#endif

	
	
	
}
