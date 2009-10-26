/**
 * 
 */
package de.enough.polish.calendar;

import java.util.Date;

import de.enough.polish.io.Serializable;

/**
 * @author Ramakrishna
 *
 */
public class CalendarListener implements Serializable {
	
	protected boolean notifyDaySelected(Date day) {
		return true;
	}
	
	protected boolean notifyEntrySelected(CalendarEntry entry) {
		return true;
	}
	
	protected boolean notifyTimeSelected(Date time) {
		return true;
	}
	
	protected boolean notifyEntryStarted(CalendarEntry entry) {
		return true;
	}

}
