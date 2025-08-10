package de.matthiasmann.twl;

import de.matthiasmann.twl.model.SortOrder;
import de.matthiasmann.twl.model.TableColumnHeaderModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.SizeSequence;
import de.matthiasmann.twl.utils.SparseGrid;
import de.matthiasmann.twl.utils.TypeMapping;

import java.util.Iterator;

public abstract class TableBase extends Widget implements ScrollPane.Scrollable, ScrollPane.AutoScrollable, ScrollPane.CustomPageSize {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_FIRST_COLUMNHEADER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("firstColumnHeader");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_LAST_COLUMNHEADER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("lastColumnHeader");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ROW_SELECTED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("rowSelected");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ROW_HOVER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("rowHover");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ROW_DROPTARGET = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("rowDropTarget");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_LEAD_ROW = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("leadRow");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_SELECTED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("selected");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_SORT_ASCENDING = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("sortAscending");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_SORT_DESCENDING = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("sortDescending");
	private final TableBase.StringCellRenderer stringCellRenderer = new TableBase.StringCellRenderer();
	private final TableBase.RemoveCellWidgets removeCellWidgetsFunction = new TableBase.RemoveCellWidgets();
	private final TableBase.InsertCellWidgets insertCellWidgetsFunction = new TableBase.InsertCellWidgets();
	private final TableBase.CellWidgetContainer cellWidgetContainer = new TableBase.CellWidgetContainer();
	protected final TypeMapping cellRenderers = new TypeMapping();
	protected final SparseGrid widgetGrid = new SparseGrid(32);
	protected final TableBase.ColumnSizeSequence columnModel = new TableBase.ColumnSizeSequence();
	protected TableColumnHeaderModel columnHeaderModel;
	protected SizeSequence rowModel;
	protected boolean hasCellWidgetCreators;
	protected TableBase.ColumnHeader[] columnHeaders;
	protected TableSelectionManager selectionManager;
	protected TableBase.KeyboardSearchHandler keyboardSearchHandler;
	protected TableBase.DragListener dragListener;
	protected TableBase.Callback[] callbacks;
	protected Image imageColumnDivider;
	protected Image imageRowBackground;
	protected Image imageRowOverlay;
	protected Image imageRowDropMarker;
	protected ThemeInfo tableBaseThemeInfo;
	protected int columnHeaderHeight;
	protected int columnDividerDragableDistance;
	protected MouseCursor columnResizeCursor;
	protected MouseCursor normalCursor;
	protected MouseCursor dragNotPossibleCursor;
	protected int numRows;
	protected int numColumns;
	protected int rowHeight = 32;
	protected int defaultColumnWidth = 256;
	protected boolean autoSizeAllRows;
	protected boolean updateAllCellWidgets;
	protected boolean updateAllColumnWidth;
	protected int scrollPosX;
	protected int scrollPosY;
	protected int firstVisibleRow;
	protected int firstVisibleColumn;
	protected int lastVisibleRow;
	protected int lastVisibleColumn;
	protected boolean firstRowPartialVisible;
	protected boolean lastRowPartialVisible;
	protected int dropMarkerRow = -1;
	protected boolean dropMarkerBeforeRow;
	protected static final int LAST_MOUSE_Y_OUTSIDE = Integer.MIN_VALUE;
	protected int lastMouseY = Integer.MIN_VALUE;
	protected int lastMouseRow = -1;
	protected int lastMouseColumn = -1;
	protected static final int DRAG_INACTIVE = 0;
	protected static final int DRAG_COLUMN_HEADER = 1;
	protected static final int DRAG_USER = 2;
	protected static final int DRAG_IGNORE = 3;
	protected int dragActive;
	protected int dragColumn;
	protected int dragStartX;
	protected int dragStartColWidth;
	protected int dragStartSumWidth;

	protected TableBase() {
		super.insertChild(this.cellWidgetContainer, 0);
		this.setCanAcceptKeyboardFocus(true);
	}

	public TableSelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	public void setSelectionManager(TableSelectionManager selectionManager) {
		if(this.selectionManager != selectionManager) {
			if(this.selectionManager != null) {
				this.selectionManager.setAssociatedTable((TableBase)null);
			}

			this.selectionManager = selectionManager;
			if(this.selectionManager != null) {
				this.selectionManager.setAssociatedTable(this);
			}
		}

	}

	public void setDefaultSelectionManager() {
		this.setSelectionManager(new TableRowSelectionManager());
	}

	public TableBase.KeyboardSearchHandler getKeyboardSearchHandler() {
		return this.keyboardSearchHandler;
	}

	public void setKeyboardSearchHandler(TableBase.KeyboardSearchHandler keyboardSearchHandler) {
		this.keyboardSearchHandler = keyboardSearchHandler;
	}

	public TableBase.DragListener getDragListener() {
		return this.dragListener;
	}

	public void setDragListener(TableBase.DragListener dragListener) {
		this.cancelDragging();
		this.dragListener = dragListener;
	}

	public boolean isDropMarkerBeforeRow() {
		return this.dropMarkerBeforeRow;
	}

	public int getDropMarkerRow() {
		return this.dropMarkerRow;
	}

	public void setDropMarker(int row, boolean beforeRow) {
		if(row >= 0 && row <= this.numRows) {
			if(row == this.numRows && !beforeRow) {
				throw new IllegalArgumentException("row");
			} else {
				this.dropMarkerRow = row;
				this.dropMarkerBeforeRow = beforeRow;
			}
		} else {
			throw new IllegalArgumentException("row");
		}
	}

	public boolean setDropMarker(Event evt) {
		int mouseY = evt.getMouseY();
		if(this.isMouseInside(evt) && !this.isMouseInColumnHeader(mouseY)) {
			mouseY -= this.getOffsetY();
			int row = this.getRowFromPosition(mouseY);
			if(row >= 0 && row < this.numRows) {
				int rowStart = this.getRowStartPosition(row);
				int rowEnd = this.getRowEndPosition(row);
				int margin = (rowEnd - rowStart + 2) / 4;
				if(mouseY - rowStart < margin) {
					this.setDropMarker(row, true);
				} else if(rowEnd - mouseY < margin) {
					this.setDropMarker(row + 1, true);
				} else {
					this.setDropMarker(row, false);
				}

				return true;
			}

			if(row == this.numRows) {
				this.setDropMarker(row, true);
				return true;
			}
		}

		return false;
	}

	public void clearDropMarker() {
		this.dropMarkerRow = -1;
	}

	public void addCallback(TableBase.Callback callback) {
		this.callbacks = (TableBase.Callback[])CallbackSupport.addCallbackToList(this.callbacks, callback, TableBase.Callback.class);
	}

