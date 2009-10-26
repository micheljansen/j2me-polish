//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Vector;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import de.enough.polish.android.midlet.MIDlet;
import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMItem;

public class ContactDao {

	private final ContactListImpl contactListImpl;
	private ContentResolver contentResolver;

	public ContactDao(ContactListImpl contactListImpl) {
		this.contactListImpl = contactListImpl;
		this.contentResolver = MIDlet.midletInstance.getContentResolver();
	}
	
	/**
	 * 
	 * @return A Vector containing ContactImpl objects.
	 */
	public Vector getAllContacts() {
		Cursor peopleCursor = this.contentResolver.query(People.CONTENT_URI, null, null, null, null);
		Vector contacts = new Vector();
		while(peopleCursor.moveToNext()) {
			ContactImpl contact = getContactFromCursor(peopleCursor);
			contacts.addElement(contact);
		}
		peopleCursor.close();
		return contacts;
	}
	
	/**
	 * 
	 * @param personCursor the cursor object must be at the right position. It is not cleaned up afterwards. The caller is responsible for that.
	 * @return
	 */
	private ContactImpl getContactFromCursor(Cursor personCursor) {
		int columnIndex;
		
		columnIndex = personCursor.getColumnIndex(People._ID);
		int id = personCursor.getInt(columnIndex);
		
		ContactImpl contactImpl = new ContactImpl(id,this.contactListImpl);
		
		putNameIntoContact(personCursor,contactImpl);
		putAddressIntoContact(id,contactImpl);
		putDisplayNameIntoContact(personCursor, contactImpl);
		putNumbersIntoContact(id,contactImpl);
		putNoteIntoContact(personCursor,contactImpl);
		
		contactImpl.setModified(false);
		return contactImpl;
	}
	
