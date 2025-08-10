package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;

public class AnimatedImage implements Image, HasBorder {
	final Renderer renderer;
	final AnimatedImage.Element root;
	final AnimationState.StateKey timeSource;
	final Border border;
	final float r;
	final float g;
	final float b;
	final float a;
	final int width;
	final int height;
	final int frozenTime;

	AnimatedImage(Renderer renderer, AnimatedImage.Element root, String timeSource, Border border, Color tintColor, int frozenTime) {
		this.renderer = renderer;
		this.root = root;
		this.timeSource = AnimationState.StateKey.get(timeSource);
		this.border = border;
		this.r = tintColor.getRedFloat();
		this.g = tintColor.getGreenFloat();
		this.b = tintColor.getBlueFloat();
		this.a = tintColor.getAlphaFloat();
		this.width = root.getWidth();
		this.height = root.getHeight();
		this.frozenTime = frozenTime;
	}

	AnimatedImage(AnimatedImage src, Color tintColor) {
		this.renderer = src.renderer;
		this.root = src.root;
		this.timeSource = src.timeSource;
		this.border = src.border;
		this.r = src.r * tintColor.getRedFloat();
		this.g = src.g * tintColor.getGreenFloat();
		this.b = src.b * tintColor.getBlueFloat();
		this.a = src.a * tintColor.getAlphaFloat();
		this.width = src.width;
		this.height = src.height;
		this.frozenTime = src.frozenTime;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.width, this.height);
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		int time = 0;
		if(as != null) {
			if(this.frozenTime >= 0 && !as.getShouldAnimateState(this.timeSource)) {
				time = this.frozenTime;
			} else {
				time = as.getAnimationTime(this.timeSource);
			}
		}

		this.root.render(time, (AnimatedImage.Img)null, x, y, width, height, this, as);
	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		return new AnimatedImage(this, color);
	}

	abstract static class Element {
		int duration;

		abstract int getWidth();

		abstract int getHeight();

		abstract AnimatedImage.Img getFirstImg();

		abstract void render(int i1, AnimatedImage.Img animatedImage$Img2, int i3, int i4, int i5, int i6, AnimatedImage animatedImage7, AnimationState animationState8);
	}

	static class Img extends AnimatedImage.Element {
		final Image image;
		final float r;
		final float g;
		final float b;
		final float a;
		final float zoomX;
		final float zoomY;
		final float zoomCenterX;
		final float zoomCenterY;

		Img(int duration, Image image, Color tintColor, float zoomX, float zoomY, float zoomCenterX, float zoomCenterY) {
			if(duration < 0) {
				throw new IllegalArgumentException("duration");
			} else {
				this.duration = duration;
				this.image = image;
				this.r = tintColor.getRedFloat();
				this.g = tintColor.getGreenFloat();
				this.b = tintColor.getBlueFloat();
				this.a = tintColor.getAlphaFloat();
				this.zoomX = zoomX;
				this.zoomY = zoomY;
				this.zoomCenterX = zoomCenterX;
				this.zoomCenterY = zoomCenterY;
			}
		}

		int getWidth() {
			return this.image.getWidth();
		}

		int getHeight() {
			return this.image.getHeight();
		}

		AnimatedImage.Img getFirstImg() {
			return this;
		}

		void render(int time, AnimatedImage.Img next, int x, int y, int width, int height, AnimatedImage ai, AnimationState as) {
			float rr = this.r;
			float gg = this.g;
			float bb = this.b;
			float aa = this.a;
			float zx = this.zoomX;
			float zy = this.zoomY;
			float cx = this.zoomCenterX;
			float cy = this.zoomCenterY;
			if(next != null) {
				float zWidth = (float)time / (float)this.duration;
				rr = blend(rr, next.r, zWidth);
				gg = blend(gg, next.g, zWidth);
				bb = blend(bb, next.b, zWidth);
				aa = blend(aa, next.a, zWidth);
				zx = blend(zx, next.zoomX, zWidth);
				zy = blend(zy, next.zoomY, zWidth);
				cx = blend(cx, next.zoomCenterX, zWidth);
				cy = blend(cy, next.zoomCenterY, zWidth);
			}

			ai.renderer.pushGlobalTintColor(rr * ai.r, gg * ai.g, bb * ai.b, aa * ai.a);

			try {
				int zWidth1 = (int)((float)width * zx);
				int zHeight = (int)((float)height * zy);
				this.image.draw(as, x + (int)((float)(width - zWidth1) * cx), y + (int)((float)(height - zHeight) * cy), zWidth1, zHeight);
			} finally {
				ai.renderer.popGlobalTintColor();
			}

		}

		private static float blend(float a, float b, float t) {
			return a + (b - a) * t;
		}
	}

	static class Repeat extends AnimatedImage.Element {
		final AnimatedImage.Element[] children;
		final int repeatCount;
		final int singleDuration;

		Repeat(AnimatedImage.Element[] children, int repeatCount) {
			this.children = children;
			this.repeatCount = repeatCount;

			assert repeatCount >= 0;

			assert children.length > 0;

			AnimatedImage.Element[] animatedImage$Element6 = children;
			int i5 = children.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				AnimatedImage.Element e = animatedImage$Element6[i4];
				this.duration += e.duration;
			}

			this.singleDuration = this.duration;
			if(repeatCount == 0) {
				this.duration = Integer.MAX_VALUE;
			} else {
				this.duration *= repeatCount;
			}

		}

		int getHeight() {
			int tmp = 0;
			AnimatedImage.Element[] animatedImage$Element5 = this.children;
			int i4 = this.children.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				AnimatedImage.Element e = animatedImage$Element5[i3];
				tmp = Math.max(tmp, e.getHeight());
			}

			return tmp;
		}

		int getWidth() {
			int tmp = 0;
			AnimatedImage.Element[] animatedImage$Element5 = this.children;
			int i4 = this.children.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				AnimatedImage.Element e = animatedImage$Element5[i3];
				tmp = Math.max(tmp, e.getWidth());
			}

			return tmp;
		}

		AnimatedImage.Img getFirstImg() {
			return this.children[0].getFirstImg();
		}

		void render(int time, AnimatedImage.Img next, int x, int y, int width, int height, AnimatedImage ai, AnimationState as) {
			if(this.singleDuration != 0) {
				int iteration = 0;
				if(this.repeatCount == 0) {
					time %= this.singleDuration;
				} else {
					iteration = time / this.singleDuration;
					time -= Math.min(iteration, this.repeatCount - 1) * this.singleDuration;
				}

				AnimatedImage.Element e = null;

				for(int i = 0; i < this.children.length; ++i) {
					e = this.children[i];
					if(time < e.duration && e.duration > 0) {
						if(i + 1 < this.children.length) {
							next = this.children[i + 1].getFirstImg();
						} else if(this.repeatCount == 0 || iteration + 1 < this.repeatCount) {
							next = this.getFirstImg();
						}
						break;
					}

					time -= e.duration;
				}

				if(e != null) {
					e.render(time, next, x, y, width, height, ai, as);
				}

			}
		}
	}
}
