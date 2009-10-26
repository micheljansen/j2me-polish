//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video;

import de.enough.polish.video.VideoCallback;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
//#if polish.api.fileconnection
	import javax.microedition.io.file.FileConnection;
//#endif
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.FramePositioningControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import de.enough.polish.io.Serializable;

/**
 * Used to define the source of video.
 * Starts a thread to close the stream, connection etc.
 * of a video
 * @author Andre Schmidt
 *
 */
public class VideoSource implements Serializable{
	private static VideoSource CAPTURE;
	
	static final String tag = "videosource";
		
	/**
	 * the id of the video
	 */
	String id;
	
	/**
	 * the file url of the video
	 */
	String file;
	
	/**
	 * the stream to the video
	 */
	transient InputStream stream;
	
	/**
	 * the connection to the video
	 */
	transient Connection connection;
	
	/**
	 * the mimetype of the video
	 */
	String mime;
	
	/**
	 * the callback
	 */
	transient VideoCallback callback;
	
	/**
	 * the player
	 */
	transient Player player;
	
	/**
	 *  the video control
	 */
	transient VideoControl videoControl;

	/**
	 *  the frame control
	 */
	transient FramePositioningControl framePositioningControl;
	
	/**
	 *  the volume control
	 */
	transient VolumeControl volumeControl;
	
	public static VideoSource capture(VideoCallback callback)
	{
		return new VideoSource("capture","capture://video",null,callback);
	}
	
	/**
	 * Constructs a basic VideoSource. Private access only.
	 * @param id the id
	 * @param callback the callback
	 */
	private VideoSource(String id, String mime, VideoCallback callback)
	{
		this.id = id;
		this.callback = callback;
		this.mime = mime;
	}
	
	/**
	 * Constructs a VideoSource with only a url and a callback,
	 * used for devices supporting progressive download
	 * @param id the id
	 * @param file the url of the video file
	 * @param callback the callback
	 */
	public VideoSource(String id, String file, String mime, VideoCallback callback)
	{
		this(id,mime,callback);
		
		this.file = file;
	}
	
	/**
	 * Construct a VideoSource with a stream, a mimetype and a callback
	 * @param id the id
	 * @param stream the stream
	 * @param mime the mimetype
	 * @param callback the callback
	 */
	public VideoSource(String id, InputStream stream, String mime, VideoCallback callback)
	{
		this(id,mime,callback);
		
		this.stream = stream;
	}
	
	/**
	 * Construct a VideoSource with a stream, a connection a mimetype and a callback
	 * @param id the id
	 * @param stream the stream
	 * @param connection
	 * @param mime the mimetype
	 * @param callback the callback
	 */
	public VideoSource(String id, InputStream stream, Connection connection, String mime, VideoCallback callback)
	{
		this(id,mime,callback);
		
		this.stream = stream;
		this.connection = connection;
	}
	
	public void open() throws Exception
	{
		try {
			//#if !polish.video.progressive && polish.api.fileconnection
			if(this.file != null  && !this.file.startsWith("rtsp://"))
			{	
			
					FileConnection fileConnection;
					
					fileConnection = (FileConnection)Connector.open(this.file, Connector.READ_WRITE);
					
					this.connection = fileConnection;
					
					this.stream = fileConnection.openInputStream();
				
			}
			//#endif
		
			if(getStream() != null)
			{
				this.player = Manager.createPlayer(getStream(), getMime());
			}
			else if(getFile() != null )
			{
				this.player = Manager.createPlayer(getFile());
			}
			
			this.player.realize();
			
			this.player.prefetch();
			
			this.videoControl = (VideoControl) player.getControl("VideoControl");
			
			this.volumeControl = (VolumeControl) player.getControl("VolumeControl");
			
			this.framePositioningControl = (FramePositioningControl) player.getControl("FramePositioningControl");
		} catch (Exception e) {
			//#debug
			System.out.println("error in VideoSource.open() : " + e.toString());
		}
	}
	
	public void close()
	{
		try
		{
			if(this.player != null)
			{
				this.player.deallocate();
				this.player.close();
				this.player = null;
			}
			
			if(this.stream != null)
			{
				this.stream.close();
				this.stream = null;
			}

			if(this.connection != null)
			{
				this.connection.close();
				this.connection = null;
			}
		}catch(IOException e)
		{
			this.callback.onVideoError(e);
			//#debug error
			System.out.println("Unable to close connection" + e);
		}
	}
	
	/**
	 * Returns the id
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the file url
	 * @return the file url
	 */
	public String getFile() {
		return file;
	}
	
	/**
	 * Returns the stream
	 * @return the stream
	 */
	public InputStream getStream() {
		return stream;
	}
	
	/**
	 * Returns the connection
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Returns the mimetype
	 * @return the mimetype
	 */
	public String getMime() {
		return mime;
	}
	
	/**
	 * Returns the player
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Returns the video control
	 * @return the video control
	 */
	public VideoControl getVideoControl() {
		System.out.println("source.getVideoControl: " + this.videoControl);
//		if (this.videoControl == null) {
//			open();
//		}
		return this.videoControl;
	}

	/**
	 * Returns the frame control
	 * @return the frame control
	 */
	public FramePositioningControl getFramePositioningControl() {
		return framePositioningControl;
	}

	/**
	 * Returns the volume control
	 * @return the volume control
	 */
	public VolumeControl getVolumeControl() {
//		if (this.volumeControl == null) {
//			open();
//		}
		return this.volumeControl;
	}
}