	public void removeCallback(TableBase.Callback callback) {
		this.callbacks = (TableBase.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
	}

	public boolean isVariableRowHeight() {
		return this.rowModel != null;
	}

	public void setVaribleRowHeight(boolean varibleRowHeight) {
		if(varibleRowHeight && this.rowModel == null) {
			this.rowModel = new TableBase.RowSizeSequence(this.numRows);
			this.autoSizeAllRows = true;
			this.invalidateLayout();
		} else if(!varibleRowHeight) {
			this.rowModel = null;
		}

	}

	public int getNumRows() {
		return this.numRows;
	}

	public int getNumColumns() {
		return this.numColumns;
	}

	public int getRowFromPosition(int y) {
		return y >= 0 ? (this.rowModel != null ? this.rowModel.getIndex(y) : Math.min(this.numRows - 1, y / this.rowHeight)) : -1;
	}

	public int getRowStartPosition(int row) {
		this.checkRowIndex(row);
		return this.rowModel != null ? this.rowModel.getPosition(row) : row * this.rowHeight;
	}

	public int getRowHeight(int row) {
		this.checkRowIndex(row);
		return this.rowModel != null ? this.rowModel.getSize(row) : this.rowHeight;
	}

	public int getRowEndPosition(int row) {
		this.checkRowIndex(row);
		return this.rowModel != null ? this.rowModel.getPosition(row + 1) : (row + 1) * this.rowHeight;
	}

	public int getColumnFromPosition(int x) {
		if(x >= 0) {
			int column = this.columnModel.getIndex(x);
			return column;
		} else {
			return -1;
		}
	}

	public int getColumnStartPosition(int column) {
		this.checkColumnIndex(column);
		return this.columnModel.getPosition(column);
	}

	public int getColumnWidth(int column) {
		this.checkColumnIndex(column);
		return this.columnModel.getSize(column);
	}

	public int getColumnEndPosition(int column) {
		this.checkColumnIndex(column);
		return this.columnModel.getPosition(column + 1);
	}

	public void setColumnWidth(int column, int width) {
		this.checkColumnIndex(column);
		this.columnHeaders[column].setColumnWidth(width);
		if(this.columnModel.update(column)) {
			this.invalidateLayout();
		}

	}

	public AnimationState getColumnHeaderAnimationState(int column) {
		this.checkColumnIndex(column);
		return this.columnHeaders[column].getAnimationState();
	}

	public void setColumnSortOrderAnimationState(int sortColumn, SortOrder sortOrder) {
		for(int column = 0; column < this.numColumns; ++column) {
			AnimationState animState = this.columnHeaders[column].getAnimationState();
			animState.setAnimationState(STATE_SORT_ASCENDING, column == sortColumn && sortOrder == SortOrder.ASCENDING);
			animState.setAnimationState(STATE_SORT_DESCENDING, column == sortColumn && sortOrder == SortOrder.DESCENDING);
		}

	}

	public void scrollToRow(int row) {
		ScrollPane scrollPane = ScrollPane.getContainingScrollPane(this);
		if(scrollPane != null && this.numRows > 0) {
			scrollPane.validateLayout();
			int rowStart = this.getRowStartPosition(row);
			int rowEnd = this.getRowEndPosition(row);
			int height = rowEnd - rowStart;
			scrollPane.scrollToAreaY(rowStart, height, height / 2);
		}

	}

	public int getNumVisibleRows() {
		int rows = this.lastVisibleRow - this.firstVisibleRow;
		if(!this.lastRowPartialVisible) {
			++rows;
		}

		return rows;
	}

	public int getMinHeight() {
		return Math.max(super.getMinHeight(), this.columnHeaderHeight);
	}

	public int getPreferredInnerWidth() {
		return this.numColumns > 0 ? this.getColumnEndPosition(this.numColumns - 1) : 0;
	}

	public int getPreferredInnerHeight() {
		return this.columnHeaderHeight + 1 + (this.numRows > 0 ? this.getRowEndPosition(this.numRows - 1) : 0);
	}

	public void registerCellRenderer(Class dataClass, TableBase.CellRenderer cellRenderer) {
		if(dataClass == null) {
			throw new NullPointerException("dataClass");
		} else {
			this.cellRenderers.put(dataClass, cellRenderer);
			if(cellRenderer instanceof TableBase.CellWidgetCreator) {
				this.hasCellWidgetCreators = true;
			}

			if(this.tableBaseThemeInfo != null) {
				this.applyCellRendererTheme(cellRenderer);
			}

		}
	}

	public void setScrollPosition(int scrollPosX, int scrollPosY) {
		if(this.scrollPosX != scrollPosX || this.scrollPosY != scrollPosY) {
			this.scrollPosX = scrollPosX;
			this.scrollPosY = scrollPosY;
			this.invalidateLayoutLocally();
		}

	}

	public void adjustScrollPosition(int row) {
		this.checkRowIndex(row);
		ScrollPane scrollPane = ScrollPane.getContainingScrollPane(this);
		int numVisibleRows = this.getNumVisibleRows();
		if(numVisibleRows >= 1 && scrollPane != null) {
			int innerHeight;
			if(row < this.firstVisibleRow || row == this.firstVisibleRow && this.firstRowPartialVisible) {
				innerHeight = this.getRowStartPosition(row);
				scrollPane.setScrollPositionY(innerHeight);
			} else if(row > this.lastVisibleRow || row == this.lastVisibleRow && this.lastRowPartialVisible) {
				innerHeight = Math.max(0, this.getInnerHeight() - this.columnHeaderHeight);
				int pos = this.getRowEndPosition(row);
				pos = Math.max(0, pos - innerHeight);
				scrollPane.setScrollPositionY(pos);
			}
		}

	}

	public int getAutoScrollDirection(Event evt, int autoScrollArea) {
		int areaY = this.getInnerY() + this.columnHeaderHeight;
		int areaHeight = this.getInnerHeight() - this.columnHeaderHeight;
		int mouseY = evt.getMouseY();
		if(mouseY >= areaY && mouseY < areaY + areaHeight) {
			mouseY -= areaY;
			if(mouseY <= autoScrollArea || areaHeight - mouseY <= autoScrollArea) {
				if(mouseY < areaHeight / 2) {
					return -1;
				}

				return 1;
			}
		}

		return 0;
	}

	public int getPageSizeX(int availableWidth) {
		return availableWidth;
	}

	public int getPageSizeY(int availableHeight) {
		return availableHeight - this.columnHeaderHeight;
	}

	public boolean isFixedWidthMode() {
		ScrollPane scrollPane = ScrollPane.getContainingScrollPane(this);
		return scrollPane == null || scrollPane.getFixed() == ScrollPane.Fixed.HORIZONTAL;
	}

	protected final void checkRowIndex(int row) {
		if(row < 0 || row >= this.numRows) {
			throw new IndexOutOfBoundsException("row");
		}
	}

	protected final void checkColumnIndex(int column) {
		if(column < 0 || column >= this.numColumns) {
			throw new IndexOutOfBoundsException("column");
		}
	}

	protected final void checkRowRange(int idx, int count) {
		if(idx < 0 || count < 0 || count > this.numRows || idx > this.numRows - count) {
			throw new IllegalArgumentException("row");
		}
	}

	protected final void checkColumnRange(int idx, int count) {
		if(idx < 0 || count < 0 || count > this.numColumns || idx > this.numColumns - count) {
			throw new IllegalArgumentException("column");
		}
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeTableBase(themeInfo);
		this.updateAll();
	}

	protected void applyThemeTableBase(ThemeInfo themeInfo) {
		this.tableBaseThemeInfo = themeInfo;
		this.imageColumnDivider = themeInfo.getImage("columnDivider");
		this.imageRowBackground = themeInfo.getImage("row.background");
		this.imageRowOverlay = themeInfo.getImage("row.overlay");
		this.imageRowDropMarker = themeInfo.getImage("row.dropmarker");
		this.rowHeight = themeInfo.getParameter("rowHeight", 32);
		this.defaultColumnWidth = themeInfo.getParameter("columnHeaderWidth", 256);
		this.columnHeaderHeight = themeInfo.getParameter("columnHeaderHeight", 10);
		this.columnDividerDragableDistance = themeInfo.getParameter("columnDividerDragableDistance", 3);
		Iterator iterator3 = this.cellRenderers.getUniqueValues().iterator();

		while(iterator3.hasNext()) {
			TableBase.CellRenderer cellRenderer = (TableBase.CellRenderer)iterator3.next();
			this.applyCellRendererTheme(cellRenderer);
		}

		this.applyCellRendererTheme(this.stringCellRenderer);
		this.updateAllColumnWidth = true;
	}

	protected void applyThemeMouseCursor(ThemeInfo themeInfo) {
		this.columnResizeCursor = themeInfo.getMouseCursor("columnResizeCursor");
		this.normalCursor = themeInfo.getMouseCursor("mouseCursor");
		this.dragNotPossibleCursor = themeInfo.getMouseCursor("dragNotPossibleCursor");
	}

	protected void applyCellRendererTheme(TableBase.CellRenderer cellRenderer) {
		String childThemeName = cellRenderer.getTheme();

		assert !isAbsoluteTheme(childThemeName);

		ThemeInfo childTheme = this.tableBaseThemeInfo.getChildTheme(childThemeName);
		if(childTheme != null) {
			cellRenderer.applyTheme(childTheme);
		}

	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException();
	}

	protected void childAdded(Widget child) {
	}

	protected void childRemoved(Widget exChild) {
	}

	protected int getOffsetX() {
		return this.getInnerX() - this.scrollPosX;
	}

	protected int getOffsetY() {
		return this.getInnerY() - this.scrollPosY + this.columnHeaderHeight;
	}

	protected void positionChanged() {
		super.positionChanged();
		if(this.keyboardSearchHandler != null) {
			this.keyboardSearchHandler.updateInfoWindowPosition();
		}

	}

	protected void sizeChanged() {
		super.sizeChanged();
		if(this.isFixedWidthMode()) {
			this.updateAllColumnWidth = true;
		}

		if(this.keyboardSearchHandler != null) {
			this.keyboardSearchHandler.updateInfoWindowPosition();
		}

	}

	protected Object getTooltipContentAt(int mouseX, int mouseY) {
		if(this.lastMouseRow >= 0 && this.lastMouseRow < this.getNumRows() && this.lastMouseColumn >= 0 && this.lastMouseColumn < this.getNumColumns()) {
			Object tooltip = this.getTooltipContentFromRow(this.lastMouseRow, this.lastMouseColumn);
			if(tooltip != null) {
				return tooltip;
			}
		}

		return super.getTooltipContentAt(mouseX, mouseY);
	}

	protected void layout() {
		int innerWidth = this.getInnerWidth();
		int innerHeight = Math.max(0, this.getInnerHeight() - this.columnHeaderHeight);
		this.cellWidgetContainer.setPosition(this.getInnerX(), this.getInnerY() + this.columnHeaderHeight);
		this.cellWidgetContainer.setSize(innerWidth, innerHeight);
		if(this.updateAllColumnWidth) {
			this.updateAllColumnWidth();
		}

		if(this.autoSizeAllRows) {
			this.autoSizeAllRows();
		}

		if(this.updateAllCellWidgets) {
			this.updateAllCellWidgets();
		}

		int scrollEndX = this.scrollPosX + innerWidth;
		int scrollEndY = this.scrollPosY + innerHeight;
		int startRow = Math.min(this.numRows - 1, Math.max(0, this.getRowFromPosition(this.scrollPosY)));
		int startColumn = Math.min(this.numColumns - 1, Math.max(0, this.getColumnFromPosition(this.scrollPosX)));
		int endRow = Math.min(this.numRows - 1, Math.max(startRow, this.getRowFromPosition(scrollEndY)));
		int endColumn = Math.min(this.numColumns - 1, Math.max(startColumn, this.getColumnFromPosition(scrollEndX)));
		if(this.numRows > 0) {
			this.firstRowPartialVisible = this.getRowStartPosition(startRow) < this.scrollPosY;
			this.lastRowPartialVisible = this.getRowEndPosition(endRow) > scrollEndY;
		} else {
			this.firstRowPartialVisible = false;
			this.lastRowPartialVisible = false;
		}

		if(!this.widgetGrid.isEmpty()) {
			if(startRow > this.firstVisibleRow) {
				this.widgetGrid.iterate(this.firstVisibleRow, 0, startRow - 1, this.numColumns, this.removeCellWidgetsFunction);
			}

			if(endRow < this.lastVisibleRow) {
				this.widgetGrid.iterate(endRow + 1, 0, this.lastVisibleRow, this.numColumns, this.removeCellWidgetsFunction);
			}

			this.widgetGrid.iterate(startRow, 0, endRow, this.numColumns, this.insertCellWidgetsFunction);
		}

		this.firstVisibleRow = startRow;
		this.firstVisibleColumn = startColumn;
		this.lastVisibleRow = endRow;
		this.lastVisibleColumn = endColumn;
		if(this.numColumns > 0) {
			int offsetX = this.getOffsetX();
			int colStartPos = this.getColumnStartPosition(0);

			for(int i = 0; i < this.numColumns; ++i) {
				int colEndPos = this.getColumnEndPosition(i);
				TableBase.ColumnHeader w = this.columnHeaders[i];
				if(w != null) {
					assert w.getParent() == this;

					w.setPosition(offsetX + colStartPos + this.columnDividerDragableDistance, this.getInnerY());
					w.setSize(Math.max(0, colEndPos - colStartPos - 2 * this.columnDividerDragableDistance), this.columnHeaderHeight);
					w.setVisible(this.columnHeaderHeight > 0);
					AnimationState animationState = w.getAnimationState();
					animationState.setAnimationState(STATE_FIRST_COLUMNHEADER, i == 0);
					animationState.setAnimationState(STATE_LAST_COLUMNHEADER, i == this.numColumns - 1);
				}

				colStartPos = colEndPos;
			}
		}

	}

	protected void paintWidget(GUI gui) {
		if(this.firstVisibleRow >= 0 && this.firstVisibleRow < this.numRows) {
			int innerX = this.getInnerX();
			int innerY = this.getInnerY() + this.columnHeaderHeight;
			int innerWidth = this.getInnerWidth();
			int innerHeight = this.getInnerHeight() - this.columnHeaderHeight;
			int offsetX = this.getOffsetX();
			int offsetY = this.getOffsetY();
			gui.clipEnter(innerX, innerY, innerWidth, innerHeight);

			try {
				AnimationState animState = this.getAnimationState();
				int leadRow;
				boolean isCellSelection;
				if(this.selectionManager != null) {
					leadRow = this.selectionManager.getLeadRow();
					int leadColumn = this.selectionManager.getLeadColumn();
					isCellSelection = this.selectionManager.getSelectionGranularity() == TableSelectionManager.SelectionGranularity.CELLS;
				} else {
					leadRow = -1;
					boolean z30 = true;
					isCellSelection = false;
				}

				if(this.imageRowBackground != null) {
					this.paintRowImage(this.imageRowBackground, leadRow);
				}

				int rowStartPos;
				int y;
				int rowEndPos;
				if(this.imageColumnDivider != null) {
					animState.setAnimationState(STATE_ROW_SELECTED, false);

					for(rowStartPos = this.firstVisibleColumn; rowStartPos <= this.lastVisibleColumn; ++rowStartPos) {
						y = this.getColumnEndPosition(rowStartPos);
						rowEndPos = offsetX + y;
						this.imageColumnDivider.draw(animState, rowEndPos, innerY, 1, innerHeight);
					}
				}

				rowStartPos = this.getRowStartPosition(this.firstVisibleRow);

				for(y = this.firstVisibleRow; y <= this.lastVisibleRow; ++y) {
					rowEndPos = this.getRowEndPosition(y);
					int curRowHeight = rowEndPos - rowStartPos;
					int curY = offsetY + rowStartPos;
					TreeTableNode rowNode = this.getNodeFromRow(y);
					boolean isRowSelected = !isCellSelection && this.isRowSelected(y);
					int colStartPos = this.getColumnStartPosition(this.firstVisibleColumn);

					int colEndPos;
					for(int col = this.firstVisibleColumn; col <= this.lastVisibleColumn; colStartPos = colEndPos) {
						colEndPos = this.getColumnEndPosition(col);
						TableBase.CellRenderer cellRenderer = this.getCellRenderer(y, col, rowNode);
						boolean isCellSelected = isRowSelected || this.isCellSelected(y, col);
						int curX = offsetX + colStartPos;
						int colSpan = 1;
						if(cellRenderer != null) {
							colSpan = cellRenderer.getColumnSpan();
							if(colSpan > 1) {
								colEndPos = this.getColumnEndPosition(Math.max(this.numColumns - 1, col + colSpan - 1));
							}

							Widget cellRendererWidget = cellRenderer.getCellRenderWidget(curX, curY, colEndPos - colStartPos, curRowHeight, isCellSelected);
							if(cellRendererWidget != null) {
								if(cellRendererWidget.getParent() != this) {
									this.insertCellRenderer(cellRendererWidget);
								}

								this.paintChild(gui, cellRendererWidget);
							}
						}

						col += Math.max(1, colSpan);
					}

					rowStartPos = rowEndPos;
				}

				if(this.imageRowOverlay != null) {
					this.paintRowImage(this.imageRowOverlay, leadRow);
				}

				if(this.dropMarkerRow >= 0 && this.dropMarkerBeforeRow && this.imageRowDropMarker != null) {
					y = this.rowModel != null ? this.rowModel.getPosition(this.dropMarkerRow) : this.dropMarkerRow * this.rowHeight;
					this.imageRowDropMarker.draw(animState, this.getOffsetX(), this.getOffsetY() + y, this.columnModel.getEndPosition(), 1);
				}
			} finally {
				gui.clipLeave();
			}

		}
	}

	private void paintRowImage(Image img, int leadRow) {
		AnimationState animState = this.getAnimationState();
		int x = this.getOffsetX();
		int width = this.columnModel.getEndPosition();
		int offsetY = this.getOffsetY();
		int rowStartPos = this.getRowStartPosition(this.firstVisibleRow);

		for(int row = this.firstVisibleRow; row <= this.lastVisibleRow; ++row) {
			int rowEndPos = this.getRowEndPosition(row);
			int curRowHeight = rowEndPos - rowStartPos;
			int curY = offsetY + rowStartPos;
			animState.setAnimationState(STATE_ROW_SELECTED, this.isRowSelected(row));
			animState.setAnimationState(STATE_ROW_HOVER, this.dragActive == 0 && this.lastMouseY >= curY && this.lastMouseY < curY + curRowHeight);
			animState.setAnimationState(STATE_LEAD_ROW, row == leadRow);
			animState.setAnimationState(STATE_ROW_DROPTARGET, !this.dropMarkerBeforeRow && row == this.dropMarkerRow);
			img.draw(animState, x, curY, width, curRowHeight);
			rowStartPos = rowEndPos;
		}

	}

	protected void insertCellRenderer(Widget widget) {
		int posX = widget.getX();
		int posY = widget.getY();
		widget.setVisible(false);
		super.insertChild(widget, super.getNumChildren());
		widget.setPosition(posX, posY);
	}

	protected abstract TreeTableNode getNodeFromRow(int i1);

	protected abstract Object getCellData(int i1, int i2, TreeTableNode treeTableNode3);

	protected abstract Object getTooltipContentFromRow(int i1, int i2);

	protected boolean isRowSelected(int row) {
		return this.selectionManager != null ? this.selectionManager.isRowSelected(row) : false;
	}

	protected boolean isCellSelected(int row, int column) {
		return this.selectionManager != null ? this.selectionManager.isCellSelected(row, column) : false;
	}

	protected TableBase.CellRenderer getCellRenderer(Object data) {
		Class dataClass = data.getClass();
		Object cellRenderer = (TableBase.CellRenderer)this.cellRenderers.get(dataClass);
		if(cellRenderer == null) {
			cellRenderer = this.stringCellRenderer;
		}

		return (TableBase.CellRenderer)cellRenderer;
	}

	protected TableBase.CellRenderer getCellRenderer(int row, int col, TreeTableNode node) {
		Object data = this.getCellData(row, col, node);
		if(data != null) {
			TableBase.CellRenderer cellRenderer = this.getCellRenderer(data);
			cellRenderer.setCellData(row, col, data);
			return cellRenderer;
		} else {
			return null;
		}
	}

	protected int computeRowHeight(int row) {
		TreeTableNode rowNode = this.getNodeFromRow(row);
		int height = 0;

		for(int column = 0; column < this.numColumns; ++column) {
			TableBase.CellRenderer cellRenderer = this.getCellRenderer(row, column, rowNode);
			if(cellRenderer != null) {
				height = Math.max(height, cellRenderer.getPreferredHeight());
				column += Math.max(cellRenderer.getColumnSpan() - 1, 0);
			}
		}

		return height;
	}

	protected int clampColumnWidth(int width) {
		return Math.max(2 * this.columnDividerDragableDistance + 1, width);
	}

	protected int computePreferredColumnWidth(int index) {
		return this.clampColumnWidth(this.columnHeaders[index].getPreferredWidth());
	}

	protected boolean autoSizeRow(int row) {
		int height = this.computeRowHeight(row);
		return this.rowModel.setSize(row, height);
	}

	protected void autoSizeAllRows() {
		if(this.rowModel != null) {
			this.rowModel.initializeAll(this.numRows);
		}

		this.autoSizeAllRows = false;
	}

	protected void removeCellWidget(Widget widget) {
		int idx = this.cellWidgetContainer.getChildIndex(widget);
		if(idx >= 0) {
			this.cellWidgetContainer.removeChild(idx);
		}

	}

	void insertCellWidget(int row, int column, TableBase.WidgetEntry widgetEntry) {
		TableBase.CellWidgetCreator cwc = (TableBase.CellWidgetCreator)this.getCellRenderer(row, column, (TreeTableNode)null);
		Widget widget = widgetEntry.widget;
		if(widget != null) {
			if(widget.getParent() != this.cellWidgetContainer) {
				this.cellWidgetContainer.insertChild(widget, this.cellWidgetContainer.getNumChildren());
			}

			int x = this.getColumnStartPosition(column);
			int w = this.getColumnEndPosition(column) - x;
			int y = this.getRowStartPosition(row);
			int h = this.getRowEndPosition(row) - y;
			cwc.positionWidget(widget, x + this.getOffsetX(), y + this.getOffsetY(), w, h);
		}

	}

	protected void updateCellWidget(int row, int column) {
		TableBase.WidgetEntry we = (TableBase.WidgetEntry)this.widgetGrid.get(row, column);
		Widget oldWidget = we != null ? we.widget : null;
		Widget newWidget = null;
		TreeTableNode rowNode = this.getNodeFromRow(row);
		TableBase.CellRenderer cellRenderer = this.getCellRenderer(row, column, rowNode);
		if(cellRenderer instanceof TableBase.CellWidgetCreator) {
			TableBase.CellWidgetCreator cellWidgetCreator = (TableBase.CellWidgetCreator)cellRenderer;
			if(we != null && we.creator != cellWidgetCreator) {
				this.removeCellWidget(oldWidget);
				oldWidget = null;
			}

			newWidget = cellWidgetCreator.updateWidget(oldWidget);
			if(newWidget != null) {
				if(we == null) {
					we = new TableBase.WidgetEntry();
					this.widgetGrid.set(row, column, we);
				}

				we.widget = newWidget;
				we.creator = cellWidgetCreator;
			}
		}

		if(newWidget == null && we != null) {
			this.widgetGrid.remove(row, column);
		}

		if(oldWidget != null && newWidget != oldWidget) {
			this.removeCellWidget(oldWidget);
		}

	}

	protected void updateAllCellWidgets() {
		if(!this.widgetGrid.isEmpty() || this.hasCellWidgetCreators) {
			for(int row = 0; row < this.numRows; ++row) {
				for(int col = 0; col < this.numColumns; ++col) {
					this.updateCellWidget(row, col);
				}
			}
		}

		this.updateAllCellWidgets = false;
	}

	protected void removeAllCellWidgets() {
		this.cellWidgetContainer.removeAllChildren();
	}

	protected DialogLayout.Gap getColumnMPM(int column) {
		if(this.tableBaseThemeInfo != null) {
			ParameterMap columnWidthMap = this.tableBaseThemeInfo.getParameterMap("columnWidths");
			Object obj = columnWidthMap.getParameterValue(Integer.toString(column), false);
			if(obj instanceof DialogLayout.Gap) {
				return (DialogLayout.Gap)obj;
			}

			if(obj instanceof Integer) {
				return new DialogLayout.Gap(((Integer)obj).intValue());
			}
		}

		return null;
	}

	protected TableBase.ColumnHeader createColumnHeader(int column) {
		TableBase.ColumnHeader btn = new TableBase.ColumnHeader();
		btn.setTheme("columnHeader");
		btn.setCanAcceptKeyboardFocus(false);
		super.insertChild(btn, super.getNumChildren());
		return btn;
	}

	protected void updateColumnHeader(int column) {
		TableBase.ColumnHeader columnHeader = this.columnHeaders[column];
		columnHeader.setText(this.columnHeaderModel.getColumnHeaderText(column));
		de.matthiasmann.twl.renderer.AnimationState.StateKey[] states = this.columnHeaderModel.getColumnHeaderStates();
		if(states.length > 0) {
			AnimationState animationState = columnHeader.getAnimationState();

			for(int i = 0; i < states.length; ++i) {
				animationState.setAnimationState(states[i], this.columnHeaderModel.getColumnHeaderState(column, i));
			}
		}

	}

	protected void updateColumnHeaderNumbers() {
		for(int i = 0; i < this.columnHeaders.length; this.columnHeaders[i].column = i++) {
		}

	}

	private void removeColumnHeaders(int column, int count) throws IndexOutOfBoundsException {
		for(int i = 0; i < count; ++i) {
			int idx = super.getChildIndex(this.columnHeaders[column + i]);
			if(idx >= 0) {
				super.removeChild(idx);
			}
		}

	}

	protected boolean isMouseInColumnHeader(int y) {
		y -= this.getInnerY();
		return y >= 0 && y < this.columnHeaderHeight;
	}

	protected int getColumnSeparatorUnderMouse(int x) {
		x -= this.getOffsetX();
		x += this.columnDividerDragableDistance;
		int col = this.columnModel.getIndex(x);
		int dist = x - this.columnModel.getPosition(col);
		return dist < 2 * this.columnDividerDragableDistance ? col - 1 : -1;
	}

	protected int getRowUnderMouse(int y) {
		y -= this.getOffsetY();
		int row = this.getRowFromPosition(y);
		return row;
	}

	protected int getColumnUnderMouse(int x) {
		x -= this.getOffsetX();
		int col = this.columnModel.getIndex(x);
		return col;
	}

	protected boolean handleEvent(Event evt) {
		return this.dragActive != 0 ? this.handleDragEvent(evt) : (evt.isKeyEvent() && this.keyboardSearchHandler != null && this.keyboardSearchHandler.isActive() && this.keyboardSearchHandler.handleKeyEvent(evt) ? true : (super.handleEvent(evt) ? true : (evt.isMouseEvent() ? this.handleMouseEvent(evt) : evt.isKeyEvent() && this.keyboardSearchHandler != null && this.keyboardSearchHandler.handleKeyEvent(evt))));
	}

	protected boolean handleKeyStrokeAction(String action, Event event) {
		if(!super.handleKeyStrokeAction(action, event)) {
			if(this.selectionManager == null) {
				return false;
			}

			if(!this.selectionManager.handleKeyStrokeAction(action, event)) {
				return false;
			}
		}

		this.requestKeyboardFocus((Widget)null);
		return true;
	}

	protected void cancelDragging() {
		if(this.dragActive == 2) {
			if(this.dragListener != null) {
				this.dragListener.dragCanceled();
			}

			this.dragActive = 3;
		}

	}

	protected boolean handleDragEvent(Event evt) {
		if(evt.isMouseEvent()) {
			return this.handleMouseEvent(evt);
		} else {
			if(evt.isKeyPressedEvent() && evt.getKeyCode() == 1) {
				switch(this.dragActive) {
				case 1:
					this.columnHeaderDragged(this.dragStartColWidth);
					this.dragActive = 3;
					break;
				case 2:
					this.cancelDragging();
				}

				this.setMouseCursor(this.normalCursor);
			}

			return true;
		}
	}

	void mouseLeftTableArea() {
		this.lastMouseY = Integer.MIN_VALUE;
		this.lastMouseRow = -1;
		this.lastMouseColumn = -1;
	}

	Widget routeMouseEvent(Event evt) {
		if(evt.getType() == Event.Type.MOUSE_EXITED) {
			this.mouseLeftTableArea();
		} else {
			this.lastMouseY = evt.getMouseY();
		}

		if(this.dragActive == 0) {
			boolean inHeader = this.isMouseInColumnHeader(evt.getMouseY());
			if(inHeader) {
				if(this.lastMouseRow != -1 || this.lastMouseColumn != -1) {
					this.lastMouseRow = -1;
					this.lastMouseColumn = -1;
					this.updateTooltip();
				}
			} else {
				int row = this.getRowUnderMouse(evt.getMouseY());
				int column = this.getColumnUnderMouse(evt.getMouseX());
				if(this.lastMouseRow != row || this.lastMouseColumn != column) {
					this.lastMouseRow = row;
					this.lastMouseColumn = column;
					this.updateTooltip();
				}
			}
		}

		return super.routeMouseEvent(evt);
	}

	protected boolean handleMouseEvent(Event evt) {
		Event.Type evtType = evt.getType();
		int row;
		if(this.dragActive != 0) {
			switch(this.dragActive) {
			case 1:
				int i10 = this.getInnerWidth();
				if(this.dragColumn >= 0 && i10 > 0) {
					row = this.clampColumnWidth(evt.getMouseX() - this.dragStartX);
					this.columnHeaderDragged(row);
				}
				break;
			case 2:
				this.setMouseCursor(this.dragListener.dragged(evt));
				if(evt.isMouseDragEnd()) {
					this.dragListener.dragStopped(evt);
				}
			case 3:
				break;
			default:
				throw new AssertionError();
			}

			if(evt.isMouseDragEnd()) {
				this.dragActive = 0;
				this.setMouseCursor(this.normalCursor);
			}

			return true;
		} else {
			boolean inHeader = this.isMouseInColumnHeader(evt.getMouseY());
			if(inHeader) {
				row = this.getColumnSeparatorUnderMouse(evt.getMouseX());
				boolean z11 = this.isFixedWidthMode();
				if(row >= 0 && (row < this.getNumColumns() - 1 || !z11)) {
					this.setMouseCursor(this.columnResizeCursor);
					if(evtType == Event.Type.MOUSE_BTNDOWN) {
						this.dragStartColWidth = this.getColumnWidth(row);
						this.dragColumn = row;
						this.dragStartX = evt.getMouseX() - this.dragStartColWidth;
						if(z11) {
							for(int i12 = 0; i12 < this.numColumns; ++i12) {
								this.columnHeaders[i12].setColumnWidth(this.getColumnWidth(i12));
							}

							this.dragStartSumWidth = this.dragStartColWidth + this.getColumnWidth(row + 1);
						}
					}

					if(evt.isMouseDragEvent()) {
						this.dragActive = 1;
					}

					return true;
				}
			} else {
				row = this.lastMouseRow;
				int column = this.lastMouseColumn;
				if(evt.isMouseDragEvent()) {
					if(this.dragListener != null && this.dragListener.dragStarted(row, row, evt)) {
						this.setMouseCursor(this.dragListener.dragged(evt));
						this.dragActive = 2;
					} else {
						this.dragActive = 3;
						this.setMouseCursor(this.dragNotPossibleCursor);
					}

					return true;
				}

				if(this.selectionManager != null) {
					this.selectionManager.handleMouseEvent(row, column, evt);
				}

				TableBase.Callback cb;
				int i7;
				int i8;
				TableBase.Callback[] tableBase$Callback9;
				if(evtType == Event.Type.MOUSE_CLICKED && evt.getMouseClickCount() == 2 && this.callbacks != null) {
					tableBase$Callback9 = this.callbacks;
					i8 = this.callbacks.length;

					for(i7 = 0; i7 < i8; ++i7) {
						cb = tableBase$Callback9[i7];
						cb.mouseDoubleClicked(row, column);
					}
				}

				if(evtType == Event.Type.MOUSE_BTNUP && evt.getMouseButton() == 1 && this.callbacks != null) {
					tableBase$Callback9 = this.callbacks;
					i8 = this.callbacks.length;

					for(i7 = 0; i7 < i8; ++i7) {
						cb = tableBase$Callback9[i7];
						cb.mouseRightClick(row, column, evt);
					}
				}
			}

			this.setMouseCursor(this.normalCursor);
			return evtType != Event.Type.MOUSE_WHEEL;
		}
	}

	private void columnHeaderDragged(int newWidth) {
		if(this.isFixedWidthMode()) {
			assert this.dragColumn + 1 < this.numColumns;

			newWidth = Math.min(newWidth, this.dragStartSumWidth - 2 * this.columnDividerDragableDistance);
			this.columnHeaders[this.dragColumn].setColumnWidth(newWidth);
			this.columnHeaders[this.dragColumn + 1].setColumnWidth(this.dragStartSumWidth - newWidth);
			this.updateAllColumnWidth = true;
			this.invalidateLayout();
		} else {
			this.setColumnWidth(this.dragColumn, newWidth);
		}

	}

	protected void columnHeaderClicked(int column) {
		if(this.callbacks != null) {
			TableBase.Callback[] tableBase$Callback5 = this.callbacks;
			int i4 = this.callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				TableBase.Callback cb = tableBase$Callback5[i3];
				cb.columnHeaderClicked(column);
			}
		}

	}

