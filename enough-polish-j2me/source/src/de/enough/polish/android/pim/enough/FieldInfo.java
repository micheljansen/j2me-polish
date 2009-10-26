//#condition polish.android
/**
 * 
 */
package de.enough.polish.android.pim.enough;

class FieldInfo {
	public static final int DEFAULT_PREFERRED_INDEX = -1;
	public static final int DEFAULT_NUMBER_OF_ARRAYELEMENTS = 0;
	protected final int numberOfArrayElements;
	protected final int id;
	protected final int type;
	protected final String label;
	protected final int preferredIndex;
	protected final int[] supportedArrayElements;
	protected final int[] supportedAttributes;
	public FieldInfo (int id, int type, String label,int numberOfArrayElements, int preferredIndex,int[] supportedArrayElements, int[] supportedAttributes) {
		this.id = id;
		this.type = type;
		this.label = label;
		this.numberOfArrayElements = numberOfArrayElements;
		this.preferredIndex = preferredIndex;
		this.supportedArrayElements = supportedArrayElements;
		this.supportedAttributes = supportedAttributes;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldInfo other = (FieldInfo) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		return result;
	}
	public String toString() {
		return "FieldInfo:Id:"+this.id+".Type:"+this.type+".Label:"+this.label+".ArrayElements:"+this.numberOfArrayElements+".";
	}
}