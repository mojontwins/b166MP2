package de.matthiasmann.twl.textarea;

public interface StyleSheetResolver {
	void startLayout();

	Style resolve(Style style1);

	void layoutFinished();
}
