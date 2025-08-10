package de.matthiasmann.twl;

import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.utils.CallbackSupport;

public class ListBox extends Widget {
	public static final int NO_SELECTION = -1;
	public static final int DEFAULT_CELL_HEIGHT = 20;
	public static final int SINGLE_COLUMN = -1;
	private static final ListBoxDisplay[] EMPTY_LABELS = new ListBoxDisplay[0];
	private final ListModel.ChangeListener modelCallback;
	private final Scrollbar scrollbar;
	private ListBoxDisplay[] labels;
	private ListModel model;
	private int cellHeight;
	private int cellWidth;
	private boolean rowMajor;
	private boolean fixedCellWidth;
	private boolean fixedCellHeight;
	private int numCols;
	private int firstVisible;
	private int selected;
	private int numEntries;
	private boolean needUpdate;
	private CallbackWithReason[] callbacks;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public ListBox() {
		this.cellHeight = 20;
		this.cellWidth = -1;
		this.rowMajor = true;
		this.numCols = 1;
		this.selected = -1;
		ListBox.LImpl li = new ListBox.LImpl((ListBox.LImpl)null);
		this.modelCallback = li;
		this.scrollbar = new Scrollbar();
		this.scrollbar.addCallback(li);
		this.labels = EMPTY_LABELS;
		super.insertChild(this.scrollbar, 0);
		this.setSize(200, 300);
		this.setCanAcceptKeyboardFocus(true);
		this.setDepthFocusTraversal(false);
	}

	public ListBox(ListModel model) {
		this();
		this.setModel(model);
	}

	public ListModel getModel() {
		return this.model;
	}

	public void setModel(ListModel model) {
		if(this.model != model) {
			if(this.model != null) {
				this.model.removeChangeListener(this.modelCallback);
			}

			this.model = model;
			if(model != null) {
				model.addChangeListener(this.modelCallback);
			}

			this.modelCallback.allChanged();
		}

	}

	public void addCallback(CallbackWithReason cb) {
		this.callbacks = (CallbackWithReason[])CallbackSupport.addCallbackToList(this.callbacks, cb, CallbackWithReason.class);
	}

