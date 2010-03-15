package de.enough.polish.video;

/**
 * Provides an interface to nofify listeners
 * of video states and actions
 * @author Andre Schmidt
 *
 */
public interface VideoCallback {
	/**
	 * Called when a video is fully prepared
	 */
	public void onVideoReady();
	
	/**
	 * Called when a video is destroyed
	 */
	public void onVideoClose();
	
	/**
	 * Called when an error occures
	 */
	public void onVideoError(Exception e);
	
	/**
	 * Called when the video is paused
	 */
	public void onVideoPause();
	
	/**
	 * Called when the video is played
	 */
	public void onVideoPlay();
	
	/**
	 * Called when the video is stopped
	 */
	public void onVideoStop();
	
	/**
	 * Called when a capture is done
	 * @param data the resulting data of the capture
	 */
	public void onSnapshot(byte[] data, String encoding);
}
