package de.matthiasmann.twl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ColumnLayout extends DialogLayout {
	final ArrayList columnGroups = new ArrayList();
	private final ColumnLayout.Panel rootPanel = new ColumnLayout.Panel((ColumnLayout.Panel)null);
	private final HashMap columns = new HashMap();

	public ColumnLayout() {
		this.setHorizontalGroup(this.createParallelGroup());
		this.setVerticalGroup(this.rootPanel.rows);
	}

	public final ColumnLayout.Panel getRootPanel() {
		return this.rootPanel;
	}

	public ColumnLayout.Columns getColumns(String... columnNames) {
		if(columnNames.length == 0) {
			throw new IllegalArgumentException("columnNames");
		} else {
			ColumnLayout.Columns key = new ColumnLayout.Columns(columnNames);
			ColumnLayout.Columns cl = (ColumnLayout.Columns)this.columns.get(key);
			if(cl != null) {
				return cl;
			} else {
				this.createColumns(key);
				return key;
			}
		}
	}

	public ColumnLayout.Row addRow(ColumnLayout.Columns columns) {
		return this.rootPanel.addRow(columns);
	}

	public ColumnLayout.Row addRow(String... columnNames) {
		return this.rootPanel.addRow(this.getColumns(columnNames));
	}

	private void createColumns(ColumnLayout.Columns cl) {
		int prefixSize = 0;
		ColumnLayout.Columns prefixColumns = null;
		Iterator h = this.columns.values().iterator();

		int i;
		while(h.hasNext()) {
			ColumnLayout.Columns numColumns = (ColumnLayout.Columns)h.next();
			i = numColumns.match(cl);
			if(i > prefixSize) {
				prefixSize = i;
				prefixColumns = numColumns;
			}
		}

		int i10 = 0;
		int i11 = 0;

		for(i = cl.names.length; i11 < i; ++i11) {
			if(!cl.isGap(i11)) {
				++i10;
			}
		}

		cl.numColumns = i10;
		cl.firstColumn = this.columnGroups.size();
		cl.childGroups = new DialogLayout.Group[cl.names.length];
		DialogLayout.Group dialogLayout$Group12 = this.createSequentialGroup();
		if(prefixColumns == null) {
			this.getHorizontalGroup().addGroup(dialogLayout$Group12);
		} else {
			for(i = 0; i < prefixSize; ++i) {
				if(!cl.isGap(i)) {
					DialogLayout.Group n = (DialogLayout.Group)this.columnGroups.get(prefixColumns.firstColumn + i);
					this.columnGroups.add(n);
				}
			}

			System.arraycopy(prefixColumns.childGroups, 0, cl.childGroups, 0, prefixSize);
			cl.childGroups[prefixSize - 1].addGroup(dialogLayout$Group12);
		}

		i = prefixSize;

		for(int i13 = cl.names.length; i < i13; ++i) {
			DialogLayout.Group nextSequential;
			if(cl.isGap(i)) {
				dialogLayout$Group12.addGap();
			} else {
				nextSequential = this.createParallelGroup();
				dialogLayout$Group12.addGroup(nextSequential);
				this.columnGroups.add(nextSequential);
			}

			nextSequential = this.createSequentialGroup();
			DialogLayout.Group childGroup = this.createParallelGroup().addGroup(nextSequential);
			dialogLayout$Group12.addGroup(childGroup);
			dialogLayout$Group12 = nextSequential;
			cl.childGroups[i] = childGroup;
		}

		this.columns.put(cl, cl);
	}

	public static final class Columns {
		final String[] names;
		final int hashcode;
		int firstColumn;
		int numColumns;
		DialogLayout.Group[] childGroups;

		Columns(String[] names) {
			this.names = (String[])names.clone();
			this.hashcode = Arrays.hashCode(this.names);
		}

		public boolean equals(Object obj) {
			if(obj != null && this.getClass() == obj.getClass()) {
				ColumnLayout.Columns other = (ColumnLayout.Columns)obj;
				return this.hashcode == other.hashcode && Arrays.equals(this.names, other.names);
			} else {
				return false;
			}
		}

		public int getNumColumns() {
			return this.numColumns;
		}

		public int getNumColumnNames() {
			return this.names.length;
		}

		public String getColumnName(int idx) {
			return this.names[idx];
		}

		public int hashCode() {
			return this.hashcode;
		}

		boolean isGap(int column) {
			String name = this.names[column];
			return name.length() == 0 || "-".equals(name);
		}

		int match(ColumnLayout.Columns other) {
			int cnt = Math.min(this.names.length, other.names.length);

			for(int i = 0; i < cnt; ++i) {
				if(!this.names[i].equals(other.names[i])) {
					return i;
				}
			}

			return cnt;
		}
	}

	public final class Panel {
		final ColumnLayout.Panel parent;
		final ArrayList usedColumnGroups;
		final ArrayList children;
		final DialogLayout.Group rows;
		boolean valid;

		Panel(ColumnLayout.Panel parent) {
			this.parent = parent;
			this.usedColumnGroups = new ArrayList();
			this.children = new ArrayList();
			this.rows = ColumnLayout.this.createSequentialGroup();
			this.valid = true;
		}

		public boolean isValid() {
			return this.valid;
		}

		public ColumnLayout.Columns getColumns(String... columnNames) {
			return ColumnLayout.this.getColumns(columnNames);
		}

		public ColumnLayout.Row addRow(String... columnNames) {
			return this.addRow(ColumnLayout.this.getColumns(columnNames));
		}

		public ColumnLayout.Row addRow(ColumnLayout.Columns columns) {
			if(columns == null) {
				throw new NullPointerException("columns");
			} else {
				this.checkValid();
				DialogLayout.Group row = ColumnLayout.this.createParallelGroup();
				this.rows.addGroup(row);
				return ColumnLayout.this.new Row(columns, this, row);
			}
		}

		public void addVerticalGap(String name) {
			this.checkValid();
			this.rows.addGap(name);
		}

		public ColumnLayout.Panel addPanel() {
			this.checkValid();
			ColumnLayout.Panel panel = ColumnLayout.this.new Panel(this);
			this.rows.addGroup(panel.rows);
			this.children.add(panel);
			return panel;
		}

		public void removePanel(ColumnLayout.Panel panel) {
			if(panel == null) {
				throw new NullPointerException("panel");
			} else {
				if(this.valid && this.children.remove(panel)) {
					panel.markInvalid();
					this.rows.removeGroup(panel.rows, true);
					int i = 0;

					for(int n = panel.usedColumnGroups.size(); i < n; ++i) {
						DialogLayout.Group column = (DialogLayout.Group)panel.usedColumnGroups.get(i);
						if(column != null) {
							((DialogLayout.Group)this.usedColumnGroups.get(i)).removeGroup(column, false);
						}
					}
				}

			}
		}

		public void clearPanel() {
			if(this.valid) {
				this.children.clear();
				this.rows.clear(true);
				int i = 0;

				for(int n = this.usedColumnGroups.size(); i < n; ++i) {
					DialogLayout.Group column = (DialogLayout.Group)this.usedColumnGroups.get(i);
					if(column != null) {
						column.clear(false);
					}
				}
			}

		}

		void markInvalid() {
			this.valid = false;
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				((ColumnLayout.Panel)this.children.get(i)).markInvalid();
			}

		}

		void checkValid() {
			if(!this.valid) {
				throw new IllegalStateException("Panel has been removed");
			}
		}

		DialogLayout.Group getColumn(int idx) {
			this.checkValid();
			if(this.usedColumnGroups.size() > idx) {
				DialogLayout.Group column = (DialogLayout.Group)this.usedColumnGroups.get(idx);
				if(column != null) {
					return column;
				}
			}

			return this.makeColumn(idx);
		}

		private DialogLayout.Group makeColumn(int idx) {
			DialogLayout.Group parentColumn;
			if(this.parent != null) {
				parentColumn = this.parent.getColumn(idx);
			} else {
				parentColumn = (DialogLayout.Group)ColumnLayout.this.columnGroups.get(idx);
			}

			DialogLayout.Group column = ColumnLayout.this.createParallelGroup();
			parentColumn.addGroup(column);

			while(this.usedColumnGroups.size() <= idx) {
				this.usedColumnGroups.add((Object)null);
			}

			this.usedColumnGroups.set(idx, column);
			return column;
		}
	}

	public final class Row {
		final ColumnLayout.Columns columns;
		final ColumnLayout.Panel panel;
		final DialogLayout.Group row;
		int curColumn;

		Row(ColumnLayout.Columns columns, ColumnLayout.Panel panel, DialogLayout.Group row) {
			this.columns = columns;
			this.panel = panel;
			this.row = row;
		}

		public int getCurrentColumn() {
			return this.curColumn;
		}

		public ColumnLayout.Columns getColumns() {
			return this.columns;
		}

		public ColumnLayout.Row add(Widget w) {
			if(this.curColumn == this.columns.numColumns) {
				throw new IllegalStateException("Too many widgets for column layout");
			} else {
				this.panel.getColumn(this.columns.firstColumn + this.curColumn).addWidget(w);
				this.row.addWidget(w);
				++this.curColumn;
				return this;
			}
		}

		public ColumnLayout.Row add(Widget w, Alignment alignment) {
			this.add(w);
			ColumnLayout.this.setWidgetAlignment(w, alignment);
			return this;
		}

		public ColumnLayout.Row addLabel(String labelText) {
			if(labelText == null) {
				throw new NullPointerException("labelText");
			} else {
				return this.add(new Label(labelText));
			}
		}

		public ColumnLayout.Row addWithLabel(String labelText, Widget w) {
			if(labelText == null) {
				throw new NullPointerException("labelText");
			} else {
				Label labelWidget = new Label(labelText);
				labelWidget.setLabelFor(w);
				this.add(labelWidget, Alignment.TOPLEFT).add(w);
				return this;
			}
		}

		public ColumnLayout.Row addWithLabel(String labelText, Widget w, Alignment alignment) {
			this.addWithLabel(labelText, w);
			ColumnLayout.this.setWidgetAlignment(w, alignment);
			return this;
		}
	}
}
