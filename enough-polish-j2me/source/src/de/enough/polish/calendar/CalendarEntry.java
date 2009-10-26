/**
 * 
 */
package de.enough.polish.calendar;

import de.enough.polish.io.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;


/**
 * Calendar Entry class provides access to data in events provided in the calendar.
 * @author Ramakrishna
 * @author Nagendra Sharma
 * 
 */
public class CalendarEntry implements Serializable {

		
	/**
	 * field to contain starting date of calendar event 
	 */
	private Date startDate;
	
	/**
	 * field to contain ending date of calendar event
	 */
	private Date endDate;
	
	/**
	 * field to contain date of creation of calendar event
	 */
	private Date createdDate;
	
	/**
	 * field to contain last modified date of calendar event
	 */
	private Date lastModifiedDate;
	
	/**
	 * field to contain time stamp of calendar event
	 */
	private Date timeStamp;
	
	/**
	 * field to contain whether is all day of calendar event
	 */
	private boolean isAllday;
	
	/**
	 * field to contain duration of minutes of calendar event
	 */
	private int durationInMinutes;
	
	/**
	 * field to contain reoccurence of calendar event
	 */
	private int reoccurence;
	
	/**
	 * field to contain sequence of calendar event
	 */
	private int sequence;
	
	/**
	 * field to contain summary of calendar event
	 */
	private String summary;
	
	
	/**
	 * field to contain alarm interval value 
	*/
	private int alarm;
	
	/**
	 * field to contain notes of calendar entry
	 */
	private String notes;
	
	/**
	 * a collection variable to hold any device specific fields in the form of name/value pairs.
	 */
	private Hashtable otherFields = new Hashtable();
	
	/**
	 * field to contain location of calendar event
	 */
	private String location;
	
	/**
	 * field to contain description of calendar event
	 */
	private String description;
	
	/**
	 * field to contain organizer of calendar event
	 */
	private String organizer;
	
	/**
	 * field to contain status of calendar event
	 */
	private String status;
	
	/**
	 * field to contain type of calendar event
	 */
	private String type;
	
	/**
	 * field to contain id of calendar event
	 */
	private String id;
	
	/**
	 * field to contain classType of calendar event
	 */
	private String classType;
	
	/**
	 * field to contain category of calendar event
	 */
	private CalendarCategory category;
	
	/**
	 * field to contain details of alarm 
	 */
	private CalendarAlarm calendarAlarm;
	
	

	/**
	 * field to contain the details for repeat rule
	 */
	transient private EventRepeatRule eventRepeatRule;

	
	/**
	 * field to contain timeZone of calendar event
	 */
	private transient TimeZone timeZone;
	
		
	/**
	 * Constructor for CalendarEntry
	 */
	public CalendarEntry() {
        
	}

