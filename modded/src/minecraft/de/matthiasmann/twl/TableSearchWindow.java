package de.matthiasmann.twl;

import de.matthiasmann.twl.model.TableModel;
import de.matthiasmann.twl.model.TableSelectionModel;

public class TableSearchWindow extends InfoWindow implements TableBase.KeyboardSearchHandler {
	private final TableSelectionModel selectionModel;
	private final EditField searchTextField;
	private final StringBuilder searchTextBuffer;
	private String searchText;
	private String searchTextLowercase;
	private Timer timer;
	private TableModel model;
	private int column;
	private int currentRow;
	private boolean searchStartOnly;

	public TableSearchWindow(Table table, TableSelectionModel selectionModel) {
		super(table);
		this.selectionModel = selectionModel;
		this.searchTextField = new EditField();
		this.searchTextBuffer = new StringBuilder();
		this.searchText = "";
		Label label = new Label("Search");
		label.setLabelFor(this.searchTextField);
		this.searchTextField.setReadOnly(true);
		DialogLayout l = new DialogLayout();
		l.setHorizontalGroup(l.createSequentialGroup().addWidget(label).addWidget(this.searchTextField));
		l.setVerticalGroup(l.createParallelGroup().addWidget(label).addWidget(this.searchTextField));
		this.add(l);
	}

	public Table getTable() {
		return (Table)this.getOwner();
	}

	public TableModel getModel() {
		return this.model;
	}

	public void setModel(TableModel model, int column) {
		if(column < 0) {
			throw new IllegalArgumentException("column");
		} else if(model != null && column >= model.getNumColumns()) {
			throw new IllegalArgumentException("column");
		} else {
			this.model = model;
			this.column = column;
			this.cancelSearch();
		}
	}

	public boolean isActive() {
		return this.isOpen();
	}

	public void updateInfoWindowPosition() {
		this.adjustSize();
		this.setPosition(this.getOwner().getX(), this.getOwner().getBottom());
	}

	public boolean handleKeyEvent(Event evt) {
		if(this.model == null) {
			return false;
		} else {
			if(evt.isKeyPressedEvent()) {
				switch(evt.getKeyCode()) {
				case 1:
					if(this.isOpen()) {
						this.cancelSearch();
						return true;
					}
					break;
				case 14:
					if(this.isOpen()) {
						int length = this.searchTextBuffer.length();
						if(length > 0) {
							this.searchTextBuffer.setLength(length - 1);
							this.updateText();
						}

						this.restartTimer();
						return true;
					}
					break;
				case 28:
					return false;
				case 200:
					if(this.isOpen()) {
						this.searchDir(-1);
						this.restartTimer();
						return true;
					}
					break;
				case 208:
					if(this.isOpen()) {
						this.searchDir(1);
						this.restartTimer();
						return true;
					}
					break;
				default:
					if(evt.hasKeyCharNoModifiers()) {
						if(this.searchTextBuffer.length() == 0) {
							this.currentRow = Math.max(0, this.getTable().getSelectionManager().getLeadRow());
							this.searchStartOnly = true;
						}

						this.searchTextBuffer.append(evt.getKeyChar());
						this.updateText();
						this.restartTimer();
						return true;
					}
				}
			}

			return false;
		}
	}

	public void cancelSearch() {
		this.searchTextBuffer.setLength(0);
		this.updateText();
		this.closeInfo();
		if(this.timer != null) {
			this.timer.stop();
		}

	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.timer = gui.createTimer();
		this.timer.setDelay(3000);
		this.timer.setCallback(new Runnable() {
			public void run() {
				TableSearchWindow.this.cancelSearch();
			}
		});
	}

	protected void beforeRemoveFromGUI(GUI gui) {
		this.timer.stop();
		this.timer = null;
		super.beforeRemoveFromGUI(gui);
	}

	private void updateText() {
		this.searchText = this.searchTextBuffer.toString();
		this.searchTextLowercase = null;
		this.searchTextField.setText(this.searchText);
		if(this.searchText.length() >= 0 && this.model != null) {
			if(!this.isOpen() && this.openInfo()) {
				this.updateInfoWindowPosition();
			}

			this.updateSearch();
		}

	}

	private void restartTimer() {
		this.timer.stop();
		this.timer.start();
	}

	private void updateSearch() {
		int numRows = this.model.getNumRows();
		if(numRows != 0) {
			int row;
			for(row = this.currentRow; row < numRows; ++row) {
				if(this.checkRow(row)) {
					this.setRow(row);
					return;
				}
			}

			if(this.searchStartOnly) {
				this.searchStartOnly = false;
			} else {
				numRows = this.currentRow;
			}

			for(row = 0; row < numRows; ++row) {
				if(this.checkRow(row)) {
					this.setRow(row);
					return;
				}
			}

			this.searchTextField.setErrorMessage("\'" + this.searchText + "\' not found");
		}
	}

	private void searchDir(int dir) {
		int numRows = this.model.getNumRows();
		if(numRows != 0) {
			int startRow = wrap(this.currentRow, numRows);
			int row = startRow;

			while(true) {
				row = wrap(row + dir, numRows);
				if(this.checkRow(row)) {
					this.setRow(row);
					return;
				}

				if(row == startRow) {
					if(!this.searchStartOnly) {
						return;
					}

					this.searchStartOnly = false;
				}
			}
		}
	}

	private void setRow(int row) {
		if(this.currentRow != row) {
			this.currentRow = row;
			this.getTable().scrollToRow(row);
			if(this.selectionModel != null) {
				this.selectionModel.setSelection(row, row);
			}
		}

		this.searchTextField.setErrorMessage((Object)null);
	}

	private boolean checkRow(int row) {
		Object data = this.model.getCell(row, this.column);
		if(data == null) {
			return false;
		} else {
			String str = data.toString();
			if(this.searchStartOnly) {
				return str.regionMatches(true, 0, this.searchText, 0, this.searchText.length());
			} else {
				str = str.toLowerCase();
				if(this.searchTextLowercase == null) {
					this.searchTextLowercase = this.searchText.toLowerCase();
				}

				return str.contains(this.searchTextLowercase);
			}
		}
	}

	private static int wrap(int row, int numRows) {
		return row < 0 ? numRows - 1 : (row >= numRows ? 0 : row);
	}
}
