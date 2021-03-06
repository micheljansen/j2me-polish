//#condition polish.usePolishGui

package de.enough.polish.ui;

import javax.microedition.lcdui.Image;

import de.enough.polish.event.AsynchronousMultipleCommandListener;

/**
 * The <code>Command</code> class is a construct that encapsulates the semantic information of an action. 
 * In J2ME Polish a command is also an IconItem, so it can be designed using normal //#style directives during
 * the construction, etc.
 * The behavior that the command  activates is not encapsulated in this object. This means that command
 * contains only information about &quot;command&quot; not the actual action
 * that happens when command is activated. The action is defined in a
 * <A HREF="CommandListener.html"><CODE>CommandListener</CODE></A>
 * associated
 * with the <code>Displayable</code>. <code>Command</code> objects are
 * <em>presented</em>
 * in the user interface and the way they are presented
 * may depend on the semantic information contained within the command.
 * 
 * <P><code>Commands</code> may be implemented in any user interface
 * construct that has
 * semantics for activating a single action. This, for example, can be a soft
 * button, item in a menu, or some other direct user interface construct.
 * For example, a
 * speech interface may present these commands as voice tags. </P>
 * 
 * <P>The mapping to concrete user interface constructs may also depend on the
 * total number of the commands.
 * For example, if an application asks for more abstract commands than can
 * be mapped onto
 * the available physical buttons on a device, then the device may use an
 * alternate human interface such as a menu. For example, the abstract
 * commands that
 * cannot be mapped onto physical buttons are placed in a menu and the label
 * &quot;Menu&quot; is mapped onto one of the programmable buttons. </P>
 * 
 * <p>A command contains four pieces of information: a <em>short label</em>,
 * an optional <em>long label</em>, a
 * <em>type</em>, and a <em>priority</em>.
 * One of the labels is used for the visual
 * representation of the command, whereas the type and the priority indicate
 * the semantics of the command. </p>
 * 
 * <a name="label"></a>
 * <h3>Labels</h3>
 * 
 * <p> Each command includes one or two label strings.  The label strings are
 * what the application requests to be shown to the user to represent this
 * command. For example, one of these strings may appear next to a soft button
 * on the device or as an element in a menu. For command types other than
 * <code>SCREEN</code>, the labels provided may be overridden by a
 * system-specific label
 * that is more appropriate for this command on this device. The contents of
 * the label strings are otherwise not interpreted by the implementation. </p>
 * 
 * <p>All commands have a short label.  The long label is optional.  If the
 * long label is not present on a command, the short label is always used.
 * </p>
 * 
 * <p>The short label string should be as short as possible so that it
 * consumes a minimum of screen real estate.  The long label can be longer and
 * more descriptive, but it should be no longer than a few words.  For
 * example, a command's short label might be &quot;Play&quot;, and its
 * long label
 * might be &quot;Play Sound Clip&quot;.</p>
 * 
 * <p>The implementation chooses one of the labels to be presented in the user
 * interface based on the context and the amount of space available.  For
 * example, the implementation might use the short label if the command
 * appears on a soft button, and it might use the long label if the command
 * appears on a menu, but only if there is room on the menu for the long
 * label.  The implementation may use the short labels of some commands and
 * the long labels of other commands, and it is allowed to switch between
 * using the short and long label at will.  The application cannot determine
 * which label is being used at any given time.  </p>
 * 
 * <a name="type"></a>
 * <h3>Type</h3>
 * 
 * <p> The application uses the command
 * type to specify the intent of this command. For example, if the
 * application specifies that the command is of type
 * <code>BACK</code>, and if the device
 * has a standard of placing the &quot;back&quot; operation on a
 * certain soft-button,
 * the implementation can follow the style of the device by using the semantic
 * information as a guide. The defined types are
 * <A HREF="Command.html#BACK"><CODE>BACK</CODE></A>,
 * <A HREF="Command.html#CANCEL"><CODE>CANCEL</CODE></A>,
 * <A HREF="Command.html#EXIT"><CODE>EXIT</CODE></A>,
 * <A HREF="Command.html#HELP"><CODE>HELP</CODE></A>,
 * <A HREF="Command.html#ITEM"><CODE>ITEM</CODE></A>,
 * <A HREF="Command.html#OK"><CODE>OK</CODE></A>,
 * <A HREF="Command.html#SCREEN"><CODE>SCREEN</CODE></A>,
 * and
 * <A HREF="Command.html#STOP"><CODE>STOP</CODE></A>. </p>
 * 
 * <a name="priority"></a>
 * <h3>Priority</h3>
 * 
 * <p> The application uses the priority
 * value to describe the importance of this command relative to other commands
 * on the same screen. Priority values are integers, where a lower number
 * indicates greater importance. The actual values are chosen by the
 * application. A priority value of one might indicate the most important
 * command, priority values of two, three, four, and so on indicate commands
 * of lesser importance. </p>
 * 
 * <p>Typically,
 * the implementation first chooses the placement of a command based on
 * the type of command and then places similar commands based on a priority
 * order. This could mean that the command with the highest priority is
 * placed so that user can trigger it directly and that commands with lower
 * priority are placed on a menu. It is not an error for there to be commands
 * on the same screen with the same priorities and types. If this occurs, the
 * implementation will choose the order in which they are presented. </p>
 * 
 * <p>For example, if the application has the following set of commands: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 * <pre><code>
 * new Command("Buy", Command.ITEM, 1);
 * new Command("Info", Command.ITEM, 1);
 * new Command("Back", Command.BACK, 1);    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * An implementation with two soft buttons may map the
 * <code>BACK</code> command to
 * the right
 * soft button and create an &quot;Options&quot; menu on the left soft
 * button to contain
 * the other commands.<BR>
 * <IMG SRC="doc-files/command1.gif" width=190 height=268><BR>
 * When user presses the left soft button, a menu with the two remaining
 * <code>Commands</code> appears:<BR>
 * <IMG SRC="doc-files/command2.gif" width=189 height=260><BR>
 * If the application had three soft buttons, all commands can be mapped
 * to soft buttons:
 * <BR><IMG SRC="doc-files/command3.gif" width=189 height=261></P>
 * 
 * <p>The application is always responsible for providing the means for the
 * user to progress through different screens. An application may set up a
 * screen that has no commands. This is allowed by the API but is generally
 * not useful; if this occurs the user would have no means to move to another
 * screen. Such program would simply considered to be in error. A typical
 * device should provide a means for the user to direct the application manager
 * to kill the erroneous application.
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class Command
//#if polish.midp
extends javax.microedition.lcdui.Command
//#elif polish.android
//# extends de.enough.polish.android.lcdui.Command
//#endif
{
	
	
	/**
	 * Specifies an application-defined command that pertains to the current
	 * screen. Examples could be &quot;Load&quot; and
	 * &quot;Save&quot;.  A <code>SCREEN</code> command
	 * generally applies to the entire screen's contents or to navigation
	 * among screens.  This is in constrast to the <CODE>ITEM</CODE> type,
	 * which applies to the currently activated or focused item or element
	 * contained within this screen.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>SCREEN</code>.</P></DL>
	 * 
	 */
	public static final int SCREEN = 1;

	/**
	 * A navigation command that returns the user to the logically
	 * previous screen.
	 * The jump to the previous screen is not done automatically by the
	 * implementation
	 * but by the <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A>
	 * provided by
	 * the application.
	 * Note that the application defines the actual action since the strictly
	 * previous screen may not be logically correct.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>BACK</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="Command.html#STOP"><CODE>STOP</CODE></A></DL>
	 * 
	 */
	public static final int BACK = 2;

	/**
	 * A command that is a standard negative answer to a dialog implemented by
	 * current screen.
	 * Nothing is cancelled automatically by the implementation; cancellation
	 * is implemented
	 * by the <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A> provided by
	 * the application.
	 * 
	 * <p> With this command type, the application hints to the implementation
	 * that the user wants to dismiss the current screen without taking any
	 * action
	 * on anything that has been entered into the current screen, and usually
	 * that
	 * the user wants to return to the prior screen. In many cases
	 * <code>CANCEL</code> is
	 * interchangeable with <code>BACK</code>, but <code>BACK</code>
	 * is mainly used for navigation
	 * as in a browser-oriented applications. </p>
	 * 
	 * <P>Value <code>3</code> is assigned to <code>CANCEL</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="Command.html#STOP"><CODE>STOP</CODE></A></DL>
	 * 
	 */
	public static final int CANCEL = 3;

	/**
	 * A command that is a standard positive answer to a dialog implemented by
	 * current screen.
	 * Nothing is done automatically by the implementation; any action taken
	 * is implemented
	 * by the <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A> provided by
	 * the application.
	 * 
	 * <p> With this command type the application hints to the
	 * implementation that
	 * the user will use this command to ask the application to confirm
	 * the data
	 * that has been entered in the current screen and to proceed to the next
	 * logical screen. </p>
	 * 
	 * <P><code>CANCEL</code> is often used together with <code>OK</code>.</P>
	 * 
	 * <P>Value <code>4</code> is assigned to <code>OK</code>.</P>
	 * <DT><B>See Also: </B>
	 * 
	 */
	public static final int OK = 4;

	/**
	 * This command specifies a request for on-line help.
	 * No help information is shown automatically by the implementation.
	 * The
	 * <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A> provided by the
	 * application is responsible for showing the help information.
	 * 
	 * <P>Value <code>5</code> is assigned to <code>HELP</code>.</P></DL>
	 * 
	 */
	public static final int HELP = 5;

	/**
	 * A command that will stop some currently running
	 * process, operation, etc.
	 * Nothing is stopped automatically by the implementation.
	 * The cessation must
	 * be performed
	 * by the <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A> provided by
	 * the application.
	 * 
	 * <p> With this command type the application hints to the
	 * implementation that
	 * the user will use this command to stop any currently running process
	 * visible to the user on the current screen. Examples of running processes
	 * might include downloading or sending of data. Use of the
	 * <code>STOP</code>
	 * command does
	 * not necessarily imply a switch to another screen. </p>
	 * 
	 * <P>Value <code>6</code> is assigned to <code>STOP</code>.</P>
	 * <DT><B>See Also: </B>
	 * <A HREF="Command.html#CANCEL"><CODE>CANCEL</CODE></A></DL>
	 * 
	 */
	public static final int STOP = 6;

	/**
	 * A command used for exiting from the application.  When the user
	 * invokes this command, the implementation does not exit automatically.
	 * The application's
	 * <A HREF="CommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)"><CODE>commandAction</CODE></A>
	 * will be called, and it should exit the application if it
	 * is appropriate to do so.
	 * 
	 * <P>Value <code>7</code> is assigned to <code>EXIT</code>.</P></DL>
	 * 
	 */
	public static final int EXIT = 7;

	/**
	 * With this command type the application can hint to the
	 * implementation that the command is specific to the items of
	 * the <code>Screen</code> or the elements of a
	 * <code>Choice</code>. Normally this
	 * means that command relates to the focused item or element.
	 * For example, an implementation of <code>List</code> can use
	 * this information for
	 * creating context sensitive menus.
	 * 
	 * <P>Value <code>8</code> is assigned to <code>ITEM</code>.</P></DL>
	 * 
	 * 
	 */
	public static final int ITEM = 8;

	private String longLabel;
	private int commandType;
	private int priority;
	
	private Container children;
	protected boolean hasChildren;
	private int childIndicatorWidth = 5;
	private int childIndicatorYOffset;
	private int childIndicatorHeight = 5;
	//#if polish.css.command-child-indicator
		private Image childIndicator;
	//#endif
	//#if polish.css.command-child-indicator-color
		private int childIndicatorColor = -1;
	//#endif
	private boolean isOpen;

	private Style style;

	private String label;

	private ItemCommandListener itemCommandListener;

	private CommandListener commandListener;

	/**
	 * Creates a new command object with the given short
	 * 
	 * <a href="#label">label</a>,
	 * <a href="#type">type</a>, and
	 * <a href="#priority">priority</a>.
	 * 
	 * The newly created command has no long label.  This constructor is
	 * identical to <code>Command(label, null, commandType, priority)</code>.
	 * 
	 * @param label the command's short label
	 * @param commandType the command's type
	 * @param priority the command's priority value
	 * @throws NullPointerException if label is null
	 * @throws IllegalArgumentException if the commandType is an invalid type
	 * @see #Command(String, String, int, int)
	 * @since  J2ME Polish 2.1
	 */
	public Command( String label, int commandType, int priority)
	{
		this( label, label, commandType, priority, null );
	}
	
	/**
	 * Creates a new command object with the given short
	 * 
	 * <a href="#label">label</a>,
	 * <a href="#type">type</a>, and
	 * <a href="#priority">priority</a>.
	 * 
	 * The newly created command has no long label.  This constructor is
	 * identical to <code>Command(label, null, commandType, priority)</code>.
	 * 
	 * @param label the command's short label
	 * @param commandType the command's type
	 * @param priority the command's priority value
	 * @param style the style for this command
	 * @throws NullPointerException if label is null
	 * @throws IllegalArgumentException if the commandType is an invalid type
	 * @see #Command(String, String, int, int)
	 * @since  J2ME Polish 2.1
	 */
	public Command( String label, int commandType, int priority, Style style)
	{
		this( label, label, commandType, priority, style );
	}

	/**
	 * Creates a new command object with the given
	 * <a href="#label">labels</a>,
	 * <a href="#type">type</a>, and
	 * <a href="#priority">priority</a>.
	 * 
	 * <p>The short label is required and must not be
	 * <code>null</code>.  The long label is
	 * optional and may be <code>null</code> if the command is to have
	 * no long label.</p>
	 * 
	 * @param shortLabel the command's short label
	 * @param longLabel the command's long label, or null if none
	 * @param commandType the command's type
	 * @param priority the command's priority value
	 * @throws NullPointerException if shortLabel is null
	 * @throws IllegalArgumentException if the commandType is an invalid type
	 * @since  J2ME Polish 2.1
	 */
	public Command( String shortLabel, String longLabel, int commandType, int priority)
	{
		this( shortLabel, longLabel, commandType, priority, null );
	}
	/**
	 * Creates a new command object with the given
	 * <a href="#label">labels</a>,
	 * <a href="#type">type</a>, and
	 * <a href="#priority">priority</a>.
	 * 
	 * <p>The short label is required and must not be
	 * <code>null</code>.  The long label is
	 * optional and may be <code>null</code> if the command is to have
	 * no long label.</p>
	 * 
	 * @param shortLabel the command's short label
	 * @param longLabel the command's long label, or null if none
	 * @param commandType the command's type
	 * @param priority the command's priority value
	 * @param style the style of this command
	 * @throws NullPointerException if shortLabel is null
	 * @throws IllegalArgumentException if the commandType is an invalid type
	 * @since  J2ME Polish 2.1
	 */
	public Command( String shortLabel, String longLabel, int commandType, int priority, Style style)
	{
		//#if polish.midp1
			//# super( shortLabel, commandType, priority);
		//#else
			super( shortLabel, longLabel, commandType, priority);
		//#endif
		this.label = shortLabel;
		this.longLabel = longLabel;
		this.commandType = commandType;
		this.priority = priority;
		this.style = style;
	}

	/**
	 * Gets the short label of the command.
	 * 
	 * @return the Command's short label
	 */
	public String getLabel()
	{
		return this.label;
	}

	/**
	 * Gets the long label of the command.
	 * 
	 * @return the Command's long label, or null if the Command has no long label
	 * @since  MIDP 2.0
	 */
	public String getLongLabel()
	{
		return this.longLabel;
	}

	/**
	 * Gets the type of the command.
	 * 
	 * @return type of the Command
	 */
	public int getCommandType()
	{
		return this.commandType;
	}

	/**
	 * Gets the priority of the command.
	 * 
	 * @return priority of the Command
	 */
	public int getPriority()
	{
		return this.priority;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (!(o instanceof Command)) {
			return false;
		}
		Command co = (Command) o;
		return co.priority == this.priority && co.commandType == this.commandType && co.label.equals(this.label);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.label.hashCode() | this.priority | (this.commandType << 3);
	}
	
	//#if polish.LibraryBuild
		/**
		 * Sets the command listener for this screen
		 * 
		 * @param listener the listener
		 */
		public void setItemCommandListener(javax.microedition.lcdui.ItemCommandListener listener) {
			// ignore
		}
	//#endif
	/**
	 * Sets a command listener for this command.
	 * @param listener the listener, use null to remove the current listener
	 */
	public void setItemCommandListener( ItemCommandListener listener ) {
		this.itemCommandListener = listener;
	}
	
	/**
	 * Retrieves the command listener for this command
	 * @return the command listener, might be null
	 */
	public ItemCommandListener getItemCommandListener() {
		return this.itemCommandListener;
	}
	
	//#if polish.LibraryBuild
		/**
		 * Sets the command listener for this screen
		 * 
		 * @param listener the listener
		 */
		public void setCommandListener(javax.microedition.lcdui.CommandListener listener) {
			// ignore
		}
	//#endif
	/**
	 * Sets a command listener for this command.
	 * @param listener the listener, use null to remove the current listener
	 */
	public void setCommandListener( CommandListener listener ) {
		this.commandListener = listener;
	}
	
	/**
	 * Retrieves the command listener for this command
	 * @return the command listener, might be null
	 */
	public CommandListener getCommandListener() {
		return this.commandListener;
	}

	/**
	 * Triggers this command
	 * @param item the corresponding item
	 * @param displayable the corresponding screen
	 * @return true when this command was forwarded
	 */
	public boolean commandAction( Item item, Displayable displayable ) {
		if (item != null && this.itemCommandListener != null) {
			while (item instanceof Container && (item.commands == null || !item.commands.contains(this)) ) {
				item = ((Container)item).getFocusedItem();
			}
			if (item != null && item.commands != null && item.commands.contains(this)) {
				this.itemCommandListener.commandAction(this, item);
				return true;
			}
		}
		if (displayable != null && this.commandListener != null) {
			//#if polish.executeCommandsAsynchrone
				AsynchronousMultipleCommandListener.getInstance().commandAction(this.commandListener, this, displayable);
			//#else
				this.commandListener.commandAction(this, displayable);
			//#endif
			return true;
		}
		return false;
	}

	/**
	 * Retrieves the style for this command.
	 * @return the style, can be null
	 */
	public Style getStyle() {
		return this.style;
	}
	
	/**
	 * Sets the style for this command. 
	 * Note that you need to set the style before adding the command to a screen, otherwise no effect will be seen. 
	 * @param style the new style
	 */
	public void setStyle(Style style) {
		this.style = style;
	}
	
}
