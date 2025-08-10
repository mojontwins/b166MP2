package de.matthiasmann.twl.textarea;

import de.matthiasmann.twl.utils.TextUtil;

public class OrderedListType {
	public static final OrderedListType DECIMAL = new OrderedListType();
	protected final String characterList;

	public OrderedListType() {
		this.characterList = null;
	}

	public OrderedListType(String characterList) {
		this.characterList = characterList;
	}

	public String format(int nr) {
		return nr >= 1 && this.characterList != null ? TextUtil.toCharListNumber(nr, this.characterList) : Integer.toString(nr);
	}
}
