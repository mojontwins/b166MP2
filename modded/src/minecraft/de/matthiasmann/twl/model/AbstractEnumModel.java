package de.matthiasmann.twl.model;

public abstract class AbstractEnumModel extends HasCallback implements EnumModel {
	private final Class enumClass;

	protected AbstractEnumModel(Class clazz) {
		if(clazz == null) {
			throw new NullPointerException("clazz");
		} else {
			this.enumClass = clazz;
		}
	}

	public Class getEnumClass() {
		return this.enumClass;
	}
}
