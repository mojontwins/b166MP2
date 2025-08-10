package de.matthiasmann.twl.model;

public class EnumListModel extends SimpleListModel {
	private final Class enumClass;
	private final Enum[] enumValues;

	public EnumListModel(Class enumClass) {
		if(!enumClass.isEnum()) {
			throw new IllegalArgumentException("not an enum class");
		} else {
			this.enumClass = enumClass;
			this.enumValues = (Enum[])enumClass.getEnumConstants();
		}
	}

	public Class getEnumClass() {
		return this.enumClass;
	}

	public Enum getEntry(int index) {
		return this.enumValues[index];
	}

	public int getNumEntries() {
		return this.enumValues.length;
	}

	public int findEntry(Enum value) {
		int i = 0;

		for(int n = this.enumValues.length; i < n; ++i) {
			if(this.enumValues[i] == value) {
				return i;
			}
		}

		return -1;
	}

	public Object getEntry(int i1) {
		return this.getEntry(i1);
	}
}
