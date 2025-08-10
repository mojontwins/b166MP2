package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;

public class GridImage implements Image, HasBorder {
	private final Image[] images;
	private final int[] weightX;
	private final int[] weightY;
	private final Border border;
	private final int width;
	private final int height;
	private final int[] columnWidth;
	private final int[] rowHeight;
	private final int weightSumX;
	private final int weightSumY;

	GridImage(Image[] images, int[] weightX, int[] weightY, Border border) {
		if(weightX.length != 0 && weightY.length != 0) {
			assert weightX.length * weightY.length == images.length;

			this.images = images;
			this.weightX = weightX;
			this.weightY = weightY;
			this.border = border;
			this.columnWidth = new int[weightX.length];
			this.rowHeight = new int[weightY.length];
			int widthTmp = 0;

			int heightTmp;
			int tmpSumX;
			int tmpSumY;
			for(heightTmp = 0; heightTmp < weightX.length; ++heightTmp) {
				tmpSumX = 0;

				for(tmpSumY = 0; tmpSumY < weightY.length; ++tmpSumY) {
					tmpSumX = Math.max(tmpSumX, this.getImage(heightTmp, tmpSumY).getWidth());
				}

				widthTmp += tmpSumX;
				this.columnWidth[heightTmp] = tmpSumX;
			}

			this.width = widthTmp;
			heightTmp = 0;

			int weight;
			for(tmpSumX = 0; tmpSumX < weightY.length; ++tmpSumX) {
				tmpSumY = 0;

				for(weight = 0; weight < weightX.length; ++weight) {
					tmpSumY = Math.max(tmpSumY, this.getImage(weight, tmpSumX).getHeight());
				}

				heightTmp += tmpSumY;
				this.rowHeight[tmpSumX] = tmpSumY;
			}

			this.height = heightTmp;
			tmpSumX = 0;
			int[] i11 = weightX;
			int i10 = weightX.length;

			for(weight = 0; weight < i10; ++weight) {
				tmpSumY = i11[weight];
				if(tmpSumY < 0) {
					throw new IllegalArgumentException("negative weight in weightX");
				}

				tmpSumX += tmpSumY;
			}

			this.weightSumX = tmpSumX;
			tmpSumY = 0;
			int[] i12 = weightY;
			int i13 = weightY.length;

			for(i10 = 0; i10 < i13; ++i10) {
				weight = i12[i10];
				if(weight < 0) {
					throw new IllegalArgumentException("negative weight in weightY");
				}

				tmpSumY += weight;
			}

			this.weightSumY = tmpSumY;
			if(this.weightSumX <= 0) {
				throw new IllegalArgumentException("zero weightX not allowed");
			} else if(this.weightSumY <= 0) {
				throw new IllegalArgumentException("zero weightX not allowed");
			}
		} else {
			throw new IllegalArgumentException("zero dimension size not allowed");
		}
	}

	private GridImage(Image[] images, GridImage src) {
		this.images = images;
		this.weightX = src.weightX;
		this.weightY = src.weightY;
		this.border = src.border;
		this.columnWidth = src.columnWidth;
		this.rowHeight = src.rowHeight;
		this.weightSumX = src.weightSumX;
		this.weightSumY = src.weightSumY;
		this.width = src.width;
		this.height = src.height;
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
		int deltaY = height - this.height;
		int remWeightY = this.weightSumY;
		int yi = 0;

		for(int idx = 0; yi < this.weightY.length; ++yi) {
			int heightRow = this.rowHeight[yi];
			int tmpX;
			if(remWeightY > 0) {
				tmpX = deltaY * this.weightY[yi] / remWeightY;
				remWeightY -= this.weightY[yi];
				heightRow += tmpX;
				deltaY -= tmpX;
			}

			tmpX = x;
			int deltaX = width - this.width;
			int remWeightX = this.weightSumX;

			for(int xi = 0; xi < this.weightX.length; ++idx) {
				int widthColumn = this.columnWidth[xi];
				if(remWeightX > 0) {
					int partX = deltaX * this.weightX[xi] / remWeightX;
					remWeightX -= this.weightX[xi];
					widthColumn += partX;
					deltaX -= partX;
				}

				this.images[idx].draw(as, tmpX, y, widthColumn, heightRow);
				tmpX += widthColumn;
				++xi;
			}

			y += heightRow;
		}

	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		Image[] newImages = new Image[this.images.length];

		for(int i = 0; i < newImages.length; ++i) {
			newImages[i] = this.images[i].createTintedVersion(color);
		}

		return new GridImage(newImages, this);
	}

	private Image getImage(int x, int y) {
		return this.images[x + y * this.weightX.length];
	}
}
