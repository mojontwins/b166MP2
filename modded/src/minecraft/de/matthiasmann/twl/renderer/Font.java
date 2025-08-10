package de.matthiasmann.twl.renderer;

import de.matthiasmann.twl.HAlignment;

public interface Font extends Resource {
	int getBaseLine();

	int getLineHeight();

	int getSpaceWidth();

	int getEM();

	int getEX();

	int computeMultiLineTextWidth(CharSequence charSequence1);

	int computeTextWidth(CharSequence charSequence1);

	int computeTextWidth(CharSequence charSequence1, int i2, int i3);

	int computeVisibleGlpyhs(CharSequence charSequence1, int i2, int i3, int i4);

	int drawMultiLineText(AnimationState animationState1, int i2, int i3, CharSequence charSequence4, int i5, HAlignment hAlignment6);

	int drawText(AnimationState animationState1, int i2, int i3, CharSequence charSequence4);

	int drawText(AnimationState animationState1, int i2, int i3, CharSequence charSequence4, int i5, int i6);

	FontCache cacheMultiLineText(FontCache fontCache1, CharSequence charSequence2, int i3, HAlignment hAlignment4);

	FontCache cacheText(FontCache fontCache1, CharSequence charSequence2);

	FontCache cacheText(FontCache fontCache1, CharSequence charSequence2, int i3, int i4);
}
