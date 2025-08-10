package de.matthiasmann.twl.textarea;

public class BoxAttribute {
	public final StyleAttribute top;
	public final StyleAttribute left;
	public final StyleAttribute right;
	public final StyleAttribute bottom;

	BoxAttribute(StyleAttribute top, StyleAttribute left, StyleAttribute right, StyleAttribute bottom) {
		this.top = top;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
	}
}
