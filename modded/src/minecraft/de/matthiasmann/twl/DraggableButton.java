package de.matthiasmann.twl;

public class DraggableButton extends Button {
	private int dragStartX;
	private int dragStartY;
	private boolean dragging;
	private DraggableButton.DragListener listener;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public DraggableButton() {
	}

	public DraggableButton(AnimationState animState) {
		super(animState);
	}

	public DraggableButton(AnimationState animState, boolean inherit) {
		super(animState, inherit);
	}

	public boolean isDragActive() {
		return this.dragging;
	}

	public DraggableButton.DragListener getListener() {
		return this.listener;
	}

	public void setListener(DraggableButton.DragListener listener) {
		this.listener = listener;
	}

	public boolean handleEvent(Event evt) {
		if(evt.isMouseEvent() && this.dragging) {
			if(evt.getType() == Event.Type.MOUSE_DRAGGED && this.listener != null) {
				this.listener.dragged(evt.getMouseX() - this.dragStartX, evt.getMouseY() - this.dragStartY);
			}

			if(evt.isMouseDragEnd()) {
				this.stopDragging(evt);
			}

			return true;
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 3:
				this.dragStartX = evt.getMouseX();
				this.dragStartY = evt.getMouseY();
			case 4:
			case 5:
			default:
				return super.handleEvent(evt);
			case 6:
				assert !this.dragging;

				this.dragging = true;
				this.getModel().setArmed(false);
				this.getModel().setPressed(true);
				if(this.listener != null) {
					this.listener.dragStarted();
				}

				return true;
			}
		}
	}

	private void stopDragging(Event evt) {
		if(this.listener != null) {
			this.listener.dragStopped();
		}

		this.dragging = false;
		this.getModel().setArmed(false);
		this.getModel().setPressed(false);
		this.getModel().setHover(this.isMouseInside(evt));
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;
		if($SWITCH_TABLE$de$matthiasmann$twl$Event$Type != null) {
			return i10000;
		} else {
			int[] i0 = new int[Event.Type.values().length];

			try {
				i0[Event.Type.KEY_PRESSED.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError12) {
			}

			try {
				i0[Event.Type.KEY_RELEASED.ordinal()] = 10;
			} catch (NoSuchFieldError noSuchFieldError11) {
			}

			try {
				i0[Event.Type.MOUSE_BTNDOWN.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError10) {
			}

			try {
				i0[Event.Type.MOUSE_BTNUP.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[Event.Type.MOUSE_CLICKED.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[Event.Type.MOUSE_DRAGGED.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[Event.Type.MOUSE_ENTERED.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[Event.Type.MOUSE_EXITED.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[Event.Type.MOUSE_MOVED.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[Event.Type.MOUSE_WHEEL.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[Event.Type.POPUP_CLOSED.ordinal()] = 12;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[Event.Type.POPUP_OPENED.ordinal()] = 11;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$Event$Type = i0;
			return i0;
		}
	}

	public interface DragListener {
		void dragStarted();

		void dragged(int i1, int i2);

		void dragStopped();
	}
}
