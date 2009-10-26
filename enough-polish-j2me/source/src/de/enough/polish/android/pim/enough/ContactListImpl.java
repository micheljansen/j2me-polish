//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;
import java.util.Vector;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.UnsupportedFieldException;

// TODO: Put a lot of stuff into a superclass.
public class ContactListImpl implements ContactList {

	public static final FieldInfo CONTACT_ADDR_FIELD_INFO = new FieldInfo(Contact.ADDR,PIMItem.STRING_ARRAY,"Address",7,Contact.ADDR_EXTRA,new int[] {Contact.ADDR_EXTRA}, new int[] {Contact.ATTR_HOME,Contact.ATTR_WORK,Contact.ATTR_OTHER,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NAME_FIELD_INFO = new FieldInfo(Contact.NAME,PIMItem.STRING_ARRAY,"Name",5,Contact.NAME_OTHER,new int[] {Contact.NAME_OTHER},new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_TEL_FIELD_INFO = new FieldInfo(Contact.TEL,PIMItem.STRING,"Phone",0,0,new int[0],new int[] {Contact.ATTR_MOBILE,Contact.ATTR_WORK,Contact.ATTR_HOME,Contact.ATTR_OTHER,Contact.ATTR_PAGER,Contact.ATTR_FAX,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NOTE_FIELD_INFO = new FieldInfo(
			Contact.NOTE,
			PIMItem.STRING,
			"Note",
			0,
			0,
			new int[0],
			new int[] {Contact.ATTR_NONE}
			);
		public static final FieldInfo CONTACT_UID_FIELD_INFO = new FieldInfo(
			Contact.UID,
			PIMItem.STRING,
			"Unique identifier",
			0,
			0,
			new int[0],
			new int[] {Contact.ATTR_NONE}
			);

	private static final FieldInfo[] fieldInfos = new FieldInfo[] {CONTACT_ADDR_FIELD_INFO,CONTACT_NAME_FIELD_INFO,CONTACT_TEL_FIELD_INFO};
	
	private final int mode;
	// TODO: This is a helper class but it is also a dependency...
	private ContactDao contactDao;
	private final String name;

	ContactListImpl(String name, int mode) {
		this.name = name;
		if(mode != PIM.READ_ONLY && mode != PIM.WRITE_ONLY && mode != PIM.READ_WRITE) {
			throw new IllegalArgumentException("The mode '"+mode+"' is not supported.");
		}
		this.mode = mode;
		this.contactDao = new ContactDao(this);
	}

	public void addCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException();
		}
//		ensureListWriteable();
		throw new PIMException("Categories are not supported.");
	}

	public void close() throws PIMException {
		//#debug error
		System.out.println("Method ContactList.close is not implemented.");
	}

	public Contact createContact() {
		ContactImpl contact = new ContactImpl(-1,this);
		return contact;
	}

	public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
		//#debug warn
		System.out.println("Method ContactList.deleteCategory is not implemented.");
	}

	public String getArrayElementLabel(int stringArrayField, int arrayElement) {
		//#debug
		System.out.println("Method ContactList.getArrayElementLabel is not implemended.");
		return "";
	}

	public String getAttributeLabel(int attribute) {
		//#debug
		System.out.println("Method ContactList.getArrayElementLabel is not implemended.");
		return "";
	}

	public String[] getCategories() throws PIMException {
		return new String[0];
	}

	public int getFieldDataType(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId);
		return field.type;
	}

	public String getFieldLabel(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId);
		return field.label;
	}

	public String getName() {
		return this.name;
	}

	public int[] getSupportedArrayElements(int stringArrayField) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		return fieldInfo.supportedArrayElements;
	}

	public int[] getSupportedAttributes(int field) {
		FieldInfo fieldInfo = findFieldInfo(field);
		return fieldInfo.supportedAttributes;
	}

	public int[] getSupportedFields() {
		int[] supportedFields = new int[fieldInfos.length];
		for (int i = 0; i < fieldInfos.length; i++) {
			supportedFields[i] = fieldInfos[i].id;
		}
		return supportedFields;
	}

	public Contact importContact(Contact contact) {
		//#debug warn
		System.out.println("Method ContactList.importContact is not implemented.");
		return null;
	}

	public boolean isCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException();
		}
		//#debug warn
		System.out.println("Method ContactList.isCategory is not implemented.");
		return false;
	}

	public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		int numberOfValues = fieldInfo.supportedArrayElements.length;
		for(int i = 0; i < numberOfValues; i++) {
			if(arrayElement == fieldInfo.supportedArrayElements[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isSupportedAttribute(int field, int attribute) {
		FieldInfo fieldInfo = findFieldInfo(field);
		int numberOfValues = fieldInfo.supportedAttributes.length;
		for(int i = 0; i < numberOfValues; i++) {
			if(attribute == fieldInfo.supportedAttributes[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isSupportedField(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId,false);
		if(field == null) {
			return false;
		}
		return true;
	}

	public Enumeration items() throws PIMException {
		ensureListReadable();
		Vector contacts = this.contactDao.getAllContacts();
		return contacts.elements();
	}

	public Enumeration items(javax.microedition.pim.PIMItem arg0) throws javax.microedition.pim.PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Enumeration items(PIMItem matchingItem) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Enumeration items(String matchingValue) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Enumeration itemsByCategory(String category) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public int maxCategories() {
		return 0;
	}

	public int maxValues(int field) {
		return -1;
	}

	public void removeContact(Contact contact) throws PIMException {
		ensureListWriteable();
		//#debug warn
		System.out.println("Method ContactList.removeContact is not implemented.");
	}

	public void renameCategory(String currentCategory, String newCategory) throws PIMException {
		ensureListWriteable();
		//#debug warn
		System.out.println("Method ContactList.renameCategory is not implemented.");
	}

	public int stringArraySize(int stringArrayField) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		return fieldInfo.numberOfArrayElements;
	}

	FieldInfo findFieldInfo(int fieldId) {
		return findFieldInfo(fieldId,true);
	}
	
	FieldInfo findFieldInfo(int fieldId, boolean throwException) {
		for (int i = 0; i < fieldInfos.length; i++) {
			FieldInfo fieldInfo = fieldInfos[i];
			if(fieldId == fieldInfo.id) {
				return fieldInfo;
			}
		}
		if(throwException) {
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		return null;
	}

	void persist(ContactImpl contact) {
		this.contactDao.persist(contact);
	}
	/**
	 * This method throws a SecurityException if the list is not readable. This is the case if the list was opened in WRITE_ONLY mode.
	 */
	private void ensureListReadable() {
		if(this.mode == PIM.WRITE_ONLY) {
			throw new SecurityException("The list is only writeable.");
		}
	}
	
	private void ensureListWriteable() {
		if(this.mode == PIM.READ_ONLY) {
			throw new SecurityException("The list is only readable.");
		}
	}

}
