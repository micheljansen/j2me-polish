package de.enough.polish.calendar;

import java.util.Date;

import de.enough.polish.io.Serializable;

/**
 * @author Nagendra Sharma
 * 
 */
public class EventRepeatRule  implements Serializable{

	/**
	 * field to contain interval of repeat
	 */
	private int interval;
	
	/**
	 * field to contain by which day of the month of repeat
	 */
	private int byMonthDay;
	
	/**
	 * field to contain date of end of repeat
	 */
	private Date untilDate;
	
	/**
	 * field to contain which day of repeat
	 */
	private String byDay;
	
	/**
	 * field to contain frequency of repeat
	 */
	private String frequency;
	
	/**
	 * field to contain starting day of week according to calendar
	 */
	private String weekStart;

	/**
	 * @return returns which day of month for repeat
	 */
	public String getByDay() {
		return this.byDay;
	}

	/**
	 * setter method for day of month for repeat
	 * @param byDay 
	 */
	public void setByDay(String byDay) {
		this.byDay = byDay;
	}

	/**
	 * @return returns which day of week for repeat
	 */
	public int getByMonthDay() {
		return this.byMonthDay;
	}

	/**
	 * setter method for which day of week for repeat
	 * @param byMonthDay
	 */
	public void setByMonthDay(int byMonthDay) {
		this.byMonthDay = byMonthDay;
	}

	/**
	 * @return returns frequency of repeat
	 */
	public String getFrequency() {
		return this.frequency;
	}

	/**
	 * setter method for frequency of repeat
	 * @param frequency
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return returns interval of repeat
	 */
	public int getInterval() {
		return this.interval;
	}

	/**
	 * setter method for interval of repeat
	 * @param interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * @return returns the end of repeat
	 */
	public Date getUntilDate() {
		return this.untilDate;
	}

	/**
	 * setter method for the end of repeat
	 * @param untilDate
	 */
	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}

	/**
	 * 
	 * @return returns staring day for week of repeat
	 */
	public String getWeekStart() {
		return this.weekStart;
	}

	/**
	 * setting method of staring day for week of repeat
	 * @param weekStart
	 */
	public void setWeekStart(String weekStart) {
		this.weekStart = weekStart;
	}

}
