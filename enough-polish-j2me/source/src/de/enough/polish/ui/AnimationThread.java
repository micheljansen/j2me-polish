//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:52:57.
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


import de.enough.polish.ui.Displayable;

import de.enough.polish.event.EventListener;
import de.enough.polish.event.EventManager;
import de.enough.polish.util.ArrayList;

/**
 * <p>Is used to animate Screens, Backgrounds and Items.</p>
 * <p>
 * 	You can specify the animation interval in milliseconds in the variables section of
 *  your build.xml script.
 *  Example:
 *  <pre>
 *  <variables>
 *		<variable name="polish.animationInterval" value="200" />
 *	</variables>
 * 	</pre> 
 *  sets the interval to 200 ms. When not specified, the default interval
 *  of 100 ms will be used. 
 * </p>
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AnimationThread extends Thread
//#if polish.css.animations
	implements EventListener
//#endif
{
	
	//#ifdef polish.animationInterval:defined
		//#= public final static int ANIMATION_INTERVAL = ${polish.animationInterval};
	//#else
		public final static int ANIMATION_INTERVAL = 50;
	//#endif
	//#ifdef polish.animationMinimumInterval:defined
		//#= public final static int ANIMATION_MINIMUM_INTERVAL = ${polish.animationMinimumInterval};
	//#else
		private static final int ANIMATION_MINIMUM_INTERVAL = 10;
	//#endif
	//#ifdef polish.sleepInterval:defined
		//#= private final static int SLEEP_INTERVAL = ${polish.sleepInterval};
	//#else
		private final static int SLEEP_INTERVAL = 300;
	//#endif
	protected static boolean releaseResourcesOnScreenChange;
	private static ArrayList animationList;
	//#if polish.Animation.MaxIdleTime:defined
		//#= private final static long ANIMATION_TIMEOUT = ${polish.Animation.MaxIdleTime};
	//#else
		private final static long ANIMATION_TIMEOUT = 3 * 60 * 1000; // after 3 minutes of inactivity stop the animations
	//#endif
	
	/**
	 * Creates a new animation thread.
	 */
	public AnimationThread() {
		//#if polish.cldc1.1 && polish.debug.error
			super("AnimationThread");
		//#else
			//# super();
		//#endif
		//#if polish.css.animations
			EventManager.getInstance().addEventListener(null, this);
		//#endif
	}
	
	/**
	 * Animates the current screen.
	 */
	public void run() {
		long sleeptime = ANIMATION_INTERVAL;
		ClippingRegion repaintRegion = new ClippingRegion();
//		int animationCounter = 0;
		while ( true ) {
			try {
				Thread.sleep(sleeptime);
				Screen screen = StyleSheet.currentScreen;
				//System.out.println("AnimationThread: animating " + screen + ", current=" + StyleSheet.display.getCurrent());
				if (screen != null 
						//#if polish.css.repaint-previous-screen
						&& screen.isShown()
						//#endif
				) {
					long currentTime = System.currentTimeMillis();
					if ( (currentTime - screen.lastInteractionTime) < ANIMATION_TIMEOUT ) { 
						screen.animate( currentTime, repaintRegion );
						if (animationList != null) {
							Object[] animationItems = animationList.getInternalArray();
							for (int i = 0; i < animationItems.length; i++) {
								Animatable animatable = (Animatable) animationItems[i];
								if (animatable == null) {
									break;
								}
								//System.out.println("animating " + animatable);
								animatable.animate(currentTime, repaintRegion);
							}
						}
						if (repaintRegion.containsRegion()) {
							//System.out.println("AnimationThread: screen needs repainting");
							//#debug debug
							System.out.println("triggering repaint for screen " + screen + ", is shown: " + screen.isShown() );
							//#if polish.Bugs.fullRepaintRequired
								screen.requestRepaint();
							//#else
								//System.out.println("repaint for " + repaintRegion.getX() + ", " + repaintRegion.getY() + ", " + repaintRegion.getWidth() + ", " + repaintRegion.getHeight()  );
								screen.requestRepaint( repaintRegion.getX(), repaintRegion.getY(), repaintRegion.getWidth() + 1, repaintRegion.getHeight() + 1 );
							//#endif
							repaintRegion.reset();
							screen.serviceRepaints();
						}
					}

					if (releaseResourcesOnScreenChange) {
						Displayable d = StyleSheet.display.getCurrent();
						if (d != screen) {
							StyleSheet.currentScreen = null;
						}
					}
					long usedTime = System.currentTimeMillis() - currentTime;
					if (usedTime >= (ANIMATION_INTERVAL-ANIMATION_MINIMUM_INTERVAL)) {
						sleeptime = ANIMATION_MINIMUM_INTERVAL;
					} else {
						sleeptime = ANIMATION_INTERVAL - usedTime;
					}
				} else {
					if (releaseResourcesOnScreenChange) {
						StyleSheet.releaseResources();
						releaseResourcesOnScreenChange = false;
					}
					sleeptime = SLEEP_INTERVAL;
				}
			} catch (InterruptedException e) {
				// ignore
			} catch (Throwable e) {
				//#debug error
				System.out.println("unable to animate screen" + e );
			}
		}
	}

	/**
	 * Adds the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *  
	 * @param item the item that needs to be animated regardless of it's focused state etc.
	 * @see #removeAnimationItem(Animatable)
	 */
	public static void addAnimationItem( Animatable item ) {
		if (animationList == null) {
			animationList = new ArrayList();
		}
		if (!animationList.contains(item)) {
			animationList.add( item );
		}
	}

	//#if polish.LibraryBuild
	/**
	 * Adds the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *  
	 * @param item the item that needs to be animated regardless of it's focused state etc.
	 * @see #removeAnimationItem(javax.microedition.lcdui.CustomItem)
	 */
	public static void addAnimationItem( javax.microedition.lcdui.CustomItem item) {
		// ignore
	}
	//#endif

	/**
	 * Removes the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *  
	 * @param item the item that does not need to be animated anymore
	 * @see #addAnimationItem(Animatable)
	 */
	public static void removeAnimationItem( Animatable item ) {
		if (animationList != null) {
			animationList.remove(item);
		}
	}

	//#if polish.LibraryBuild
	/**
	 * Removes the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *  
	 * @param item the item that does not need to be animated anymore
	 * @see #addAnimationItem(javax.microedition.lcdui.CustomItem)
	 */
	public static void removeAnimationItem( javax.microedition.lcdui.CustomItem item) {
		// ignore
	}
	//#endif

	//#if polish.css.animations
	/**
	 * Adds a new CSS animation into the processing queue.
	 * @param animation the animation
	 * @param item the corresponding UI element (either screen or item)
	 */
	public static void addAnimation(CssAnimation animation, UiElement item)
	{
		if (animationList == null) {
			animationList = new ArrayList();
		}
		Object[] existingAnimations = animationList.getInternalArray();
		for (int i = 0; i < existingAnimations.length; i++)
		{
			Object anim = existingAnimations[i];
			if (anim == null) {
				break;
			}
			if (anim instanceof CssAnimationRun) {
				CssAnimationRun run = (CssAnimationRun) anim;
				if (run.uiElement == item && run.animation.cssAttributeId == animation.cssAttributeId) {
					run.exitOrRepeat( true, 0 );
				}
			}
		}
		CssAnimationRun cssAnimationRun = new CssAnimationRun( animation, item );
		if (animation.duration != 0 || animation.delay != 0) {
			animationList.add( cssAnimationRun);
		}
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.event.EventListener#handleEvent(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void handleEvent(String name, Object source, Object data)
	{
		//#if polish.css.animations
			if (source instanceof UiElement) {
				UiElement uiElement = (UiElement) source;
				Style style = uiElement.getStyle();
				CssAnimation[] animations = style != null ?  style.getAnimations() : null;
				if (animations != null) {
					//System.out.println(name + ": checking animations from style " +  style.name + " of "+ uiElement );
					for (int i = 0; i < animations.length; i++)
					{
						CssAnimation animation = animations[i];
						//System.out.println( name + ": trigger=" + animation.triggerEventId);
						if (animation.triggerEventId.equals(name)) {
							//System.out.println("found animation in " + style.name + " for trigger " + name + " from " + animation.startValue + " to " + animation.endValue + " for " + uiElement);
							if (animation.delay == 0) {
								if (animation.duration == 0) {
									animation.setEndValue( style );
								} else {
									animation.setStartValue( style );
								}
							}
							if (animation.repeat == -2) {
								// only use this animation once in a lifetime of the application:
								animation.triggerEventId = "";
							}
							addAnimation( animation, uiElement);
						}
					}
				}
			}
		//#endif				
	}

	//#if polish.css.animations
	private static class CssAnimationRun implements Animatable{
		private final CssAnimation animation;
		private Object lastValue;
		private UiElement uiElement;
		private Style style;
		private long startTime;
		private final Style uiElementStyle;
		private boolean isStarted;
		private int repeats;
		
		public CssAnimationRun( CssAnimation animation, UiElement item ) {
			this.animation = animation;
			this.startTime = System.currentTimeMillis();
			this.uiElement = item;
			this.uiElementStyle = item.getStyle();
			this.lastValue = this.uiElementStyle.getObjectProperty( animation.cssAttributeId );
			this.style = new Style();
			//this.style.font = this.itemStyle.font;
			//this.style.name = item.getStyle().name;
			this.isStarted = (animation.delay == 0);
			this.repeats = animation.getRepeat();
			if (this.isStarted) {
				if (animation.duration != 0) {
					this.style.addAttribute( animation.cssAttributeId, animation.getStartValue() );
				} else {
					this.style.addAttribute( animation.cssAttributeId, animation.getEndValue() );
					if (animation.fireEvent != null) {
						EventManager.fireEvent(animation.fireEvent, item, animation );
					}
				}
				item.setStyle(this.style, false);
			} else {
				this.style.addAttribute( animation.cssAttributeId, this.lastValue);
			}
		}
		
		public void animate( long currentTime, ClippingRegion repaintArea ) {
			this.uiElement.addRepaintArea(repaintArea);
			if (!this.isStarted) {
				if (currentTime - this.startTime >= this.animation.delay) {
					this.isStarted = true;
					this.startTime = currentTime;
					if (this.animation.duration != 0) {
						this.animation.setStartValue(this.style);
					} else {
						this.animation.setEndValue(this.style);
					}
					if (this.uiElement.getStyle() == this.uiElementStyle) {
						this.uiElement.setStyle( this.style, false );
						this.uiElement.addRepaintArea(repaintArea);
					}
					if (this.animation.duration == 0) {
						exitOrRepeat(true, currentTime);
					}
				}
				return;
			}
			long passedTime = currentTime - this.startTime;
			Object newValue = this.animation.animate( this.style, this.lastValue, passedTime );
			boolean animate = false;
			if (newValue == CssAnimation.ANIMATION_FINISHED) {
				exitOrRepeat( false, currentTime );
				this.animation.setEndValue( this.uiElementStyle );
				this.animation.setEndValue( this.style );
				animate = true;
				//System.out.println("finished animation in " + (passedTime) + "ms, expected=" + this.animation.duration + ", value=" + this.itemStyle.getObjectProperty(this.animation.cssAttributeId) + ", for item " + this.item);
			} else if (newValue != null) {
				//System.out.println("setting " + animation.cssAttributeId + " to " + newValue + " for " + this.itemStyle.name + " and item " +  this.item );
				if (newValue != this.lastValue) {
					this.style.addAttribute(this.animation.cssAttributeId, newValue);
					this.lastValue = newValue;
					animate = true;
				}
			} else {
				this.style.removeAttribute( this.animation.cssAttributeId );
				animate = true;
			}
			if (this.uiElement.getStyle() == this.uiElementStyle) {
				if (animate) {
					this.uiElement.setStyle( this.style, false );
					this.uiElement.addRepaintArea(repaintArea);
				}
			} else {
				// style has changed:
				//System.out.println("style changed to " + this.item.getStyle().name  + ", value=" + this.itemStyle.getObjectProperty(this.animation.cssAttributeId) + ", for item " + this.item );
				exitOrRepeat( true, currentTime );
			}
		}

		/**
		 * @param force
		 */
		protected void exitOrRepeat(boolean force, long currentTime)
		{
			//System.out.println("exit or repeat: force=" + force + ", repeat=" + this.repeats + ", passedTime=" + (currentTime - this.startTime) );
			if (force || this.repeats == 0 || this.repeats  == -2) {
				AnimationThread.animationList.remove(this);
				if (this.animation.fireEvent != null) {
					EventManager.fireEvent(this.animation.fireEvent, this.uiElement, this.animation );
				}
			} else  {
				if (this.repeats != -1) {
					this.repeats--;
				}
				//System.out.println("repeating animation: repeat=" + this.repeats );
				if (this.animation.delay != 0) {
					this.isStarted = false;
				}
				this.startTime = currentTime;
			}
		}
		
	}
	//#endif

}
