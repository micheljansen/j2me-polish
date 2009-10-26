//#condition polish.usePolishGui && polish.api.mmapi



/*

 * Created on Sep 8, 2006 at 5:00:10 PM.

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

package de.enough.polish.ui;

import java.util.Date;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;


import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.TextUtil;

//#if polish.api.advancedmultimedia
	import de.enough.polish.multimedia.AdvancedMultimediaManager;
//#endif

/**
 * <p>A convenience screen for taking snapshots. This screen requires support of the MMAPI by the current target device!</p>
 * <pre>
 * //#if polish.api.mmapi
 *    import de.enough.polish.ui.SnapshotScreen;
 * //#endif
 * ...
 * //#if polish.api.mmapi
 *    //#style snapshotScreen
 *    SnapshotScreen screen = new SnapshotScreen("Snapshot");
 * //#endif
 * </pre>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Sep 8, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SnapshotScreen 
extends Screen 
implements Runnable 
{

	//private static final long PLAYER_TIMEOUT = 300;
	/**
	 * The mount position of the camera is not known.
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_UNKNOWN = -1;
	/**
	 * The mount position of the camera is landscape (wider than high).
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_LANDSCAPE = 1;
	/**
	 * The mount position of the camera is portrait (higher than wide).
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_PORTRAIT = 2;
	private Player player;
	private VideoControl videoControl;
	private boolean isSnapshotInProgress;
	//#if polish.api.advancedmultimedia
		private boolean isAutofocusEnabled = false;
	//#endif
	private Command cmdCapture;
	private boolean isInitializing;

	
	/**
	 * Creates a new screen for taking screenshots.
	 *
	 * @param title the title of the screen
	 */
	public SnapshotScreen(String title) {
		this(title, null);
	}


	/**
	 * Creates a new screen for taking screenshots.
	 *
	 * @param title the title of the screen
	 * @param style the style
	 */
	public SnapshotScreen(String title, Style style ) {
		super(title, false, style );
	}
	
	/**
	 * Determines whether a snapshot is currently made
	 * @return true when a this screen tries to create a snapshot at the moment.
	 */
	public boolean isSnapshotInProgress() {
		return this.isSnapshotInProgress;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#hideNotify()
	 */
	public void hideNotify() {
		//#debug
		System.out.println("SnapshotScreen.hideNotify(): isSnapshotInProgress=" + this.isSnapshotInProgress);
		super.hideNotify();
		if (!this.isSnapshotInProgress) {
			stopSnapshot(false);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#showNotify()
	 */
	public void showNotify() {
		//#debug
		System.out.println("SnapshotScreen.showNotify(): isSnapshotInProgress=" + this.isSnapshotInProgress + ", (player==null)=" + (this.player == null));
		super.showNotify();
		if (this.player == null) {
			// display snapshot window:
			(new Thread( this )).start();
		} else {
			VideoControl vc = this.videoControl;
			if (vc != null) {
				try {
					vc.setVisible(true);
				} catch (IllegalStateException e) {
					//#debug warn
					System.out.println("Unable to set the VideoControl to visible" + e);
					// ignore
				}
			}
		}
	}


	public void run() {
		try
		{
			initPlayer();
		} catch (MediaException e)
		{
			// ignore
		}
	}
	
	/**
	 * Retrieves the position of the camera.
	 * @return either portrait, landscape or unknown
	 * @see #MOUNT_POSITION_LANDSCAPE
	 * @see #MOUNT_POSITION_PORTRAIT
	 * @see #MOUNT_POSITION_UNKNOWN
	 */
	public static int getCameraMountPosition() {
		try {
			String positionStr = System.getProperty("camera.mountorientation");
			if (positionStr != null) {
				positionStr = positionStr.toUpperCase();
				if (positionStr.indexOf("LANDSCAPE") != -1) {
					return MOUNT_POSITION_LANDSCAPE;
				} else if (positionStr.indexOf("PORTRAIT") != -1) {
					return MOUNT_POSITION_PORTRAIT;	
				}
			}
		} catch (Exception e) {
			// ignore, unsupported system property
		}
		return MOUNT_POSITION_UNKNOWN;
	}
	

	/**
	 * Retrieves the supported snapshot encodings available on the current device.
	 *
	 * @return an array of encodings.
	 *         When the "video.snapshot.encodings" system property is null, an empty array is returned.
	 */
	public static String[] getSnapshotEncodings(){
		String supportedEncodingsStr = System.getProperty("video.snapshot.encodings");
		if(supportedEncodingsStr == null){
			return new String[0];
		}
		String[] encodings = TextUtil.splitAndTrim(supportedEncodingsStr, ' ');
		Arrays.sort(encodings);
		return encodings;
	}
	
	/**
	 * Tries to determine the available resolutions from the supported encodings
	 * @return an array of detected resolutions, may be empty but not null. Each resolution is returned as a Point object, where x defines the width and y defines the height of the resolution.
	 */
	public static Point[] getSnapshotResolutions() {
		String[] encodings = getSnapshotEncodings();
		ArrayList resolutionsList = new ArrayList();
		for (int i = 0; i < encodings.length; i++)
		{
			String encoding = encodings[i];
			int widthIndex = encoding.indexOf("width="); 
			if (widthIndex != -1) {
				int splitPos = encoding.indexOf('&', widthIndex + 1);
				String width;
				if (splitPos != -1) {
					width = encoding.substring(widthIndex + "width=".length(), splitPos);
				} else {
					width = encoding.substring(widthIndex + "width=".length());
				}
				int heightIndex = encoding.indexOf("height=");
				if (heightIndex != -1) {
					splitPos = encoding.indexOf('&', heightIndex + 1);
					String height;
					if (splitPos == -1) {
						height = encoding.substring(heightIndex + "height=".length());
					} else {
						height = encoding.substring(heightIndex + "height=".length(), splitPos);
					}
					Point resolution = new Point( Integer.parseInt( width ), Integer.parseInt( height ) );
					if (!resolutionsList.contains(resolution)) {
						resolutionsList.add(resolution);
					}
				}
			} else {
				int middlePos = encoding.indexOf('x');
				if (middlePos != -1) {
					int start = middlePos - 1;
					int end = middlePos + 1;
					while (start >= 0 && Character.isDigit(encoding.charAt(start))) {
						start--;
					}
					if (start != 0) {
						start++;
					}
					while (end < encoding.length() && Character.isDigit(encoding.charAt(end)) ) {
						end++;
					}
					if (end != encoding.length() - 1) {
						end--;
					}
					if (start != middlePos && end != middlePos) {
						String resolutionStr = encoding.substring(start, end + 1 );
						int xIndex = resolutionStr.indexOf('x');
						Point resolution = new Point( Integer.parseInt( resolutionStr.substring(0, xIndex) ), 
								Integer.parseInt( resolutionStr.substring(xIndex + 1) ) );
						if (!resolutionsList.contains(resolution)) {
							resolutionsList.add(resolution);
						}
					}
				}
			}
		}
		Point[] resolutions = (Point[]) resolutionsList.toArray( new Point[ resolutionsList.size() ] );
		Arrays.sort( resolutions );
		return resolutions;
	}
	
	/**
	 * Retrieves the default resolution.
	 * 
	 * @return the default resolution as a point object - x represents the width, y the height. This can be null if no resolution is specified in "video.snapshot.encodings"!
	 */
	public static Point getDefaultResolution() {
		String encoding = System.getProperty("video.snapshot.encodings");
		if(encoding == null){
			return null;
		}
		int splitPos = encoding.indexOf(' ');
		if (splitPos != -1) {
			encoding = encoding.substring( 0, splitPos );
		}
		int widthIndex = encoding.indexOf("width="); 
		if (widthIndex != -1) {
			splitPos = encoding.indexOf('&', widthIndex + 1);
			String width;
			if (splitPos != -1) {
				width = encoding.substring(widthIndex + "width=".length(), splitPos);
			} else {
				width = encoding.substring(widthIndex + "width=".length());
			}
			int heightIndex = encoding.indexOf("height=");
			if (heightIndex != -1) {
				splitPos = encoding.indexOf('&', heightIndex + 1);
				String height;
				if (splitPos == -1) {
					height = encoding.substring(heightIndex + "height=".length());
				} else {
					height = encoding.substring(heightIndex + "height=".length(), splitPos);
				}
				return new Point( Integer.parseInt(width), Integer.parseInt( height ));
			}
		} else {
			int middlePos = encoding.indexOf('x');
			if (middlePos != -1) {
				int start = middlePos - 1;
				int end = middlePos + 1;
				while (start >= 0 && Character.isDigit(encoding.charAt(start))) {
					start--;
				}
				if (start != 0) {
					start++;
				}
				while (end < encoding.length() && Character.isDigit(encoding.charAt(end)) ) {
					end++;
				}
				if (end != encoding.length() - 1) {
					end--;
				}
				if (start != middlePos && end != middlePos) {
					String resolution = encoding.substring(start, end + 1 );
					splitPos = resolution.indexOf('x');
					return new Point( Integer.parseInt(resolution.substring(0, splitPos)), Integer.parseInt( resolution.substring(splitPos + 1)  ));
				}
			}
		}
		return null;
	}
	
	/**
	 * Determines the best fitting encoding for reaching snapshots of at least the specified dimensions
	 * @param width the minimum width, e.g. 800
	 * @param height the minum height, e.g. 640
	 * @return an array of suitable encodings, when format is specified this will typically only contain one element;
	 *         the array will be empty (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo( int width, int height) {
		return getSnapshotEncodingsWithResolutionCloseTo(width, height, false, false, null);
	}

	/**
	 * Determines the best fitting encoding
	 * @param format the format, e.g. jpg
	 * @param width the desired width, e.g. 800
	 * @param height the desired height, e.g. 640
	 * @param needsToBeLarger true when the found encodings needs to be the same size or larger than the specified dimensions
	 * @param needsToBeSmaller true when the found encodings needs to be the same size or smaller than the specified dimensions 
	 * @return an array of suitable encodings, when format is specified this will typically only contain one element;
	 *         the array will be empty (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo( int width, int height, boolean needsToBeLarger, boolean needsToBeSmaller, String format ) {
		Point[] resolutions = getSnapshotResolutions();
		String defaultEncoding;
		if (format != null) {
			defaultEncoding = "encoding=" + format + "&width=" + width + "&height=" + height;
		} else {
			defaultEncoding = "encoding=jpeg&width=" + width + "&height=" + height;
		}
		if (resolutions.length == 0) {
			return new String[]{ defaultEncoding };
		}
		int minDistance = Integer.MAX_VALUE;
		int closestWidth = 0;
		int closestHeight = 0;
		for (int i = 0; i < resolutions.length; i++)
		{
			Point point = resolutions[i];
			int resWidth = point.x;
			int resHeight = point.y;
			if (
				(needsToBeLarger && (resWidth < width || resHeight < height))
				|| (needsToBeSmaller && (resWidth > width || resHeight > height))
				) 
			{
				continue;
			}
			int distance = Math.abs( width - resWidth ) + Math.abs( height - resHeight );
			if (distance < minDistance) {
				closestWidth = resWidth;
				closestHeight = resHeight;
				minDistance = distance;
			}
		}
		if (minDistance == Integer.MAX_VALUE) {
			return new String[] { defaultEncoding };
		}
		String[] encodings = getSnapshotEncodings();
		if (format != null) {
			format = format.toLowerCase();
		}
		ArrayList bestEncodings = new ArrayList();
		String closestWidthStr = Integer.toString(closestWidth);
		String closestHeightStr = Integer.toString(closestHeight);
		for (int i = 0; i < encodings.length; i++)
		{
			String encoding = encodings[i];
			if ( format == null || encoding.toLowerCase().indexOf(format) != -1) {
				if (encoding.indexOf(closestWidthStr) != -1 && encoding.indexOf(closestHeightStr) != -1) {
					bestEncodings.add(encoding);
				}
			}
		}
		if (bestEncodings.size() == 0) {
			return new String[] { defaultEncoding };			
		}
		encodings = (String[]) bestEncodings.toArray( new String[ bestEncodings.size() ]);
		Arrays.sort(encodings);
		return encodings;
	}
	
	
	
	/**
	 * Tries to determine the available formats from the supported encodings
	 * @return an array of detected formats (in lower case), may be emtpy but not null
	 */
	public static String[] getSnapshotFormats() {
		String[] encodings = getSnapshotEncodings();
		ArrayList formats = new ArrayList();
		boolean pngAdded = false;
		boolean jpegAdded = false;
		boolean bmpAdded = false;
		boolean gifAdded = false;
		for (int i = 0; i < encodings.length; i++)
		{
			String encoding = encodings[i].toLowerCase();
			if (!pngAdded && encoding.indexOf("png") != -1) {
				formats.add("png");
				pngAdded = true;
			} else if (!jpegAdded && encoding.indexOf("jpg") != -1) {
				formats.add("jpg");
				jpegAdded = true;
			} else if (!jpegAdded && encoding.indexOf("jpeg") != -1) {
				formats.add("jpeg");
				jpegAdded = true;
			} else if (!gifAdded && encoding.indexOf("gif") != -1) {
				formats.add("gif");
				gifAdded = true;
			} else if (!bmpAdded && encoding.indexOf("bmp") != -1) {
				formats.add("bmp");
				bmpAdded = true;
			}
		}
	
		return (String[]) formats.toArray( new String[ formats.size() ] );
	}

	/**
	 * Takes a snapshot in the default encoding.
	 *
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 * @throws SecurityException when the user does not allow the snapshot
	 * @see de.enough.polish.event.AsynchronousCommandListener
	 * @see de.enough.polish.event.ThreadedCommandListener
	 */
	public Image getSnapshotImage() throws MediaException {
		return getSnapshotImage( null );
	}

	/**
	 * Takes a snapshot in the desired encoding/settings.
	 * Attention: taking the snapshot is executed synchronously, so don't call this method within the device's event thread
	 * (commandAction, keyPressed, etc). You can safely call it from within commandAction if you use the AsysnchronousCommandListener.
	 *
	 * @param encoding the encoding and optionally size
	 * @return the image taken
	 * @throws MediaException when taking the snapshot fails
	 * @throws SecurityException when the user does not allow the snapshot
	 * @see de.enough.polish.event.AsynchronousCommandListener
	 * @see de.enough.polish.event.ThreadedCommandListener
	 */
	public Image getSnapshotImage( String encoding ) throws MediaException {
		//System.out.println("GET SNAPSHOT IMAGE");
		byte[] data = getSnapshot(encoding);
		if (data == null) {
			throw new MediaException("no data");
		}
		return Image.createImage( data, 0, data.length );
	}

	/**
	 * Takes a snapshot in the default encoding.
	 * Attention: taking the snapshot is executed synchronously, so don't call this method within the device's event thread
	 * (commandAction, keyPressed, etc). You can safely call it from within commandAction if you use the AsysnchronousCommandListener.
	 *
	 * @return the image data
	 * @throws MediaException when taking the snapshot fails
	 * @throws SecurityException when the user does not allow the snapshot
	 * @see de.enough.polish.event.AsynchronousCommandListener
	 * @see de.enough.polish.event.ThreadedCommandListener
	 */
	public byte[] getSnapshot() throws MediaException {
		return getSnapshot( null );
	}



	/**
	 * Takes a snapshot in the desired encoding/settings.
	 * Attention: taking the snapshot is executed synchronously, so don't call this method within the device's event thread
	 * (commandAction, keyPressed, etc). You can safely call it from within commandAction if you use the AsysnchronousCommandListener.
	 *
	 * @param encoding the encoding and optionally size
	 * @return the image data
	 * @throws MediaException when taking the snapshot fails
	 * @throws SecurityException when the user does not allow the snapshot
	 * @see de.enough.polish.event.AsynchronousCommandListener
	 * @see de.enough.polish.event.ThreadedCommandListener
	 */
	public byte[] getSnapshot( String encoding ) throws MediaException {
		//#debug
		System.out.println("getting snapshot for " + encoding + ", isSnapshotInProgress=" + this.isSnapshotInProgress);
		encoding = null;
		if (this.isSnapshotInProgress) {
			throw new MediaException("Snapshot in progress");
		}
		this.isSnapshotInProgress = true;
		int step = 0;
		try {
            //#debug info
            System.out.println("getSnapshot(" + encoding + ") at " + (new Date()).toString());
			VideoControl vc = this.videoControl;
			Player pl = this.player;
			int tries = 0;
			while (vc == null || pl.getState() == Player.CLOSED) {
				initPlayer();
				pl = this.player;
				vc = this.videoControl;
				tries++;
				if (tries > 3) {
					throw new MediaException("Unable to init snapshot");
				}
			}
			step = 1;
//			if (tries == 0) {
//				try {
//					step = 2;
//		        	vc.setVisible(false);
//		        	vc.setVisible(true);
//				} catch (Exception e) {
//					throw new MediaException("While setting visibility: " + e);
//				}
//			}
			//#if polish.api.advancedmultimedia
				if (this.isAutofocusEnabled) {
					step = 3;
					//#debug
					System.out.println("Setting focus for player");
					AdvancedMultimediaManager.setFocus(pl);
				}
			//#endif
            //#debug info
            System.out.println("Start to capture data at " + (new Date()).toString());
            byte[] data = null;
            String message = null;
            try {
            	step = 4;
            	if (encoding != null) {
            		String supported = System.getProperty("video.snapshot.encodings");
	            	if (supported != null && supported.indexOf(' ') == -1) {
	            		encoding = supported; 
	            	}
            	}
            	data = vc.getSnapshot(encoding);
            } catch (MediaException e) {
            	//#debug info
            	System.out.println("did not get data for encoding " + encoding + e);
            	if (encoding == null) {
            		throw e;
            	}
            	message = e.getMessage();
            }
            if (data == null && encoding != null) {
            	try {
            		step = 5;
            		//#debug
            		System.out.println("retrying snapshot with <null> encoding.");
            		data = vc.getSnapshot(null);
            		//#debug
            		System.out.println("got data for <null> encoding.");
            	} catch (MediaException e) {
            		throw new MediaException( "(1): " + message + " enc=[" + encoding + "], (2):" + e.getMessage() + ", supported:[" + System.getProperty("video.snapshot.encodings") + "]" );
            	}
            }
            //#debug info
            System.out.println("End of capturing data at " + (new Date()).toString());
            //#debug
            System.out.println("data.length=" + data.length);
			this.isSnapshotInProgress = false;
			stopSnapshot();
            return data;
		} catch (MediaException e) {
			//#debug error
			System.out.println("Unable to take snapshot " + e);
			throw e;
		} catch (Throwable e) {
			//#debug error
			System.out.println("Unable to take snapshot at step " + step + e);
			if (e instanceof SecurityException) {
				throw (SecurityException) e;
			}
			throw new MediaException( e.toString() + " at step " + step ); 
		} finally {
			//#debug
			System.out.println("stopping snapshot, last step=" + step);
			this.isSnapshotInProgress = false;
		}
	}
	
	/**
	 * Enables or disables the autofocus mode.
	 * Note that autofocusing is only supported for devices with the camera supplements of the Advanced Media API (JSR 234).
	 * 
	 * @param autofocus true when the autofocus mode should be enabled
	 */
	public void setAutofocus( boolean autofocus ) {
		//#if polish.api.advancedmultimedia
			this.isAutofocusEnabled = autofocus;
		//#endif
	}
	
	/**
	 * Checks if the autofocus mode is enabled.
	 * Note that autofocusing is only supported for devices with the camera supplements of the Advanced Media API (JSR 234).
	 * 
	 * @return true when the autofocus mode should be enabled and when JSR 234 is supported
	 */
	public boolean isAutofocus() {
		boolean result;
		//#if polish.api.advancedmultimedia
			result = this.isAutofocusEnabled;
		//#else
			result = false;
		//#endif
		return result;
	}


	/**
	 * Initializes the snapshot player.
	 * @throws MediaException when initialization fails
	 */
	private synchronized void initPlayer() throws MediaException
	{
		
		//#debug
		System.out.println("initPlayer()");
		if (this.player != null) {
			stopSnapshot();
		}
		this.isInitializing = true;
    	try {
			String[] contentTypes = Manager.getSupportedContentTypes("capture");
			if (contentTypes == null || contentTypes.length == 0) {
				throw new MediaException("capture not supported");
			}
			String protocol;
			//#if polish.identifier.motorola/v3xx
				protocol = "capture://camera";
			//#elif polish.group.series60e3
				protocol = "capture://devcam0";
			//#elif polish.group.series40				
				protocol = "capture://image";
			//#else
				protocol = "capture://video";
				String device = "video";
				//#debug info
				System.out.println("Here are the supported contentTypes");
				boolean deviceIsSupported = false;
				for (int i = 0; i < contentTypes.length; i++) {
					String contentType = contentTypes[i];
	                //#debug info
	                System.out.println("ContentType " + i + " " + contentType);
					if (contentType.equals("image")) { // this is the case on Series 40, for example
						protocol = "capture://image";
						deviceIsSupported = true;
						break;
					}
					if (contentType.startsWith(device)) {
						deviceIsSupported = true;
					}
				}
				if (!deviceIsSupported && contentTypes.length > 0) {
					protocol = "capture://" + contentTypes[0];
				}
            //#endif
            //#debug info
            System.out.println("The capture protocol is " + protocol);
            try {
            	this.player = Manager.createPlayer(protocol);
            } catch (MediaException e) {
            	if (!"capture://video".equals(protocol)) {
            		protocol = "capture://video";
            		this.player = Manager.createPlayer(protocol);
            	}
            }
            //this.player.addPlayerListener( this );
			this.player.realize();
			VideoControl vc = (VideoControl) this.player.getControl("VideoControl");
			this.videoControl = vc;
			if (vc != null) {
				try {
					/*
					 * todo: add BB specific code:
					 * 			if (this._bbField != null) {
				getScreen().removePermanentNativeItem(this);
			}
			this._bbField = (Field) videoControl.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
			if (this.adjustSizeAutomatically) {
				this.contentWidth = videoControl.getSourceWidth();
				this.contentHeight = videoControl.getSourceHeight();
			}
					 */
					vc.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, Display.getInstance());
					vc.setVisible(true);
					this.player.prefetch();
					// it's a bit weird to first start the player before setting the size and location,
					// but in this way it works fine on Nokia Series 60 as well:
					//#if polish.Bugs.videoControlBeforePlayer
						this.player.start();
					//#endif
					int width = this.contentWidth;
					int height = this.contentHeight;
					int locX = this.contentX;
					int locY = this.contentY;
//					Point resolution = getDefaultResolution();
//					if (resolution != null) {
						int resW = vc.getDisplayWidth();
						int resH = vc.getDisplayHeight();
//						int resW = resolution.x;
//						int resH = resolution.y;
						//#debug
						System.out.println("current resolution: " + resW + "x" + resH + ", target res=" + width + "x" + height);
//						if (( (resW > resH && getWidth() <= getHeight()) || (resW < resH && getWidth() > getHeight()) ) ) {
//							//#debug
//							System.out.println("resolution is switched to horizontal mode");
//							int tmp = resW;
//							resW = resH;
//							resH = tmp;
//						}
						// 1: assume height stays the same:
						int adjustedWidth = (resW * height) / resH;
						if (adjustedWidth <= width) {
							// height can stay the same, but the width is shrinked:
							locX += (width - adjustedWidth)/2;
							width = adjustedWidth;
						} else {
							// 2. width can stay the same, but the height is shrinked:
							int adjustedHeight = (width * resH) / resW;
							locY += (height - adjustedHeight)/2;
							height = adjustedHeight;
						}
						//#debug
						System.out.println("Switched res to " + width + "x" + height);
//					}

					vc.setDisplaySize( width, height );
					vc.setDisplayLocation( locX, locY );
					//#if !polish.Bugs.videoControlBeforePlayer
						this.player.start();
					//#endif
				} catch (MediaException e) {
	                //#debug error
	                System.out.println("Cannot start video player. The error is: " + e);
	                throw e;
				}
			}
    	} catch (MediaException e) {
    		throw e;
    	} catch (Throwable e) {
			//#debug error
			System.out.println("unable to initialize capture player" + e);
			throw new MediaException(e.toString());
		} finally {
			//#if polish.Bugs.SnapshotRequiresScreenChange
				//#debug
				System.out.println("Screen toggle for preview window.");
				this.isSnapshotInProgress = true;
				Display.getInstance().toggleScreen();
				this.isSnapshotInProgress = false;
			//#endif
			this.isInitializing = false;
			repaint();
		}    	
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
//		if (this.error != null) {
//			g.drawString( this.error.toString(), getWidth() - 10, getHeight()/2, Graphics.RIGHT | Graphics.TOP );
//		}
	}


	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "snapshotscreen";
	}
	//#endif

	
	/**
	 * Stops the snapshot and closes the snapshot player.
	 */
	public void stopSnapshot()
	{
		stopSnapshot(false);
	}
	
	/**
	 * Stops the snapshot and closes the snapshot player.
	 * @param removeInstanceVariables true when instance variables of VideoControl and player should be removed
	 */
	public void stopSnapshot(boolean removeInstanceVariables)
	{
		//#debug
		System.out.println("stopSnapshot(): removeInstanceVars=" + removeInstanceVariables + ", isSnapshotInProgress=" + this.isSnapshotInProgress);
		if (this.isSnapshotInProgress) {
			return;
		}
		VideoControl vControl = this.videoControl;
		if (vControl != null) {
			try {
				vControl.setVisible(false);
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to close VideoControl" + e);
				this.videoControl = null;
			}
		}
		Player pl = this.player;
		if ( pl != null && pl.getState() != Player.CLOSED) {
			try {
				pl.stop();
				try {
					Thread.sleep(200);
				} catch (Exception e2) {
					// ignore thread interrupt
				}
				pl.close();
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to close player" + e);
				this.player = null;
			}
		}
		// when there is an error we keep references so that we can later onwards
		// stop the player...
		if (removeInstanceVariables) {
			this.videoControl = null;
			this.player = null;
		}
		//#if polish.Bugs.SnapshotRequiresScreenChange && polish.midp
			( new Thread() { 
				public void run() {
					Display.getInstance().toggleScreen();
				}
			}).start();
		//#endif
	}

	/**
	 * Retrieves the player used for taking the snapshot.
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}

	//#if polish.vendor.Sony-Ericsson || polish.vendor.Generic
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyReleased(int, int)
	 */
	public boolean handleKeyReleased( int keyCode, int gameAction ) {
		boolean handled = super.handleKeyReleased( keyCode, gameAction );
		if (!handled && (keyCode == -24 || keyCode == -26) && this.cmdCapture != null && getCommandListener() != null && !isBusy()) {
			getCommandListener().commandAction(this.cmdCapture, this );
			handled = true;
		}
		return handled;
	}
	//#endif


	/**
	 * Retrieves the command that has been set previously with setCaptureCommand
	 * @return the capture command, can be null
	 */
	public Command getCaptureCommand()
	{
		return this.cmdCapture;
	}


	/**
	 * Sets the command that is used for capturing.
	 * This command is automatically added to this screen as well.
	 * @param cmdCapture the capture command to set, use null to remove it
	 */
	public void setCaptureCommand(Command cmdCapture)
	{
		if (this.cmdCapture != null) {
			removeCommand( this.cmdCapture );
		}
		this.cmdCapture = cmdCapture;
		if (cmdCapture != null) {
			addCommand( cmdCapture );
		}
	}
	
	//#if polish.LibraryBuild
	/**
	 * Sets the command that is used for capturing.
	 * This command is automatically added to this screen as well.
	 * @param cmdCapture the capture command to set, use null to remove it
	 */
	public void setCaptureCommand(javax.microedition.lcdui.Command cmdCapture)
	{
		// ignore
	}
	//#endif


	/**
	 * Determines whether this snapshot screen is currently busy and should not be interrupted.
	 * This is case when it is currently taking a snapshot or when it is currently initializing.
	 * @return true when this screen is busy.
	 */
	public boolean isBusy()
	{
		return this.isSnapshotInProgress || this.isInitializing ;
	}

	

}

