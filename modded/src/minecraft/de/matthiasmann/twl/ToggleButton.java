package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;
import de.matthiasmann.twl.model.ButtonModel;
import de.matthiasmann.twl.model.ToggleButtonModel;

public class ToggleButton extends Button {
	public ToggleButton() {
		super((ButtonModel)(new ToggleButtonModel()));
	}

	public ToggleButton(BooleanModel model) {
		super((ButtonModel)(new ToggleButtonModel(model)));
	}

	public ToggleButton(String text) {
		this();
		this.setText(text);
	}

	public void setModel(BooleanModel model) {
		((ToggleButtonModel)this.getModel()).setModel(model);
	}

	public boolean isActive() {
		return this.getModel().isSelected();
	}

	public void setActive(boolean active) {
		this.getModel().setSelected(active);
	}
}
