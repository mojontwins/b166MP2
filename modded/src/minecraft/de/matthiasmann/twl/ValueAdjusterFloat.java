package de.matthiasmann.twl;

import de.matthiasmann.twl.model.FloatModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.IllegalFormatException;
import java.util.Locale;

public class ValueAdjusterFloat extends ValueAdjuster {
	private float value;
	private float minValue;
	private float maxValue = 100.0F;
	private float dragStartValue;
	private float stepSize = 1.0F;
	private FloatModel model;
	private Runnable modelCallback;
	private String format = "%.2f";
	private Locale locale = Locale.ENGLISH;

	public ValueAdjusterFloat() {
		this.setTheme("valueadjuster");
		this.setDisplayText();
	}

	public ValueAdjusterFloat(FloatModel model) {
		this.setTheme("valueadjuster");
		this.setModel(model);
	}

	public float getMaxValue() {
		return this.maxValue;
	}

	public float getMinValue() {
		return this.minValue;
	}

	public void setMinMaxValue(float minValue, float maxValue) {
		if(maxValue < minValue) {
			throw new IllegalArgumentException("maxValue < minValue");
		} else {
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.setValue(this.value);
		}
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		if(value > this.maxValue) {
			value = this.maxValue;
		} else if(value < this.minValue) {
			value = this.minValue;
		}

		if(this.value != value) {
			this.value = value;
			if(this.model != null) {
				this.model.setValue(value);
			}

			this.setDisplayText();
		}

	}

	public float getStepSize() {
		return this.stepSize;
	}

	public void setStepSize(float stepSize) {
		if(stepSize <= 0.0F) {
			throw new IllegalArgumentException("stepSize");
		} else {
			this.stepSize = stepSize;
		}
	}

	public FloatModel getModel() {
		return this.model;
	}

	public void setModel(FloatModel model) {
		if(this.model != model) {
			this.removeModelCallback();
			if(this.model != null) {
				this.model.removeCallback(this.modelCallback);
			}

			this.model = model;
			if(model != null) {
				this.minValue = model.getMinValue();
				this.maxValue = model.getMaxValue();
				this.addModelCallback();
			}
		}

	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) throws IllegalFormatException {
		String.format(this.locale, format, new Object[]{42.0F});
		this.format = format;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		if(locale == null) {
			throw new NullPointerException("locale");
		} else {
			this.locale = locale;
		}
	}

	protected String onEditStart() {
		return this.formatText();
	}

	protected boolean onEditEnd(String text) {
		try {
			this.setValue(this.parseText(text));
			return true;
		} catch (ParseException parseException3) {
			return false;
		}
	}

	protected String validateEdit(String text) {
		try {
			this.parseText(text);
			return null;
		} catch (ParseException parseException3) {
			return parseException3.toString();
		}
	}

	protected void onEditCanceled() {
	}

	protected boolean shouldStartEdit(char ch) {
		return ch >= 48 && ch <= 57 || ch == 45 || ch == 46;
	}

	protected void onDragStart() {
		this.dragStartValue = this.value;
	}

	protected void onDragUpdate(int dragDelta) {
		float range = Math.max(1.0E-4F, Math.abs(this.getMaxValue() - this.getMinValue()));
		this.setValue(this.dragStartValue + (float)dragDelta / Math.max(3.0F, (float)this.getWidth() / range));
	}

	protected void onDragCancelled() {
		this.setValue(this.dragStartValue);
	}

	protected void doDecrement() {
		this.setValue(this.value - this.getStepSize());
	}

	protected void doIncrement() {
		this.setValue(this.value + this.getStepSize());
	}

	protected String formatText() {
		return String.format(this.locale, this.format, new Object[]{this.value});
	}

	protected float parseText(String value) throws ParseException {
		return NumberFormat.getNumberInstance(this.locale).parse(value).floatValue();
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
