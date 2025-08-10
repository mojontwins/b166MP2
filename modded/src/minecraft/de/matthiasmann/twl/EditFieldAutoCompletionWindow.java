package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AutoCompletionDataSource;
import de.matthiasmann.twl.model.AutoCompletionResult;
import de.matthiasmann.twl.model.SimpleListModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditFieldAutoCompletionWindow extends InfoWindow {
	private final EditFieldAutoCompletionWindow.ResultListModel listModel;
	private final ListBox listBox;
	private boolean captureKeys;
	private boolean useInvokeAsync;
	private AutoCompletionDataSource dataSource;
	private ExecutorService executorService;
	private Future future;

	public EditFieldAutoCompletionWindow(EditField editField) {
		super(editField);
		this.listModel = new EditFieldAutoCompletionWindow.ResultListModel();
		this.listBox = new ListBox(this.listModel);
		this.add(this.listBox);
		EditFieldAutoCompletionWindow.Callbacks cb = new EditFieldAutoCompletionWindow.Callbacks();
		this.listBox.addCallback(cb);
	}

	public EditFieldAutoCompletionWindow(EditField editField, AutoCompletionDataSource dataSource) {
		this(editField);
		this.dataSource = dataSource;
	}

	public EditFieldAutoCompletionWindow(EditField editField, AutoCompletionDataSource dataSource, ExecutorService executorService) {
		this(editField);
		this.dataSource = dataSource;
		this.executorService = executorService;
	}

	public final EditField getEditField() {
		return (EditField)this.getOwner();
	}

	public ExecutorService getExecutorService() {
		return this.executorService;
	}

	public boolean isUseInvokeAsync() {
		return this.useInvokeAsync;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
		this.useInvokeAsync = false;
		this.cancelFuture();
	}

	public void setUseInvokeAsync(boolean useInvokeAsync) {
		this.executorService = null;
		this.useInvokeAsync = useInvokeAsync;
		this.cancelFuture();
	}

	public AutoCompletionDataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(AutoCompletionDataSource dataSource) {
		this.dataSource = dataSource;
		this.cancelFuture();
		if(this.isOpen()) {
			this.updateAutoCompletion();
		}

	}

	public void updateAutoCompletion() {
		this.cancelFuture();
		AutoCompletionResult result = null;
		if(this.dataSource != null) {
			EditField ef = this.getEditField();
			int cursorPos = ef.getCursorPos();
			if(cursorPos > 0) {
				String text = ef.getText();
				GUI gui = ef.getGUI();
				if(this.listModel.result != null) {
					result = this.listModel.result.refine(text, cursorPos);
				}

				if(result == null) {
					if(gui != null && (this.useInvokeAsync || this.executorService != null)) {
						this.future = (this.useInvokeAsync ? gui.executorService : this.executorService).submit(new EditFieldAutoCompletionWindow.AsyncQuery(gui, this.dataSource, text, cursorPos, this.listModel.result));
					} else {
						try {
							result = this.dataSource.collectSuggestions(text, cursorPos, this.listModel.result);
						} catch (Exception exception7) {
							this.reportQueryException(exception7);
						}
					}
				}
			}
		}

		this.updateAutoCompletion(result);
	}

	public void stopAutoCompletion() {
		this.listModel.setResult((AutoCompletionResult)null);
		this.installAutoCompletion();
	}

	protected void infoWindowClosed() {
		this.stopAutoCompletion();
	}

	protected void updateAutoCompletion(AutoCompletionResult results) {
		this.listModel.setResult(results);
		this.captureKeys = false;
		this.installAutoCompletion();
	}

	void checkFuture() {
		if(this.future != null && this.future.isDone()) {
			AutoCompletionResult result = null;

			try {
				result = (AutoCompletionResult)this.future.get();
			} catch (InterruptedException interruptedException3) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException executionException4) {
				this.reportQueryException(executionException4.getCause());
			}

			this.future = null;
			this.updateAutoCompletion(result);
		}

	}

	void cancelFuture() {
		if(this.future != null) {
			this.future.cancel(true);
			this.future = null;
		}

	}

	protected void reportQueryException(Throwable ex) {
		Logger.getLogger(EditFieldAutoCompletionWindow.class.getName()).log(Level.SEVERE, "Exception while collecting auto completion results", ex);
	}

	protected boolean handleEvent(Event evt) {
		if(evt.isKeyEvent()) {
			if(!this.captureKeys) {
				switch(evt.getKeyCode()) {
				case 1:
					this.stopAutoCompletion();
					return false;
				case 57:
					if((evt.getModifiers() & 36) != 0) {
						this.updateAutoCompletion();
						return true;
					}

					return false;
				case 200:
				case 208:
				case 209:
					this.listBox.handleEvent(evt);
					this.startCapture();
					return this.captureKeys;
				default:
					return false;
				}
			} else {
				if(evt.isKeyPressedEvent()) {
					switch(evt.getKeyCode()) {
					case 1:
						this.stopAutoCompletion();
						break;
					case 28:
					case 156:
						return this.acceptAutoCompletion();
					case 199:
					case 200:
					case 201:
					case 207:
					case 208:
					case 209:
						this.listBox.handleEvent(evt);
						break;
					case 203:
					case 205:
						return false;
					default:
						if(evt.hasKeyChar() || evt.getKeyCode() == 14) {
							if(!this.acceptAutoCompletion()) {
								this.stopAutoCompletion();
							}

							return false;
						}
					}
				}

				return true;
			}
		} else {
			return super.handleEvent(evt);
		}
	}

	boolean acceptAutoCompletion() {
		int selected = this.listBox.getSelected();
		if(selected >= 0) {
			EditField editField = this.getEditField();
			String text = this.listModel.getEntry(selected);
			int pos = this.listModel.getCursorPosForEntry(selected);
			editField.setText(text);
			if(pos >= 0 && pos < text.length()) {
				editField.setCursorPos(pos);
			}

			this.stopAutoCompletion();
			return true;
		} else {
			return false;
		}
	}

	private void startCapture() {
		this.captureKeys = true;
		this.installAutoCompletion();
	}

	private void installAutoCompletion() {
		if(this.listModel.getNumEntries() > 0) {
			this.openInfo();
		} else {
			this.captureKeys = false;
			this.closeInfo();
		}

	}

	class AsyncQuery implements Callable, Runnable {
		private final GUI gui;
		private final AutoCompletionDataSource dataSource;
		private final String text;
		private final int cursorPos;
		private final AutoCompletionResult prevResult;

		public AsyncQuery(GUI gui, AutoCompletionDataSource dataSource, String text, int cursorPos, AutoCompletionResult prevResult) {
			this.gui = gui;
			this.dataSource = dataSource;
			this.text = text;
			this.cursorPos = cursorPos;
			this.prevResult = prevResult;
		}

		public AutoCompletionResult call() throws Exception {
			AutoCompletionResult acr = this.dataSource.collectSuggestions(this.text, this.cursorPos, this.prevResult);
			this.gui.invokeLater(this);
			return acr;
		}

		public void run() {
			EditFieldAutoCompletionWindow.this.checkFuture();
		}

		public Object call() throws Exception {
			return this.call();
		}
	}

	class Callbacks implements CallbackWithReason {
		private static int[] $SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason;

		public void callback(ListBox.CallbackReason reason) {
			switch($SWITCH_TABLE$de$matthiasmann$twl$ListBox$CallbackReason()[reason.ordinal()]) {
			case 4:
				EditFieldAutoCompletionWindow.this.acceptAutoCompletion();
			default:
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
	}

	static class ResultListModel extends SimpleListModel {
		AutoCompletionResult result;

		public void setResult(AutoCompletionResult result) {
			this.result = result;
			this.fireAllChanged();
		}

		public int getNumEntries() {
			return this.result == null ? 0 : this.result.getNumResults();
		}

		public String getEntry(int index) {
			return this.result.getResult(index);
		}

		public int getCursorPosForEntry(int index) {
			return this.result.getCursorPosForResult(index);
		}

		public Object getEntry(int i1) {
			return this.getEntry(i1);
		}
	}
}
