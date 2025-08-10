package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;
import de.matthiasmann.twl.utils.CallbackSupport;

public class AnimatedWindow extends Widget {
	private int numAnimSteps = 10;
	private int currentStep;
	private int animSpeed;
	private BooleanModel model;
	private Runnable modelCallback;
	private Runnable[] callbacks;

	public AnimatedWindow() {
		this.setVisible(false);
	}

	public void addCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, cb, Runnable.class);
	}

	public void removeCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	private void doCallback() {
		CallbackSupport.fireCallbacks(this.callbacks);
	}

	public int getNumAnimSteps() {
		return this.numAnimSteps;
	}

	public void setNumAnimSteps(int numAnimSteps) {
		if(numAnimSteps < 1) {
			throw new IllegalArgumentException("numAnimSteps");
		} else {
			this.numAnimSteps = numAnimSteps;
		}
	}

	public void setState(boolean open) {
		if(open && !this.isOpen()) {
			this.animSpeed = 1;
			this.setVisible(true);
			this.doCallback();
		} else if(!open && !this.isClosed()) {
			this.animSpeed = -1;
			this.doCallback();
		}

		if(this.model != null) {
			this.model.setValue(open);
		}

	}

	public BooleanModel getModel() {
		return this.model;
	}

	public void setModel(BooleanModel model) {
		if(this.model != model) {
			if(this.model != null) {
				this.model.removeCallback(this.modelCallback);
			}

			this.model = model;
			if(model != null) {
				if(this.modelCallback == null) {
					this.modelCallback = new AnimatedWindow.ModelCallback();
				}

				model.addCallback(this.modelCallback);
				this.syncWithModel();
			}
		}

	}

	public boolean isOpen() {
		return this.currentStep == this.numAnimSteps && this.animSpeed >= 0;
	}

	public boolean isOpening() {
		return this.animSpeed > 0;
	}

	public boolean isClosed() {
		return this.currentStep == 0 && this.animSpeed <= 0;
	}

	public boolean isClosing() {
		return this.animSpeed < 0;
	}

	public boolean isAnimating() {
		return this.animSpeed != 0;
	}

	public boolean handleEvent(Event evt) {
		if(this.isOpen()) {
			if(super.handleEvent(evt)) {
				return true;
			} else {
				if(evt.isKeyPressedEvent()) {
					switch(evt.getKeyCode()) {
					case 1:
						this.setState(false);
						return true;
					}
				}

				return false;
			}
		} else if(this.isClosed()) {
			return false;
		} else {
			int mouseX = evt.getMouseX() - this.getX();
			int mouseY = evt.getMouseY() - this.getY();
			return mouseX >= 0 && mouseX < this.getAnimatedWidth() && mouseY >= 0 && mouseY < this.getAnimatedHeight();
		}
	}

	public int getMinWidth() {
		int minWidth = 0;
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			minWidth = Math.max(minWidth, child.getMinWidth());
		}

		return Math.max(super.getMinWidth(), minWidth + this.getBorderHorizontal());
	}

	public int getMinHeight() {
		int minHeight = 0;
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			minHeight = Math.max(minHeight, child.getMinHeight());
		}

		return Math.max(super.getMinHeight(), minHeight + this.getBorderVertical());
	}

	public int getPreferredInnerWidth() {
		return BoxLayout.computePreferredWidthVertical(this);
	}

	public int getPreferredInnerHeight() {
		return BoxLayout.computePreferredHeightHorizontal(this);
	}

	protected void layout() {
		this.layoutChildrenFullInnerArea();
	}

	protected void paint(GUI gui) {
		if(this.animSpeed != 0) {
			this.animate();
		}

		if(this.isOpen()) {
			super.paint(gui);
		} else if(!this.isClosed() && this.getBackground() != null) {
			this.getBackground().draw(this.getAnimationState(), this.getX(), this.getY(), this.getAnimatedWidth(), this.getAnimatedHeight());
		}

	}

	private void animate() {
		this.currentStep += this.animSpeed;
		if(this.currentStep == 0 || this.currentStep == this.numAnimSteps) {
			this.setVisible(this.currentStep > 0);
			this.animSpeed = 0;
			this.doCallback();
		}

	}

	private int getAnimatedWidth() {
		return this.getWidth() * this.currentStep / this.numAnimSteps;
	}

	private int getAnimatedHeight() {
		return this.getHeight() * this.currentStep / this.numAnimSteps;
	}

	void syncWithModel() {
		this.setState(this.model.getValue());
	}

	class ModelCallback implements Runnable {
		public void run() {
			AnimatedWindow.this.syncWithModel();
		}
	}
}