	protected void updateAllColumnWidth() {
		this.columnModel.initializeAll(this.numColumns);
		this.updateAllColumnWidth = false;
	}

	protected void updateAll() {
		if(!this.widgetGrid.isEmpty()) {
			this.removeAllCellWidgets();
			this.widgetGrid.clear();
		}

		if(this.rowModel != null) {
			this.autoSizeAllRows = true;
		}

		this.updateAllCellWidgets = true;
		this.updateAllColumnWidth = true;
		this.invalidateLayout();
	}

	protected void modelAllChanged() {
		if(this.columnHeaders != null) {
			this.removeColumnHeaders(0, this.columnHeaders.length);
		}

		this.dropMarkerRow = -1;
		this.columnHeaders = new TableBase.ColumnHeader[this.numColumns];

		for(int i = 0; i < this.numColumns; ++i) {
			this.columnHeaders[i] = this.createColumnHeader(i);
			this.updateColumnHeader(i);
		}

		this.updateColumnHeaderNumbers();
		if(this.selectionManager != null) {
			this.selectionManager.modelChanged();
		}

		this.updateAll();
	}

	protected void modelRowChanged(int row) {
		if(this.rowModel != null && this.autoSizeRow(row)) {
			this.invalidateLayout();
		}

		for(int col = 0; col < this.numColumns; ++col) {
			this.updateCellWidget(row, col);
		}

		this.invalidateLayoutLocally();
	}

