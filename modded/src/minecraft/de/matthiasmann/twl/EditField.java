package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AutoCompletionDataSource;
import de.matthiasmann.twl.model.StringModel;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.TextUtil;

import java.util.concurrent.ExecutorService;

public class EditField extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ERROR = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("error");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_READONLY = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("readonly");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HOVER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hover");
	final StringBuilder editBuffer;
	private final EditField.TextRenderer textRenderer;
	private EditField.PasswordMasker passwordMasking;
	private Runnable modelChangeListener;
	private StringModel model;
	private boolean readOnly;
	private int cursorPos;
	int scrollPos;
	int selectionStart;
	int selectionEnd;
	int numberOfLines;
	boolean multiLine;
	boolean pendingScrollToCursor;
	boolean pendingScrollToCursorForce;
	private int maxTextLength;
	private int columns;
	private Image cursorImage;
	Image selectionImage;
	private char passwordChar;
	private Object errorMsg;
	private EditField.Callback[] callbacks;
	private Menu popupMenu;
	private boolean textLongerThenWidget;
	private boolean forwardUnhandledKeysToCallback;
	private boolean autoCompletionOnSetText;
	private EditFieldAutoCompletionWindow autoCompletionWindow;
	private int autoCompletionHeight;
	private InfoWindow errorInfoWindow;
	private Label errorInfoLabel;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public EditField(AnimationState parentAnimationState) {
		super(parentAnimationState, true);
		this.maxTextLength = 32767;
		this.columns = 5;
		this.autoCompletionOnSetText = true;
		this.autoCompletionHeight = 100;
		this.editBuffer = new StringBuilder();
		this.textRenderer = new EditField.TextRenderer(this.getAnimationState());
		this.passwordChar = 42;
		this.textRenderer.setTheme("renderer");
		this.textRenderer.setClip(true);
		this.add(this.textRenderer);
		this.setCanAcceptKeyboardFocus(true);
		this.setDepthFocusTraversal(false);
		this.addActionMapping("cut", "cutToClipboard", new Object[0]);
		this.addActionMapping("copy", "copyToClipboard", new Object[0]);
		this.addActionMapping("paste", "pasteFromClipboard", new Object[0]);
		this.addActionMapping("selectAll", "selectAll", new Object[0]);
	}

	public EditField() {
		this((AnimationState)null);
	}

	public void addCallback(EditField.Callback cb) {
		this.callbacks = (EditField.Callback[])CallbackSupport.addCallbackToList(this.callbacks, cb, EditField.Callback.class);
	}

	public void removeCallback(EditField.Callback cb) {
		this.callbacks = (EditField.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	public boolean isForwardUnhandledKeysToCallback() {
		return this.forwardUnhandledKeysToCallback;
	}

	public void setForwardUnhandledKeysToCallback(boolean forwardUnhandledKeysToCallback) {
		this.forwardUnhandledKeysToCallback = forwardUnhandledKeysToCallback;
	}

	public boolean isAutoCompletionOnSetText() {
		return this.autoCompletionOnSetText;
	}

	public void setAutoCompletionOnSetText(boolean autoCompletionOnSetText) {
		this.autoCompletionOnSetText = autoCompletionOnSetText;
	}

	protected void doCallback(int key) {
		if(this.callbacks != null) {
			EditField.Callback[] editField$Callback5 = this.callbacks;
			int i4 = this.callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				EditField.Callback cb = editField$Callback5[i3];
				cb.callback(key);
			}
		}

	}

	public boolean isPasswordMasking() {
		return this.passwordMasking != null;
	}

	public void setPasswordMasking(boolean passwordMasking) {
		if(passwordMasking != this.isPasswordMasking()) {
			if(passwordMasking) {
				this.passwordMasking = new EditField.PasswordMasker(this.editBuffer, this.passwordChar);
			} else {
				this.passwordMasking = null;
			}

			this.updateTextDisplay();
		}

	}

	public char getPasswordChar() {
		return this.passwordChar;
	}

	public void setPasswordChar(char passwordChar) {
		this.passwordChar = passwordChar;
		if(this.passwordMasking != null && this.passwordMasking.maskingChar != passwordChar) {
			this.passwordMasking = new EditField.PasswordMasker(this.editBuffer, passwordChar);
			this.updateTextDisplay();
		}

	}

	public int getColumns() {
		return this.columns;
	}

	public void setColumns(int columns) {
		if(columns < 0) {
			throw new IllegalArgumentException("columns");
		} else {
			this.columns = columns;
		}
	}

	public boolean isMultiLine() {
		return this.multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
		if(!multiLine && this.numberOfLines > 1) {
			this.setText("");
		}

	}

	public StringModel getModel() {
		return this.model;
	}

	public void setModel(StringModel model) {
		if(this.model != null) {
			this.model.removeCallback(this.modelChangeListener);
		}

		this.model = model;
		if(this.model != null) {
			if(this.modelChangeListener == null) {
				this.modelChangeListener = new EditField.ModelChangeListener();
			}

			this.model.addCallback(this.modelChangeListener);
			this.modelChanged();
		}

	}

	public void setText(String text) {
		text = TextUtil.limitStringLength(text, this.maxTextLength);
		this.editBuffer.replace(0, this.editBuffer.length(), text);
		this.cursorPos = this.editBuffer.length();
		this.selectionStart = 0;
		this.selectionEnd = 0;
		this.updateText(this.autoCompletionOnSetText, 0);
		this.scrollToCursor(true);
	}

	public String getText() {
		return this.editBuffer.toString();
	}

	public String getSelectedText() {
		return this.editBuffer.substring(this.selectionStart, this.selectionEnd);
	}

	public boolean hasSelection() {
		return this.selectionStart != this.selectionEnd;
	}

	public int getCursorPos() {
		return this.cursorPos;
	}

	public int getTextLength() {
		return this.editBuffer.length();
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		if(this.readOnly != readOnly) {
			this.readOnly = readOnly;
			this.popupMenu = null;
			this.getAnimationState().setAnimationState(STATE_READONLY, readOnly);
			this.firePropertyChange("readonly", !readOnly, readOnly);
		}

	}

	public void insertText(String str) {
		if(!this.readOnly) {
			boolean update = false;
			if(this.hasSelection()) {
				this.deleteSelection();
				update = true;
			}

			int insertLength = Math.min(str.length(), this.maxTextLength - this.editBuffer.length());
			if(insertLength > 0) {
				this.editBuffer.insert(this.cursorPos, str, 0, insertLength);
				this.cursorPos += insertLength;
				update = true;
			}

			if(update) {
				this.updateText(true, 0);
			}
		}

	}

	public void pasteFromClipboard() {
		String cbText = Clipboard.getClipboard();
		if(cbText != null) {
			if(!this.multiLine) {
				cbText = TextUtil.stripNewLines(cbText);
			}

			this.insertText(cbText);
		}

	}

	public void copyToClipboard() {
		String text;
		if(this.hasSelection()) {
			text = this.getSelectedText();
		} else {
			text = this.getText();
		}

		if(this.isPasswordMasking()) {
			text = TextUtil.createString(this.passwordChar, text.length());
		}

		Clipboard.setClipboard(text);
	}

	public void cutToClipboard() {
		if(!this.hasSelection()) {
			this.selectAll();
		}

		String text = this.getSelectedText();
		if(!this.readOnly) {
			this.deleteSelection();
			this.updateText(true, 211);
		}

		if(this.isPasswordMasking()) {
			text = TextUtil.createString(this.passwordChar, text.length());
		}

		Clipboard.setClipboard(text);
	}

	public int getMaxTextLength() {
		return this.maxTextLength;
	}

	public void setMaxTextLength(int maxTextLength) {
		this.maxTextLength = maxTextLength;
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeEditField(themeInfo);
	}

	protected void applyThemeEditField(ThemeInfo themeInfo) {
		this.cursorImage = themeInfo.getImage("cursor");
		this.selectionImage = themeInfo.getImage("selection");
		this.autoCompletionHeight = themeInfo.getParameter("autocompletion-height", 100);
		this.columns = themeInfo.getParameter("columns", 5);
		this.setPasswordChar((char)themeInfo.getParameter("passwordChar", 42));
	}

	protected void layout() {
		this.layoutChildFullInnerArea(this.textRenderer);
		this.checkTextWidth();
		this.layoutInfoWindows();
	}

	protected void positionChanged() {
		this.layoutInfoWindows();
	}

	private void layoutInfoWindows() {
		if(this.autoCompletionWindow != null) {
			this.layoutAutocompletionWindow();
		}

		if(this.errorInfoWindow != null) {
			this.layoutErrorInfoWindow();
		}

	}

	private void layoutAutocompletionWindow() {
		this.autoCompletionWindow.setPosition(this.getX(), this.getBottom());
		this.autoCompletionWindow.setSize(this.getWidth(), this.autoCompletionHeight);
	}

	private int computeInnerWidth() {
		if(this.columns > 0) {
			Font font = this.getFont();
			if(font != null) {
				return font.computeTextWidth("X") * this.columns;
			}
		}

		return 0;
	}

	private int computeInnerHeight() {
		int lineHeight = this.getLineHeight();
		return this.multiLine ? lineHeight * this.numberOfLines : lineHeight;
	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		minWidth = Math.max(minWidth, this.computeInnerWidth() + this.getBorderHorizontal());
		return minWidth;
	}

	public int getMinHeight() {
		int minHeight = super.getMinHeight();
		minHeight = Math.max(minHeight, this.computeInnerHeight() + this.getBorderVertical());
		return minHeight;
	}

	public int getPreferredInnerWidth() {
		return this.computeInnerWidth();
	}

	public int getPreferredInnerHeight() {
		return this.computeInnerHeight();
	}

	public void setErrorMessage(Object errorMsg) {
		this.getAnimationState().setAnimationState(STATE_ERROR, errorMsg != null);
		if(this.errorMsg != errorMsg) {
			this.errorMsg = errorMsg;
			GUI gui = this.getGUI();
			if(gui != null) {
				gui.requestToolTipUpdate(this);
			}
		}

		if(errorMsg != null) {
			if(this.hasKeyboardFocus()) {
				this.openErrorInfoWindow();
			}
		} else if(this.errorInfoWindow != null) {
			this.errorInfoWindow.closeInfo();
		}

	}

	public Object getTooltipContent() {
		if(this.errorMsg != null) {
			return this.errorMsg;
		} else {
			Object tooltip = super.getTooltipContent();
			if(tooltip == null && !this.isPasswordMasking() && this.textLongerThenWidget && !this.hasKeyboardFocus()) {
				tooltip = this.getText();
			}

			return tooltip;
		}
	}

	public void setAutoCompletionWindow(EditFieldAutoCompletionWindow window) {
		if(this.autoCompletionWindow != window) {
			if(this.autoCompletionWindow != null) {
				this.autoCompletionWindow.closeInfo();
			}

			this.autoCompletionWindow = window;
		}

	}

	public EditFieldAutoCompletionWindow getAutoCompletionWindow() {
		return this.autoCompletionWindow;
	}

	public void setAutoCompletion(AutoCompletionDataSource dataSource) {
		if(dataSource == null) {
			this.setAutoCompletionWindow((EditFieldAutoCompletionWindow)null);
		} else {
			this.setAutoCompletionWindow(new EditFieldAutoCompletionWindow(this, dataSource));
		}

	}

	public void setAutoCompletion(AutoCompletionDataSource dataSource, ExecutorService executorService) {
		if(dataSource == null) {
			this.setAutoCompletionWindow((EditFieldAutoCompletionWindow)null);
		} else {
			this.setAutoCompletionWindow(new EditFieldAutoCompletionWindow(this, dataSource, executorService));
		}

	}

	public boolean handleEvent(Event evt) {
		boolean selectPressed = (evt.getModifiers() & 9) != 0;
		if(evt.isMouseEvent()) {
			boolean newPos = evt.getType() != Event.Type.MOUSE_EXITED && this.isMouseInside(evt);
			this.getAnimationState().setAnimationState(STATE_HOVER, newPos);
		}

		int newPos1;
		if(evt.isMouseDragEvent()) {
			if(evt.getType() == Event.Type.MOUSE_DRAGGED && (evt.getModifiers() & 64) != 0) {
				newPos1 = this.getCursorPosFromMouse(evt.getMouseX(), evt.getMouseY());
				this.setCursorPos(newPos1, true);
			}

			return true;
		} else if(super.handleEvent(evt)) {
			return true;
		} else if(this.autoCompletionWindow != null && this.autoCompletionWindow.handleEvent(evt)) {
			return true;
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 3:
				if(evt.getMouseButton() == 0 && this.isMouseInside(evt)) {
					newPos1 = this.getCursorPosFromMouse(evt.getMouseX(), evt.getMouseY());
					this.setCursorPos(newPos1, selectPressed);
					this.scrollPos = this.textRenderer.lastScrollPos;
					return true;
				}
				break;
			case 4:
				if(evt.getMouseButton() == 1 && this.isMouseInside(evt)) {
					this.showPopupMenu(evt);
					return true;
				}
				break;
			case 5:
				if(evt.getMouseClickCount() == 2) {
					newPos1 = this.getCursorPosFromMouse(evt.getMouseX(), evt.getMouseY());
					this.selectWordFromMouse(newPos1);
					this.cursorPos = this.selectionStart;
					this.scrollToCursor(false);
					this.cursorPos = this.selectionEnd;
					this.scrollToCursor(false);
					return true;
				}

				if(evt.getMouseClickCount() == 3) {
					this.selectAll();
					return true;
				}
			case 6:
			case 7:
			default:
				break;
			case 8:
				return false;
			case 9:
				switch(evt.getKeyCode()) {
				case 1:
					this.doCallback(evt.getKeyCode());
					return true;
				case 14:
					this.deletePrev();
					return true;
				case 28:
				case 156:
					if(this.multiLine) {
						if(!evt.hasKeyCharNoModifiers()) {
							break;
						}

						this.insertChar('\n');
					} else {
						this.doCallback(28);
					}

					return true;
				case 199:
					this.setCursorPos(this.computeLineStart(this.cursorPos), selectPressed);
					return true;
				case 200:
					if(this.multiLine) {
						this.moveCursorY(-1, selectPressed);
						return true;
					}
					break;
				case 203:
					this.moveCursor(-1, selectPressed);
					return true;
				case 205:
					this.moveCursor(1, selectPressed);
					return true;
				case 207:
					this.setCursorPos(this.computeLineEnd(this.cursorPos), selectPressed);
					return true;
				case 208:
					if(this.multiLine) {
						this.moveCursorY(1, selectPressed);
						return true;
					}
					break;
				case 211:
					this.deleteNext();
					return true;
				default:
					if(evt.hasKeyCharNoModifiers()) {
						this.insertChar(evt.getKeyChar());
						return true;
					}
				}

				if(this.forwardUnhandledKeysToCallback) {
					this.doCallback(evt.getKeyCode());
					return true;
				}

				return false;
			case 10:
				switch(evt.getKeyCode()) {
				case 1:
				case 14:
				case 28:
				case 156:
				case 199:
				case 203:
				case 205:
				case 207:
				case 211:
					return true;
				default:
					if(!evt.hasKeyCharNoModifiers() && !this.forwardUnhandledKeysToCallback) {
						return false;
					}

					return true;
				}
			}

			return evt.isMouseEvent();
		}
	}

	protected void showPopupMenu(Event evt) {
		if(this.popupMenu == null) {
			this.popupMenu = this.createPopupMenu();
		}

		if(this.popupMenu != null) {
			this.popupMenu.openPopupMenu(this, evt.getMouseX(), evt.getMouseY());
		}

	}

	protected Menu createPopupMenu() {
		Menu menu = new Menu();
		if(!this.readOnly) {
			menu.add("cut", new Runnable() {
				public void run() {
					EditField.this.cutToClipboard();
				}
			});
		}

		menu.add("copy", new Runnable() {
			public void run() {
				EditField.this.copyToClipboard();
			}
		});
		if(!this.readOnly) {
			menu.add("paste", new Runnable() {
				public void run() {
					EditField.this.pasteFromClipboard();
				}
			});
			menu.add("clear", new Runnable() {
				public void run() {
					if(!EditField.this.isReadOnly()) {
						EditField.this.setText("");
					}

				}
			});
		}

		menu.addSpacer();
		menu.add("select all", new Runnable() {
			public void run() {
				EditField.this.selectAll();
			}
		});
		return menu;
	}

	private void updateText(boolean updateAutoCompletion, int key) {
		if(this.model != null) {
			this.model.setValue(this.getText());
		}

		this.updateTextDisplay();
		if(this.multiLine) {
			int numLines = this.textRenderer.getNumTextLines();
			if(this.numberOfLines != numLines) {
				this.numberOfLines = numLines;
				this.invalidateLayout();
			}
		}

		this.doCallback(key);
		if(this.autoCompletionWindow != null && this.autoCompletionWindow.isOpen() || updateAutoCompletion) {
			this.updateAutoCompletion();
		}

	}

	private void updateTextDisplay() {
		this.textRenderer.setCharSequence((CharSequence)(this.passwordMasking != null ? this.passwordMasking : this.editBuffer));
		this.checkTextWidth();
		this.scrollToCursor(false);
	}

	private void checkTextWidth() {
		this.textLongerThenWidget = this.textRenderer.getPreferredWidth() > this.textRenderer.getWidth();
	}

	protected void moveCursor(int dir, boolean select) {
		this.setCursorPos(this.cursorPos + dir, select);
	}

	protected void moveCursorY(int dir, boolean select) {
		if(this.multiLine) {
			int x = this.computeRelativeCursorPositionX(this.cursorPos);
			int lineStart;
			if(dir < 0) {
				lineStart = this.computeLineStart(this.cursorPos);
				if(lineStart == 0) {
					this.setCursorPos(0, select);
					return;
				}

				lineStart = this.computeLineStart(lineStart - 1);
			} else {
				lineStart = Math.min(this.computeLineEnd(this.cursorPos) + 1, this.editBuffer.length());
			}

			this.setCursorPos(this.computeCursorPosFromX(x, lineStart), select);
		}

	}

	protected void setCursorPos(int pos, boolean select) {
		pos = Math.max(0, Math.min(this.editBuffer.length(), pos));
		if(!select) {
			this.selectionStart = pos;
			this.selectionEnd = pos;
		}

		if(this.cursorPos != pos) {
			if(select) {
				if(this.hasSelection()) {
					if(this.cursorPos == this.selectionStart) {
						this.selectionStart = pos;
					} else {
						this.selectionEnd = pos;
					}
				} else {
					this.selectionStart = this.cursorPos;
					this.selectionEnd = pos;
				}

				if(this.selectionStart > this.selectionEnd) {
					int t = this.selectionStart;
					this.selectionStart = this.selectionEnd;
					this.selectionEnd = t;
				}
			}

			this.cursorPos = pos;
			this.scrollToCursor(false);
			this.updateAutoCompletion();
		}

	}

	public void setCursorPos(int pos) {
		if(pos >= 0 && pos <= this.editBuffer.length()) {
			this.setCursorPos(pos, false);
		} else {
			throw new IllegalArgumentException("pos");
		}
	}

	public void selectAll() {
		this.selectionStart = 0;
		this.selectionEnd = this.editBuffer.length();
	}

	public void setSelection(int start, int end) {
		if(start >= 0 && start <= end && end <= this.editBuffer.length()) {
			this.selectionStart = start;
			this.selectionEnd = end;
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected void selectWordFromMouse(int index) {
		this.selectionStart = index;

		for(this.selectionEnd = index; this.selectionStart > 0 && !Character.isWhitespace(this.editBuffer.charAt(this.selectionStart - 1)); --this.selectionStart) {
		}

		while(this.selectionEnd < this.editBuffer.length() && !Character.isWhitespace(this.editBuffer.charAt(this.selectionEnd))) {
			++this.selectionEnd;
		}

	}

	protected void scrollToCursor(boolean force) {
		int renderWidth = this.textRenderer.getWidth() - 5;
		if(renderWidth <= 0) {
			this.pendingScrollToCursor = true;
			this.pendingScrollToCursorForce = force;
		} else {
			this.pendingScrollToCursor = false;
			int xpos = this.computeRelativeCursorPositionX(this.cursorPos);
			if(xpos < this.scrollPos + 5) {
				this.scrollPos = Math.max(0, xpos - 5);
			} else if(force || xpos - this.scrollPos > renderWidth) {
				this.scrollPos = Math.max(0, xpos - renderWidth);
			}

			if(this.multiLine) {
				ScrollPane sp = ScrollPane.getContainingScrollPane(this);
				if(sp != null) {
					int lineHeight = this.getLineHeight();
					int lineY = this.computeLineNumber(this.cursorPos) * lineHeight;
					sp.validateLayout();
					sp.scrollToAreaY(lineY, lineHeight, lineHeight / 2);
				}
			}

		}
	}

	protected void insertChar(char ch) {
		if(!this.readOnly && (!Character.isISOControl(ch) || this.multiLine && ch == 10)) {
			boolean update = false;
			if(this.hasSelection()) {
				this.deleteSelection();
				update = true;
			}

			if(this.editBuffer.length() < this.maxTextLength) {
				this.editBuffer.insert(this.cursorPos, ch);
				++this.cursorPos;
				update = true;
			}

			if(update) {
				this.updateText(true, 0);
			}
		}

	}

	protected void deletePrev() {
		if(!this.readOnly) {
			if(this.hasSelection()) {
				this.deleteSelection();
				this.updateText(true, 211);
			} else if(this.cursorPos > 0) {
				--this.cursorPos;
				this.deleteNext();
			}
		}

	}

	protected void deleteNext() {
		if(!this.readOnly) {
			if(this.hasSelection()) {
				this.deleteSelection();
				this.updateText(true, 211);
			} else if(this.cursorPos < this.editBuffer.length()) {
				this.editBuffer.deleteCharAt(this.cursorPos);
				this.updateText(true, 211);
			}
		}

	}

	protected void deleteSelection() {
		this.editBuffer.delete(this.selectionStart, this.selectionEnd);
		this.selectionEnd = this.selectionStart;
		this.setCursorPos(this.selectionStart, false);
	}

	protected void modelChanged() {
		String modelText = this.model.getValue();
		if(this.editBuffer.length() != modelText.length() || !this.getText().equals(modelText)) {
			this.setText(modelText);
		}

	}

	protected boolean hasFocusOrPopup() {
		return this.hasKeyboardFocus() || this.hasOpenPopups();
	}

	protected Font getFont() {
		return this.textRenderer.getFont();
	}

	protected int getLineHeight() {
		Font font = this.getFont();
		return font != null ? font.getLineHeight() : 0;
	}

	protected int computeLineNumber(int cursorPos) {
		StringBuilder eb = this.editBuffer;
		int lineNr = 0;

		for(int i = 0; i < cursorPos; ++i) {
			if(eb.charAt(i) == 10) {
				++lineNr;
			}
		}

		return lineNr;
	}

	protected int computeLineStart(int cursorPos) {
		if(!this.multiLine) {
			return 0;
		} else {
			for(StringBuilder eb = this.editBuffer; cursorPos > 0 && eb.charAt(cursorPos - 1) != 10; --cursorPos) {
			}

			return cursorPos;
		}
	}

	protected int computeLineEnd(int cursorPos) {
		StringBuilder eb = this.editBuffer;
		int endIndex = eb.length();
		if(!this.multiLine) {
			return endIndex;
		} else {
			while(cursorPos < endIndex && eb.charAt(cursorPos) != 10) {
				++cursorPos;
			}

			return cursorPos;
		}
	}

	protected int computeRelativeCursorPositionX(int cursorPos) {
		int lineStart = 0;
		if(this.multiLine) {
			lineStart = this.computeLineStart(cursorPos);
		}

		return this.textRenderer.computeRelativeCursorPositionX(lineStart, cursorPos);
	}

	protected int computeRelativeCursorPositionY(int cursorPos) {
		return this.multiLine ? this.getLineHeight() * this.computeLineNumber(cursorPos) : 0;
	}

	protected int getCursorPosFromMouse(int x, int y) {
		Font font = this.getFont();
		if(font == null) {
			return 0;
		} else {
			x -= this.textRenderer.lastTextX;
			int lineStart = 0;
			int lineEnd = this.editBuffer.length();
			if(this.multiLine) {
				y -= this.textRenderer.computeTextY();
				int lineHeight = font.getLineHeight();
				int endIndex = lineEnd;

				while(true) {
					lineEnd = this.computeLineEnd(lineStart);
					if(lineStart >= endIndex || y < lineHeight) {
						break;
					}

					lineStart = Math.min(lineEnd + 1, endIndex);
					y -= lineHeight;
				}
			}

			return this.computeCursorPosFromX(x, lineStart, lineEnd);
		}
	}

	protected int computeCursorPosFromX(int x, int lineStart) {
		return this.computeCursorPosFromX(x, lineStart, this.computeLineEnd(lineStart));
	}

	protected int computeCursorPosFromX(int x, int lineStart, int lineEnd) {
		Font font = this.getFont();
		return font != null ? lineStart + font.computeVisibleGlpyhs((CharSequence)(this.passwordMasking != null ? this.passwordMasking : this.editBuffer), lineStart, lineEnd, x + font.getSpaceWidth() / 2) : lineStart;
	}

	protected void paintOverlay(GUI gui) {
		if(this.cursorImage != null && this.hasFocusOrPopup()) {
			int xpos = this.textRenderer.lastTextX + this.computeRelativeCursorPositionX(this.cursorPos);
			int ypos = this.textRenderer.computeTextY() + this.computeRelativeCursorPositionY(this.cursorPos);
			this.cursorImage.draw(this.getAnimationState(), xpos, ypos, this.cursorImage.getWidth(), this.getLineHeight());
		}

		super.paintOverlay(gui);
	}

	private void openErrorInfoWindow() {
		if(this.autoCompletionWindow == null || !this.autoCompletionWindow.isOpen()) {
			if(this.errorInfoWindow == null) {
				this.errorInfoLabel = new Label();
				this.errorInfoLabel.setClip(true);
				this.errorInfoWindow = new InfoWindow(this);
				this.errorInfoWindow.setTheme("editfield-errorinfowindow");
				this.errorInfoWindow.add(this.errorInfoLabel);
			}

			this.errorInfoLabel.setText(this.errorMsg.toString());
			this.errorInfoWindow.openInfo();
			this.layoutErrorInfoWindow();
		}

	}

	private void layoutErrorInfoWindow() {
		this.errorInfoWindow.setSize(this.getWidth(), this.errorInfoWindow.getPreferredHeight());
		this.errorInfoWindow.setPosition(this.getX(), this.getBottom());
	}

	protected void keyboardFocusGained() {
		if(this.errorMsg != null) {
			this.openErrorInfoWindow();
		} else {
			this.updateAutoCompletion();
		}

	}

	protected void keyboardFocusLost() {
		super.keyboardFocusLost();
		if(this.errorInfoWindow != null) {
			this.errorInfoWindow.closeInfo();
		}

		if(this.autoCompletionWindow != null) {
			this.autoCompletionWindow.closeInfo();
		}

	}

	protected void updateAutoCompletion() {
		if(this.autoCompletionWindow != null) {
			this.autoCompletionWindow.updateAutoCompletion();
		}

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

	public interface Callback {
		void callback(int i1);
	}

	protected class ModelChangeListener implements Runnable {
		public void run() {
			EditField.this.modelChanged();
		}
	}

	static class PasswordMasker implements CharSequence {
		final CharSequence base;
		final char maskingChar;

		public PasswordMasker(CharSequence base, char maskingChar) {
			this.base = base;
			this.maskingChar = maskingChar;
		}

		public int length() {
			return this.base.length();
		}

		public char charAt(int index) {
			return this.maskingChar;
		}

		public CharSequence subSequence(int start, int end) {
			throw new UnsupportedOperationException("Not supported.");
		}
	}

	protected class TextRenderer extends TextWidget {
		int lastTextX;
		int lastScrollPos;

		protected TextRenderer(AnimationState animState) {
			super(animState);
		}

		protected void paintWidget(GUI gui) {
			if(EditField.this.pendingScrollToCursor) {
				EditField.this.scrollToCursor(EditField.this.pendingScrollToCursorForce);
			}

			this.lastScrollPos = EditField.this.hasFocusOrPopup() ? EditField.this.scrollPos : 0;
			this.lastTextX = this.computeTextX();
			if(EditField.this.hasSelection() && EditField.this.hasFocusOrPopup()) {
				if(EditField.this.multiLine) {
					this.paintMultiLineWithSelection();
				} else {
					this.paintWithSelection(0, EditField.this.editBuffer.length(), this.computeTextY());
				}
			} else {
				this.paintLabelText(this.getAnimationState());
			}

		}

		protected void paintWithSelection(int lineStart, int lineEnd, int yoff) {
			int selStart = EditField.this.selectionStart;
			int selEnd = EditField.this.selectionEnd;
			if(EditField.this.selectionImage != null && selEnd > lineStart && selStart < lineEnd) {
				int xpos0 = this.lastTextX + this.computeRelativeCursorPositionX(lineStart, selStart);
				int xpos1 = this.lastTextX + this.computeRelativeCursorPositionX(lineStart, Math.min(lineEnd, selEnd));
				EditField.this.selectionImage.draw(this.getAnimationState(), xpos0, yoff, xpos1 - xpos0, this.getFont().getLineHeight());
			}

			this.paintWithSelection(this.getAnimationState(), selStart, selEnd, lineStart, lineEnd, yoff);
		}

		protected void paintMultiLineWithSelection() {
			StringBuilder eb = EditField.this.editBuffer;
			int lineStart = 0;
			int endIndex = eb.length();
			int yoff = this.computeTextY();

			int lineEnd;
			for(int lineHeight = EditField.this.getLineHeight(); lineStart < endIndex; lineStart = lineEnd + 1) {
				lineEnd = EditField.this.computeLineEnd(lineStart);
				this.paintWithSelection(lineStart, lineEnd, yoff);
				yoff += lineHeight;
			}

		}

		protected void sizeChanged() {
			EditField.this.scrollToCursor(true);
		}

		protected int computeTextX() {
			return this.getInnerX() - this.lastScrollPos;
		}
	}
}
