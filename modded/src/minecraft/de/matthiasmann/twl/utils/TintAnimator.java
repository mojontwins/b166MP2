package de.matthiasmann.twl.utils;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Renderer;

public class TintAnimator {
	private static final float ZERO_EPSILON = 0.001F;
	private static final float ONE_EPSILON = 0.999F;
	private final TintAnimator.TimeSource timeSource;
	private final float[] currentTint;
	private int fadeDuration;
	private boolean fadeActive;
	private boolean hasTint;

	public TintAnimator(TintAnimator.TimeSource timeSource, Color color) {
		if(timeSource == null) {
			throw new NullPointerException("timeSource");
		} else if(color == null) {
			throw new NullPointerException("color");
		} else {
			this.timeSource = timeSource;
			this.currentTint = new float[12];
			this.setColor(color);
		}
	}

	public TintAnimator(TintAnimator.TimeSource timeSource) {
		this(timeSource, Color.WHITE);
	}

	public void setColor(Color color) {
		color.getFloats(this.currentTint, 0);
		color.getFloats(this.currentTint, 4);
		this.hasTint = !Color.WHITE.equals(color);
		this.fadeActive = false;
		this.fadeDuration = 0;
		this.timeSource.resetTime();
	}

	public void fadeTo(Color color, int fadeDuration) {
		if(fadeDuration <= 0) {
			this.setColor(color);
		} else {
			color.getFloats(this.currentTint, 8);
			System.arraycopy(this.currentTint, 0, this.currentTint, 4, 4);
			this.fadeActive = true;
			this.fadeDuration = fadeDuration;
			this.hasTint = true;
			this.timeSource.resetTime();
		}

	}

	public void fadeToHide(int fadeDuration) {
		if(fadeDuration <= 0) {
			this.currentTint[3] = 0.0F;
			this.fadeActive = false;
			this.fadeDuration = 0;
			this.hasTint = true;
		} else {
			System.arraycopy(this.currentTint, 0, this.currentTint, 4, 8);
			this.currentTint[11] = 0.0F;
			this.fadeActive = !this.isZeroAlpha();
			this.fadeDuration = fadeDuration;
			this.hasTint = true;
			this.timeSource.resetTime();
		}

	}

	public void update() {
		if(this.fadeActive) {
			int time = this.timeSource.getTime();
			float t = (float)Math.min(time, this.fadeDuration) / (float)this.fadeDuration;
			float tm1 = 1.0F - t;
			float[] tint = this.currentTint;

			for(int i = 0; i < 4; ++i) {
				tint[i] = tm1 * tint[i + 4] + t * tint[i + 8];
			}

			if(time >= this.fadeDuration) {
				this.fadeActive = false;
				this.hasTint = this.currentTint[0] < 0.999F || this.currentTint[1] < 0.999F || this.currentTint[2] < 0.999F || this.currentTint[3] < 0.999F;
			}
		}

	}

	public boolean isFadeActive() {
		return this.fadeActive;
	}

	public boolean hasTint() {
		return this.hasTint;
	}

	public boolean isZeroAlpha() {
		return this.currentTint[3] <= 0.001F;
	}

	public void paintWithTint(Renderer renderer) {
		float[] tint = this.currentTint;
		renderer.pushGlobalTintColor(tint[0], tint[1], tint[2], tint[3]);
	}

	public static class AnimationStateTimeSource implements TintAnimator.TimeSource {
		private final AnimationState animState;
		private final de.matthiasmann.twl.renderer.AnimationState.StateKey animStateKey;

		public AnimationStateTimeSource(AnimationState animState, String animStateName) {
			this(animState, de.matthiasmann.twl.renderer.AnimationState.StateKey.get(animStateName));
		}

		public AnimationStateTimeSource(AnimationState animState, de.matthiasmann.twl.renderer.AnimationState.StateKey animStateKey) {
			if(animState == null) {
				throw new NullPointerException("animState");
			} else if(animStateKey == null) {
				throw new NullPointerException("animStateKey");
			} else {
				this.animState = animState;
				this.animStateKey = animStateKey;
			}
		}

		public int getTime() {
			return this.animState.getAnimationTime(this.animStateKey);
		}

		public void resetTime() {
			this.animState.resetAnimationTime(this.animStateKey);
		}
	}

	public static final class GUITimeSource implements TintAnimator.TimeSource {
		private final Widget owner;
		private long startTime;

		public GUITimeSource(Widget owner) {
			if(owner == null) {
				throw new NullPointerException("owner");
			} else {
				this.owner = owner;
				this.resetTime();
			}
		}

		public int getTime() {
			return (int)(this.getCurrentTime() - this.startTime);
		}

		public void resetTime() {
			this.startTime = this.getCurrentTime();
		}

		private long getCurrentTime() {
			GUI gui = this.owner.getGUI();
			return gui != null ? gui.getCurrentTime() : 0L;
		}
	}

	public interface TimeSource {
		void resetTime();

		int getTime();
	}
}