	protected void modelRowsChanged(int idx, int count) {
		this.checkRowRange(idx, count);
		boolean rowHeightChanged = false;

		for(int i = 0; i < count; ++i) {
			if(this.rowModel != null) {
				rowHeightChanged |= this.autoSizeRow(idx + i);
			}

			for(int col = 0; col < this.numColumns; ++col) {
				this.updateCellWidget(idx + i, col);
			}
		}

		this.invalidateLayoutLocally();
		if(rowHeightChanged) {
			this.invalidateLayout();
		}

	}

	protected void modelCellChanged(int row, int column) {
		this.checkRowIndex(row);
		this.checkColumnIndex(column);
		if(this.rowModel != null) {
			this.autoSizeRow(row);
		}

		this.updateCellWidget(row, column);
		this.invalidateLayout();
	}

	protected void modelRowsInserted(int row, int count) {
		this.checkRowRange(row, count);
		if(this.rowModel != null) {
			this.rowModel.insert(row, count);
		}

		if(this.dropMarkerRow > row || this.dropMarkerRow == row && this.dropMarkerBeforeRow) {
			this.dropMarkerRow += count;
		}

		int rowsStart;
		if(!this.widgetGrid.isEmpty() || this.hasCellWidgetCreators) {
			this.removeAllCellWidgets();
			this.widgetGrid.insertRows(row, count);

			for(int sp = 0; sp < count; ++sp) {
				for(rowsStart = 0; rowsStart < this.numColumns; ++rowsStart) {
					this.updateCellWidget(row + sp, rowsStart);
				}
			}
		}

		this.invalidateLayout();
		if(row < this.getRowFromPosition(this.scrollPosY)) {
			ScrollPane scrollPane6 = ScrollPane.getContainingScrollPane(this);
			if(scrollPane6 != null) {
				rowsStart = this.getRowStartPosition(row);
				int rowsEnd = this.getRowEndPosition(row + count - 1);
				scrollPane6.setScrollPositionY(this.scrollPosY + rowsEnd - rowsStart);
			}
		}

		if(this.selectionManager != null) {
			this.selectionManager.rowsInserted(row, count);
		}

	}

