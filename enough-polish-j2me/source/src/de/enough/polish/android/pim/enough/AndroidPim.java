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

	private static ContactListImpl contactListInstance;

	@Override
	public PIMItem[] fromSerialFormat(InputStream is, String enc) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

	@Override
	public String[] listPIMLists(int pimListType) {
		return new String[] {"contacts"};
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode) throws PIMException {
		switch(pimListType) {
			case PIM.CONTACT_LIST:
				if(contactListInstance == null) {
					contactListInstance = new ContactListImpl("contacts",mode);
				}
				return contactListInstance;
			default:
				throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
		if( ! "contacts".equals(name)) {
			throw new PIMException("No PIMList with the name '"+name+"' present. See method listPIMLists(int) for available names.");
		}
		return openPIMList(pimListType, mode);
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