	/**
	 * Overloaded constructor of CalendarEntry to initialize staring date and description of calendar event
	 * @param startDate
	 * @param description
	 */
	public CalendarEntry(Date startDate,String description) {
		this.startDate=startDate;
		this.description = description;
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 */
	CalendarEntry(Date startDate, Date endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 * @param location
	 * @param reoccurence
	 * @param organizer
	 */
	CalendarEntry(Date startDate, Date endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes,String location,	int reoccurence,String organizer) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		this.location=location;
		this.reoccurence=reoccurence;
		this.organizer=organizer;
		
	}
	
	/**
	 * @return returns alarm setting of calendar entry
	 */
	public CalendarAlarm getCalendarAlarm() {
		return this.calendarAlarm;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public CalendarCategory getCategory() {
		return this.category;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public String getDescription() {
		this.description = this.description != null ? this.description: "";
		return this.description;
	}
	
	/**
	 * @return returns starting date of calendar entry
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	
	/**
	 * @return returns ending date of calendar entry
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	
	/**
	 * @return returns time stamp of calendar entry
	 */
	public Date getTimeStamp() {
		return this.timeStamp;
	}
	
	/**
	 * @return returns date of creation of calendar entry
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * @return returns last modified date of calendar entry
	 */
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * @return returns local time zone of calendar entry
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	/**
	 * @return returns duration in minutes of calendar entry
	 */
	public int getDurationInMinutes() {
		return this.durationInMinutes;
	}
	
	/**
	 * @return returns organizer of calendar entry
	 */
	public String getOrganizer() {
		this.organizer = this.organizer != null ? this.organizer: "";
		return this.organizer;
	}
	
	/**
	 * @return returns location of calendar entry
	 */
	public String getLocation() {
		this.location = this.location != null ? this.location: "";
		return this.location;
	}
	
	/**
	 * @return returns reoccurence of calendar entry
	 */
	public int getReoccurence() {
		return this.reoccurence;
	}
	
	/**
	 * @return returns sequence of calendar entry
	 */
	public int getSequence() {
		return this.sequence;
	}
	
	/**
	 * @return returns status of calendar entry
	 */
	public String getStatus() {
		this.status = this.status != null ? this.status: "";
		return this.status;
	}
	
	/**
	 * @return returns type of calendar entry
	 */
	public String getType() {
		this.type = this.type != null ? this.type: "";
		return this.type;
	}

	/**
	 * @return returns isAllday of calendar entry
	 */
	public boolean isAllday() {
		return this.isAllday;
	}
	
		
	/**
	 * @return returns id of calendar entry
	 */
	public String getId() {
		this.id = this.id != null ? this.id: "";
		return this.id;
	}
	
	/**
	 * @return returns classType of calendar entry
	 */
	public String getClassType() {
		this.classType = this.classType != null ? this.classType: "";
		return this.classType;
	}
	
	/**
	 * @return returns summary of calendar entry
	 */
	public String getSummary() {
		this.summary = this.summary != null ? this.summary: "";
		return this.summary;
	}

	/**
	 * setter method for CalendarCategory
	 * @param category
	 */
	public void setCategory(CalendarCategory category) {
		this.category = category;
	}
	
	
	
	/**
	 * setter method for calendarAlarm
	 * @param calendarAlarm
	 */
	public void setCalendarAlarm(CalendarAlarm calendarAlarm) {
		this.calendarAlarm = calendarAlarm;
	}

	/**
	 * setter method to set starting date and duration in minutes for calendar entry
	 * @param startDate
	 * @param durationInMinutes
	 */
	public void setDate(Date startDate, int durationInMinutes) {
		this.startDate = startDate;
		this.durationInMinutes = durationInMinutes;
	}

	/**
	 * setter method for event description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * setter method for start date 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * setter method for end date
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * setter method for time stamp details
	 * @param timeStamp
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	
	/**
	 * setter method for date of creation
	 * @param createdDate
	 */
	public void setCreatedDate(Date createdDate) {
	  this.createdDate=createdDate;
	}

	/**
	 * setter method for last modified date
	 * @param lastModifiedDate
	 */
	public void  setLastModifiedDate(Date lastModifiedDate) {
	 this.lastModifiedDate=lastModifiedDate;
	}

	
	
	/**
	 * setter method for local time zone
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	

	/**
	 * setter method for duration in minutes
	 * @param durationInMinutes
	 */
	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	
	

	/**
	 * setter method for organizer
	 * @param organizer
	 */
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	

	/**
	 * setter method for location
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	

	/**
	 * setter method for reoccurence
	 * @param reoccurence
	 */
	public void setReoccurence(int reoccurence) {
		this.reoccurence = reoccurence;
	}

	
	/**
	 * setter method for status
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * setter method for type
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	

	/**
	 * setter method for id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	

	/**
	 * setter method for classType of calendar entry
	 * @param classType
	 */
	public void setClassType(String classType) {
		this.classType = classType;
	}

	/**
	 * setter method for isAllday of calendar entry
	 * @param isAllday
	 */
	public void setAllday(boolean isAllday) {
		this.isAllday = isAllday;
	}

	
	/**
	 * setter method for sequence of calendar entry
	 * @param sequence
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	
	/**
	 * setter method for summary of calendar entry
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return returns the repeat rule of calendar entry
	 */
	public EventRepeatRule getRepeat() {
		return this.eventRepeatRule;
	}

	/**
	 * setter method for repeat rule of calendar entry
	 * @param eventRepeatRule
	 */
	public void setRepeat(EventRepeatRule eventRepeatRule) {
		this.eventRepeatRule = eventRepeatRule;
	}

	/**
	 * @return the alarm value
	 */
	public int getAlarm() {
		return this.alarm;
	}

	/**
	 * @param alarm sets the alarm to the given value 
	 */
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	/**
	 * @return notes gets the notes for this calendar entry  
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * @param notes sets the notes for this calendar entry
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return otherFields gets the the other fields of this calendar entry
	 */
	public Hashtable getOtherFields() {
		return this.otherFields;
	}

	/**
	 * @param otherFields gets the the other fields of this calendar entry
	 */
	public void setOtherFields(Hashtable otherFields) {
		this.otherFields = otherFields;
	}
	
	
	
	
	
	

}
