/**
 * 
 */
package de.enough.polish.calendar;

import de.enough.polish.io.Serializable;

/**
 * @author Ramakrishna
 *
 */
public class CalendarCategory implements Serializable {
	private String name;
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