	protected void modelRowsDeleted(int row, int count) {
		if(row + count <= this.getRowFromPosition(this.scrollPosY)) {
			ScrollPane sp = ScrollPane.getContainingScrollPane(this);
			if(sp != null) {
				int rowsStart = this.getRowStartPosition(row);
				int rowsEnd = this.getRowEndPosition(row + count - 1);
				sp.setScrollPositionY(this.scrollPosY - rowsEnd + rowsStart);
			}
		}

		if(this.rowModel != null) {
			this.rowModel.remove(row, count);
		}

		if(this.dropMarkerRow >= row) {
			if(this.dropMarkerRow < row + count) {
				this.dropMarkerRow = -1;
			} else {
				this.dropMarkerRow -= count;
			}
		}

		if(!this.widgetGrid.isEmpty()) {
			this.widgetGrid.iterate(row, 0, row + count - 1, this.numColumns, this.removeCellWidgetsFunction);
			this.widgetGrid.removeRows(row, count);
		}

		if(this.selectionManager != null) {
			this.selectionManager.rowsDeleted(row, count);
		}

		this.invalidateLayout();
	}

	protected void modelColumnsInserted(int column, int count) {
		this.checkColumnRange(column, count);
		TableBase.ColumnHeader[] newColumnHeaders = new TableBase.ColumnHeader[this.numColumns];
		System.arraycopy(this.columnHeaders, 0, newColumnHeaders, 0, column);
		System.arraycopy(this.columnHeaders, column, newColumnHeaders, column + count, this.numColumns - (column + count));

		int sp;
		for(sp = 0; sp < count; ++sp) {
			newColumnHeaders[column + sp] = this.createColumnHeader(column + sp);
		}

		this.columnHeaders = newColumnHeaders;
		this.updateColumnHeaderNumbers();
		this.columnModel.insert(column, count);
		int columnsStart;
		if(!this.widgetGrid.isEmpty() || this.hasCellWidgetCreators) {
			this.removeAllCellWidgets();
			this.widgetGrid.insertColumns(column, count);

			for(sp = 0; sp < this.numRows; ++sp) {
				for(columnsStart = 0; columnsStart < count; ++columnsStart) {
					this.updateCellWidget(sp, column + columnsStart);
				}
			}
		}

		if(column < this.getColumnStartPosition(this.scrollPosX)) {
			ScrollPane scrollPane7 = ScrollPane.getContainingScrollPane(this);
			if(scrollPane7 != null) {
				columnsStart = this.getColumnStartPosition(column);
				int columnsEnd = this.getColumnEndPosition(column + count - 1);
				scrollPane7.setScrollPositionX(this.scrollPosX + columnsEnd - columnsStart);
			}
		}

		this.invalidateLayout();
	}

