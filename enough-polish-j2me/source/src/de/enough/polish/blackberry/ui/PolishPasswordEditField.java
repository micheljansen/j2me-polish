//#condition polish.usePolishGui && polish.blackberry

/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry.ui;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import net.rim.device.api.ui.component.PasswordEditField;

public class PolishPasswordEditField extends PasswordEditField implements PolishTextField {


        private boolean isFocused;
        public boolean processKeyEvents = true;
		private int fontColor;
		//#if ${ version(polish.JavaPlatform, BlackBerry) } >= ${version(4.6)}
			private BackgroundWrapper backgroundWrapper;
		//#endif

        public PolishPasswordEditField(String label, String text, int numChars, long style) {
                super(label, text, numChars, style);
        }

        public void focusAdd( boolean draw ) {
                //System.out.println("EditField: focusAdd (" + getText() + ")");
                super.focusAdd( draw );
                this.isFocused = true;
        }

        public void focusRemove() {
                //System.out.println("EditField: focusRemove (" + getText() + ")");
                super.focusRemove();
                this.isFocused = false;
        }

        public void layout( int width, int height) {
        	if (height < 0 || width < 0) {
        		//#debug info
        		System.out.println("ignoring invalid layout params: width=" + width + ", height=" + height );                
        		return;
            }
            super.layout( width, height );
        }
        
        public void paint( net.rim.device.api.ui.Graphics g ) {
        	if (this.isFocused && !StyleSheet.currentScreen.isMenuOpened()) {
        		g.setColor( this.fontColor );
        		super.paint( g );
        	}
        }
        
		public void setFont(Font font, int textColor) {
			try {
				super.setFont( font.font );
			} catch (IllegalStateException e) {
				//#debug
				System.out.println("Layout error: " + e );
			}
			this.fontColor = textColor;
		}

		public void setFont(javax.microedition.lcdui.Font font, int textColor) {
			// never used anyhow, since javax.microedition.lcdui.Font is mapped to Font
		}

		public void setPaintPosition(int x, int y ) {
            this.isFocused = true;
            super.setPosition(x, y);
		}
		
		public int getInsertPositionOffset() {
			return getCursorPosition();
		}

		
		public void setStyle(Style style) {
			Font font = (Font)(Object)style.getFont();
			if (font == null) {
				font = Font.getDefaultFont();
			}
			try {
				super.setFont( font.font );
			} catch (IllegalStateException e) {
				//#debug error
				System.out.println("Layout error: " + e );
			}
			this.fontColor = style.getFontColor();
			//#if ${ version(polish.JavaPlatform, BlackBerry) } >= ${version(4.6)}
				if (style.background != null) {
					if (this.backgroundWrapper == null) {
						this.backgroundWrapper = new BackgroundWrapper( style.background );
						try {
							setBackground(this.backgroundWrapper);
						} catch (Exception e) {
							//#debug error
							System.out.println("Unable to set background" + e);
						}
					} else {
						this.backgroundWrapper.setBackground(style.background);
					}
				}
			//#endif
		}

}
