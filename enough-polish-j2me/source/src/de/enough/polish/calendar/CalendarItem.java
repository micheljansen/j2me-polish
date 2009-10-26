//#condition polish.usePolishGui
/*
 * Created on Mar 15, 2009 at 12:29:46 PM.
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
package de.enough.polish.calendar;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TableItem;
import de.enough.polish.util.TextUtil;

/**
 * <p>Displays a calendar for a specific month</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalendarItem extends TableItem
{
	
	/**
	 * Show mode for displaying the current month and year within the label of this CalendarItem.
	 */
	public static final int SHOW_MODE_LABEL = 0;
	/**
	 * Show mode for displaying the current month and year within the title of this CalendarItem's screen.
	 */
	public static final int SHOW_MODE_TITLE = 1;
	/**
	 * Show mode for displaying the current month and year within the item that has been specified with setMonthItem().
	 */
	public static final int SHOW_MODE_ITEM = 2;
	
	/**
	 * Days per month - note that February might be 28 or 29 days, depending on whether the current year is a leap year
	 */
	private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	/**
	 * first day of the week can be Sunday or Monday depending on the country/religion.
	 * ISO mandates Monday is the first day of the week, so we use this as default, unless
	 * polish.CalendarItem.FirstDayOfWeek is set to "Sunday".
	 */
	private static int FIRST_DAY_OF_WEEK =
		//#if ${lowercase(polish.CalendarItem.FirstDayOfWeek)} == sunday
			//# Calendar.SUNDAY;
		//#else
			Calendar.MONDAY;
		//#endif
	private static String WEEKDAY_ABBREVIATIONS = 
		//#if ${lowercase(polish.CalendarItem.FirstDayOfWeek)} == sunday
			//# "S,M,T,W,T,F,S";
		//#else
			"M,T,W,T,F,S,S";
		//#endif
	private static String MONTHS = "January,February,March,April,May,June,July,August,September,October,November,December";

	private Calendar calendar;
	private int shownMonth;
	private int shownYear;
	private int showMode;
	private int lastMonth;
	private int lastYear;
	private int lastDay;
	
	

	/**
	 * Retrieves the first day of the week, typically either Calendar.SUNDAY or Calendar.MONDAY.
	 * @return the first day of the week in Calendar format
	 */
	public static int getFirstDayOfWeek()
	{
		return FIRST_DAY_OF_WEEK;
	}

	/**
	 * Sets the first day of the week, typically this is either Monday (ISO, Europe) or Sunday (e.g. US)
	 * @param firstDayOfWeek the first day of the week, e.g. Calendar.MONDAY
	 */
	public static void setFirstDayOfWeek(int firstDayOfWeek)
	{
		FIRST_DAY_OF_WEEK = firstDayOfWeek;
	}
	
	/**
	 * Retrieves a comma separated list of abbreviations for week days.
	 * @return the day abbreviations, by default "M,T,W,T,F,S,S"
	 */
	public static String getWeekDayAbbreviations() {
		return WEEKDAY_ABBREVIATIONS;
	}
	
	/**
	 * Sets the comma separated list of abbreviations for week days
	 * @param abbreviations the list of abbreviations, e.g. "Mo,Tu,We,Th,Fr,Sa,Su"
	 */
	public static void setWeekDayAbbreviations( String abbreviations ) {
		WEEKDAY_ABBREVIATIONS = abbreviations;
	}
	
	/**
	 * Retrieves the names of the month in a comma separated list.
	 * @return the names of the months, by default "January,February,March,..."
	 */
	public static String getMonths() {
		return MONTHS;
	}
	
	/**
	 * Sets the names of the month in a comma separated list.
	 * @param months the names of the months, e.g. "Jan,Feb,Mar,..."
	 */
	public static void setMonths(String months) {
		MONTHS = months;
	}


	/**
	 * Creates a new Calendar Item with the current month shown.
	 */
	public CalendarItem()
	{
		this( Calendar.getInstance(), null );
	}

	/**
	 * Creates a new Calendar Item with the current month shown.
	 * 
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Style style)
	{
		this(Calendar.getInstance(),style);
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be dislayed by default.
	 */
	public CalendarItem(Calendar cal)
	{
		this( cal, null );
	}

	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be dislayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Calendar cal, Style style)
	{
		// depending on the month up to 6 rows my be used, typically 5 are enough + 1 for the day abbreviations
		//TODO where are the year and the month name shown?
		super( 7, 7, style); 
		this.calendar = cal;
		
//		cal.set( Calendar.YEAR, 2009);
//		cal.set( Calendar.MONTH, Calendar.FEBRUARY);
		
		String[] abbreviations = TextUtil.split( WEEKDAY_ABBREVIATIONS, ',' );
		for (int i = 0; i < abbreviations.length; i++)
		{
			String abbreviation = abbreviations[i];
			//#style calendarWeekday?
			StringItem item = new StringItem( null, abbreviation);
			set( i, 0, item );
		}
		
		this.lastYear = this.calendar.get( Calendar.YEAR );
		this.lastMonth = this.calendar.get( Calendar.MONTH );
		this.lastDay = this.calendar.get( Calendar.DAY_OF_MONTH );
		
		buildCalendar( cal );
		setSelectionMode( SELECTION_MODE_CELL | SELECTION_MODE_INTERACTIVE );
	}
	
	/**
	 * @param cal
	 */
	private void buildCalendar(Calendar cal)
	{
		this.ignoreRepaintRequests = true;
		
		int selRow = getSelectedRow();
		int selCol = getSelectedColumn();

		int calMonth = cal.get( Calendar.MONTH );
		int calYear = cal.get( Calendar.YEAR );
		this.shownMonth = calMonth;
		this.shownYear = calYear;
		
		String infoText = TextUtil.split( MONTHS, ',')[calMonth] + " " + calYear;
		if (this.showMode == SHOW_MODE_LABEL) {
			setLabel( infoText );
		} else if (this.showMode == SHOW_MODE_TITLE) {
			Screen scr = getScreen();
			if (scr != null) {
				scr.setTitle( infoText );
			}
		}
		
		cal.set( Calendar.DAY_OF_MONTH, 1);
		int dayOfWeek = cal.get( Calendar.DAY_OF_WEEK);
		boolean isInCurrentMonth = (calMonth == this.lastMonth && calYear == this.lastYear);
		
		int col = getColumn(dayOfWeek);
		int row = 1;
		//#debug
		System.out.println(infoText + ": dayOfWeek=" + dayOfWeek + "(" + getDayOfWeekName(dayOfWeek) + "), col=" + col + ", row=" + row + ", daysInMonth=" + getDaysInMonth(cal));

		if (col > 0) {
			Calendar previous = Calendar.getInstance();
			if (calMonth == 0) {
				previous.set( Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
				previous.set( Calendar.MONTH, Calendar.DECEMBER );
			} else {
				previous.set( Calendar.YEAR, cal.get(Calendar.YEAR) );
				previous.set( Calendar.MONTH, calMonth - 1 );			
			}
			int daysInMonth = getDaysInMonth( previous );
			for (int day = daysInMonth; --col >= 0; ) {
				//#style calendarDayInactive?
				StringItem item = new StringItem( null, Integer.toString( day ));
				item.setAppearanceMode( INTERACTIVE );
				set( col, row, item);			
				day--;
			}
			col = getColumn(dayOfWeek);
		}
		
		StringItem item;
	
		int daysInMonth = getDaysInMonth(cal);
		for (int day = 1; day <= daysInMonth; day++) {
			if (isInCurrentMonth && day == this.lastDay) {
				//#style calendarCurrentday?
				item = new StringItem( null, Integer.toString( day ));				
			} else {
				//#style calendarDay?
				item = new StringItem( null, Integer.toString( day ));
			}
			item.setAppearanceMode( INTERACTIVE );
			set( col, row, item);
			col++;
			if (col > 6) {
				col = 0;
				row++;
			}
//			System.out.println("day=" + day + ", col=" + col + ", row=" + row);
		}
		for (int day=1; col < 7; col++) {
			//#style calendarDayInactive?
			item = new StringItem( null, Integer.toString( day ));
			item.setAppearanceMode( INTERACTIVE );
			set( col, row, item);			
			day++;
		}
		
		for (; ++row < 7; ) {
			for (int i=0; i<7; i++) {
				set( i, row, null);
			}
		}
		
		this.ignoreRepaintRequests = false;
		if (selCol != -1 && selRow != -1) {
			setSelectedCell( selCol, selRow );
		} else if (isInCurrentMonth) {
			col = getColumn(dayOfWeek);
			row = (col + this.lastDay) / 7 + 1;
			cal.set( Calendar.DAY_OF_MONTH, this.lastDay );
			col = getColumn( cal.get( Calendar.DAY_OF_WEEK) );
			setSelectedCell( col, row );
		}
		if (this.availableWidth != 0) {
			init( this.availableWidth, this.availableWidth, this.availableHeight );
		}
		repaint();
	}

	/**
	 * @param dayOfWeek
	 * @return
	 */
	private int getColumn(int dayOfWeek)
	{
		return dayOfWeek >= FIRST_DAY_OF_WEEK ? dayOfWeek - FIRST_DAY_OF_WEEK  :  7 + dayOfWeek - FIRST_DAY_OF_WEEK;
	}

	/**
	 * @param dayOfWeek
	 * @return
	 */
	private String getDayOfWeekName(int dayOfWeek)
	{
		String[] days = {null, "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		return days[dayOfWeek];
	}

	public int getDaysInMonth() {
		return getDaysInMonth( this.calendar );
	}
	
	public static int getDaysInMonth(Calendar cal) {
		int daysInMonth = DAYS_PER_MONTH[ cal.get( Calendar.MONTH ) ];
		if (daysInMonth == 28) {
			// this is February, check for leap year:
			int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
			if (dayOfMonth == 29) { // okay, this is easy ;-)
				daysInMonth = 29;
			} else {
				long addedTime = (29L - dayOfMonth) * 24 * 60 * 60 * 1000;
				Date testDate = new Date( cal.getTime().getTime() + addedTime );
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime( testDate );
				if (cal2.get( Calendar.DAY_OF_MONTH) == 29) {
					daysInMonth = 29;
				}
			}
		}
		return daysInMonth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction)
	{
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		if (!handled) {
			if (gameAction == Canvas.LEFT || gameAction == Canvas.UP) {
				goPreviousMonth();
				return true;
			} else if (gameAction == Canvas.RIGHT || gameAction == Canvas.DOWN) {
				goNextMonth();
				return true;
			}
		}
		return handled;
	}

	/**
	 * 
	 */
	public void goNextMonth()
	{
		Calendar cal = Calendar.getInstance();
		int nextMonth = this.shownMonth + 1;
		int nextYear = this.shownYear; 
		if (nextMonth > Calendar.DECEMBER) {
			nextMonth = Calendar.JANUARY;
			nextYear++;
		}
		cal.set( Calendar.DAY_OF_MONTH, 1);
		cal.set( Calendar.MONTH, nextMonth );
		cal.set( Calendar.YEAR, nextYear );
		buildCalendar( cal );
	}

	/**
	 * 
	 */
	public void goPreviousMonth()
	{
		Calendar cal = Calendar.getInstance();
		int nextMonth = this.shownMonth - 1;
		int nextYear = this.shownYear; 
		if (nextMonth < 0) {
			nextMonth = Calendar.DECEMBER;
			nextYear--;
		}
		cal.set( Calendar.DAY_OF_MONTH, 1);
		cal.set( Calendar.MONTH, nextMonth );
		cal.set( Calendar.YEAR, nextYear );
		buildCalendar( cal );
	}

	
	//#if polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY)
	{
		if (relY <= this.contentY && relY >= 0) {
			if (relX <= this.itemWidth / 2) {
				goPreviousMonth();
				return true;
			} else if (relX <= this.itemWidth) {
				goNextMonth();
				return true;
			}
		}
		return super.handlePointerReleased(relX, relY);
	}
	//#endif
	
	public Calendar getSelectedCalendar()
	{
		int col = getSelectedColumn();
		int row = getSelectedRow() - 1;
//		System.out.println("selected col/row=" + col + "/" + row);
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.DAY_OF_MONTH, 1);
		cal.set( Calendar.YEAR, this.shownYear );
		cal.set( Calendar.MONTH, this.shownMonth );
		int startCol = getColumn( cal.get( Calendar.DAY_OF_WEEK) );
//		System.out.println("start col=" + startCol);
		if (row == 0 && col < startCol) {
			// previous month selected:
			CalendarHelper helper = new CalendarHelper( cal );
			helper.previousMonth();
			int days = helper.getDaysInMonth();
			cal.set( Calendar.DAY_OF_MONTH, days + col - startCol + 1 );
		} else {
			int day = (row * 7) - startCol + col + 1;
			int daysInMonth = CalendarHelper.getDaysInMonth(cal);
			if (day <= daysInMonth) {
				cal.set( Calendar.DAY_OF_MONTH, day);
			} else {
				CalendarHelper helper = new CalendarHelper( cal );
				helper.nextMonth();
				cal.set( Calendar.DAY_OF_MONTH, day - daysInMonth );
			}
		}
		//#debug
		System.out.println("detected date=" + cal.get( Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));
		
		return cal;
	}

	/**
	 * @return the selected date
	 */
	public Date getSelectedDate()
	{
		return getSelectedCalendar().getTime();
	}

}
