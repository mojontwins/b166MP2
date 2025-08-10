package de.matthiasmann.twl;

import de.matthiasmann.twl.input.Input;
import de.matthiasmann.twl.input.lwjgl.LWJGLInput;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.theme.ThemeManager;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GUI extends Widget {
	private static final int DRAG_DIST = 3;
	private static final int DBLCLICK_TIME = 500;
	private static final int KEYREPEAT_INITIAL_DELAY = 250;
	private static final int KEYREPEAT_INTERVAL_DELAY = 33;
	private static final int NO_REPEAT = 0;
	private int tooltipOffsetX;
	private int tooltipOffsetY;
	private int tooltipDelay;
	private int tooltipReappearDelay;
	private final Renderer renderer;
	private final Input input;
	long curTime;
	private int deltaTime;
	private Widget rootPane;
	boolean hasInvalidLayouts;
	final Event event;
	private boolean wasInside;
	private boolean dragActive;
	private int mouseClickCount;
	private int dragButton;
	private int mouseDownX;
	private int mouseDownY;
	private int mouseLastX;
	private int mouseLastY;
	private int mouseClickedX;
	private int mouseClickedY;
	private long mouseEventTime;
	private long mouseClickedTime;
	private long keyEventTime;
	private int keyRepeatDelay;
	private boolean popupEventOccured;
	private Widget lastMouseDownWidget;
	private Widget lastMouseClickWidget;
	private PopupWindow boundDragPopup;
	private Runnable boundDragCallback;
	private int mouseIdleTime;
	private boolean mouseIdleState;
	private GUI.MouseIdleListener mouseIdleListener;
	private Rect[] clipRects;
	private int numClipRects;
	private InfoWindow activeInfoWindow;
	private final Widget infoWindowPlaceholder;
	private final GUI.TooltipWindow tooltipWindow;
	private final Label tooltipLabel;
	private Widget tooltipOwner;
	private boolean hadOpenTooltip;
	private long tooltipClosedTime;
	final ArrayList activeTimers;
	private final ArrayList invokeLaterQueue;
	final ExecutorService executorService;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Alignment;

	public GUI(Renderer renderer) {
		this(new Widget(), renderer);
		this.rootPane.setTheme("");
		this.rootPane.setFocusKeyEnabled(false);
	}

	public GUI(Widget rootPane, Renderer renderer) {
		this(rootPane, renderer, new LWJGLInput());
	}

	public GUI(Widget rootPane, Renderer renderer, Input input) {
		this.tooltipOffsetX = 0;
		this.tooltipOffsetY = 0;
		this.tooltipDelay = 1000;
		this.tooltipReappearDelay = 100;
		this.dragButton = -1;
		this.mouseIdleTime = 60;
		if(rootPane == null) {
			throw new IllegalArgumentException("rootPane is null");
		} else if(renderer == null) {
			throw new IllegalArgumentException("renderer is null");
		} else {
			this.guiInstance = this;
			this.renderer = renderer;
			this.input = input;
			this.event = new Event();
			this.rootPane = rootPane;
			this.rootPane.setFocusKeyEnabled(false);
			this.clipRects = new Rect[8];
			this.infoWindowPlaceholder = new Widget();
			this.infoWindowPlaceholder.setTheme("");
			this.tooltipLabel = new Label();
			this.tooltipWindow = new GUI.TooltipWindow();
			this.tooltipWindow.setVisible(false);
			this.activeTimers = new ArrayList();
			this.invokeLaterQueue = new ArrayList();
			this.executorService = Executors.newSingleThreadExecutor(new GUI.TF());
			this.setTheme("");
			this.setFocusKeyEnabled(false);
			this.setSize();
			super.insertChild(rootPane, 0);
			super.insertChild(this.infoWindowPlaceholder, 1);
			super.insertChild(this.tooltipWindow, 2);
			this.resyncTimerAfterPause();
		}
	}

	public void applyTheme(ThemeManager themeManager) {
		if(themeManager == null) {
			throw new IllegalArgumentException("themeManager is null");
		} else {
			super.applyTheme(themeManager);
		}
	}

	public Widget getRootPane() {
		return this.rootPane;
	}

	public void setRootPane(Widget rootPane) {
		if(rootPane == null) {
			throw new IllegalArgumentException("rootPane is null");
		} else {
			this.rootPane = rootPane;
			super.removeChild(0);
			super.insertChild(rootPane, 0);
		}
	}

	public Renderer getRenderer() {
		return this.renderer;
	}

	public Input getInput() {
		return this.input;
	}

	public MouseSensitiveRectangle createMouseSenitiveRectangle() {
		return new MouseSensitiveRectangle() {
			public boolean isMouseOver() {
				return this.isInside(GUI.this.event.mouseX, GUI.this.event.mouseY);
			}
		};
	}

	public Timer createTimer() {
		return new Timer(this);
	}

	public long getCurrentTime() {
		return this.curTime;
	}

	public int getCurrentDeltaTime() {
		return this.deltaTime;
	}

	public void invokeLater(Runnable runnable) {
		if(runnable == null) {
			throw new IllegalArgumentException("runnable is null");
		} else {
			ArrayList arrayList2 = this.invokeLaterQueue;
			synchronized(this.invokeLaterQueue) {
				this.invokeLaterQueue.add(runnable);
			}
		}
	}

	public Future invokeAsync(Callable job, GUI.AsyncCompletionListener listener) {
		if(job == null) {
			throw new IllegalArgumentException("job is null");
		} else if(listener == null) {
			throw new IllegalArgumentException("listener is null");
		} else {
			return this.executorService.submit(new GUI.AC(job, (Runnable)null, listener));
		}
	}

	public Future invokeAsync(Runnable job, GUI.AsyncCompletionListener listener) {
		if(job == null) {
			throw new IllegalArgumentException("job is null");
		} else if(listener == null) {
			throw new IllegalArgumentException("listener is null");
		} else {
			return this.executorService.submit(new GUI.AC((Callable)null, job, listener));
		}
	}

	public boolean requestToolTip(Widget widget, int x, int y, Object content, Alignment alignment) {
		if(alignment == null) {
			throw new IllegalArgumentException("alignment is null");
		} else if(widget == this.getWidgetUnderMouse()) {
			this.setTooltip(x, y, widget, content, alignment);
			return true;
		} else {
			return false;
		}
	}

	public void requestToolTipUpdate(Widget widget) {
		if(this.tooltipOwner == widget) {
			this.tooltipOwner = null;
		}

	}

	public GUI.MouseIdleListener getMouseIdleListener() {
		return this.mouseIdleListener;
	}

	public void setMouseIdleListener(GUI.MouseIdleListener mouseIdleListener) {
		this.mouseIdleListener = mouseIdleListener;
		this.callMouseIdleListener();
	}

	public int getMouseIdleTime() {
		return this.mouseIdleTime;
	}

	public void setMouseIdleTime(int mouseIdleTime) {
		if(mouseIdleTime < 1) {
			throw new IllegalArgumentException("mouseIdleTime < 1");
		} else {
			this.mouseIdleTime = mouseIdleTime;
		}
	}

	public int getTooltipDelay() {
		return this.tooltipDelay;
	}

	public void setTooltipDelay(int tooltipDelay) {
		if(tooltipDelay < 1) {
			throw new IllegalArgumentException("tooltipDelay");
		} else {
			this.tooltipDelay = tooltipDelay;
		}
	}

	public int getTooltipReappearDelay() {
		return this.tooltipReappearDelay;
	}

	public void setTooltipReappearDelay(int tooltipReappearDelay) {
		this.tooltipReappearDelay = tooltipReappearDelay;
	}

	public int getTooltipOffsetX() {
		return this.tooltipOffsetX;
	}

	public int getTooltipOffsetY() {
		return this.tooltipOffsetY;
	}

	public void setTooltipOffset(int tooltipOffsetX, int tooltipOffsetY) {
		this.tooltipOffsetX = tooltipOffsetX;
		this.tooltipOffsetY = tooltipOffsetY;
	}

	public boolean setPosition(int x, int y) {
		throw new UnsupportedOperationException();
	}

	public void insertChild(Widget child, int index) {
		throw new UnsupportedOperationException();
	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException();
	}

	public Widget removeChild(int index) {
		throw new UnsupportedOperationException();
	}

	public void adjustSize() {
	}

	protected void layout() {
		this.layoutChildFullInnerArea(this.rootPane);
	}

	public void validateLayout() {
		if(this.hasInvalidLayouts) {
			boolean MAX_ITERATIONS = true;

			int iterations;
			for(iterations = 0; this.hasInvalidLayouts && iterations < 1000; ++iterations) {
				this.hasInvalidLayouts = false;
				super.validateLayout();
			}

			ArrayList widgetsInLoop = null;
			if(this.hasInvalidLayouts) {
				widgetsInLoop = new ArrayList();
				this.collectLayoutLoop(widgetsInLoop);
			}

			DebugHook.getDebugHook().guiLayoutValidated(iterations, widgetsInLoop);
		}

	}

	public void setSize() {
		this.setSize(this.renderer.getWidth(), this.renderer.getHeight());
	}

	public void update() {
		this.setSize();
		this.updateTime();
		this.handleInput();
		this.handleKeyRepeat();
		this.handleTooltips();
		this.updateTimers();
		this.invokeRunables();
		this.validateLayout();
		this.draw();
		this.setCursor();
	}

	public void resyncTimerAfterPause() {
		this.curTime = this.renderer.getTimeMillis();
		this.deltaTime = 0;
	}

	public void updateTime() {
		long newTime = this.renderer.getTimeMillis();
		this.deltaTime = Math.max(0, (int)(newTime - this.curTime));
		this.curTime = newTime;
	}

	public void updateTimers() {
		int i = 0;

		while(i < this.activeTimers.size()) {
			if(!((Timer)this.activeTimers.get(i)).tick(this.deltaTime)) {
				this.activeTimers.remove(i);
			} else {
				++i;
			}
		}

	}

	public void invokeRunables() {
		Runnable[] runnables = (Runnable[])null;
		ArrayList r = this.invokeLaterQueue;
		int size;
		synchronized(this.invokeLaterQueue) {
			size = this.invokeLaterQueue.size();
			if(size > 0) {
				runnables = (Runnable[])this.invokeLaterQueue.toArray(new Runnable[size]);
				this.invokeLaterQueue.clear();
			}
		}

		if(runnables != null) {
			Runnable[] runnable5 = runnables;
			int i4 = runnables.length;

			for(size = 0; size < i4; ++size) {
				Runnable runnable9 = runnable5[size];

				try {
					runnable9.run();
				} catch (Throwable throwable7) {
					Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, "Exception in runnable", throwable7);
				}
			}
		}

	}

	public void draw() {
		this.numClipRects = 0;
		if(this.renderer.startRenderering()) {
			try {
				this.drawWidget(this);
			} finally {
				this.renderer.endRendering();
			}
		}

	}

	public void setCursor() {
		Widget widget = this.getWidgetUnderMouse();
		if(widget != null && widget.isEnabled()) {
			MouseCursor cursor = widget.getMouseCursor();
			this.renderer.setCursor(cursor);
		}

	}

	public void handleInput() {
		if(this.input != null && !this.input.pollInput(this)) {
			this.clearKeyboardState();
			this.clearMouseState();
		}

	}

	public final boolean handleMouse(int mouseX, int mouseY, int button, boolean pressed) {
		this.mouseEventTime = this.curTime;
		this.event.mouseButton = button;
		int prevButtonState = this.event.modifier & 448;
		short buttonMask = 0;
		switch(button) {
		case 0:
			buttonMask = 64;
			break;
		case 1:
			buttonMask = 128;
			break;
		case 2:
			buttonMask = 256;
		}

		this.event.setModifier(buttonMask, pressed);
		boolean wasPressed = (prevButtonState & buttonMask) != 0;
		if(buttonMask != 0) {
			this.renderer.setMouseButton(button, pressed);
		}

		if(!this.dragActive && prevButtonState != 0) {
			this.event.mouseX = this.mouseDownX;
			this.event.mouseY = this.mouseDownY;
		} else {
			this.event.mouseX = mouseX;
			this.event.mouseY = mouseY;
		}

		boolean handled = this.dragActive;
		if(!this.dragActive) {
			if(!this.isInside(mouseX, mouseY)) {
				pressed = false;
				this.mouseClickCount = 0;
				if(this.wasInside) {
					this.sendMouseEvent(Event.Type.MOUSE_EXITED, (Widget)null);
					this.wasInside = false;
				}
			} else if(!this.wasInside) {
				this.wasInside = true;
				if(this.sendMouseEvent(Event.Type.MOUSE_ENTERED, (Widget)null) != null) {
					handled = true;
				}
			}
		}

		if(mouseX != this.mouseLastX || mouseY != this.mouseLastY) {
			this.mouseLastX = mouseX;
			this.mouseLastY = mouseY;
			if(prevButtonState != 0 && !this.dragActive && (Math.abs(mouseX - this.mouseDownX) > 3 || Math.abs(mouseY - this.mouseDownY) > 3)) {
				this.dragActive = true;
				this.mouseClickCount = 0;
				this.hideTooltip();
				this.hadOpenTooltip = false;
				this.tooltipOwner = this.lastMouseDownWidget;
			}

			if(this.dragActive) {
				if(this.boundDragPopup != null) {
					assert this.getTopPane() == this.boundDragPopup;

					this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null);
				} else if(this.lastMouseDownWidget != null) {
					this.sendMouseEvent(Event.Type.MOUSE_DRAGGED, this.lastMouseDownWidget);
				}
			} else if(prevButtonState == 0 && this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null) != null) {
				handled = true;
			}
		}

		if(buttonMask != 0 && pressed != wasPressed) {
			if(pressed) {
				if(this.dragButton < 0) {
					this.mouseDownX = mouseX;
					this.mouseDownY = mouseY;
					this.dragButton = button;
					this.lastMouseDownWidget = this.sendMouseEvent(Event.Type.MOUSE_BTNDOWN, (Widget)null);
				} else if(this.lastMouseDownWidget != null && this.boundDragPopup == null) {
					this.sendMouseEvent(Event.Type.MOUSE_BTNDOWN, this.lastMouseDownWidget);
				}
			} else if(this.dragButton >= 0 && (this.boundDragPopup == null || this.event.isMouseDragEnd())) {
				if(this.boundDragPopup != null && button == this.dragButton) {
					this.sendMouseEvent(Event.Type.MOUSE_BTNUP, this.getWidgetUnderMouse());
				}

				if(this.lastMouseDownWidget != null) {
					this.sendMouseEvent(Event.Type.MOUSE_BTNUP, this.lastMouseDownWidget);
				}
			}

			if(this.lastMouseDownWidget != null) {
				handled = true;
			}

			if(button == 0 && !this.popupEventOccured && !pressed && !this.dragActive) {
				if(this.mouseClickCount == 0 || this.curTime - this.mouseClickedTime > 500L || this.lastMouseClickWidget != this.lastMouseDownWidget) {
					this.mouseClickedX = mouseX;
					this.mouseClickedY = mouseY;
					this.lastMouseClickWidget = this.lastMouseDownWidget;
					this.mouseClickCount = 0;
					this.mouseClickedTime = this.curTime;
				}

				if(Math.abs(mouseX - this.mouseClickedX) < 3 && Math.abs(mouseY - this.mouseClickedY) < 3) {
					this.event.mouseX = this.mouseClickedX;
					this.event.mouseY = this.mouseClickedY;
					this.event.mouseClickCount = ++this.mouseClickCount;
					this.mouseClickedTime = this.curTime;
					if(this.lastMouseClickWidget != null) {
						this.sendMouseEvent(Event.Type.MOUSE_CLICKED, this.lastMouseClickWidget);
					}
				} else {
					this.lastMouseClickWidget = null;
				}
			}
		}

		if(this.event.isMouseDragEnd()) {
			if(this.dragActive) {
				this.dragActive = false;
				this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null);
			}

			this.dragButton = -1;
			if(this.boundDragCallback != null) {
				try {
					this.boundDragCallback.run();
				} catch (Exception exception13) {
					Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, "Exception in bound drag callback", exception13);
				} finally {
					this.boundDragCallback = null;
					this.boundDragPopup = null;
				}
			}
		}

		return handled;
	}

	public void clearMouseState() {
		this.event.setModifier(64, false);
		this.event.setModifier(256, false);
		this.event.setModifier(128, false);
		this.renderer.setMouseButton(0, false);
		this.renderer.setMouseButton(2, false);
		this.renderer.setMouseButton(1, false);
		this.lastMouseClickWidget = null;
		this.mouseClickCount = 0;
		this.mouseClickedTime = this.curTime;
		this.boundDragPopup = null;
		this.boundDragCallback = null;
		if(this.dragActive) {
			this.dragActive = false;
			this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null);
		}

		this.dragButton = -1;
	}

	public final boolean handleMouseWheel(int wheelDelta) {
		this.event.mouseWheelDelta = wheelDelta;
		boolean handled = this.sendMouseEvent(Event.Type.MOUSE_WHEEL, this.dragActive ? this.lastMouseDownWidget : null) != null;
		this.event.mouseWheelDelta = 0;
		return handled;
	}

	public final boolean handleKey(int keyCode, char keyChar, boolean pressed) {
		this.event.keyCode = keyCode;
		this.event.keyChar = keyChar;
		this.event.keyRepeated = false;
		this.keyEventTime = this.curTime;
		if(this.event.keyCode == 0 && this.event.keyChar == 0) {
			this.keyRepeatDelay = 0;
			return false;
		} else {
			this.event.setModifiers(pressed);
			if(pressed) {
				this.keyRepeatDelay = 250;
				return this.sendEvent(Event.Type.KEY_PRESSED);
			} else {
				this.keyRepeatDelay = 0;
				return this.sendEvent(Event.Type.KEY_RELEASED);
			}
		}
	}

	public final void clearKeyboardState() {
		this.event.modifier &= -1600;
		this.keyRepeatDelay = 0;
	}

	public final void handleKeyRepeat() {
		if(this.keyRepeatDelay != 0) {
			long keyDeltaTime = this.curTime - this.keyEventTime;
			if(keyDeltaTime > (long)this.keyRepeatDelay) {
				this.keyEventTime = this.curTime;
				this.keyRepeatDelay = 33;
				this.event.keyRepeated = true;
				this.sendEvent(Event.Type.KEY_PRESSED);
			}
		}

	}

	public final void handleTooltips() {
		Widget widgetUnderMouse = this.getWidgetUnderMouse();
		if(widgetUnderMouse != this.tooltipOwner) {
			if(widgetUnderMouse == null || this.curTime - this.mouseEventTime <= (long)this.tooltipDelay && (!this.hadOpenTooltip || this.curTime - this.tooltipClosedTime >= (long)this.tooltipReappearDelay)) {
				this.hideTooltip();
			} else {
				this.setTooltip(this.event.mouseX + this.tooltipOffsetX, this.event.mouseY + this.tooltipOffsetY, widgetUnderMouse, widgetUnderMouse.getTooltipContentAt(this.event.mouseX, this.event.mouseY), Alignment.BOTTOMLEFT);
			}
		}

		boolean mouseIdle = this.curTime - this.mouseEventTime > (long)this.mouseIdleTime;
		if(this.mouseIdleState != mouseIdle) {
			this.mouseIdleState = mouseIdle;
			this.callMouseIdleListener();
		}

	}

	private Widget getTopPane() {
		return super.getChild(super.getNumChildren() - 3);
	}

	Widget getWidgetUnderMouse() {
		return this.getTopPane().getWidgetUnderMouse();
	}

	private Widget sendMouseEvent(Event.Type type, Widget target) {
		assert type.isMouseEvent;

		this.popupEventOccured = false;
		this.event.type = type;
		this.event.dragEvent = this.dragActive && this.boundDragPopup == null;
		this.renderer.setMousePosition(this.event.mouseX, this.event.mouseY);
		if(target == null) {
			assert !this.dragActive || this.boundDragPopup != null;

			Object widget = null;
			if(this.activeInfoWindow != null && this.activeInfoWindow.isMouseInside(this.event) && this.setMouseOverChild(this.activeInfoWindow, this.event)) {
				widget = this.activeInfoWindow;
			}

			if(widget == null) {
				widget = this.getTopPane();
				this.setMouseOverChild((Widget)widget, this.event);
			}

			return ((Widget)widget).routeMouseEvent(this.event);
		} else {
			if(target.isEnabled() || !isMouseAction(this.event)) {
				target.handleEvent(this.event);
			}

			return target;
		}
	}

	private boolean sendEvent(Event.Type type) {
		assert !type.isMouseEvent;

		this.popupEventOccured = false;
		this.event.type = type;
		this.event.dragEvent = false;
		return this.getTopPane().handleEvent(this.event);
	}

	private void sendPopupEvent(Event.Type type) {
		assert type == Event.Type.POPUP_OPENED || type == Event.Type.POPUP_CLOSED;

		this.popupEventOccured = false;
		this.event.type = type;
		this.event.dragEvent = false;

		try {
			this.getTopPane().routePopupEvent(this.event);
		} catch (Exception exception3) {
			Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, "Exception in sendPopupEvent()", exception3);
		}

	}

	void resendLastMouseMove() {
		if(!this.dragActive) {
			this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null);
		}

	}

	void openPopup(PopupWindow popup) {
		if(popup.getParent() == this) {
			this.closePopup(popup);
		} else if(popup.getParent() != null) {
			throw new IllegalArgumentException("popup must not be added anywhere");
		}

		this.hideTooltip();
		this.hadOpenTooltip = false;
		this.sendPopupEvent(Event.Type.POPUP_OPENED);
		super.insertChild(popup, this.getNumChildren() - 2);
		popup.getOwner().setOpenPopup(this, true);
		this.popupEventOccured = true;
		if(this.activeInfoWindow != null) {
			this.closeInfo(this.activeInfoWindow);
		}

	}

	void closePopup(PopupWindow popup) {
		if(this.boundDragPopup == popup) {
			this.boundDragPopup = null;
		}

		int idx = this.getChildIndex(popup);
		if(idx > 0) {
			super.removeChild(idx);
		}

		popup.getOwner().recalcOpenPopups(this);
		this.sendPopupEvent(Event.Type.POPUP_CLOSED);
		this.popupEventOccured = true;
		this.closeInfoFromWidget(popup);
		this.requestKeyboardFocus(this.getTopPane());
	}

	boolean hasOpenPopups(Widget owner) {
		int i = this.getNumChildren() - 2;

		while(i-- > 1) {
			PopupWindow popup = (PopupWindow)this.getChild(i);
			if(popup.getOwner() == owner) {
				return true;
			}
		}

		return false;
	}

	private boolean isOwner(Widget owner, Widget widget) {
		while(owner != null && owner != widget) {
			owner = owner.getParent();
		}

		if(owner == widget) {
			return true;
		} else {
			return false;
		}
	}

	void closePopupFromWidgets(Widget widget) {
		int i = this.getNumChildren() - 2;

		while(i-- > 1) {
			PopupWindow popup = (PopupWindow)this.getChild(i);
			if(this.isOwner(popup.getOwner(), widget)) {
				this.closePopup(popup);
			}
		}

	}

	void closeIfPopup(Widget widget) {
		if(widget instanceof PopupWindow) {
			this.closePopup((PopupWindow)widget);
		}

	}

	boolean bindDragEvent(PopupWindow popup, Runnable cb) {
		if(this.boundDragPopup == null && this.getTopPane() == popup && this.dragButton >= 0 && !this.isOwner(this.lastMouseDownWidget, popup)) {
			this.dragActive = true;
			this.boundDragPopup = popup;
			this.boundDragCallback = cb;
			this.sendMouseEvent(Event.Type.MOUSE_MOVED, (Widget)null);
			return true;
		} else {
			return false;
		}
	}

	void widgetHidden(Widget widget) {
		this.closeIfPopup(widget);
		this.closePopupFromWidgets(widget);
		if(this.isOwner(this.tooltipOwner, widget)) {
			this.hideTooltip();
			this.hadOpenTooltip = false;
		}

		this.closeInfoFromWidget(widget);
	}

	void widgetDisabled(Widget widget) {
		this.closeIfPopup(widget);
		this.closeInfoFromWidget(widget);
	}

	void closeInfoFromWidget(Widget widget) {
		if(this.activeInfoWindow != null && (this.activeInfoWindow == widget || this.isOwner(this.activeInfoWindow.getOwner(), widget))) {
			this.closeInfo(this.activeInfoWindow);
		}

	}

	void openInfo(InfoWindow info) {
		int idx = this.getNumChildren() - 2;
		super.removeChild(idx);
		super.insertChild(info, idx);
		this.activeInfoWindow = info;
	}

	void closeInfo(InfoWindow info) {
		if(info == this.activeInfoWindow) {
			int idx = this.getNumChildren() - 2;
			super.removeChild(idx);
			super.insertChild(this.infoWindowPlaceholder, idx);
			this.activeInfoWindow = null;

			try {
				info.infoWindowClosed();
			} catch (Exception exception4) {
				Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, "Exception in infoWindowClosed()", exception4);
			}
		}

	}

	public boolean requestKeyboardFocus() {
		return true;
	}

	protected boolean requestKeyboardFocus(Widget child) {
		return child != null && child != this.getTopPane() ? false : super.requestKeyboardFocus(child);
	}

	private void hideTooltip() {
		if(this.tooltipWindow.isVisible()) {
			this.tooltipClosedTime = this.curTime;
			this.hadOpenTooltip = true;
		}

		this.tooltipWindow.setVisible(false);
		this.tooltipOwner = null;
		if(this.tooltipLabel.getParent() != this.tooltipWindow) {
			this.tooltipWindow.removeAllChildren();
		}

	}

	private void setTooltip(int x, int y, Widget widget, Object content, Alignment alignment) throws IllegalArgumentException {
		if(content == null) {
			this.hideTooltip();
		} else {
			if(content instanceof String) {
				String ttWidth = (String)content;
				if(ttWidth.length() == 0) {
					this.hideTooltip();
					return;
				}

				if(this.tooltipLabel.getParent() != this.tooltipWindow) {
					this.tooltipWindow.removeAllChildren();
					this.tooltipWindow.add(this.tooltipLabel);
				}

				this.tooltipLabel.setBackground((Image)null);
				this.tooltipLabel.setText(ttWidth);
				this.tooltipWindow.adjustSize();
			} else {
				if(!(content instanceof Widget)) {
					throw new IllegalArgumentException("Unsupported data type");
				}

				Widget ttWidth1 = (Widget)content;
				if(ttWidth1.getParent() != null && ttWidth1.getParent() != this.tooltipWindow) {
					throw new IllegalArgumentException("Content widget must not be added to another widget");
				}

				this.tooltipWindow.removeAllChildren();
				this.tooltipWindow.add(ttWidth1);
				this.tooltipWindow.adjustSize();
			}

			int ttWidth2 = this.tooltipWindow.getWidth();
			int ttHeight = this.tooltipWindow.getHeight();
			switch($SWITCH_TABLE$de$matthiasmann$twl$Alignment()[alignment.ordinal()]) {
			case 2:
			case 4:
			case 5:
				x -= ttWidth2 / 2;
				break;
			case 3:
			case 7:
			case 9:
				x -= ttWidth2;
			case 6:
			case 8:
			}

			switch($SWITCH_TABLE$de$matthiasmann$twl$Alignment()[alignment.ordinal()]) {
			case 1:
			case 2:
			case 3:
				y -= ttHeight / 2;
			case 4:
			case 6:
			case 7:
			default:
				break;
			case 5:
			case 8:
			case 9:
				y -= ttHeight;
			}

			if(x + ttWidth2 > this.getWidth()) {
				x = this.getWidth() - ttWidth2;
			}

			if(y + ttHeight > this.getHeight()) {
				y = this.getHeight() - ttHeight;
			}

			if(x < 0) {
				x = 0;
			}

			if(y < 0) {
				y = 0;
			}

			this.tooltipOwner = widget;
			this.tooltipWindow.setPosition(x, y);
			this.tooltipWindow.setVisible(true);
		}
	}

	void clipEnter(int x, int y, int w, int h) {
		if(this.numClipRects == this.clipRects.length) {
			Rect[] newRects = new Rect[this.numClipRects * 2];
			System.arraycopy(this.clipRects, 0, newRects, 0, this.numClipRects);
			this.clipRects = newRects;
		}

		Rect rect;
		if((rect = this.clipRects[this.numClipRects]) == null) {
			rect = new Rect();
			this.clipRects[this.numClipRects] = rect;
		}

		rect.setXYWH(x, y, w, h);
		if(this.numClipRects > 0) {
			rect.intersect(this.clipRects[this.numClipRects - 1]);
		}

		this.renderer.setClipRect(rect);
		++this.numClipRects;
	}

	boolean clipEmpty() {
		return this.clipRects[this.numClipRects - 1].isEmpty();
	}

	void clipLeave() {
		--this.numClipRects;
		if(this.numClipRects == 0) {
			this.renderer.setClipRect((Rect)null);
		} else {
			this.renderer.setClipRect(this.clipRects[this.numClipRects - 1]);
		}

	}

	private void callMouseIdleListener() {
		if(this.mouseIdleListener != null) {
			if(this.mouseIdleState) {
				this.mouseIdleListener.mouseEnterIdle();
			} else {
				this.mouseIdleListener.mouseExitIdle();
			}
		}

	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$Alignment() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Alignment;
		if($SWITCH_TABLE$de$matthiasmann$twl$Alignment != null) {
			return i10000;
		} else {
			int[] i0 = new int[Alignment.values().length];

			try {
				i0[Alignment.BOTTOM.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError10) {
			}

			try {
				i0[Alignment.BOTTOMLEFT.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[Alignment.BOTTOMRIGHT.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[Alignment.CENTER.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[Alignment.FILL.ordinal()] = 10;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[Alignment.LEFT.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[Alignment.RIGHT.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[Alignment.TOP.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[Alignment.TOPLEFT.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[Alignment.TOPRIGHT.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$Alignment = i0;
			return i0;
		}
	}

	class AC implements Callable, Runnable {
		private final Callable jobC;
		private final Runnable jobR;
		private final GUI.AsyncCompletionListener listener;
		private Object result;
		private Exception exception;

		public AC(Callable jobC, Runnable jobR, GUI.AsyncCompletionListener listener) {
			this.jobC = jobC;
			this.jobR = jobR;
			this.listener = listener;
		}

		public Object call() throws Exception {
			try {
				if(this.jobC != null) {
					this.result = this.jobC.call();
				} else {
					this.jobR.run();
				}

				GUI.this.invokeLater(this);
				return this.result;
			} catch (Exception exception2) {
				this.exception = exception2;
				GUI.this.invokeLater(this);
				throw exception2;
			}
		}

		public void run() {
			if(this.exception != null) {
				this.listener.failed(this.exception);
			} else {
				this.listener.completed(this.result);
			}

		}
	}

	public interface AsyncCompletionListener {
		void completed(Object object1);

		void failed(Exception exception1);
	}

	public interface MouseIdleListener {
		void mouseEnterIdle();

		void mouseExitIdle();
	}

	static class TF implements ThreadFactory {
		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String prefix = "GUI-" + poolNumber.getAndIncrement() + "-invokeAsync-";

		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, this.prefix + this.threadNumber.getAndIncrement());
			t.setDaemon(true);
			t.setPriority(5);
			return t;
		}
	}

	static class TooltipWindow extends Widget {
		public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_FADE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("fade");
		private int fadeInTime;

		protected void applyTheme(ThemeInfo themeInfo) {
			super.applyTheme(themeInfo);
			this.fadeInTime = themeInfo.getParameter("fadeInTime", 0);
		}

		public void setVisible(boolean visible) {
			super.setVisible(visible);
			this.getAnimationState().resetAnimationTime(STATE_FADE);
		}

		protected void paint(GUI gui) {
			int time = this.getAnimationState().getAnimationTime(STATE_FADE);
			if(time < this.fadeInTime) {
				float alpha = (float)time / (float)this.fadeInTime;
				gui.getRenderer().pushGlobalTintColor(1.0F, 1.0F, 1.0F, alpha);

				try {
					super.paint(gui);
				} finally {
					gui.getRenderer().popGlobalTintColor();
				}
			} else {
				super.paint(gui);
			}

		}

		public int getMinWidth() {
			return BoxLayout.computeMinWidthVertical(this);
		}

		public int getMinHeight() {
			return BoxLayout.computeMinHeightHorizontal(this);
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
	}
}
