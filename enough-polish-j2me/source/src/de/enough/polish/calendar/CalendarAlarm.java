package de.enough.polish.calendar;

import de.enough.polish.io.Serializable;


/**
 * 
 * @author Nagendra Sharma
 *
 */
public class CalendarAlarm implements Serializable{
	
	/**
	 * field to contain trigger 
	 */
	private String trigger;
	
	/**
	 * field to contain action to be done
	 */
	private String action;
	
	/**
	 * field to contain description for alarm
	 */
	private String description;
	
	
	/**
	 * @return returns trigger for alarm
	 */
	public String getTrigger() {
		return this.trigger;
	}
	/**
	 * setter method for tigger of alarm
	 * @param trigger
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	/**
	 * @return returns action for alarm
	 */
	public String getAction() {
		return this.action;
	}
	/**
	 * setter method for action of alarm
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return returns description for alarm
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * setter method for description of alarm
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
