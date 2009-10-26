//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.midlet;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.ResultReceiver;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import de.enough.polish.android.io.ConnectionNotFoundException;
import de.enough.polish.android.lcdui.AndroidDisplay;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.util.ArrayList;

//#if polish.android1.5
import android.view.inputmethod.InputMethodManager;
//#endif


/**
 * A MIDlet is a MID Profile application.
 * 
 * A <code>MIDlet</code> is a MID Profile application. The application must
 * extend this class to allow the application management software to control the
 * MIDlet and to be able to retrieve properties from the application descriptor
 * and notify and request state changes. The methods of this class allow the
 * application management software to create, start, pause, and destroy a
 * MIDlet. A <code>MIDlet</code> is a set of classes designed to be run and
 * controlled by the application management software via this interface. The
 * states allow the application management software to manage the activities of
 * multiple <CODE>MIDlets</CODE> within a runtime environment. It can select
 * which <code>MIDlet</code>s are active at a given time by starting and
 * pausing them individually. The application management software maintains the
 * state of the <code>MIDlet</code> and invokes methods on the
 * <code>MIDlet</code> to notify the MIDlet of change states. The
 * <code>MIDlet</code> implements these methods to update its internal
 * activities and resource usage as directed by the application management
 * software. The <code>MIDlet</code> can initiate some state changes itself
 * and notifies the application management software of those state changes by
 * invoking the appropriate methods.
 * <p>
 * 
 * <strong>Note:</strong> The methods on this interface signal state changes.
 * The state change is not considered complete until the state change method has
 * returned. It is intended that these methods return quickly.
 * <p>
 * <HR>
 * 
 * 
 */
public abstract class MIDlet extends Activity {
	
	//#if polish.useFullScreen
		//#define tmp.fullScreen
	//#else
		private final ArrayList addedCommands = new ArrayList();
		private final ArrayList addedCommandMenuItemBridges = new ArrayList();
		private boolean isMenuOpened;
	//#endif


	//The one and only MIDlet
	public static MIDlet midletInstance;
	
	//Tag for logging
	public static final String TAG = "Polish";
	
	// The view of the application
//	private AndroidDisplay display;

	private HashMap<String,String> appProperties;

	private ContentResolver contentResolver;

	private boolean shuttingDown;
	
	private boolean isSoftkeyboardOpen;

	private int currentScreenYOffset;

	private boolean suicideOnExit;

//	private PowerManager.WakeLock wakeLock;

	/**
	 * Protected constructor for subclasses. The application management software
	 * is responsible for creating MIDlets and creation of MIDlets is
	 * restricted. MIDlets should not attempt to create other MIDlets.
	 * 
	 * @throws SecurityException -
	 *             unless the application management software is creating the
	 *             MIDlet.
	 */
	protected MIDlet() {
		midletInstance = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//TODO: Remove this as we do not want to shrink the application on softkeyboard display.
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		//TODO: Extract all strings to constants.
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener listener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				//#debug
				System.out.println("onCellLocationChanged");
				if(location instanceof GsmCellLocation) {
					GsmCellLocation gsmCellLocation = (GsmCellLocation)location;
					//#debug
					System.out.println("gsmCellLocation:"+gsmCellLocation);
					int cellId = gsmCellLocation.getCid();
					String cellIdString = Integer.toString(cellId);
					setSystemProperty("Cell-Id",cellIdString);

					int lac = gsmCellLocation.getLac();
					String lacString = Integer.toString(lac);
					setSystemProperty("Cell-lac",lacString);
				}
			}