	public void removeCallback(CallbackWithReason cb) {
		this.callbacks = (CallbackWithReason[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	private void doCallback(ListBox.CallbackReason reason) {
		CallbackSupport.fireCallbacks(this.callbacks, reason);
	}

	public int getCellHeight() {
		return this.cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		if(cellHeight < 1) {
			throw new IllegalArgumentException("cellHeight < 1");
		} else {
			this.cellHeight = cellHeight;
		}
	}

	public int getCellWidth() {
		return this.cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		if(cellWidth < 1 && cellWidth != -1) {
			throw new IllegalArgumentException("cellWidth < 1");
		} else {
			this.cellWidth = cellWidth;
		}
	}

	public boolean isFixedCellHeight() {
		return this.fixedCellHeight;
	}

	public void setFixedCellHeight(boolean fixedCellHeight) {
		this.fixedCellHeight = fixedCellHeight;
	}

	public boolean isFixedCellWidth() {
		return this.fixedCellWidth;
	}

	public void setFixedCellWidth(boolean fixedCellWidth) {
		this.fixedCellWidth = fixedCellWidth;
	}

	public boolean isRowMajor() {
		return this.rowMajor;
	}

	public void setRowMajor(boolean rowMajor) {
		this.rowMajor = rowMajor;
	}

	public int getFirstVisible() {
		return this.firstVisible;
	}

	public int getLastVisible() {
		return this.getFirstVisible() + this.labels.length - 1;
	}

	public void setFirstVisible(int firstVisible) {
		firstVisible = Math.max(0, Math.min(firstVisible, this.numEntries - 1));
		if(this.firstVisible != firstVisible) {
			this.firstVisible = firstVisible;
			this.scrollbar.setValue(firstVisible / this.numCols, false);
			this.needUpdate = true;
		}

	}

	public int getSelected() {
		return this.selected;
	}

	public void setSelected(int selected) {
		this.setSelected(selected, true, ListBox.CallbackReason.SET_SELECTED);
	}

	public void setSelected(int selected, boolean scroll) {
		this.setSelected(selected, scroll, ListBox.CallbackReason.SET_SELECTED);
	}

	void setSelected(int selected, boolean scroll, ListBox.CallbackReason reason) {
		if(selected >= -1 && selected < this.numEntries) {
			if(scroll) {
				this.validateLayout();
				if(selected == -1) {
					this.setFirstVisible(0);
				} else {
					int delta = this.getFirstVisible() - selected;
					int deltaRows;
					if(delta > 0) {
						deltaRows = (delta + this.numCols - 1) / this.numCols;
						this.setFirstVisible(this.getFirstVisible() - deltaRows * this.numCols);
					} else {
						delta = selected - this.getLastVisible();
						if(delta > 0) {
							deltaRows = (delta + this.numCols - 1) / this.numCols;
							this.setFirstVisible(this.getFirstVisible() + deltaRows * this.numCols);
						}
					}
				}
			}

			if(this.selected != selected) {
				this.selected = selected;
				this.needUpdate = true;
				this.doCallback(reason);
			} else if(reason.actionRequested() || reason == ListBox.CallbackReason.MOUSE_CLICK) {
				this.doCallback(reason);
			}

		} else {
			throw new IllegalArgumentException();
		}
	}

	public void scrollToSelected() {
		this.setSelected(this.selected, true, ListBox.CallbackReason.SET_SELECTED);
	}

	public int getNumEntries() {
		return this.numEntries;
	}

	public int getNumRows() {
		return (this.numEntries + this.numCols - 1) / this.numCols;
	}

	public int getNumColumns() {
		return this.numCols;
	}

	public int findEntryByName(String prefix) {
		int i;
		for(i = this.selected + 1; i < this.numEntries; ++i) {
			if(this.model.matchPrefix(i, prefix)) {
				return i;
			}
		}

		for(i = 0; i < this.selected; ++i) {
			if(this.model.matchPrefix(i, prefix)) {
				return i;
			}
		}

		return -1;
	}

	public Widget getWidgetAt(int x, int y) {
		return this;
	}

	public int getEntryAt(int x, int y) {
		int n = Math.max(this.labels.length, this.numEntries - this.firstVisible);

		for(int i = 0; i < n; ++i) {
			if(this.labels[i].getWidget().isInside(x, y)) {
				return this.firstVisible + i;
			}
		}

		return -1;
	}

	public void insertChild(Widget child, int index) throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException();
	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException();
	}

	public Widget removeChild(int index) throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException();
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.setCellHeight(themeInfo.getParameter("cellHeight", 20));
		this.setCellWidth(themeInfo.getParameter("cellWidth", -1));
		this.setRowMajor(themeInfo.getParameter("rowMajor", true));
		this.setFixedCellWidth(themeInfo.getParameter("fixedCellWidth", false));
		this.setFixedCellHeight(themeInfo.getParameter("fixedCellHeight", false));
	}

	protected void goKeyboard(int dir) {
		int newPos = this.selected + dir;
		if(newPos >= 0 && newPos < this.numEntries) {
			this.setSelected(newPos, true, ListBox.CallbackReason.KEYBOARD);
		}

	}

	protected boolean isSearchChar(char ch) {
		return ch != 0 && Character.isLetterOrDigit(ch);
	}

	protected void keyboardFocusGained() {
		this.setLabelFocused(true);
	}

	protected void keyboardFocusLost() {
		this.setLabelFocused(false);
	}

	private void setLabelFocused(boolean focused) {
		int idx = this.selected - this.firstVisible;
		if(idx >= 0 && idx < this.labels.length) {
			this.labels[idx].setFocused(focused);
		}

	}

	public boolean handleEvent(Event evt) {
		switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
		case 8:
			this.scrollbar.scroll(-evt.getMouseWheelDelta());
			return true;
		case 9:
			switch(evt.getKeyCode()) {
			case 28:
				this.setSelected(this.selected, false, ListBox.CallbackReason.KEYBOARD_RETURN);
				break;
			case 199:
				if(this.numEntries > 0) {
					this.setSelected(0, true, ListBox.CallbackReason.KEYBOARD);
				}
				break;
			case 200:
				this.goKeyboard(-this.numCols);
				break;
			case 201:
				if(this.numEntries > 0) {
					this.setSelected(Math.max(0, this.selected - this.labels.length), true, ListBox.CallbackReason.KEYBOARD);
				}
				break;
			case 203:
				this.goKeyboard(-1);
				break;
			case 205:
				this.goKeyboard(1);
				break;
			case 207:
				this.setSelected(this.numEntries - 1, true, ListBox.CallbackReason.KEYBOARD);
				break;
			case 208:
				this.goKeyboard(this.numCols);
				break;
			case 209:
				this.setSelected(Math.min(this.numEntries - 1, this.selected + this.labels.length), true, ListBox.CallbackReason.KEYBOARD);
				break;
			default:
				if(evt.hasKeyChar() && this.isSearchChar(evt.getKeyChar())) {
					int idx = this.findEntryByName(Character.toString(evt.getKeyChar()));
					if(idx != -1) {
						this.setSelected(idx, true, ListBox.CallbackReason.KEYBOARD);
					}

					return true;
				}
			}

			return true;
		case 10:
			return true;
		default:
			return super.handleEvent(evt) ? true : evt.isMouseEvent();
		}
	}

