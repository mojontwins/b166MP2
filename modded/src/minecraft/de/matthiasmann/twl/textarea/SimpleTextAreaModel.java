package de.matthiasmann.twl.textarea;

import de.matthiasmann.twl.model.HasCallback;

import java.util.Collections;
import java.util.Iterator;

public class SimpleTextAreaModel extends HasCallback implements TextAreaModel {
	private static final Style EMPTY_STYLE = new Style();
	private TextAreaModel.Element element;

	public SimpleTextAreaModel() {
	}

	public SimpleTextAreaModel(String text) {
		this.setText(text);
	}

	public void setText(String text) {
		this.setText(text, true);
	}

	public void setText(String text, boolean preformatted) {
		Style style = EMPTY_STYLE;
		if(preformatted) {
			style = style.with(StyleAttribute.PREFORMATTED, Boolean.TRUE);
		}

		this.element = new TextAreaModel.TextElement(style, text);
		this.doCallback();
	}

	public Iterator iterator() {
		return (this.element != null ? Collections.singletonList(this.element) : Collections.emptyList()).iterator();
	}
}
