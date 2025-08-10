package de.matthiasmann.twl;

public class SplitPane extends Widget {
	public static final int CENTER = -1;
	private final DraggableButton splitter = new DraggableButton();
	private SplitPane.Direction direction;
	private int splitPosition = -1;
	private boolean reverseSplitPosition;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction;

	public SplitPane() {
		this.splitter.setListener(new DraggableButton.DragListener() {
			int initialPos;

			public void dragStarted() {
				this.initialPos = SplitPane.this.getSplitPosNoCenter();
			}

			public void dragged(int deltaX, int deltaY) {
				SplitPane.this.dragged(this.initialPos, deltaX, deltaY);
			}

			public void dragStopped() {
			}
		});
		this.setDirection(SplitPane.Direction.HORIZONTAL);
		this.add(this.splitter);
	}

	public SplitPane.Direction getDirection() {
		return this.direction;
	}

	public void setDirection(SplitPane.Direction direction) {
		if(direction == null) {
			throw new NullPointerException("direction");
		} else {
			this.direction = direction;
			this.splitter.setTheme(direction.splitterTheme);
		}
	}

	public int getMaxSplitPosition() {
		return Math.max(0, this.direction.get(this.getInnerWidth() - this.splitter.getPreferredWidth(), this.getInnerHeight() - this.splitter.getPreferredHeight()));
	}

	public int getSplitPosition() {
		return this.splitPosition;
	}

	public void setSplitPosition(int pos) {
		this.splitPosition = pos;
		this.invalidateLayout();
	}

	public boolean getReverseSplitPosition() {
		return this.reverseSplitPosition;
	}

	public void setReverseSplitPosition(boolean reverseSplitPosition) {
		this.reverseSplitPosition = reverseSplitPosition;
	}

	void dragged(int initialPos, int deltaX, int deltaY) {
		int delta = this.direction.get(deltaX, deltaY);
		if(this.reverseSplitPosition) {
			delta = -delta;
		}

		this.setSplitPosition(this.clamp(initialPos + delta));
	}

	protected void childRemoved(Widget exChild) {
		super.childRemoved(exChild);
		if(exChild == this.splitter) {
			this.add(this.splitter);
		}

	}

	protected void childAdded(Widget child) {
		super.childAdded(child);
		int numChildren = this.getNumChildren();
		if(numChildren > 0 && this.getChild(numChildren - 1) != this.splitter) {
			this.moveChild(this.getChildIndex(this.splitter), numChildren - 1);
		}

	}

	public int getMinWidth() {
		int min;
		if(this.direction == SplitPane.Direction.HORIZONTAL) {
			min = BoxLayout.computeMinWidthHorizontal(this, 0);
		} else {
			min = BoxLayout.computeMinWidthVertical(this);
		}

		return Math.max(super.getMinWidth(), min);
	}

	public int getMinHeight() {
		int min;
		if(this.direction == SplitPane.Direction.HORIZONTAL) {
			min = BoxLayout.computeMinHeightHorizontal(this);
		} else {
			min = BoxLayout.computeMinHeightVertical(this, 0);
		}

		return Math.max(super.getMinHeight(), min);
	}

	public int getPreferredInnerWidth() {
		return this.direction == SplitPane.Direction.HORIZONTAL ? BoxLayout.computePreferredWidthHorizontal(this, 0) : BoxLayout.computePreferredWidthVertical(this);
	}

	public int getPreferredInnerHeight() {
		return this.direction == SplitPane.Direction.HORIZONTAL ? BoxLayout.computePreferredHeightHorizontal(this) : BoxLayout.computePreferredHeightVertical(this, 0);
	}

	protected void layout() {
		Widget a = null;
		Widget b = null;

		int innerX;
		for(innerX = 0; innerX < this.getNumChildren(); ++innerX) {
			Widget innerY = this.getChild(innerX);
			if(innerY != this.splitter) {
				if(a != null) {
					b = innerY;
					break;
				}

				a = innerY;
			}
		}

		innerX = this.getInnerX();
		int i8 = this.getInnerY();
		int splitPos = this.getSplitPosNoCenter();
		if(this.reverseSplitPosition) {
			splitPos = this.getMaxSplitPosition() - splitPos;
		}

		switch($SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction()[this.direction.ordinal()]) {
		case 1:
			int innerHeight = this.getInnerHeight();
			this.splitter.setPosition(innerX + splitPos, i8);
			this.splitter.setSize(this.splitter.getPreferredWidth(), innerHeight);
			if(a != null) {
				a.setPosition(innerX, i8);
				a.setSize(splitPos, innerHeight);
			}

			if(b != null) {
				b.setPosition(this.splitter.getRight(), i8);
				b.setSize(Math.max(0, this.getInnerRight() - this.splitter.getRight()), innerHeight);
			}
			break;
		case 2:
			int innerWidth = this.getInnerWidth();
			this.splitter.setPosition(innerX, i8 + splitPos);
			this.splitter.setSize(innerWidth, this.splitter.getPreferredHeight());
			if(a != null) {
				a.setPosition(innerX, i8);
				a.setSize(innerWidth, splitPos);
			}

			if(b != null) {
				b.setPosition(innerX, this.splitter.getBottom());
				b.setSize(innerWidth, Math.max(0, this.getInnerBottom() - this.splitter.getBottom()));
			}
		}

	}

	int getSplitPosNoCenter() {
		return this.splitPosition == -1 ? this.getMaxSplitPosition() / 2 : this.clamp(this.splitPosition);
	}

	private int clamp(int pos) {
		return Math.max(0, Math.min(this.getMaxSplitPosition(), pos));
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction;
		if($SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction != null) {
			return i10000;
		} else {
			int[] i0 = new int[SplitPane.Direction.values().length];

			try {
				i0[SplitPane.Direction.HORIZONTAL.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[SplitPane.Direction.VERTICAL.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$SplitPane$Direction = i0;
			return i0;
		}
	}

	public static enum Direction {
		HORIZONTAL("splitterHorizontal") {
			int get(int x, int y) {
				return x;
			}
		},
		VERTICAL("splitterVertical") {
			int get(int x, int y) {
				return y;
			}
		};

		final String splitterTheme;

		private Direction(String splitterTheme) {
			this.splitterTheme = splitterTheme;
		}

		abstract int get(int i1, int i2);

		Direction(String string3, SplitPane.Direction splitPane$Direction4) {
			this(string3);
		}
	}
}
