package de.matthiasmann.twl.textarea;

import java.util.ArrayList;
import java.util.Iterator;

public interface TextAreaModel extends Iterable {
	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);

	public static class BlockElement extends TextAreaModel.ContainerElement {
		public BlockElement(Style style) {
			super(style);
		}
	}

	public static enum Clear {
		NONE,
		LEFT,
		RIGHT,
		BOTH;
	}

	public static class ContainerElement extends TextAreaModel.Element implements Iterable {
		protected final ArrayList children = new ArrayList();

		public ContainerElement(Style style) {
			super(style);
		}

		public Iterator iterator() {
			return this.children.iterator();
		}

		public TextAreaModel.Element getElement(int index) {
			return (TextAreaModel.Element)this.children.get(index);
		}

		public int getNumElements() {
			return this.children.size();
		}

		public void add(TextAreaModel.Element element) {
			this.children.add(element);
		}
	}

	public static enum Display {
		INLINE,
		BLOCK;
	}

	public abstract static class Element {
		private Style style;

		protected Element(Style style) {
			notNull(style, "style");
			this.style = style;
		}

		public Style getStyle() {
			return this.style;
		}

		public void setStyle(Style style) {
			notNull(style, "style");
			this.style = style;
		}

		static void notNull(Object o, String name) {
			if(o == null) {
				throw new NullPointerException(name);
			}
		}
	}

	public static enum FloatPosition {
		NONE,
		LEFT,
		RIGHT;
	}

	public static enum HAlignment {
		LEFT,
		RIGHT,
		CENTER,
		JUSTIFY;
	}

	public static class ImageElement extends TextAreaModel.Element {
		private final String imageName;
		private final String tooltip;

		public ImageElement(Style style, String imageName, String tooltip) {
			super(style);
			this.imageName = imageName;
			this.tooltip = tooltip;
		}

		public ImageElement(Style style, String imageName) {
			this(style, imageName, (String)null);
		}

		public String getImageName() {
			return this.imageName;
		}

		public String getToolTip() {
			return this.tooltip;
		}
	}

	public static class LinkElement extends TextAreaModel.ContainerElement {
		private String href;

		public LinkElement(Style style, String href) {
			super(style);
			this.href = href;
		}

		public String getHREF() {
			return this.href;
		}

		public void setHref(String href) {
			this.href = href;
		}
	}

	public static class ListElement extends TextAreaModel.ContainerElement {
		public ListElement(Style style) {
			super(style);
		}
	}

	public static class OrderedListElement extends TextAreaModel.ContainerElement {
		private final int start;

		public OrderedListElement(Style style, int start) {
			super(style);
			this.start = start;
		}

		public int getStart() {
			return this.start;
		}
	}

	public static class ParagraphElement extends TextAreaModel.ContainerElement {
		public ParagraphElement(Style style) {
			super(style);
		}
	}

	public static class TableCellElement extends TextAreaModel.ContainerElement {
		private final int colspan;

		public TableCellElement(Style style) {
			this(style, 1);
		}

		public TableCellElement(Style style, int colspan) {
			super(style);
			this.colspan = colspan;
		}

		public int getColspan() {
			return this.colspan;
		}
	}

	public static class TableElement extends TextAreaModel.Element {
		private final int numColumns;
		private final int numRows;
		private final int cellSpacing;
		private final int cellPadding;
		private final TextAreaModel.TableCellElement[] cells;
		private final Style[] rowStyles;

		public TableElement(Style style, int numColumns, int numRows, int cellSpacing, int cellPadding) {
			super(style);
			if(numColumns < 0) {
				throw new IllegalArgumentException("numColumns");
			} else if(numRows < 0) {
				throw new IllegalArgumentException("numRows");
			} else {
				this.numColumns = numColumns;
				this.numRows = numRows;
				this.cellSpacing = cellSpacing;
				this.cellPadding = cellPadding;
				this.cells = new TextAreaModel.TableCellElement[numRows * numColumns];
				this.rowStyles = new Style[numRows];
			}
		}

		public int getNumColumns() {
			return this.numColumns;
		}

		public int getNumRows() {
			return this.numRows;
		}

		public int getCellPadding() {
			return this.cellPadding;
		}

		public int getCellSpacing() {
			return this.cellSpacing;
		}

		public TextAreaModel.TableCellElement getCell(int row, int column) {
			if(column >= 0 && column < this.numColumns) {
				if(row >= 0 && row < this.numRows) {
					return this.cells[row * this.numColumns + column];
				} else {
					throw new IndexOutOfBoundsException("row");
				}
			} else {
				throw new IndexOutOfBoundsException("column");
			}
		}

		public Style getRowStyle(int row) {
			return this.rowStyles[row];
		}

		public void setSell(int row, int column, TextAreaModel.TableCellElement cell) {
			if(column >= 0 && column < this.numColumns) {
				if(row >= 0 && row < this.numRows) {
					this.cells[row * this.numColumns + column] = cell;
				} else {
					throw new IndexOutOfBoundsException("row");
				}
			} else {
				throw new IndexOutOfBoundsException("column");
			}
		}

		public void setRowStyle(int row, Style style) {
			this.rowStyles[row] = style;
		}
	}

	public static class TextElement extends TextAreaModel.Element {
		private String text;

		public TextElement(Style style, String text) {
			super(style);
			notNull(text, "text");
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

		public void setText(String text) {
			notNull(text, "text");
			this.text = text;
		}
	}

	public static enum VAlignment {
		TOP,
		MIDDLE,
		BOTTOM,
		FILL;
	}

	public static class WidgetElement extends TextAreaModel.Element {
		private final String widgetName;
		private final String widgetParam;

		public WidgetElement(Style style, String widgetName, String widgetParam) {
			super(style);
			this.widgetName = widgetName;
			this.widgetParam = widgetParam;
		}

		public String getWidgetName() {
			return this.widgetName;
		}

		public String getWidgetParam() {
			return this.widgetParam;
		}
	}
}
