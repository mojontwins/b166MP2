package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AbstractListModel;
import de.matthiasmann.twl.model.AbstractTreeTableModel;
import de.matthiasmann.twl.model.AbstractTreeTableNode;
import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.model.Property;
import de.matthiasmann.twl.model.PropertyList;
import de.matthiasmann.twl.model.SimplePropertyList;
import de.matthiasmann.twl.model.TreeTableModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.utils.TypeMapping;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertySheet extends TreeTable {
	private final SimplePropertyList rootList;
	private final PropertySheet.PropertyListCellRenderer subListRenderer;
	private final TableBase.CellRenderer editorRenderer;
	private final TypeMapping factories;

	public PropertySheet() {
		this(new PropertySheet.Model());
	}

	private PropertySheet(PropertySheet.Model model) {
		super(model);
		this.rootList = new SimplePropertyList("<root>");
		this.subListRenderer = new PropertySheet.PropertyListCellRenderer();
		this.editorRenderer = new PropertySheet.EditorRenderer();
		this.factories = new TypeMapping();
		this.rootList.addValueChangedCallback(new PropertySheet.TreeGenerator(this.rootList, model));
		this.registerPropertyEditorFactory(String.class, new PropertySheet.StringEditorFactory());
	}

	public SimplePropertyList getPropertyList() {
		return this.rootList;
	}

	public void registerPropertyEditorFactory(Class clazz, PropertySheet.PropertyEditorFactory factory) {
		if(clazz == null) {
			throw new NullPointerException("clazz");
		} else if(factory == null) {
			throw new NullPointerException("factory");
		} else {
			this.factories.put(clazz, factory);
		}
	}

	public void setModel(TreeTableModel model) {
		if(model instanceof PropertySheet.Model) {
			super.setModel(model);
		} else {
			throw new UnsupportedOperationException("Do not call this method");
		}
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemePropertiesSheet(themeInfo);
	}

	protected void applyThemePropertiesSheet(ThemeInfo themeInfo) {
		this.applyCellRendererTheme(this.subListRenderer);
		this.applyCellRendererTheme(this.editorRenderer);
	}

	protected TableBase.CellRenderer getCellRenderer(int row, int col, TreeTableNode node) {
		if(node == null) {
			node = this.getNodeFromRow(row);
		}

		if(node instanceof PropertySheet.ListNode) {
			if(col == 0) {
				PropertySheet.PropertyListCellRenderer cr1 = this.subListRenderer;
				TreeTable.NodeState nodeState = this.getOrCreateNodeState(node);
				cr1.setCellData(row, col, node.getData(col), nodeState);
				return cr1;
			} else {
				return null;
			}
		} else if(col == 0) {
			return super.getCellRenderer(row, col, node);
		} else {
			TableBase.CellRenderer cr = this.editorRenderer;
			cr.setCellData(row, col, node.getData(col));
			return cr;
		}
	}

	TreeTableNode createNode(TreeTableNode parent, Property property) {
		if(property.getType() == PropertyList.class) {
			return new PropertySheet.ListNode(parent, property);
		} else {
			Class type = property.getType();
			PropertySheet.PropertyEditorFactory factory = (PropertySheet.PropertyEditorFactory)this.factories.get(type);
			if(factory != null) {
				PropertySheet.PropertyEditor editor = factory.createEditor(property);
				if(editor != null) {
					return new PropertySheet.LeafNode(parent, property, editor);
				}
			} else {
				Logger.getLogger(PropertySheet.class.getName()).log(Level.WARNING, "No property editor factory for type {0}", type);
			}

			return null;
		}
	}

	public static class ComboBoxEditor implements PropertySheet.PropertyEditor, Runnable {
		protected final ComboBox comboBox;
		protected final Property property;
		protected final ListModel model;

		public ComboBoxEditor(Property property, ListModel model) {
			this.property = property;
			this.comboBox = new ComboBox(model);
			this.model = model;
			this.comboBox.addCallback(this);
			this.resetValue();
		}

		public Widget getWidget() {
			return this.comboBox;
		}

		public void valueChanged() {
			this.resetValue();
		}

		public void preDestroy() {
			this.comboBox.removeCallback(this);
		}

		public void setSelected(boolean selected) {
		}

		public void run() {
			if(this.property.isReadOnly()) {
				this.resetValue();
			} else {
				int idx = this.comboBox.getSelected();
				if(idx >= 0) {
					this.property.setPropertyValue(this.model.getEntry(idx));
				}
			}

		}

		protected void resetValue() {
			this.comboBox.setSelected(this.findEntry(this.property.getPropertyValue()));
		}

		protected int findEntry(Object value) {
			int i = 0;

			for(int n = this.model.getNumEntries(); i < n; ++i) {
				if(this.model.getEntry(i).equals(value)) {
					return i;
				}
			}

			return -1;
		}
	}

	public static class ComboBoxEditorFactory implements PropertySheet.PropertyEditorFactory {
		private final PropertySheet.ComboBoxEditorFactory.ModelForwarder modelForwarder;

		public ComboBoxEditorFactory(ListModel model) {
			this.modelForwarder = new PropertySheet.ComboBoxEditorFactory.ModelForwarder(model);
		}

		public ListModel getModel() {
			return this.modelForwarder.getModel();
		}

		public void setModel(ListModel model) {
			this.modelForwarder.setModel(model);
		}

		public PropertySheet.PropertyEditor createEditor(Property property) {
			return new PropertySheet.ComboBoxEditor(property, this.modelForwarder);
		}

		class ModelForwarder extends AbstractListModel implements ListModel.ChangeListener {
			private ListModel model;

			public ModelForwarder(ListModel model) {
				this.setModel(model);
			}

			public int getNumEntries() {
				return this.model.getNumEntries();
			}

			public Object getEntry(int index) {
				return this.model.getEntry(index);
			}

			public Object getEntryTooltip(int index) {
				return this.model.getEntryTooltip(index);
			}

			public boolean matchPrefix(int index, String prefix) {
				return this.model.matchPrefix(index, prefix);
			}

			public ListModel getModel() {
				return this.model;
			}

			public void setModel(ListModel model) {
				if(this.model != null) {
					this.model.removeChangeListener(this);
				}

				this.model = model;
				this.model.addChangeListener(this);
				this.fireAllChanged();
			}

			public void entriesInserted(int first, int last) {
				this.fireEntriesInserted(first, last);
			}

			public void entriesDeleted(int first, int last) {
				this.fireEntriesDeleted(first, last);
			}

			public void entriesChanged(int first, int last) {
				this.fireEntriesChanged(first, last);
			}

			public void allChanged() {
				this.fireAllChanged();
			}
		}
	}

	static class EditorRenderer implements TableBase.CellRenderer, TableBase.CellWidgetCreator {
		private PropertySheet.PropertyEditor editor;

		public void applyTheme(ThemeInfo themeInfo) {
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			this.editor.setSelected(isSelected);
			return null;
		}

		public int getColumnSpan() {
			return 1;
		}

		public int getPreferredHeight() {
			return this.editor.getWidget().getPreferredHeight();
		}

		public String getTheme() {
			return "PropertyEditorCellRender";
		}

		public void setCellData(int row, int column, Object data) {
			this.editor = (PropertySheet.PropertyEditor)data;
		}

		public Widget updateWidget(Widget existingWidget) {
			return this.editor.getWidget();
		}

		public void positionWidget(Widget widget, int x, int y, int w, int h) {
			widget.setPosition(x, y);
			widget.setSize(w, h);
		}
	}

	static class LeafNode extends PropertySheet.PropertyNode {
		private final PropertySheet.PropertyEditor editor;

		public LeafNode(TreeTableNode parent, Property property, PropertySheet.PropertyEditor editor) {
			super(parent, property);
			this.editor = editor;
			this.setLeaf(true);
		}

		public Object getData(int column) {
			switch(column) {
			case 0:
				return this.property.getName();
			case 1:
				return this.editor;
			default:
				return "???";
			}
		}

		public void run() {
			this.editor.valueChanged();
			this.fireNodeChanged();
		}
	}

	class ListNode extends PropertySheet.PropertyNode {
		protected final PropertySheet.TreeGenerator treeGenerator;

		public ListNode(TreeTableNode parent, Property property) {
			super(parent, property);
			this.treeGenerator = PropertySheet.this.new TreeGenerator((PropertyList)property.getPropertyValue(), this);
			this.treeGenerator.run();
		}

		public Object getData(int column) {
			return this.property.getName();
		}

		public void run() {
			this.treeGenerator.run();
		}

		protected void removeCallback() {
			super.removeCallback();
			this.treeGenerator.removeChildCallbacks(this);
		}
	}

	static class Model extends AbstractTreeTableModel implements PropertySheet.PSTreeTableNode {
		public String getColumnHeaderText(int column) {
			switch(column) {
			case 0:
				return "Name";
			case 1:
				return "Value";
			default:
				return "???";
			}
		}

		public int getNumColumns() {
			return 2;
		}

		public void removeAllChildren() {
			super.removeAllChildren();
		}

		public void addChild(TreeTableNode parent) {
			this.insertChild(parent, this.getNumChildren());
		}
	}

	interface PSTreeTableNode extends TreeTableNode {
		void addChild(TreeTableNode treeTableNode1);

		void removeAllChildren();
	}

	public interface PropertyEditor {
		Widget getWidget();

		void valueChanged();

		void preDestroy();

		void setSelected(boolean z1);
	}

	public interface PropertyEditorFactory {
		PropertySheet.PropertyEditor createEditor(Property property1);
	}

	class PropertyListCellRenderer extends TreeTable.TreeNodeCellRenderer {
		private final Widget bgRenderer = new Widget();
		private final Label textRenderer = new Label(this.bgRenderer.getAnimationState());

		public PropertyListCellRenderer() {
			super();
			this.bgRenderer.add(this.textRenderer);
			this.bgRenderer.setTheme(this.getTheme());
		}

		public int getColumnSpan() {
			return 2;
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			this.bgRenderer.setPosition(x, y);
			this.bgRenderer.setSize(width, height);
			int indent = this.getIndentation();
			this.textRenderer.setPosition(x + indent, y);
			this.textRenderer.setSize(Math.max(0, width - indent), height);
			this.bgRenderer.getAnimationState().setAnimationState(PropertySheet.STATE_SELECTED, isSelected);
			return this.bgRenderer;
		}

		public void setCellData(int row, int column, Object data, TreeTable.NodeState nodeState) {
			super.setCellData(row, column, data, nodeState);
			this.textRenderer.setText((String)data);
		}

		protected void setSubRenderer(Object colData) {
		}
	}

	abstract static class PropertyNode extends AbstractTreeTableNode implements Runnable, PropertySheet.PSTreeTableNode {
		protected final Property property;

		public PropertyNode(TreeTableNode parent, Property property) {
			super(parent);
			this.property = property;
			property.addValueChangedCallback(this);
		}

		protected void removeCallback() {
			this.property.removeValueChangedCallback(this);
		}

		public void removeAllChildren() {
			super.removeAllChildren();
		}

		public void addChild(TreeTableNode parent) {
			this.insertChild(parent, this.getNumChildren());
		}
	}

	static class StringEditor implements PropertySheet.PropertyEditor, EditField.Callback {
		private final EditField editField;
		private final Property property;

		public StringEditor(Property property) {
			this.property = property;
			this.editField = new EditField();
			this.editField.addCallback(this);
			this.resetValue();
		}

		public Widget getWidget() {
			return this.editField;
		}

		public void valueChanged() {
			this.resetValue();
		}

		public void preDestroy() {
			this.editField.removeCallback(this);
		}

		public void setSelected(boolean selected) {
		}

		public void callback(int key) {
			if(key == 1) {
				this.resetValue();
			} else if(!this.property.isReadOnly()) {
				try {
					this.property.setPropertyValue(this.editField.getText());
					this.editField.setErrorMessage((Object)null);
				} catch (IllegalArgumentException illegalArgumentException3) {
					this.editField.setErrorMessage(illegalArgumentException3.getMessage());
				}
			}

		}

		private void resetValue() {
			this.editField.setText((String)this.property.getPropertyValue());
			this.editField.setErrorMessage((Object)null);
			this.editField.setReadOnly(this.property.isReadOnly());
		}
	}

	static class StringEditorFactory implements PropertySheet.PropertyEditorFactory {
		public PropertySheet.PropertyEditor createEditor(Property property) {
			return new PropertySheet.StringEditor(property);
		}
	}

	class TreeGenerator implements Runnable {
		private final PropertyList list;
		private final PropertySheet.PSTreeTableNode parent;

		public TreeGenerator(PropertyList list, PropertySheet.PSTreeTableNode parent) {
			this.list = list;
			this.parent = parent;
		}

		public void run() {
			this.parent.removeAllChildren();
			this.addSubProperties();
		}

		void removeChildCallbacks(PropertySheet.PSTreeTableNode parent) {
			int i = 0;

			for(int n = parent.getNumChildren(); i < n; ++i) {
				((PropertySheet.PropertyNode)parent.getChild(i)).removeCallback();
			}

		}

		void addSubProperties() {
			for(int i = 0; i < this.list.getNumProperties(); ++i) {
				TreeTableNode node = PropertySheet.this.createNode(this.parent, this.list.getProperty(i));
				if(node != null) {
					this.parent.addChild(node);
				}
			}

		}
	}
}