	protected void modelColumnsDeleted(int column, int count) {
		if(column + count <= this.getColumnStartPosition(this.scrollPosX)) {
			ScrollPane newColumnHeaders = ScrollPane.getContainingScrollPane(this);
			if(newColumnHeaders != null) {
				int columnsStart = this.getColumnStartPosition(column);
				int columnsEnd = this.getColumnEndPosition(column + count - 1);
				newColumnHeaders.setScrollPositionY(this.scrollPosX - columnsEnd + columnsStart);
			}
		}

		this.columnModel.remove(column, count);
		if(!this.widgetGrid.isEmpty()) {
			this.widgetGrid.iterate(0, column, this.numRows, column + count - 1, this.removeCellWidgetsFunction);
			this.widgetGrid.removeColumns(column, count);
		}

		this.removeColumnHeaders(column, count);
		TableBase.ColumnHeader[] newColumnHeaders1 = new TableBase.ColumnHeader[this.numColumns];
		System.arraycopy(this.columnHeaders, 0, newColumnHeaders1, 0, column);
		System.arraycopy(this.columnHeaders, column + count, newColumnHeaders1, column, this.numColumns - count);
		this.columnHeaders = newColumnHeaders1;
		this.updateColumnHeaderNumbers();
		this.invalidateLayout();
	}