	public int getMinWidth() {
		return Math.max(super.getMinWidth(), this.scrollbar.getMinWidth());
	}

	public int getMinHeight() {
		return Math.max(super.getMinHeight(), this.scrollbar.getMinHeight());
	}

	public int getPreferredInnerWidth() {
		return Math.max(super.getPreferredInnerWidth(), this.scrollbar.getPreferredWidth());
	}

	public int getPreferredInnerHeight() {
		return Math.max(this.getNumRows() * this.getCellHeight(), this.scrollbar.getPreferredHeight());
	}

	protected void paint(GUI gui) {
		if(this.needUpdate) {
			this.updateDisplay();
		}

		int maxFirstVisibleRow = this.computeMaxFirstVisibleRow();
		this.scrollbar.setMinMaxValue(0, maxFirstVisibleRow);
		this.scrollbar.setValue(this.firstVisible / this.numCols, false);
		super.paint(gui);
	}

	private int computeMaxFirstVisibleRow() {
		int maxFirstVisibleRow = Math.max(0, this.numEntries - this.labels.length);
		maxFirstVisibleRow = (maxFirstVisibleRow + this.numCols - 1) / this.numCols;
		return maxFirstVisibleRow;
	}

	private void updateDisplay() {
		this.needUpdate = false;
		if(this.selected >= this.numEntries) {
			this.selected = -1;
		}

		int maxFirstVisibleRow = this.computeMaxFirstVisibleRow();
		int maxFirstVisible = maxFirstVisibleRow * this.numCols;
		if(this.firstVisible > maxFirstVisible) {
			this.firstVisible = Math.max(0, maxFirstVisible);
		}

		boolean hasFocus = this.hasKeyboardFocus();

		for(int i = 0; i < this.labels.length; ++i) {
			ListBoxDisplay label = this.labels[i];
			int cell = i + this.firstVisible;
			if(cell < this.numEntries) {
				label.setData(this.model.getEntry(cell));
				label.setTooltipContent(this.model.getEntryTooltip(cell));
			} else {
				label.setData((Object)null);
				label.setTooltipContent((Object)null);
			}

			label.setSelected(cell == this.selected);
			label.setFocused(cell == this.selected && hasFocus);
		}

	}

