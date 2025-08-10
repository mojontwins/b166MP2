package de.matthiasmann.twl;

import de.matthiasmann.twl.model.IntegerModel;

public class ValueAdjusterInt extends ValueAdjuster {
	private int value;
	private int minValue;
	private int maxValue = 100;
	private int dragStartValue;
	private IntegerModel model;
	private Runnable modelCallback;

	public ValueAdjusterInt() {
		this.setTheme("valueadjuster");
		this.setDisplayText();
	}

	public ValueAdjusterInt(IntegerModel model) {
		this.setTheme("valueadjuster");
		this.setModel(model);
	}

	public int getMaxValue() {
		if(this.model != null) {
			this.maxValue = this.model.getMaxValue();
		}

		return this.maxValue;
	}

	public int getMinValue() {
		if(this.model != null) {
			this.minValue = this.model.getMinValue();
		}

		return this.minValue;
	}

	public void setMinMaxValue(int minValue, int maxValue) {
		if(maxValue < minValue) {
			throw new IllegalArgumentException("maxValue < minValue");
		} else {
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.setValue(this.value);
		}
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		value = Math.max(this.getMinValue(), Math.min(this.getMaxValue(), value));
		if(this.value != value) {
			this.value = value;
			if(this.model != null) {
				this.model.setValue(value);
			}

			this.setDisplayText();
		}

	}

	public IntegerModel getModel() {
		return this.model;
	}

	public void setModel(IntegerModel model) {
		if(this.model != model) {
			this.removeModelCallback();
			this.model = model;
			if(model != null) {
				this.minValue = model.getMinValue();
				this.maxValue = model.getMaxValue();
				this.addModelCallback();
			}
		}

	}

	protected String onEditStart() {
		return this.formatText();
	}

	protected boolean onEditEnd(String text) {
		try {
			this.setValue(Integer.parseInt(text));
			return true;
		} catch (NumberFormatException numberFormatException3) {
			return false;
		}
	}

	protected String validateEdit(String text) {
		try {
			Integer.parseInt(text);
			return null;
		} catch (NumberFormatException numberFormatException3) {
			return numberFormatException3.toString();
		}
	}

	protected void onEditCanceled() {
	}

	protected boolean shouldStartEdit(char ch) {
		return ch >= 48 && ch <= 57 || ch == 45;
	}

	protected void onDragStart() {
		this.dragStartValue = this.value;
	}

	protected void onDragUpdate(int dragDelta) {
		int range = Math.max(1, Math.abs(this.getMaxValue() - this.getMinValue()));
		this.setValue(this.dragStartValue + dragDelta / Math.max(3, this.getWidth() / range));
	}

	protected void onDragCancelled() {
		this.setValue(this.dragStartValue);
	}

	protected void doDecrement() {
		this.setValue(this.value - 1);
	}

	protected void doIncrement() {
		this.setValue(this.value + 1);
	}

	protected String formatText() {
		return Integer.toString(this.value);
	}

	protected void syncWithModel() {
		this.cancelEdit();
		this.value = this.model.getValue();
		this.setDisplayText();
	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.addModelCallback();
	}

	protected void beforeRemoveFromGUI(GUI gui) {
		this.removeModelCallback();
		super.beforeRemoveFromGUI(gui);
	}

	protected void removeModelCallback() {
		if(this.model != null && this.modelCallback != null) {
			this.model.removeCallback(this.modelCallback);
		}

	}

	protected void addModelCallback() {
		if(this.model != null && this.getGUI() != null) {
			if(this.modelCallback == null) {
				this.modelCallback = new ValueAdjuster.ModelCallback();
			}

			this.model.addCallback(this.modelCallback);
			this.syncWithModel();
		}

	}
}
