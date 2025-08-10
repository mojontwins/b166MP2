package de.matthiasmann.twl;

import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.utils.CallbackSupport;

public class ComboBox extends ComboBoxBase {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ERROR = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("error");
	private static final int INVALID_WIDTH = -1;
	private final ComboBox.ComboboxLabel label;
	private final ListBox listbox;
	private Runnable[] selectionChangedListeners;
	private ListModel.ChangeListener modelChangeListener;
	String displayTextNoSelection;
	boolean noSelectionIsError;
	boolean computeWidthFromModel;
	int modelWidth;

	public ComboBox(ListModel model) {
		this();
		this.setModel(model);
	}

	public ComboBox() {
		this.displayTextNoSelection = "";
		this.modelWidth = -1;
		this.label = new ComboBox.ComboboxLabel(this.getAnimationState());
		this.listbox = new ComboBox.ComboboxListbox();
		this.button.getModel().addStateCallback(new Runnable() {
			public void run() {
				ComboBox.this.updateHover();
			}
		});
		this.label.addCallback(new CallbackWithReason() {
			public void callback(Label.CallbackReason reason) {
				ComboBox.this.openPopup();
			}

			public void callback(Enum enum1) {
				this.callback((Label.CallbackReason)enum1);
			}
		});
		this.listbox.addCallback(new CallbackWithReason() {
			private static int[] $SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason;

			public void callback(ListBox.CallbackReason reason) {
				switch($SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason()[reason.ordinal()]) {
				case 3:
				case 4:
				case 6:
					ComboBox.this.listBoxSelectionChanged(true);
					break;
				case 5:
				default:
					ComboBox.this.listBoxSelectionChanged(false);
				}

			}

			public void callback(Enum enum1) {
				this.callback((ListBox.CallbackReason)enum1);
			}

			static int[] $SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason() {
				int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason;
				if($SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason != null) {
					return i10000;
				} else {
					int[] i0 = new int[ListBox.CallbackReason.values().length];

					try {
						i0[ListBox.CallbackReason.KEYBOARD.ordinal()] = 5;
					} catch (NoSuchFieldError noSuchFieldError6) {
					}

					try {
						i0[ListBox.CallbackReason.KEYBOARD_RETURN.ordinal()] = 6;
					} catch (NoSuchFieldError noSuchFieldError5) {
					}

					try {
						i0[ListBox.CallbackReason.MODEL_CHANGED.ordinal()] = 1;
					} catch (NoSuchFieldError noSuchFieldError4) {
					}

					try {
						i0[ListBox.CallbackReason.MOUSE_CLICK.ordinal()] = 3;
					} catch (NoSuchFieldError noSuchFieldError3) {
					}

					try {
						i0[ListBox.CallbackReason.MOUSE_DOUBLE_CLICK.ordinal()] = 4;
					} catch (NoSuchFieldError noSuchFieldError2) {
					}

					try {
						i0[ListBox.CallbackReason.SET_SELECTED.ordinal()] = 2;
					} catch (NoSuchFieldError noSuchFieldError1) {
					}

					$SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason = i0;
					return i0;
				}
			}
		});
		this.popup.setTheme("comboboxPopup");
		this.popup.add(this.listbox);
		this.add(this.label);
	}

	public void addCallback(Runnable cb) {
		this.selectionChangedListeners = (Runnable[])CallbackSupport.addCallbackToList(this.selectionChangedListeners, cb, Runnable.class);
	}

	public void removeCallback(Runnable cb) {
		this.selectionChangedListeners = (Runnable[])CallbackSupport.removeCallbackFromList(this.selectionChangedListeners, cb);
	}

	private void doCallback() {
		CallbackSupport.fireCallbacks(this.selectionChangedListeners);
	}

	public void setModel(ListModel model) {
		this.unregisterModelChangeListener();
		this.listbox.setModel(model);
		if(this.computeWidthFromModel) {
			this.registerModelChangeListener();
		}

	}

	public ListModel getModel() {
		return this.listbox.getModel();
	}

	public void setSelected(int selected) {
		this.listbox.setSelected(selected);
		this.updateLabel();
	}

	public int getSelected() {
		return this.listbox.getSelected();
	}

	public boolean isComputeWidthFromModel() {
		return this.computeWidthFromModel;
	}

	public void setComputeWidthFromModel(boolean computeWidthFromModel) {
		if(this.computeWidthFromModel != computeWidthFromModel) {
			this.computeWidthFromModel = computeWidthFromModel;
			if(computeWidthFromModel) {
				this.registerModelChangeListener();
			} else {
				this.unregisterModelChangeListener();
			}
		}

	}

	public String getDisplayTextNoSelection() {
		return this.displayTextNoSelection;
	}

	public void setDisplayTextNoSelection(String displayTextNoSelection) {
		if(displayTextNoSelection == null) {
			throw new NullPointerException("displayTextNoSelection");
		} else {
			this.displayTextNoSelection = displayTextNoSelection;
			this.updateLabel();
		}
	}

	public boolean isNoSelectionIsError() {
		return this.noSelectionIsError;
	}

	public void setNoSelectionIsError(boolean noSelectionIsError) {
		this.noSelectionIsError = noSelectionIsError;
		this.updateLabel();
	}

	private void registerModelChangeListener() {
		ListModel model = this.getModel();
		if(model != null) {
			this.modelWidth = -1;
			if(this.modelChangeListener == null) {
				this.modelChangeListener = new ComboBox.ModelChangeListener();
			}

			model.addChangeListener(this.modelChangeListener);
		}

	}

