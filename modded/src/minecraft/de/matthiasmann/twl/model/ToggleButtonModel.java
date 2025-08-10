package de.matthiasmann.twl.model;

public class ToggleButtonModel extends SimpleButtonModel {
	protected static final int STATE_MASK_SELECTED = 256;
	private BooleanModel model;
	private Runnable modelCallback;
	private boolean invertModelState;
	private boolean isConnected;

	public ToggleButtonModel() {
	}

	public ToggleButtonModel(BooleanModel model) {
		this(model, false);
	}

	public ToggleButtonModel(BooleanModel model, boolean invertModelState) {
		this.setModel(model, invertModelState);
	}

	public boolean isSelected() {
		return (this.state & 256) != 0;
	}

	public void setSelected(boolean selected) {
		if(this.model != null) {
			this.model.setValue(selected ^ this.invertModelState);
		} else {
			this.setSelectedState(selected);
		}

	}

	protected void buttonAction() {
		this.setSelected(!this.isSelected());
		super.buttonAction();
	}

	public BooleanModel getModel() {
		return this.model;
	}

	public void setModel(BooleanModel model) {
		this.setModel(model, false);
	}

	public void setModel(BooleanModel model, boolean invertModelState) {
		this.invertModelState = invertModelState;
		if(this.model != model) {
			this.removeModelCallback();
			this.model = model;
			this.addModelCallback();
		}

		if(model != null) {
			this.syncWithModel();
		}

	}

	public boolean isInvertModelState() {
		return this.invertModelState;
	}

	void syncWithModel() {
		this.setSelectedState(this.model.getValue() ^ this.invertModelState);
	}

	public void connect() {
		this.isConnected = true;
		this.addModelCallback();
	}

	public void disconnect() {
		this.isConnected = false;
		this.removeModelCallback();
	}

	private void addModelCallback() {
		if(this.model != null && this.isConnected) {
			if(this.modelCallback == null) {
				this.modelCallback = new ToggleButtonModel.ModelCallback();
			}

			this.model.addCallback(this.modelCallback);
			this.syncWithModel();
		}

	}

	private void removeModelCallback() {
		if(this.model != null && this.modelCallback != null) {
			this.model.removeCallback(this.modelCallback);
		}

	}

	private void setSelectedState(boolean selected) {
		if(selected != this.isSelected()) {
			this.setStateBit(256, selected);
			this.fireStateCallback();
		}

	}

	class ModelCallback implements Runnable {
		public void run() {
			ToggleButtonModel.this.syncWithModel();
		}
	}
}
