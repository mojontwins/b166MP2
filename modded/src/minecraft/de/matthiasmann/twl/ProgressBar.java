package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Image;

public class ProgressBar extends TextWidget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_VALUE_CHANGED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("valueChanged");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_INDETERMINATE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("indeterminate");
	public static final float VALUE_INDETERMINATE = -1.0F;
	private Image progressImage;
	private float value;

	public ProgressBar() {
		this.getAnimationState().resetAnimationTime(STATE_VALUE_CHANGED);
	}

	public float getValue() {
		return this.value;
	}

	public void setIndeterminate() {
		if(this.value >= 0.0F) {
			this.value = -1.0F;
			AnimationState animationState = this.getAnimationState();
			animationState.setAnimationState(STATE_INDETERMINATE, true);
			animationState.resetAnimationTime(STATE_VALUE_CHANGED);
		}

	}

	public void setValue(float value) {
		if(value <= 0.0F) {
			value = 0.0F;
		} else if(value > 1.0F) {
			value = 1.0F;
		}

		if(this.value != value) {
			this.value = value;
			AnimationState animationState = this.getAnimationState();
			animationState.setAnimationState(STATE_INDETERMINATE, false);
			animationState.resetAnimationTime(STATE_VALUE_CHANGED);
		}

	}

	public String getText() {
		return (String)this.getCharSequence();
	}

	public void setText(String text) {
		this.setCharSequence(text);
	}

	public Image getProgressImage() {
		return this.progressImage;
	}

	public void setProgressImage(Image progressImage) {
		this.progressImage = progressImage;
	}

	protected void applyThemeProgressBar(ThemeInfo themeInfo) {
		this.setProgressImage(themeInfo.getImage("progressImage"));
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeProgressBar(themeInfo);
	}

	protected void paintWidget(GUI gui) {
		int width = this.getInnerWidth();
		int height = this.getInnerHeight();
		if(this.progressImage != null && this.value >= 0.0F) {
			int imageWidth = this.progressImage.getWidth();
			int progressWidth = width - imageWidth;
			int scaledWidth = (int)((float)progressWidth * this.value);
			if(scaledWidth < 0) {
				scaledWidth = 0;
			} else if(scaledWidth > progressWidth) {
				scaledWidth = progressWidth;
			}

			this.progressImage.draw(this.getAnimationState(), this.getInnerX(), this.getInnerY(), imageWidth + scaledWidth, height);
		}

		super.paintWidget(gui);
	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		Image bg = this.getBackground();
		if(bg != null) {
			minWidth = Math.max(minWidth, bg.getWidth() + this.getBorderHorizontal());
		}

		return minWidth;
	}

	public int getMinHeight() {
		int minHeight = super.getMinHeight();
		Image bg = this.getBackground();
		if(bg != null) {
			minHeight = Math.max(minHeight, bg.getHeight() + this.getBorderVertical());
		}

		return minHeight;
	}

	public int getPreferredInnerWidth() {
		int prefWidth = super.getPreferredInnerWidth();
		if(this.progressImage != null) {
			prefWidth = Math.max(prefWidth, this.progressImage.getWidth());
		}

		return prefWidth;
	}

	public int getPreferredInnerHeight() {
		int prefHeight = super.getPreferredInnerHeight();
		if(this.progressImage != null) {
			prefHeight = Math.max(prefHeight, this.progressImage.getHeight());
		}

		return prefHeight;
	}
}