	private void unregisterModelChangeListener() {
		if(this.modelChangeListener != null) {
			ListModel model = this.getModel();
			if(model != null) {
				model.removeChangeListener(this.modelChangeListener);
			}
		}

	}

	protected boolean openPopup() {
		if(super.openPopup()) {
			this.popup.validateLayout();
			this.listbox.scrollToSelected();
			return true;
		} else {
			return false;
		}
	}

	protected void listBoxSelectionChanged(boolean close) {
		this.updateLabel();
		if(close) {
			this.popup.closePopup();
		}

		this.doCallback();
	}

	protected String getModelData(int idx) {
		return String.valueOf(this.getModel().getEntry(idx));
	}

	protected Widget getLabel() {
		return this.label;
	}

	protected void updateLabel() {
		int selected = this.getSelected();
		if(selected == -1) {
			this.label.setText(this.displayTextNoSelection);
			this.label.getAnimationState().setAnimationState(STATE_ERROR, this.noSelectionIsError);
		} else {
			this.label.setText(this.getModelData(selected));
			this.label.getAnimationState().setAnimationState(STATE_ERROR, false);
		}

		if(!this.computeWidthFromModel) {
			this.invalidateLayout();
		}

	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.modelWidth = -1;
	}

	protected boolean handleEvent(Event evt) {
		if(super.handleEvent(evt)) {
			return true;
		} else {
			if(evt.isKeyPressedEvent()) {
				switch(evt.getKeyCode()) {
				case 28:
				case 57:
					this.openPopup();
					return true;
				case 199:
				case 200:
				case 207:
				case 208:
					this.listbox.handleEvent(evt);
					return true;
				}
			}

			return false;
		}
	}

	void invalidateModelWidth() {
		if(this.computeWidthFromModel) {
			this.modelWidth = -1;
			this.invalidateLayout();
		}

	}

	void updateModelWidth() {
		if(this.computeWidthFromModel) {
			this.modelWidth = 0;
			this.updateModelWidth(0, this.getModel().getNumEntries() - 1);
		}

	}

	void updateModelWidth(int first, int last) {
		if(this.computeWidthFromModel) {
			int newModelWidth = this.modelWidth;

			for(int idx = first; idx <= last; ++idx) {
				newModelWidth = Math.max(newModelWidth, this.computeEntryWidth(idx));
			}

			if(newModelWidth > this.modelWidth) {
				this.modelWidth = newModelWidth;
				this.invalidateLayout();
			}
		}

	}

	protected int computeEntryWidth(int idx) {
		int width = this.label.getBorderHorizontal();
		Font font = this.label.getFont();
		if(font != null) {
			width += font.computeMultiLineTextWidth(this.getModelData(idx));
		}

		return width;
	}

	void updateHover() {
		this.getAnimationState().setAnimationState(Label.STATE_HOVER, this.label.hover || this.button.getModel().isHover());
	}

	class ComboboxLabel extends Label {
		boolean hover;

		public ComboboxLabel(AnimationState animState) {
			super(animState);
			this.setAutoSize(false);
			this.setClip(true);
			this.setTheme("display");
		}

		public int getPreferredInnerWidth() {
			if(ComboBox.this.computeWidthFromModel && ComboBox.this.getModel() != null) {
				if(ComboBox.this.modelWidth == -1) {
					ComboBox.this.updateModelWidth();
				}

				return ComboBox.this.modelWidth;
			} else {
				return super.getPreferredInnerWidth();
			}
		}

		public int getPreferredInnerHeight() {
			int prefHeight = super.getPreferredInnerHeight();
			if(this.getFont() != null) {
				prefHeight = Math.max(prefHeight, this.getFont().getLineHeight());
			}

			return prefHeight;
		}

		protected void handleMouseHover(Event evt) {
			if(evt.isMouseEvent()) {
				boolean newHover = evt.getType() != Event.Type.MOUSE_EXITED;
				if(newHover != this.hover) {
					this.hover = newHover;
					ComboBox.this.updateHover();
				}
			}

		}
	}

	static class ComboboxListbox extends ListBox {
		public ComboboxListbox() {
			this.setTheme("listbox");
		}

		protected ListBoxDisplay createDisplay() {
			return new ComboBox.ComboboxListboxLabel();
		}
	}

	static class ComboboxListboxLabel extends ListBox.ListBoxLabel {
		protected boolean handleListBoxEvent(Event evt) {
			if(evt.getType() == Event.Type.MOUSE_CLICKED) {
				this.doListBoxCallback(ListBox.CallbackReason.MOUSE_CLICK);
				return true;
			} else if(evt.getType() == Event.Type.MOUSE_BTNDOWN) {
				this.doListBoxCallback(ListBox.CallbackReason.SET_SELECTED);
				return true;
			} else {
				return false;
			}
		}
	}

	class ModelChangeListener implements ListModel.ChangeListener {
		public void entriesInserted(int first, int last) {
			ComboBox.this.updateModelWidth(first, last);
		}

		public void entriesDeleted(int first, int last) {
			ComboBox.this.invalidateModelWidth();
		}

		public void entriesChanged(int first, int last) {
			ComboBox.this.invalidateModelWidth();
		}

		public void allChanged() {
			ComboBox.this.invalidateModelWidth();
		}
	}
}
