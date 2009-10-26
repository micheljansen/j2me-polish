/**
 * 
 */
package de.enough.polish.calendar;

import java.util.Date;

import de.enough.polish.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;

import de.enough.polish.io.Serializable;
/**
 * @author Ramakrishna
 *
 */
public class CalendarModel implements Serializable {
	private int day;
	private transient TimeZone timeZone; 
	private CalendarListener listener;
	private Hashtable calendarEntries;
	
	/**
	 * Default constructor initializes calendar entries
	*/
	public CalendarModel() {
		this.calendarEntries = new Hashtable();
	}
	
	/**
	* sets first day of week
	* @param day day value
	*/
	public void setFirstDayOfWeek(int day ) {
		this.day = day;
	}
	
	/**
	* Adds calendar entry on specified date
	* @param date Given Date
	* @param entry Calendar Entry
	*/
	public void addEntry(Date date, CalendarEntry entry) {
		this.calendarEntries.put(date, entry);
	}
	
	/**
	* sets the calendar Time Zone
	* @param timeZone Calendar Time Zone
	*/
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	* gets the day of calendar 
	* @return day value
	*/
	public int getDay() {
		return this.day;
	}
	
	/**
	* gets the calendar Time Zone
	* @return Calendar Time Zone
	*/
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	/**
	* getter method for the calendar listener
	* @return CalendarListener
	*/
	public CalendarListener getListener() {
		return this.listener;
	}
	
	/**
	* setter method for the calendar listener
	* @param listener CalendarListener
	*/
	public void setListener(CalendarListener listener) {
		this.listener = listener;
	}
	
	
	/**
	* gets all event entries on specified date
	* @param day date on which calendar entries have to be returned
	 * @return all calendar entry, null when none are defined
	*/
	public CalendarEntry[] getEntries(Date day ) {
		if(this.calendarEntries != null && this.calendarEntries.size() > 1) {
			if(this.calendarEntries.get(day) != null) {
				return (CalendarEntry[])this.calendarEntries.get(day);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	* gets all event entries on specified date of specific category
	* @param day date on which calendar entries have to be returned
	*/
	public CalendarEntry[] getEntries(Date day, CalendarCategory category ) {
		CalendarEntry[] calEntries = (CalendarEntry[])this.calendarEntries.get(day);
		int size = calEntries.length;
		
		ArrayList filteredEntries = new ArrayList();
		for(int i = 0 ; i < size ; i++) {
			if(calEntries[i].getCategory().getName().equals(category.getName())) {
				filteredEntries.add(calEntries[i]);
			}
		}
		return (CalendarEntry[])filteredEntries.toArray();
	}
	
	/**
	* gets all event entries from start date to end date
	* @param start date on which calendar entries have to be returned as a start date
	* @param end date on which calendar entries have to be returned as a end date
	*/
	public CalendarEntry[] getEntries(Date start, Date end) {
		ArrayList filteredEntries = new ArrayList();
		if(start.getTime() <= end.getTime()) {
			Enumeration keysEnumeration = this.calendarEntries.keys();
			while(keysEnumeration.hasMoreElements()) {
				Date key = (Date)keysEnumeration.nextElement();
				if(key.getTime() >= start.getTime() && key.getTime() <= end.getTime()) {
					CalendarEntry[] calEntries = (CalendarEntry[])this.calendarEntries.get(key);
					int size = calEntries.length;
					for(int i = 0 ; i < size ; i++) {
						filteredEntries.add(calEntries[i]);
					}
				}
			}
			return (CalendarEntry[])filteredEntries.toArray();
		} else {
			return null;
		}
		
	}
	
	/**
	* gets all event entries from start date to end date of specific calendar category
	* @param startDate date on which calendar entries have to be returned as a start date
	* @param endDate date on which calendar entries have to be returned as a end date
	*/
	public CalendarEntry[] getEntries(Date startDate, Date endDate, CalendarCategory category) {
		ArrayList filteredEntries = new ArrayList();
		if(startDate.getTime() <= endDate.getTime()) {
			Enumeration keysEnumeration = calendarEntries.keys();
			while(keysEnumeration.hasMoreElements()) {
				Date key = (Date)keysEnumeration.nextElement();
				if(key.getTime() >= startDate.getTime() && key.getTime() <= endDate.getTime()) {
					CalendarEntry[] calEntries = (CalendarEntry[])calendarEntries.get(key);
					int size = calEntries.length;
					for(int i = 0 ; i < size ; i++) {
						if(calEntries[i].getCategory().getName().equals(category.getName())) {
							filteredEntries.add(calEntries[i]);
						}
					}
				}
			}
			return (CalendarEntry[])filteredEntries.toArray();
		} else {
			return null;
		}
	}


}
