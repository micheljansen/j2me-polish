//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;

public class AndroidPim extends PIM {

	public static final String DEFAULT_PIMLIST_NAME_CONTACTS = "contacts";
	private static ContactListImpl contactListInstance;

	@Override
	public PIMItem[] fromSerialFormat(InputStream is, String enc) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

	@Override
	public String[] listPIMLists(int pimListType) {
		switch(pimListType) {
			case PIM.CONTACT_LIST:
				return new String[] {DEFAULT_PIMLIST_NAME_CONTACTS};
			default:
				return new String[0];
		}
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			return openPIMList(pimListType, mode,DEFAULT_PIMLIST_NAME_CONTACTS);
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
		
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			if( ! DEFAULT_PIMLIST_NAME_CONTACTS.equals(name)) {
				throw new PIMException("A PIMList with name '"+name+"' and type '"+pimListType+"' does not exist.");
			}
			if(contactListInstance == null) {
				contactListInstance = new ContactListImpl(name,mode);
			}
			return contactListInstance;
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
	}

	@Override
	public String[] supportedSerialFormats(int pimListType) {
		return new String[0];
	}

	@Override
	public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

}
