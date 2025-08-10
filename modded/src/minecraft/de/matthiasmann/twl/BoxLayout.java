package de.matthiasmann.twl;

public class BoxLayout extends Widget {
	private BoxLayout.Direction direction;
	private int spacing;
	private boolean scroll;
	private Alignment alignment;

	public BoxLayout() {
		this(BoxLayout.Direction.HORIZONTAL);
	}

	public BoxLayout(BoxLayout.Direction direction) {
		this.alignment = Alignment.TOP;
		this.direction = direction;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public void setSpacing(int spacing) {
		if(this.spacing != spacing) {
			this.spacing = spacing;
			this.invalidateLayout();
		}

	}

	public boolean isScroll() {
		return this.scroll;
	}

	public void setScroll(boolean scroll) {
		if(this.scroll != scroll) {
			this.scroll = scroll;
			this.invalidateLayout();
		}

	}

	public Alignment getAlignment() {
		return this.alignment;
	}

	public void setAlignment(Alignment alignment) {
		if(alignment == null) {
			throw new NullPointerException("alignment");
		} else {
			if(this.alignment != alignment) {
				this.alignment = alignment;
				this.invalidateLayout();
			}

		}
	}

	public BoxLayout.Direction getDirection() {
		return this.direction;
	}

	public void setDirection(BoxLayout.Direction direction) {
		if(direction == null) {
			throw new NullPointerException("direction");
		} else {
			if(this.direction != direction) {
				this.direction = direction;
				this.invalidateLayout();
			}

		}
	}

	public int getMinWidth() {
		int minWidth = this.direction == BoxLayout.Direction.HORIZONTAL ? computeMinWidthHorizontal(this, this.spacing) : computeMinWidthVertical(this);
		return Math.max(super.getMinWidth(), minWidth + this.getBorderHorizontal());
	}

	public int getMinHeight() {
		int minHeight = this.direction == BoxLayout.Direction.HORIZONTAL ? computeMinHeightHorizontal(this) : computeMinHeightVertical(this, this.spacing);
		return Math.max(super.getMinHeight(), minHeight + this.getBorderVertical());
	}

	public int getPreferredInnerWidth() {
		return this.direction == BoxLayout.Direction.HORIZONTAL ? computePreferredWidthHorizontal(this, this.spacing) : computePreferredWidthVertical(this);
	}

	public int getPreferredInnerHeight() {
		return this.direction == BoxLayout.Direction.HORIZONTAL ? computePreferredHeightHorizontal(this) : computePreferredHeightVertical(this, this.spacing);
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.setSpacing(themeInfo.getParameter("spacing", 0));
		this.setAlignment((Alignment)themeInfo.getParameter("alignment", Alignment.TOP));
	}

	public static int computeMinWidthHorizontal(Widget container, int spacing) {
		int n = container.getNumChildren();
		int minWidth = Math.max(0, n - 1) * spacing;

		for(int i = 0; i < n; ++i) {
			minWidth += container.getChild(i).getMinWidth();
		}

		return minWidth;
	}

	public static int computeMinHeightHorizontal(Widget container) {
		int n = container.getNumChildren();
		int minHeight = 0;

		for(int i = 0; i < n; ++i) {
			minHeight = Math.max(minHeight, container.getChild(i).getMinHeight());
		}

		return minHeight;
	}

	public static int computePreferredWidthHorizontal(Widget container, int spacing) {
		int n = container.getNumChildren();
		int prefWidth = Math.max(0, n - 1) * spacing;

		for(int i = 0; i < n; ++i) {
			prefWidth += getPrefChildWidth(container.getChild(i));
		}

		return prefWidth;
	}

	public static int computePreferredHeightHorizontal(Widget container) {
		int n = container.getNumChildren();
		int prefHeight = 0;

		for(int i = 0; i < n; ++i) {
			prefHeight = Math.max(prefHeight, getPrefChildHeight(container.getChild(i)));
		}

		return prefHeight;
	}

	public static int computeMinWidthVertical(Widget container) {
		int n = container.getNumChildren();
		int minWidth = 0;

		for(int i = 0; i < n; ++i) {
			minWidth = Math.max(minWidth, container.getChild(i).getMinWidth());
		}

		return minWidth;
	}

	public static int computeMinHeightVertical(Widget container, int spacing) {
		int n = container.getNumChildren();
		int minHeight = Math.max(0, n - 1) * spacing;

		for(int i = 0; i < n; ++i) {
			minHeight += container.getChild(i).getMinHeight();
		}

		return minHeight;
	}

	public static int computePreferredWidthVertical(Widget container) {
		int n = container.getNumChildren();
		int prefWidth = 0;

		for(int i = 0; i < n; ++i) {
			prefWidth = Math.max(prefWidth, getPrefChildWidth(container.getChild(i)));
		}

		return prefWidth;
	}

	public static int computePreferredHeightVertical(Widget container, int spacing) {
		int n = container.getNumChildren();
		int prefHeight = Math.max(0, n - 1) * spacing;

		for(int i = 0; i < n; ++i) {
			prefHeight += getPrefChildHeight(container.getChild(i));
		}

		return prefHeight;
	}

	public static void layoutHorizontal(Widget container, int spacing, Alignment alignment, boolean scroll) {
		int numChildren = container.getNumChildren();
		int height = container.getInnerHeight();
		int x = container.getInnerX();
		int y = container.getInnerY();
		int idx;
		if(scroll) {
			idx = computePreferredWidthHorizontal(container, spacing);
			if(idx > container.getInnerWidth()) {
				x -= idx - container.getInnerWidth();
			}
		}

		for(idx = 0; idx < numChildren; ++idx) {
			Widget child = container.getChild(idx);
			int childWidth = getPrefChildWidth(child);
			int childHeight = alignment == Alignment.FILL ? height : getPrefChildHeight(child);
			int yoff = (height - childHeight) * alignment.vpos / 2;
			child.setSize(childWidth, childHeight);
			child.setPosition(x, y + yoff);
			x += childWidth + spacing;
		}

	}

	public static void layoutVertical(Widget container, int spacing, Alignment alignment, boolean scroll) {
		int numChildren = container.getNumChildren();
		int width = container.getInnerWidth();
		int x = container.getInnerX();
		int y = container.getInnerY();
		int idx;
		if(scroll) {
			idx = computePreferredHeightVertical(container, spacing);
			if(idx > container.getInnerHeight()) {
				x -= idx - container.getInnerHeight();
			}
		}

		for(idx = 0; idx < numChildren; ++idx) {
			Widget child = container.getChild(idx);
			int childWidth = alignment == Alignment.FILL ? width : getPrefChildWidth(child);
			int childHeight = getPrefChildHeight(child);
			int xoff = (width - childWidth) * alignment.hpos / 2;
			child.setSize(childWidth, childHeight);
			child.setPosition(x + xoff, y);
			y += childHeight + spacing;
		}

	}

	protected void layout() {
		if(this.getNumChildren() > 0) {
			if(this.direction == BoxLayout.Direction.HORIZONTAL) {
				layoutHorizontal(this, this.spacing, this.alignment, this.scroll);
			} else {
				layoutVertical(this, this.spacing, this.alignment, this.scroll);
			}
		}

	}

	private static int getPrefChildWidth(Widget child) {
		return computeSize(child.getMinWidth(), child.getPreferredWidth(), child.getMaxWidth());
	}

	private static int getPrefChildHeight(Widget child) {
		return computeSize(child.getMinHeight(), child.getPreferredHeight(), child.getMaxHeight());
	}

	public static enum Direction {
		HORIZONTAL,
		VERTICAL;
	}
}
