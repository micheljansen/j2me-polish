/*
 * Created on Mar 15, 2009 at 11:16:05 PM.
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

/**
 * <p>Eases the interaction with calendars</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalendarHelper
{
	/**
	 * Days per month - note that February might be 28 or 29 days, depending on whether the current year is a leap year
	 */
	private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	private final Calendar calendar;

	/**
	 * Creates a new helper
	 * @param calendar the calendar
	 */
	public CalendarHelper( Calendar calendar) {
		this.calendar = calendar;
	}
	
	public void nextMonth() {
		nextMonth( this.calendar );
	}

	/**
	 * @param cal
	 */
	public static void nextMonth(Calendar cal)
	{
		int month = cal.get( Calendar.MONTH );
		if (month < Calendar.DECEMBER) {
			month++;
			cal.set( Calendar.MONTH, month );
		} else {
			int year = cal.get( Calendar.YEAR );
			cal.set( Calendar.YEAR, year + 1 );
			cal.set( Calendar.MONTH, Calendar.JANUARY );
		}
	}

	public void previousMonth() {
		previousMonth( this.calendar );
	}

	/**
	 * @param cal
	 */
	public static void previousMonth(Calendar cal)
	{
		int month = cal.get( Calendar.MONTH );
		if (month > Calendar.JANUARY) {
			month--;
			cal.set( Calendar.MONTH, month );
		} else {
			int year = cal.get( Calendar.YEAR );
			cal.set( Calendar.YEAR, year - 1 );
			cal.set( Calendar.MONTH, Calendar.DECEMBER );
		}
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

}