	protected void modelColumnHeaderChanged(int column) {
		this.checkColumnIndex(column);
		this.updateColumnHeader(column);
	}

	public interface Callback {
		void mouseDoubleClicked(int i1, int i2);

		void mouseRightClick(int i1, int i2, Event event3);

		void columnHeaderClicked(int i1);
	}

	public interface CellRenderer {
		void applyTheme(ThemeInfo themeInfo1);

		String getTheme();

		void setCellData(int i1, int i2, Object object3);

		int getColumnSpan();

		int getPreferredHeight();

		Widget getCellRenderWidget(int i1, int i2, int i3, int i4, boolean z5);
	}

	static class CellWidgetContainer extends Widget {
		CellWidgetContainer() {
			this.setTheme("");
			this.setClip(true);
		}

		protected void childInvalidateLayout(Widget child) {
		}

		protected void sizeChanged() {
		}

		protected void childAdded(Widget child) {
		}

		protected void childRemoved(Widget exChild) {
		}

		protected void allChildrenRemoved() {
		}
	}

	public interface CellWidgetCreator extends TableBase.CellRenderer {
		Widget updateWidget(Widget widget1);

		void positionWidget(Widget widget1, int i2, int i3, int i4, int i5);
	}

	protected class ColumnHeader extends Button implements Runnable {
		int column;
		private int columnWidth;
		int springWidth;
		final DialogLayout.Spring spring = new DialogLayout.Spring() {
			int getMinSize(int axis) {
				return TableBase.this.clampColumnWidth(ColumnHeader.this.getMinWidth());
			}

			int getPrefSize(int axis) {
				return ColumnHeader.this.getPreferredWidth();
			}

			int getMaxSize(int axis) {
				return ColumnHeader.this.getMaxWidth();
			}

			void setSize(int axis, int pos, int size) {
				ColumnHeader.this.springWidth = size;
			}
		};