	private void putNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NAME);
		String name = personCursor.getString(columnIndex);
		String[] names = new String[ContactListImpl.CONTACT_NAME_FIELD_INFO.numberOfArrayElements];
		names[Contact.NAME_OTHER] = name;
		contactImpl.addStringArray(Contact.NAME,PIMItem.ATTR_NONE, names);
	}

	private void putNumbersIntoContact(int id, ContactImpl contactImpl) {
		String where = Contacts.ContactMethods.PERSON_ID + " == " + id;
		Cursor phoneCursor = this.contentResolver.query(Contacts.Phones.CONTENT_URI, null, where, null, null);
		int dataColumn = phoneCursor.getColumnIndex(Contacts.Phones.NUMBER);
		int typeColumn = phoneCursor.getColumnIndex(Contacts.Phones.TYPE);
		while(phoneCursor.moveToNext()) {
			int androidType = phoneCursor.getInt(typeColumn);
			Integer attribute = convertPhoneTypeToAttribute(androidType);
			String number = phoneCursor.getString(dataColumn);
			contactImpl.addString(Contact.TEL, attribute.intValue(), number);
		}
		phoneCursor.close();
	}

	private void putAddressIntoContact(int id, ContactImpl contact) {
		String where;
		int dataColumn;
		int typeColumn;

		where = Contacts.ContactMethods.PERSON_ID + " == " + id + " AND " + Contacts.ContactMethods.KIND + " == " + Contacts.KIND_POSTAL;
		
		Cursor addressCursor = this.contentResolver.query(Contacts.ContactMethods.CONTENT_URI, null, where, null, null);
		
		dataColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.DATA);
		typeColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.TYPE);
		
		while(addressCursor.moveToNext()) {
			int androidType = addressCursor.getInt(typeColumn);
			Integer attribute = convertAddressTypeToAttribute(androidType);
			String address = addressCursor.getString(dataColumn);
			String[] value = new String[ContactListImpl.CONTACT_ADDR_FIELD_INFO.numberOfArrayElements];
			value[Contact.ADDR_EXTRA] = address;
			contact.addStringArray(Contact.ADDR, attribute.intValue(), value);
		}
		addressCursor.close();
	}

	/**
	 * 
	 * @param addressType
	 * @return returns the Contact.ATTR_ value.
	 */
	private Integer convertAddressTypeToAttribute(int addressType) {
		switch(addressType) {
		case Contacts.ContactMethods.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
		case Contacts.ContactMethods.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
		case Contacts.ContactMethods.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
		case Contacts.ContactMethods.TYPE_CUSTOM: return new Integer(Contact.ATTR_OTHER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}
	
	/**
	 * 
	 * @param addressType
	 * @return returns the Contact.ATTR_ value.
	 */
	private Integer convertPhoneTypeToAttribute(int addressType) {
		switch(addressType) {
			case Contacts.Phones.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
			case Contacts.Phones.TYPE_MOBILE: return new Integer(Contact.ATTR_MOBILE);
			case Contacts.Phones.TYPE_FAX_HOME: return new Integer(Contact.ATTR_FAX|Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_FAX_WORK: return new Integer(Contact.ATTR_FAX|Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_PAGER: return new Integer(Contact.ATTR_PAGER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}
	
	public void persist(ContactImpl contact) {
		final boolean isNew = contact.isNew();
		final Uri personUri;
		final ContentValues values = new ContentValues();
		final long id;
		if(isNew) {
			personUri = this.contentResolver.insert(People.CONTENT_URI, values);
			id = ContentUris.parseId(personUri);
			// insert this contact into "My Contacts" group in order to see it in the Contact viewer
			Contacts.People.addToMyContactsGroup(this.contentResolver, id);
		} else {
			id = contact.getId();
			personUri = ContentUris.withAppendedId(People.CONTENT_URI, id);
		}
		//#debug
		System.out.println("Uri for person is '"+personUri+"' with id '"+id+"'.");

		// Update the name
		int numberOfNames = contact.countValues(Contact.NAME);
		if(numberOfNames > 0) {
			values.clear();
			String[] names = contact.getStringArray(Contact.NAME, 0);
			String aName = names[Contact.NAME_OTHER];
			values.put(People.NAME, aName);
		}
		this.contentResolver.update(personUri, values, null, null);
		
		// Update the Display name
		int numberOfDisplayNames = contact.countValues(Contact.FORMATTED_NAME);
		if(numberOfDisplayNames > 0) {
			values.clear();
			String name = contact.getString(Contact.FORMATTED_NAME, 0);
			values.put(People.DISPLAY_NAME, name);
		}
		this.contentResolver.update(personUri, values, null, null);
		
		// Update the note
		int numberOfNotes = contact.countValues(Contact.NOTE);
		if(numberOfNotes > 0) {
			values.clear();
			String name = contact.getString(Contact.NOTE, 0);
			values.put(People.NOTES, name);
		}
		this.contentResolver.update(personUri, values, null, null);

		// Update the address.
		int numberOfAddresses = contact.countValues(Contact.ADDR);
		for(int i = 0; i < numberOfAddresses; i++) {
			Uri addressUri = Uri.withAppendedPath(personUri, Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			//#debug
			System.out.println("uri for address:"+addressUri);
			values.clear();
			values.put(Contacts.ContactMethods.KIND,new Integer(Contacts.KIND_POSTAL));
			
			int attributes = contact.getAttributes(Contact.ADDR, i);
			int type = convertAttrToAddressType(attributes);
			values.put(Contacts.ContactMethods.TYPE,new Integer(type));
			
			String[] addressElements = contact.getStringArray(Contact.ADDR, i);
			String address = addressElements[Contact.ADDR_EXTRA];
			values.put(Contacts.ContactMethods.DATA,address);
			this.contentResolver.insert(addressUri, values);
		}
		
		// Telephone.
		int numberOfTelephoneNumbers = contact.countValues(Contact.TEL);
		for(int i = 0; i < numberOfTelephoneNumbers; i++) {
			Uri phoneUri = Uri.withAppendedPath(personUri, Contacts.People.Phones.CONTENT_DIRECTORY);
			//#debug
			System.out.println("uri for tel:"+phoneUri);
			values.clear();
			String telephoneNumber = contact.getString(Contact.TEL, i);
			values.put(Contacts.People.Phones.NUMBER,telephoneNumber);
			
			int attributes = contact.getAttributes(Contact.TEL, i);
			int type = convertAttrToTelType(attributes);
			values.put(Contacts.ContactMethods.TYPE,new Integer(type));
			
			this.contentResolver.insert(phoneUri, values);
		}
		
	}
	
	private int convertAttrToAddressType(int attribute) {
		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
			return Contacts.ContactMethods.TYPE_HOME;
		}
		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
			return Contacts.ContactMethods.TYPE_HOME;
		}
		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
			return Contacts.ContactMethods.TYPE_OTHER;
		}
		return Contacts.ContactMethods.TYPE_OTHER;
	}
	
	private int convertAttrToTelType(int attribute) {
		if((attribute & Contact.ATTR_MOBILE) == Contact.ATTR_MOBILE){
			return Contacts.People.Phones.TYPE_MOBILE;
		}
		if((attribute & Contact.ATTR_FAX) == Contact.ATTR_FAX){
			if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
				return Contacts.People.Phones.TYPE_FAX_HOME;
			}
			if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
				return Contacts.People.Phones.TYPE_FAX_WORK;
			}
			return Contacts.People.Phones.TYPE_FAX_WORK;
		}
		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
			return Contacts.People.Phones.TYPE_HOME;
		}
		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
			return Contacts.People.Phones.TYPE_WORK;
		}
		if((attribute & Contact.ATTR_PAGER) == Contact.ATTR_PAGER){
			return Contacts.People.Phones.TYPE_PAGER;
		}
		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
			return Contacts.People.Phones.TYPE_OTHER;
		}
		return Contacts.People.Phones.TYPE_OTHER;
	}
	
	private void putDisplayNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.DISPLAY_NAME);
		String name = personCursor.getString(columnIndex);
		contactImpl.addString(Contact.FORMATTED_NAME, PIMItem.ATTR_NONE, name);
	}

	private void putNoteIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NOTES);
		while(personCursor.moveToNext()) {
			String name = personCursor.getString(columnIndex);
			if(name != null)
				contactImpl.addString(Contact.NOTE, PIMItem.ATTR_NONE, name);
		}
	}

}
