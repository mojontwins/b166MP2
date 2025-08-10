package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;
import de.matthiasmann.twl.model.ToggleButtonModel;

import java.beans.PropertyChangeEvent;

public class MenuCheckbox extends MenuElement {
	private BooleanModel model;

	public MenuCheckbox() {
	}

	public MenuCheckbox(BooleanModel model) {
		this.model = model;
	}

	public MenuCheckbox(String name, BooleanModel model) {
		super(name);
		this.model = model;
	}

	public BooleanModel getModel() {
		return this.model;
	}

	public void setModel(BooleanModel model) {
		BooleanModel oldModel = this.model;
		this.model = model;
		this.firePropertyChange("model", oldModel, model);
	}

	protected Widget createMenuWidget(MenuManager mm, int level) {
		MenuElement.MenuBtn btn = new MenuElement.MenuBtn() {
			public void propertyChange(PropertyChangeEvent evt) {
				super.propertyChange(evt);
				((ToggleButtonModel)this.getModel()).setModel(MenuCheckbox.this.getModel());
			}
		};
		btn.setModel(new ToggleButtonModel(this.getModel()));
		this.setWidgetTheme(btn, "checkbox");
		btn.addCallback(mm.getCloseCallback());
		return btn;
	}
}
