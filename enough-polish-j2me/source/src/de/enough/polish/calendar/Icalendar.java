package de.enough.polish.calendar;

import de.enough.polish.io.Serializable;
import java.util.Vector;
import java.util.TimeZone;

/**
 *
 * @author Nagendra Sharma
 * 
 */

public class Icalendar implements Serializable {
	
	/**
	 * field to contain name of calendar
	 */
	private String name;
	
	/**
	 * field to contain ProductID of calendar 
	 */
	private String productID;
	
	/**
	 * field to contain Version of calendar
	 */
	private String version;
	
	/**
	 * field to contain Scale of calendar
	 */
	private String calscale;
	
	/**
	 * field to contain method of communication
	 */
	private String method;
	
	/**
	 * field to contain time zone of calendar
	 */
	private String calendarTimezone;
	
	/**
	 * field to contain time zone of calendar
	 */
	private String localTimezone;
	
	
	/**
	 * field to contain events in calendar
	 */
	private Vector calendarEntries;
	
	/**
	 * field to contain details of time zone
	 */
	private transient TimeZone timeZone;
	
	
	/**
	 * 
	 * @return time zone details 
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

	/**
	 * setter method for calendar time zone
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return calendar entries
	 */
	public Vector getCalendarEntries() {
		return this.calendarEntries;
	}

	/**
	 * setter method for calendar entries
	 * @param calendarEntries
	 */
	public void setCalendarEntries(Vector calendarEntries) {
		this.calendarEntries = calendarEntries;
	}

	/**
	 * setter method for calendar name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return returns the name of calendar
	 */
	public String getName() {
		this.name = this.name != null ? this.name: "";
		return this.name;
	}

	/**
	 * @return returns product id of calendar
	 */
	public String getProductID() {
		this.productID = this.productID != null ? this.productID: "";
		return this.productID;
	}

	/**
	 * setter method of product id
	 * @param productID
	 */
	public void setProductID(String productID) {
		this.productID = productID;
	}

	/**
	 * @return returns version of calendar
	 */
	public String getVersion() {
		this.version = this.version != null ? this.version: "";
		return this.version;
	}

	/**
	 * setter method for version of calendar
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return returns scale of calendar
	 */
	public String getCalscale() {
		this.calscale = this.calscale != null ? this.calscale: "";
		return this.calscale;
	}

	/**
	 * setter method for scale of calendar
	 * @param calscale
	 */
	public void setCalscale(String calscale) {
		this.calscale = calscale;
	}

	/**
	 * @return returns method name of calendar
	 */
	public String getMethod() {
		this.method = this.method != null ? this.method: "";
		return this.method;
	}

	/**
	 * setter method for method name of calendar
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return returns time zone details of calendar
	 */
	public String getCalendarTimezone() {
		return this.calendarTimezone;
	}

	/**
	 * setter method for time zone details of calendar
	 * @param calendarTimezone
	 */
	public void setCalendarTimezone(String calendarTimezone) {
		this.calendarTimezone = calendarTimezone;
	}

	/**
	 * @return returns time zone details of calendar
	 */
	public String getLocalTimezone() {
		return this.localTimezone;
	}

	/**
	 * setter method for time zone details of calendar
	 * @param localTimezone
	 */
	public void setLocalTimezone(String localTimezone) {
		this.localTimezone = localTimezone;
	}

}