	protected void layout() {
		this.scrollbar.setSize(this.scrollbar.getPreferredWidth(), this.getInnerHeight());
		this.scrollbar.setPosition(this.getInnerRight() - this.scrollbar.getWidth(), this.getInnerY());
		int numRows = Math.max(1, this.getInnerHeight() / this.cellHeight);
		if(this.cellWidth != -1) {
			this.numCols = Math.max(1, (this.scrollbar.getX() - this.getInnerX()) / this.cellWidth);
		} else {
			this.numCols = 1;
		}

		this.setVisibleCells(numRows);
		this.needUpdate = true;
	}

	private void setVisibleCells(int numRows) {
		int visibleCells = numRows * this.numCols;

		assert visibleCells >= 1;

		this.scrollbar.setPageSize(visibleCells);
		int curVisible = this.labels.length;
		int newLabels = curVisible;

		while(newLabels-- > visibleCells) {
			super.removeChild(1 + newLabels);
		}

		ListBoxDisplay[] listBoxDisplay15 = new ListBoxDisplay[visibleCells];
		System.arraycopy(this.labels, 0, listBoxDisplay15, 0, Math.min(visibleCells, this.labels.length));
		this.labels = listBoxDisplay15;

		final int innerWidth;
		for(innerWidth = curVisible; innerWidth < visibleCells; ++innerWidth) {
			ListBoxDisplay i = this.createDisplay();
			i.addListBoxCallback(new CallbackWithReason() {
				public void callback(ListBox.CallbackReason reason) {
					int cell = ListBox.this.getFirstVisible() + innerWidth;
					if(cell < ListBox.this.getNumEntries()) {
						ListBox.this.setSelected(cell, false, reason);
					}

				}

				public void callback(Enum enum1) {
					this.callback((ListBox.CallbackReason)enum1);
				}
			});
			super.insertChild(i.getWidget(), 1 + innerWidth);
			this.labels[innerWidth] = i;
		}

		innerWidth = this.scrollbar.getX() - this.getInnerX();
		int innerHeight = this.getInnerHeight();

		for(int i16 = 0; i16 < visibleCells; ++i16) {
			int row;
			int col;
			if(this.rowMajor) {
				row = i16 / this.numCols;
				col = i16 % this.numCols;
			} else {
				row = i16 % numRows;
				col = i16 / numRows;
			}

			int y;
			int h;
			if(this.fixedCellHeight) {
				y = row * this.cellHeight;
				h = this.cellHeight;
			} else {
				y = row * innerHeight / numRows;
				h = (row + 1) * innerHeight / numRows - y;
			}

			int x;
			int w;
			if(this.fixedCellWidth && this.cellWidth != -1) {
				x = col * this.cellWidth;
				w = this.cellWidth;
			} else {
				x = col * innerWidth / this.numCols;
				w = (col + 1) * innerWidth / this.numCols - x;
			}

			Widget cell = (Widget)this.labels[i16];
			cell.setSize(Math.max(0, w), Math.max(0, h));
			cell.setPosition(x + this.getInnerX(), y + this.getInnerY());
		}

	}

	protected ListBoxDisplay createDisplay() {
		return new ListBox.ListBoxLabel();
	}

	void entriesInserted(int first, int last) {
		int delta = last - first + 1;
		int prevNumEntries = this.numEntries;
		this.numEntries += delta;
		int fv = this.getFirstVisible();
		if(fv >= first && prevNumEntries >= this.labels.length) {
			fv += delta;
			this.setFirstVisible(fv);
		}

		int s = this.getSelected();
		if(s >= first) {
			this.setSelected(s + delta, false, ListBox.CallbackReason.MODEL_CHANGED);
		}

		if(first <= this.getLastVisible() && last >= fv) {
			this.needUpdate = true;
		}

	}