			@Override
			public void onSignalStrengthChanged(int asu) {
				String asuString = Integer.toString(asu);
				//#debug
				System.out.println("SignalStrength (asu) is '"+asu+"'");
				setSystemProperty("SignalStrength",asuString);
			}
		};
		int events = PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTH;
		telephonyManager.listen(listener, events);
		setSystemProperty("Cell-Id","-1");
		setSystemProperty("Cell-lac","-1");
		setSystemProperty("SignalStrength","0");
		String subscriberId = telephonyManager.getSubscriberId();
		if(subscriberId == null) {
			subscriberId = "";
		}
		setSystemProperty("IMSI", subscriberId);
		String deviceId = telephonyManager.getDeviceId();
		if(deviceId == null) {
			deviceId = "";
		}
		setSystemProperty("IMEI", deviceId);
		
		Locale locale = getBaseContext().getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		setSystemProperty("microedition.locale", language);
		if(this.contentResolver == null) {
			this.contentResolver = getContentResolver();
		}
		this.appProperties = new HashMap<String,String>();
		//#= this.appProperties.put("MIDlet-Name", "${MIDlet-Name}");
		//#= this.appProperties.put("MIDlet-Vendor", "${MIDlet-Vendor}");
		//#= this.appProperties.put("MIDlet-Version", "${MIDlet-Version}");
		
		
		// read files directory and save it as a system property
		String appDirectory = getApplicationContext().getFilesDir().getAbsolutePath();
		setSystemProperty("fileconn.dir.private", appDirectory);
	}

	public void backlightOn() {
//		if(this.wakeLock != null && this.wakeLock.isHeld()) {
//			//#debug
//			System.out.println("The backlight was requested to go on but the wakeLock is already held. I return without doing anything.");
//			return;
//		}
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "Skobbler Wake Lock");
//		this.wakeLock.acquire();
//		//#debug
//		System.out.println("WakeLock acquired?"+this.wakeLock.isHeld());
		AndroidDisplay.getDisplay(this).setKeepScreenOn(true);
	}
	
	/**
	 * You need to call this method when you switched on the light with {@link #backlightOn()}.
	 */
	public void backlightRelease() {
//		if(this.wakeLock != null && this.wakeLock.isHeld()) {
//			this.wakeLock.release();
//		}
		AndroidDisplay.getDisplay(this).setKeepScreenOn(false);
	}
	
	protected void setSystemProperty(String name, String value) {
		if(value == null) {
			value = "";
		}
		// Hidden because midp does not like this method
		//#= System.setProperty(name,value);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//#debug
		System.out.println("Config changed:"+newConfig);
		Locale locale = newConfig.locale;
		String language = locale.getLanguage();
		setSystemProperty("microedition.locale", language);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return AndroidDisplay.getDisplay(this).onKeyDown(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return AndroidDisplay.getDisplay(this).onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
	{
		return AndroidDisplay.getDisplay(this).onKeyMultiple(keyCode, repeatCount, event);
	}
	
	protected void onPause() {
		//#debug
		System.out.println("onPause().");
		try {
			pauseApp();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	
	protected void onRestart() {
		//#debug
		System.out.println("onRestart().");
		super.onRestart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	
	protected void onResume() {
		//#debug
		System.out.println("onResume().");
		super.onResume();
		AndroidDisplay display = AndroidDisplay.getDisplay(this);
		setContentView(display);
		// This should allow to control the audio volume with the volume keys on the handset when the application has focus.
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.shuttingDown = false;
		// Needed to redraw the any previous screens of a previous run. So the application need not call setCurrent in the case of a rerun.
		display.refresh();
		try {
			startApp();
		} catch (Exception e) {
			e.printStackTrace();
			//#debug fatal
			System.out.println("starApp() failed: " + e);
			//TODO: add fatal error handling here, e.g. by displaying system error message
		}
	}

	protected void onStart() {
		//#debug
		System.out.println("onStart().");
		super.onStart();
		//Debug.startMethodTracing("skobbler");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	
	protected void onStop() {
		//#debug
		System.out.println("onStop().");
		//#if polish.android1.5
			hideSoftKeyboard();
		//#endif
		//Debug.stopMethodTracing();
		pauseApp();
		// Release the wake lock if it was acquired.
		backlightRelease();
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	
	protected void onDestroy() {
		//#debug
		System.out.println("onDestroy().");
		//#if polish.android1.5
			hideSoftKeyboard();
		//#endif
		//Debug.stopMethodTracing();
		super.onDestroy();
		//TODO: Use listener pattern to register and unregister Lifecycle listeners.
//		deregisterSqlDao();
		AndroidDisplay display = AndroidDisplay.getDisplay(this);
		display.shutdown();
		if ( ! this.shuttingDown) {
			this.shuttingDown = true;
			try {
				destroyApp(true);
			} catch (MIDletStateChangeException e) {
				//
			}
		}
		if(this.suicideOnExit) {
			int myPid = Process.myPid();
			Process.killProcess(myPid);
		}
	}

	// TODO:
//	private void deregisterSqlDao() {
//		SqlDao.getInstance().destroy();
//	}

	/**
	 * Signals the <code>MIDlet</code> that it has entered the <em>Active</em>
	 * state. In the <em>Active</EM> state the <code>MIDlet</code> may
	 * hold resources.
	 * The method will only be called when
	 * the <code>MIDlet</code> is in the <em>Paused</em> state.
	 * <p>
	 * Two kinds of failures can prevent the service from starting,
	 * transient and non-transient.  For transient failures the
	 * <code>MIDletStateChangeException</code> exception should be thrown.
	 * For non-transient failures the <code>notifyDestroyed</code>
	 * method should be called.
	 * <p>
	 * If a Runtime exception occurs during <code>startApp</code> the
	 * MIDlet will be
	 * destroyed immediately.  Its <code>destroyApp</code> will be
	 * called allowing
	 * the MIDlet to cleanup.
	 * 
	 * @throws MIDletStateChangeException - is thrown if the MIDlet cannot start now but might be able to start at a later time.
	 */
	protected abstract void startApp() throws MIDletStateChangeException;

	/**
	 * Signals the <code>MIDlet</code> to enter the <em>Paused</em> state.
	 * In the <em>Paused</em> state the <code>MIDlet</code> must release
	 * shared resources and become quiescent. This method will only be called
	 * called when the <code>MIDlet</code> is in the <em>Active</em> state.
	 * <p>
	 * <p>
	 * If a Runtime exception occurs during <code>pauseApp</code> the MIDlet
	 * will be destroyed immediately. Its <code>destroyApp</code> will be
	 * called allowing the MIDlet to cleanup.
	 * </DL>
	 * 
	 */
	protected abstract void pauseApp();

	/**
	 * Signals the <code>MIDlet</code> to terminate and enter the
	 * <em>Destroyed</em> state. In the destroyed state the
	 * <code>MIDlet</code> must release all resources and save any persistent
	 * state. This method may be called from the <em>Paused</em> or
	 * <em>Active</em> states.
	 * <p>
	 * <code>MIDlet</code>s should perform any operations required before
	 * being terminated, such as releasing resources or saving preferences or
	 * state.
	 * <p>
	 * 
	 * <strong>Note:</strong> The <code>MIDlet</code> can request that it not
	 * enter the <em>Destroyed</em> state by throwing an
	 * <code>MIDletStateChangeException</code>. This is only a valid response
	 * if the <code>unconditional</code> flag is set to <code>false</code>.
	 * If it is <code>true</code> the <code>MIDlet</code> is assumed to be
	 * in the <em>Destroyed</em> state regardless of how this method
	 * terminates. If it is not an unconditional request, the
	 * <code>MIDlet</code> can signify that it wishes to stay in its current
	 * state by throwing the <code>MIDletStateChangeException</code>. This
	 * request may be honored and the <code>destroy()</code> method called
	 * again at a later time.
	 * 
	 * <p>
	 * If a Runtime exception occurs during <code>destroyApp</code> then they
	 * are ignored and the MIDlet is put into the <em>Destroyed</em> state.
	 * 
	 * @param unconditional -
	 *            If true when this method is called, the MIDlet must cleanup
	 *            and release all resources. If false the MIDlet may throw
	 *            MIDletStateChangeException to indicate it does not want to be
	 *            destroyed at this time.
	 * @throws MIDletStateChangeException -
	 *             is thrown if the MIDlet wishes to continue to execute (Not
	 *             enter the Destroyed state). This exception is ignored if
	 *             unconditional is equal to true.
	 */
	protected abstract void destroyApp(boolean unconditional)
			throws MIDletStateChangeException;

	/**
	 * Used by an <code>MIDlet</code> to notify the application management
	 * software that it has entered into the <em>Destroyed</em> state. The
	 * application management software will not call the MIDlet's
	 * <code>destroyApp</code> method, and all resources held by the
	 * <code>MIDlet</code> will be considered eligible for reclamation. The
	 * <code>MIDlet</code> must have performed the same operations (clean up,
	 * releasing of resources etc.) it would have if the
	 * <code>MIDlet.destroyApp()</code> had been called.
	 * </DL>
	 * 
	 */
	public final void notifyDestroyed() {
		if( ! this.shuttingDown) {
			this.shuttingDown = true;
			super.finish();
		}
		
	}

	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}



	/**
	 * Notifies the application management software that the MIDlet does not
	 * want to be active and has entered the <em>Paused</em> state. Invoking
	 * this method will have no effect if the <code>MIDlet</code> is
	 * destroyed, or if it has not yet been started.
	 * <p>
	 * It may be invoked by the <code>MIDlet</code> when it is in the
	 * <em>Active</em> state.
	 * <p>
	 * 
	 * If a <code>MIDlet</code> calls <code>notifyPaused()</code>, in the
	 * future its <code>startApp()</code> method may be called make it active
	 * again, or its <code>destroyApp()</code> method may be called to request
	 * it to destroy itself.
	 * <p>
	 * 
	 * If the application pauses itself it will need to call
	 * <code>resumeRequest</code> to request to reenter the
	 * <code>active</code> state.
	 * </DL>
	 * 
	 */
	public final void notifyPaused() {
		//TODO: Trigger the lifecycle but do not call the lifecycle notification methods directly.
//		onPause();
	}

	/**
	 * Provides a <code>MIDlet</code> with a mechanism to retrieve named
	 * properties from the application management software. The properties are
	 * retrieved from the combination of the application descriptor file and the
	 * manifest. For trusted applications the values in the manifest MUST NOT be
	 * overridden by those in the application descriptor. If they differ, the
	 * MIDlet will not be installed on the device. For untrusted applications,
	 * if an attribute in the descriptor has the same name as an attribute in
	 * the manifest the value from the descriptor is used and the value from the
	 * manifest is ignored.
	 * 
	 * @param key -
	 *            the name of the property
	 * @return A string with the value of the property. null is returned if no
	 *         value is available for the key.
	 * @throws NullPointerException -
	 *             is thrown if key is null.
	 */
	public final String getAppProperty(String key) {
		String value = this.appProperties.get(key);
		//#debug
		System.out.println("AppProperty for key '"+key+"' is '"+value+"'");
		return value;
	}

	/**
	 * Provides a <code>MIDlet</code> with a mechanism to indicate that it is
	 * interested in entering the <em>Active</em> state. Calls to this method
	 * can be used by the application management software to determine which
	 * applications to move to the <em>Active</em> state.
	 * <p>
	 * When the application management software decides to activate this
	 * application it will call the <code>startApp</code> method.
	 * <p>
	 * The application is generally in the <em>Paused</em> state when this is
	 * called. Even in the paused state the application may handle asynchronous
	 * events such as timers or callbacks.
	 * </DL>
	 * 
	 */
	public final void resumeRequest() {
		// TODO implement resumeRequest
	}

	/**
	 * <p>
	 * Requests that the device handle (for example, display or install) the
	 * indicated URL.
	 * </p>
	 * 
	 * <p>
	 * If the platform has the appropriate capabilities and resources available,
	 * it SHOULD bring the appropriate application to the foreground and let the
	 * user interact with the content, while keeping the MIDlet suite running in
	 * the background. If the platform does not have appropriate capabilities or
	 * resources available, it MAY wait to handle the URL request until after
	 * the MIDlet suite exits. In this case, when the requesting MIDlet suite
	 * exits, the platform MUST then bring the appropriate application (if one
	 * exists) to the foreground to let the user interact with the content.
	 * </p>
	 * 
	 * <p>
	 * This is a non-blocking method. In addition, this method does NOT queue
	 * multiple requests. On platforms where the MIDlet suite must exit before
	 * the request is handled, the platform MUST handle only the last request
	 * made. On platforms where the MIDlet suite and the request can be handled
	 * concurrently, each request that the MIDlet suite makes MUST be passed to
	 * the platform software for handling in a timely fashion.
	 * </p>
	 * 
	 * <p>
	 * If the URL specified refers to a MIDlet suite (either an Application
	 * Descriptor or a JAR file), the application handling the request MUST
	 * interpret it as a request to install the named package. In this case, the
	 * platform's normal MIDlet suite installation process SHOULD be used, and
	 * the user MUST be allowed to control the process (including cancelling the
	 * download and/or installation). If the MIDlet suite being installed is an
	 * <em>update</em> of the currently running MIDlet suite, the platform
	 * MUST first stop the currently running MIDlet suite before performing the
	 * update. On some platforms, the currently running MIDlet suite MAY need to
	 * be stopped before any installations can occur.
	 * </p>
	 * 
	 * <p>
	 * If the URL specified is of the form <code>tel:&lt;number&gt;</code>,
	 * as specified in <a href="http://www.ietf.org/rfc/rfc2806.txt">RFC2806</a>,
	 * then the platform MUST interpret this as a request to initiate a voice
	 * call. The request MUST be passed to the &quot;phone&quot; application to
	 * handle if one is present in the platform. The &quot;phone&quot;
	 * application, if present, MUST be able to set up local and global phone
	 * calls and also perform DTMF post dialing. Not all elements of RFC2806
	 * need be implemented, especially the area-specifier or any other
	 * requirement on the terminal to know its context. The isdn-subaddress,
	 * service-provider and future-extension may also be ignored. Pauses during
	 * dialing are not relevant in some telephony services.
	 * </p>
	 * 
	 * <p>
	 * Devices MAY choose to support additional URL schemes beyond the
	 * requirements listed above.
	 * </p>
	 * 
	 * <p>
	 * Many of the ways this method will be used could have a financial impact
	 * to the user (e.g. transferring data through a wireless network, or
	 * initiating a voice call). Therefore the platform MUST ask the user to
	 * explicitly acknowlege each request before the action is taken.
	 * Implementation freedoms are possible so that a pleasant user experience
	 * is retained. For example, some platforms may put up a dialog for each
	 * request asking the user for permission, while other platforms may launch
	 * the appropriate application and populate the URL or phone number fields,
	 * but not take the action until the user explicitly clicks the load or dial
	 * buttons.
	 * </p>
	 * 
	 * @param url the URL for the platform to load. An empty string (not null)
	 *            cancels any pending requests.
	 * @return true if the MIDlet suite MUST first exit before the content can
	 *         be fetched.
	 * @throws ConnectionNotFoundException -
	 *             if the platform cannot handle the URL requested.
	 * @since MIDP 2.0
	 */
	public final boolean platformRequest(String url) throws ConnectionNotFoundException{
		if(url == null) {
			throw new IllegalArgumentException("Parameter 'url' must not be null.");
		}
		if("".equals(url)) {
			// TODO: Cancel pending requests.
			return false;
		}
		if(url.startsWith("tel:")) {
			String number = url.substring(4);
			// The line is hidden from the IDE as eclipse uses the MIDP String which does not implement CharSequence.
			boolean matches = false;
			//#= matches = java.util.regex.Pattern.compile("\\+?\\d+").matcher(number).matches();
			if(!matches) {
				throw new ConnectionNotFoundException("The telephone number '"+number+"' is malformed. It must be described by the regular expression '\\+?\\d+'");
			}
			Intent i = new Intent();
			i.setAction(Intent.ACTION_DIAL);
			Uri numberUri = Uri.parse("tel:"+number);
			//#debug
			System.out.println("Uri for phone number:"+numberUri);
			i.setData(numberUri);
			startActivity(i);
			return false;
		}
		if(url.startsWith("http:") || url.startsWith("https:")) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			return false;
		}
		throw new ConnectionNotFoundException("The url '"+url+"' can not behandled. The url scheme is not supported");
	}

	/**
	 * Get the status of the specified permission. If no API on the device
	 * defines the specific permission requested then it must be reported as
	 * denied. If the status of the permission is not known because it might
	 * require a user interaction then it should be reported as unknown.
	 * 
	 * @param permission -
	 *            to check if denied, allowed, or unknown.
	 * @return 0 if the permission is denied; 1 if the permission is allowed; -1
	 *         if the status is unknown
	 * @since MIDP 2.0
	 */
	public final int checkPermission(String permission) {
		return -1;
		// TODO implement checkPermission
	}

	public void showSoftKeyboard() {
		//#debug
		System.out.println("Handling showSoftKeyboard");
		//#if polish.android1.5
			
			Configuration configuration;
			configuration = getBaseContext().getResources().getConfiguration();
			//#debug
			System.out.println("Configuration before showing softkeyboard is '"+configuration+"'");
		
			if (this.isSoftkeyboardOpen) {
				//#debug
				System.out.println("Canceling the display of the softkeybard as it is already visible.");
				return;
			}
			//#debug
			System.out.println("About to show softkeyboard");
			
			// TODO: This code is not needed as android does this test for us?!
//			if(configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
//				//#debug
//				System.out.println("Canceling the display of the softkeyboard because the hardkeyboard is already shown.");
//				return;
//			}
			AndroidDisplay display = AndroidDisplay.getDisplay(this);
			if(display == null) {
				//#debug
				System.out.println("Canceling the display of the softkeyboard as no AndroidDisplay is available.");
				return;
			}
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			boolean active;
			active = inputMethodManager.isActive(display);
			//#debug
			System.out.println("Input method before showing is active: '"+active+"'");
			// requestingFocus is important! Do not remove it!
			display.requestFocus();
			inputMethodManager.hideSoftInputFromWindow(display.getWindowToken(), 0, new ResultReceiver(display.getHandler()) {
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					//#debug
					System.out.println("Result for hiding softinput (before showing) is '"+resultCode+"' with resultData '"+resultData+"'");
				}
			});
			inputMethodManager.showSoftInput(display, InputMethodManager.SHOW_FORCED,new ResultReceiver(display.getHandler()) {
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					//#debug
					System.out.println("Result for showing softinput is '"+resultCode+"' with resultData '"+resultData+"'");
				}
			});
			active = inputMethodManager.isActive(display);
			//#debug
			System.out.println("Input method after showing is active: '"+active+"'");
			configuration = getBaseContext().getResources().getConfiguration();
			//#debug
			System.out.println("Configuration after showing softkeyboard is '"+configuration+"'");
			// TODO: Find out the height of the soft input!!
//				View rootView = display.getRootView();
//				ArrayList<View> touchables = rootView.getTouchables();
//				if(touchables == null || touchables.isEmpty()) {
//					//#debug
//					System.out.println("No touchable view found in RootView.");
//				} else {
//					for (View view : touchables) {
//						//#debug
//						System.out.println("Found touchable view with id '"+view.getId()+"'");
//					}
//				}
//				View focusView = rootView.findFocus();
//				if(focusView != null) {
//					//#debug
//					System.out.println("View with focus is '"+focusView.getId()+"'");
//				} else {
//					//#debug
//					System.out.println("No view with focus in RootView found.");
//				}
//				Window window = getWindow();
			this.isSoftkeyboardOpen = true;
			onSoftKeyboardOpened();
		//#endif
	}

	public void hideSoftKeyboard() {
		//#debug
		System.out.println("Handling hideSoftKeyboard");
		//#if polish.android1.5
		Configuration configuration;
		configuration = getBaseContext().getResources().getConfiguration();
		//#debug
		System.out.println("Configuration before hiding softkeyboard is '"+configuration+"'");
		
		// TODO: Do not use a self managed field but the configuration or InputMethodManager.isActive or something similar.
		if (!this.isSoftkeyboardOpen) {
			//#debug
			System.out.println("Canceling the hiding of the softkeyboard as it is not visible anyway.");
			return;
		}
		//#debug
		System.out.println("About to show softkeyboard");
		AndroidDisplay display = AndroidDisplay.getDisplay(this);
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
		boolean active;
		active = inputMethodManager.isActive(display);
		//#debug
		System.out.println("Input method before hiding is active: '"+active+"'");
		IBinder windowToken = display.getWindowToken();
		inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
		active = inputMethodManager.isActive(display);
		//#debug
		System.out.println("Input method after hiding is active: '"+active+"'");
		configuration = getBaseContext().getResources().getConfiguration();
		//#debug
		System.out.println("Configuration after hiding softkeyboard is '"+configuration+"'");
		this.isSoftkeyboardOpen = false;
		onSoftKeyboardClosed();
		//#endif
	}
	
	/**
	 * Shows the virtual keyboard when it is hidden and hides it when it is shown.
	 */
	public void toggleSoftKeyboard() {
		//#debug
		System.out.println("Handling toggleSoftKeyboard");
		//#if polish.android1.5
		
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			Configuration configuration;
//			if(configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
//				if(this.isSoftkeyboardOpen) {
//					hideSoftKeyboard();
//				}
//				return;
//			}
			configuration = getBaseContext().getResources().getConfiguration();
			//#debug
			System.out.println("Configuration before toggling softkeyboard is '"+configuration+"'");
			
			AndroidDisplay display = AndroidDisplay.getDisplay(this);
//			boolean active = inputMethodManager.isActive(display);
//			if(active) {
			IBinder windowToken = display.getWindowToken();
			boolean active;
			active = inputMethodManager.isActive(display);
			//#debug
			System.out.println("Input method before toggle is active: '"+active+"'");
			//#debug
			System.out.println("Toggling the softinput method.");
			inputMethodManager.toggleSoftInputFromWindow(windowToken,  InputMethodManager.SHOW_FORCED, 0);
				//inputMethodManager.hideSoftInputFromWindow(windowToken, 0);			
//			}
			active = inputMethodManager.isActive(display);
			//#debug
			System.out.println("Input method after toogle is active: '"+active+"'");
			configuration = getBaseContext().getResources().getConfiguration();
			//#debug
			System.out.println("Configuration after toggling softkeyboard is '"+configuration+"'");
			this.isSoftkeyboardOpen = !this.isSoftkeyboardOpen;
			if (this.isSoftkeyboardOpen) {
				onSoftKeyboardOpened();
			} else {
				onSoftKeyboardClosed();
			}
		//#endif
	}
	
	private Screen getCurrentScreen() {
		Display display = Display.getInstance();
		if (display == null) {
			return null;
		}
		Displayable disp = display.getCurrent();
		if (disp == null || (!(disp instanceof Screen))) {
			return null;
		}
		return (Screen) disp;
	}
	
	private void onSoftKeyboardOpened() {
		Screen screen = getCurrentScreen();
		if (screen != null) {
			Item item = screen.getCurrentItem();
			if (item != null) {
				while (item instanceof Container && ((Container)item).getFocusedItem() != null) {
					item = ((Container)item).getFocusedItem();
				}
				this.currentScreenYOffset = screen.getScrollYOffset();
				int contAbsY = 0;
				Container cont = screen.getRootContainer();
				if (cont != null) {
					contAbsY = cont.getAbsoluteY() + (screen.getScreenContentY() - cont.relativeY);
					//System.out.println("contentY=" + screen.getScreenContentY() + ", cont.relativeY=" + cont.relativeY + ", cont.absY=" + cont.getAbsoluteY() + ", item.absY=" + item.getAbsoluteY());
				}
				int newYOffset = - (item.getAbsoluteY() - contAbsY); // - this.currentScreenYOffset;
				screen.setScrollYOffset( newYOffset, true);
			}
		}
	}

	private void onSoftKeyboardClosed() {
		Screen screen = getCurrentScreen();
		if (screen != null) {
			screen.setScrollYOffset(this.currentScreenYOffset, true);
		}
	}

	/**
	 * 
	 * @param suicideOnExit true if the process should be killed after the destroy event is received.
	 */
	public void setSuicideOnExit(boolean suicideOnExit) {
		this.suicideOnExit = suicideOnExit;
	}

	public void switchInputMethod() {
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showInputMethodPicker();
	}

	public void onSizeChanged( int w, int h) {
		//#if polish.android1.5
			hideSoftKeyboard();
			this.isSoftkeyboardOpen = false;
		//#endif
	}
	
	
	
	public synchronized void addCommand( de.enough.polish.ui.Command cmd ) {
		//#if !tmp.fullscreen
			if (!this.addedCommands.contains(cmd)) {
				this.addedCommands.add( cmd );
			}
		//#endif
	}
	
	public synchronized void removeCommand( de.enough.polish.ui.Command cmd ) {
		//#if !tmp.fullscreen
			this.addedCommands.remove( cmd );
		//#endif
	}
	
	
	//#if !tmp.fullScreen
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			this.isMenuOpened = true;
			menu.clear();
			this.addedCommandMenuItemBridges.clear();
			Object[] commands = this.addedCommands.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command cmd = (Command) commands[i];
				if (cmd == null) {
					break;
				}
				int groupId = 0;
				int itemId = this.addedCommandMenuItemBridges.size();
				MenuItem item = menu.add( groupId, itemId, cmd.getPriority(), cmd.getLabel() );
				CommandMenuItemBridge bridge = new CommandMenuItemBridge( cmd, item );
				this.addedCommandMenuItemBridges.add(bridge);
			}
		}
		return super.onPrepareOptionsMenu(menu) || true;
	}
	//#endif

	//#if !tmp.fullScreen
	public boolean onOptionsItemSelected(MenuItem item) {
		Object[] items = this.addedCommandMenuItemBridges.getInternalArray();
		for (int i = 0; i < items.length; i++) {
			CommandMenuItemBridge bridge = (CommandMenuItemBridge) items[i];
			if (bridge == null) {
				break;
			}
			if (bridge.menuItem == item) {
				Display.getInstance().commandAction(bridge.cmd, (de.enough.polish.ui.Displayable)null );
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	//#endif

	//#if !tmp.fullScreen
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		this.isMenuOpened = false;
	}
	//#endif
	
	
	//#if !tmp.fullScreen
	public boolean onBack() {
		if (this.isMenuOpened) {
			return false;
		}
		//#if polish.android1.5
			if (this.isSoftkeyboardOpen) {
				hideSoftKeyboard();
				return true;
			}
		//#endif
		Command cmdBack = null;
		Object[] commands = this.addedCommands.getInternalArray();
		for (int i = 0; i < commands.length; i++) {
			Command cmd = (Command) commands[i];
			if (cmd == null) {
				break;
			}
			int type = cmd.getCommandType();
			if (type == Command.BACK || type == Command.CANCEL || type == Command.EXIT) {
				if (cmdBack == null || cmdBack.getPriority() > cmd.getPriority()) {
					cmdBack = cmd;
				}
			}
		}
		if (cmdBack == null && this.addedCommands.size() == 1 && ((Command)commands[0]).getCommandType() == Command.OK) {
			// this seems to be an alert with only the OK command present:
			cmdBack = (Command)commands[0];
		}
		if (cmdBack != null) {
			Display.getInstance().commandAction(cmdBack, (de.enough.polish.ui.Displayable)null );
			return true;
		}
		return false;
	}
	//#endif
	
	//#if !tmp.fullScreen
	public boolean onOK() {
		Command cmdOK = null;
		Object[] commands = this.addedCommands.getInternalArray();
		for (int i = 0; i < commands.length; i++) {
			Command cmd = (Command) commands[i];
			if (cmd == null) {
				break;
			}
			int type = cmd.getCommandType();
			if (type == Command.OK) {
				if (cmdOK == null || cmdOK.getPriority() > cmd.getPriority()) {
					cmdOK = cmd;
				}
			}
		}
		if (cmdOK != null) {
			Display.getInstance().commandAction(cmdOK, (de.enough.polish.ui.Displayable)null );
			return true;
		}
		return false;
	}
	//#endif


	static class CommandMenuItemBridge {
		final Command cmd;
		final MenuItem menuItem;

		public CommandMenuItemBridge(de.enough.polish.ui.Command cmd, MenuItem menuItem) {
			this.cmd = cmd;
			this.menuItem = menuItem;
		}
		
	}









}