		public ColumnHeader() {
			this.addCallback(this);
		}

		public int getColumnWidth() {
			return this.columnWidth;
		}

		public void setColumnWidth(int columnWidth) {
			this.columnWidth = columnWidth;
		}

		public int getPreferredWidth() {
			if(this.columnWidth > 0) {
				return this.columnWidth;
			} else {
				DialogLayout.Gap mpm = TableBase.this.getColumnMPM(this.column);
				int prefWidth = mpm != null ? mpm.preferred : TableBase.this.defaultColumnWidth;
				return Math.max(prefWidth, super.getPreferredWidth());
			}
		}

		public int getMinWidth() {
			DialogLayout.Gap mpm = TableBase.this.getColumnMPM(this.column);
			int minWidth = mpm != null ? mpm.min : 0;
			return Math.max(minWidth, super.getPreferredWidth());
		}

		public int getMaxWidth() {
			DialogLayout.Gap mpm = TableBase.this.getColumnMPM(this.column);
			int maxWidth = mpm != null ? mpm.max : 32767;
			return maxWidth;
		}

		public void adjustSize() {
		}

		protected boolean handleEvent(Event evt) {
			if(evt.isMouseEventNoWheel()) {
				TableBase.this.mouseLeftTableArea();
			}

			return super.handleEvent(evt);
		}

		public void run() {
			TableBase.this.columnHeaderClicked(this.column);
		}
	}

	protected class ColumnSizeSequence extends SizeSequence {
		protected void initializeSizes(int index, int count) {
			int i;
			if(TableBase.this.isFixedWidthMode()) {
				this.computeColumnHeaderLayout();

				for(i = 0; i < count; ++index) {
					this.table[index] = TableBase.this.clampColumnWidth(TableBase.this.columnHeaders[i].springWidth);
					++i;
				}
			} else {
				for(i = 0; i < count; ++index) {
					this.table[index] = TableBase.this.computePreferredColumnWidth(index);
					++i;
				}
			}

		}

		protected boolean update(int index) {
			int width;
			if(TableBase.this.isFixedWidthMode()) {
				this.computeColumnHeaderLayout();
				width = TableBase.this.clampColumnWidth(TableBase.this.columnHeaders[index].springWidth);
			} else {
				width = TableBase.this.computePreferredColumnWidth(index);
			}

			return this.setSize(index, width);
		}

		void computeColumnHeaderLayout() {
			if(TableBase.this.columnHeaders != null) {
				DialogLayout.SequentialGroup g = (DialogLayout.SequentialGroup)(new DialogLayout()).createSequentialGroup();
				TableBase.ColumnHeader[] tableBase$ColumnHeader5 = TableBase.this.columnHeaders;
				int i4 = TableBase.this.columnHeaders.length;

				for(int i3 = 0; i3 < i4; ++i3) {
					TableBase.ColumnHeader h = tableBase$ColumnHeader5[i3];
					g.addSpring(h.spring);
				}

				g.setSize(0, 0, TableBase.this.getInnerWidth());
			}

		}
	}

	public interface DragListener {
		boolean dragStarted(int i1, int i2, Event event3);

		MouseCursor dragged(Event event1);

		void dragStopped(Event event1);

		void dragCanceled();
	}

	class InsertCellWidgets implements SparseGrid.GridFunction {
		public void apply(int row, int column, SparseGrid.Entry e) {
			TableBase.this.insertCellWidget(row, column, (TableBase.WidgetEntry)e);
		}
	}

	public interface KeyboardSearchHandler {
		boolean handleKeyEvent(Event event1);

		boolean isActive();

		void updateInfoWindowPosition();
	}

	class RemoveCellWidgets implements SparseGrid.GridFunction {
		public void apply(int row, int column, SparseGrid.Entry e) {
			TableBase.WidgetEntry widgetEntry = (TableBase.WidgetEntry)e;
			Widget widget = widgetEntry.widget;
			if(widget != null) {
				TableBase.this.removeCellWidget(widget);
			}

		}
	}

	class RowSizeSequence extends SizeSequence {
		public RowSizeSequence(int initialCapacity) {
			super(initialCapacity);
		}

		protected void initializeSizes(int index, int count) {
			for(int i = 0; i < count; ++index) {
				this.table[index] = TableBase.this.computeRowHeight(index);
				++i;
			}

		}
	}

	public static class StringCellRenderer extends TextWidget implements TableBase.CellRenderer {
		public StringCellRenderer() {
			this.setCache(false);
			this.setClip(true);
		}

		public void applyTheme(ThemeInfo themeInfo) {
			super.applyTheme(themeInfo);
		}

		public void setCellData(int row, int column, Object data) {
			this.setCharSequence(String.valueOf(data));
		}

		public int getColumnSpan() {
			return 1;
		}

		protected void sizeChanged() {
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			this.setPosition(x, y);
			this.setSize(width, height);
			this.getAnimationState().setAnimationState(TableBase.STATE_SELECTED, isSelected);
			return this;
		}
	}

	static class WidgetEntry extends SparseGrid.Entry {
		Widget widget;
		TableBase.CellWidgetCreator creator;
	}
}