	void entriesDeleted(int first, int last) {
		int delta = last - first + 1;
		this.numEntries -= delta;
		int fv = this.getFirstVisible();
		int lv = this.getLastVisible();
		if(fv > last) {
			this.setFirstVisible(fv - delta);
		} else if(fv <= last && lv >= first) {
			this.setFirstVisible(first);
		}

		int s = this.getSelected();
		if(s > last) {
			this.setSelected(s - delta, false, ListBox.CallbackReason.MODEL_CHANGED);
		} else if(s >= first && s <= last) {
			this.setSelected(-1, false, ListBox.CallbackReason.MODEL_CHANGED);
		}

	}

	void entriesChanged(int first, int last) {
		int fv = this.getFirstVisible();
		int lv = this.getLastVisible();
		if(fv <= last && lv >= first) {
			this.needUpdate = true;
		}

	}

	void allChanged() {
		this.numEntries = this.model != null ? this.model.getNumEntries() : 0;
		this.setSelected(-1, false, ListBox.CallbackReason.MODEL_CHANGED);
		this.setFirstVisible(0);
		this.needUpdate = true;
	}

	void scrollbarChanged() {
		this.setFirstVisible(this.scrollbar.getValue() * this.numCols);
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;
		if($SWITCH_TABLE$de$matthiasmann$twl$Event$Type != null) {
			return i10000;
		} else {
			int[] i0 = new int[Event.Type.values().length];

			try {
				i0[Event.Type.KEY_PRESSED.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError12) {
			}

			try {
				i0[Event.Type.KEY_RELEASED.ordinal()] = 10;
			} catch (NoSuchFieldError noSuchFieldError11) {
			}

			try {
				i0[Event.Type.MOUSE_BTNDOWN.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError10) {
			}

			try {
				i0[Event.Type.MOUSE_BTNUP.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[Event.Type.MOUSE_CLICKED.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[Event.Type.MOUSE_DRAGGED.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[Event.Type.MOUSE_ENTERED.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[Event.Type.MOUSE_EXITED.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[Event.Type.MOUSE_MOVED.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[Event.Type.MOUSE_WHEEL.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[Event.Type.POPUP_CLOSED.ordinal()] = 12;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[Event.Type.POPUP_OPENED.ordinal()] = 11;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$Event$Type = i0;
			return i0;
		}
	}

	public static enum CallbackReason {
		MODEL_CHANGED(false),
		SET_SELECTED(false),
		MOUSE_CLICK(false),
		MOUSE_DOUBLE_CLICK(true),
		KEYBOARD(false),
		KEYBOARD_RETURN(true);

		final boolean forceCallback;

		private CallbackReason(boolean forceCallback) {
			this.forceCallback = forceCallback;
		}

		public boolean actionRequested() {
			return this.forceCallback;
		}
	}

	private class LImpl implements ListModel.ChangeListener, Runnable {
		private LImpl() {
		}

		public void entriesInserted(int first, int last) {
			ListBox.this.entriesInserted(first, last);
		}

		public void entriesDeleted(int first, int last) {
			ListBox.this.entriesDeleted(first, last);
		}

		public void entriesChanged(int first, int last) {
			ListBox.this.entriesChanged(first, last);
		}

		public void allChanged() {
			ListBox.this.allChanged();
		}

		public void run() {
			ListBox.this.scrollbarChanged();
		}

		LImpl(ListBox.LImpl listBox$LImpl2) {
			this();
		}
	}

	protected static class ListBoxLabel extends TextWidget implements ListBoxDisplay {
		public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_SELECTED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("selected");
		public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_EMPTY = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("empty");
		private boolean selected;
		private CallbackWithReason[] callbacks;
		private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

		public ListBoxLabel() {
			this.setClip(true);
			this.setTheme("display");
		}

		public boolean isSelected() {
			return this.selected;
		}

		public void setSelected(boolean selected) {
			if(this.selected != selected) {
				this.selected = selected;
				this.getAnimationState().setAnimationState(STATE_SELECTED, selected);
			}

		}

		public boolean isFocused() {
			return this.getAnimationState().getAnimationState(STATE_KEYBOARD_FOCUS);
		}

		public void setFocused(boolean focused) {
			this.getAnimationState().setAnimationState(STATE_KEYBOARD_FOCUS, focused);
		}

		public void setData(Object data) {
			this.setCharSequence(data == null ? "" : data.toString());
			this.getAnimationState().setAnimationState(STATE_EMPTY, data == null);
		}

		public Widget getWidget() {
			return this;
		}

		public void addListBoxCallback(CallbackWithReason cb) {
			this.callbacks = (CallbackWithReason[])CallbackSupport.addCallbackToList(this.callbacks, cb, CallbackWithReason.class);
		}

		public void removeListBoxCallback(CallbackWithReason cb) {
			this.callbacks = (CallbackWithReason[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
		}

		protected void doListBoxCallback(ListBox.CallbackReason reason) {
			CallbackSupport.fireCallbacks(this.callbacks, reason);
		}

		protected boolean handleListBoxEvent(Event evt) {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 3:
				if(!this.selected) {
					this.doListBoxCallback(ListBox.CallbackReason.MOUSE_CLICK);
				}

				return true;
			case 4:
			default:
				return false;
			case 5:
				if(this.selected && evt.getMouseClickCount() == 2) {
					this.doListBoxCallback(ListBox.CallbackReason.MOUSE_DOUBLE_CLICK);
				}

				return true;
			}
		}

		protected boolean handleEvent(Event evt) {
			this.handleMouseHover(evt);
			return !evt.isMouseDragEvent() && this.handleListBoxEvent(evt) ? true : (super.handleEvent(evt) ? true : evt.isMouseEventNoWheel());
		}

		static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type() {
			int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;
			if($SWITCH_TABLE$de$matthiasmann$twl$Event$Type != null) {
				return i10000;
			} else {
				int[] i0 = new int[Event.Type.values().length];

				try {
					i0[Event.Type.KEY_PRESSED.ordinal()] = 9;
				} catch (NoSuchFieldError noSuchFieldError12) {
				}

				try {
					i0[Event.Type.KEY_RELEASED.ordinal()] = 10;
				} catch (NoSuchFieldError noSuchFieldError11) {
				}

				try {
					i0[Event.Type.MOUSE_BTNDOWN.ordinal()] = 3;
				} catch (NoSuchFieldError noSuchFieldError10) {
				}

				try {
					i0[Event.Type.MOUSE_BTNUP.ordinal()] = 4;
				} catch (NoSuchFieldError noSuchFieldError9) {
				}

				try {
					i0[Event.Type.MOUSE_CLICKED.ordinal()] = 5;
				} catch (NoSuchFieldError noSuchFieldError8) {
				}

				try {
					i0[Event.Type.MOUSE_DRAGGED.ordinal()] = 6;
				} catch (NoSuchFieldError noSuchFieldError7) {
				}

				try {
					i0[Event.Type.MOUSE_ENTERED.ordinal()] = 1;
				} catch (NoSuchFieldError noSuchFieldError6) {
				}

				try {
					i0[Event.Type.MOUSE_EXITED.ordinal()] = 7;
				} catch (NoSuchFieldError noSuchFieldError5) {
				}

				try {
					i0[Event.Type.MOUSE_MOVED.ordinal()] = 2;
				} catch (NoSuchFieldError noSuchFieldError4) {
				}

				try {
					i0[Event.Type.MOUSE_WHEEL.ordinal()] = 8;
				} catch (NoSuchFieldError noSuchFieldError3) {
				}

				try {
					i0[Event.Type.POPUP_CLOSED.ordinal()] = 12;
				} catch (NoSuchFieldError noSuchFieldError2) {
				}

				try {
					i0[Event.Type.POPUP_OPENED.ordinal()] = 11;
				} catch (NoSuchFieldError noSuchFieldError1) {
				}

				$SWITCH_TABLE$de$matthiasmann$twl$Event$Type = i0;
				return i0;
			}
		}
	}
}
