package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twl.utils.TextUtil;
import de.matthiasmann.twl.utils.TintAnimator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_KEYBOARD_FOCUS = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("keyboardFocus");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HAS_OPEN_POPUPS = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hasOpenPopups");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HAS_FOCUSED_CHILD = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hasFocusedChild");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_DISABLED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("disabled");
	private static final int FOCUS_KEY = 15;
	private static final int LAYOUT_INVALID_LOCAL = 1;
	private static final int LAYOUT_INVALID_GLOBAL = 3;
	private Widget parent;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private int layoutInvalid;
	private boolean clip;
	private boolean visible;
	private boolean hasOpenPopup;
	private boolean enabled;
	private boolean locallyEnabled;
	private String theme;
	private ThemeManager themeManager;
	private Image background;
	private Image overlay;
	private Object tooltipContent;
	private Object themeTooltipContent;
	private InputMap inputMap;
	private ActionMap actionMap;
	private TintAnimator tintAnimator;
	private PropertyChangeSupport propertyChangeSupport;
	volatile GUI guiInstance;
	private final AnimationState animState;
	private final boolean sharedAnimState;
	private short borderLeft;
	private short borderTop;
	private short borderRight;
	private short borderBottom;
	private short minWidth;
	private short minHeight;
	private short maxWidth;
	private short maxHeight;
	private ArrayList children;
	private Widget lastChildMouseOver;
	private Widget focusChild;
	private MouseCursor mouseCursor;
	private FocusGainedCause focusGainedCause;
	private boolean focusKeyEnabled;
	private boolean canAcceptKeyboardFocus;
	private boolean depthFocusTraversal;
	private static final ThreadLocal focusTransferInfo = new ThreadLocal();
	private static final boolean WARN_ON_UNHANDLED_ACTION = getSafeBooleanProperty("warnOnUnhandledAction");

	public Widget() {
		this((AnimationState)null, false);
	}

	public Widget(AnimationState animState) {
		this(animState, false);
	}

	public Widget(AnimationState animState, boolean inherit) {
		this.visible = true;
		this.enabled = true;
		this.locallyEnabled = true;
		this.focusKeyEnabled = true;
		this.depthFocusTraversal = true;
		Class clazz = this.getClass();

		do {
			this.theme = clazz.getSimpleName().toLowerCase(Locale.ENGLISH);
			clazz = clazz.getSuperclass();
		} while(this.theme.length() == 0 && clazz != null);

		if(animState != null && !inherit) {
			this.animState = animState;
			this.sharedAnimState = true;
		} else {
			this.animState = new AnimationState(animState);
			this.sharedAnimState = false;
		}

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.createPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.createPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.removePropertyChangeListener(listener);
		}

	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		}

	}

	public boolean hasOpenPopups() {
		return this.hasOpenPopup;
	}

	public final Widget getParent() {
		return this.parent;
	}

	public final Widget getRootWidget() {
		Widget w = this;

		while(true) {
			Widget p = w.parent;
			if(w.parent == null) {
				return w;
			}

			w = p;
		}
	}

	public final GUI getGUI() {
		return this.guiInstance;
	}

	public final boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		if(this.visible != visible) {
			this.visible = visible;
			if(!visible) {
				GUI gui = this.getGUI();
				if(gui != null) {
					gui.widgetHidden(this);
				}

				if(this.parent != null) {
					this.parent.childHidden(this);
				}
			}

			if(this.parent != null) {
				this.parent.childVisibilityChanged(this);
			}
		}

	}

	public final boolean isLocallyEnabled() {
		return this.locallyEnabled;
	}

	public final boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if(this.locallyEnabled != enabled) {
			this.locallyEnabled = enabled;
			this.firePropertyChange("locallyEnabled", !enabled, enabled);
			this.recursivelyEnabledChanged(this.getGUI(), this.parent != null ? this.parent.enabled : true);
		}

	}

	public final int getX() {
		return this.posX;
	}

	public final int getY() {
		return this.posY;
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}

	public final int getRight() {
		return this.posX + this.width;
	}

	public final int getBottom() {
		return this.posY + this.height;
	}

	public final int getInnerX() {
		return this.posX + this.borderLeft;
	}

	public final int getInnerY() {
		return this.posY + this.borderTop;
	}

	public final int getInnerWidth() {
		return Math.max(0, this.width - this.borderLeft - this.borderRight);
	}

	public final int getInnerHeight() {
		return Math.max(0, this.height - this.borderTop - this.borderBottom);
	}

	public final int getInnerRight() {
		return this.posX + Math.max(this.borderLeft, this.width - this.borderRight);
	}

	public final int getInnerBottom() {
		return this.posY + Math.max(this.borderTop, this.height - this.borderBottom);
	}

	public boolean isInside(int x, int y) {
		return x >= this.posX && y >= this.posY && x < this.posX + this.width && y < this.posY + this.height;
	}

	public boolean setPosition(int x, int y) {
		return this.setPositionImpl(x, y);
	}

	public boolean setSize(int width, int height) {
		if(width >= 0 && height >= 0) {
			int oldWidth = this.width;
			int oldHeight = this.height;
			if(oldWidth == width && oldHeight == height) {
				return false;
			} else {
				this.width = width;
				this.height = height;
				this.sizeChanged();
				if(this.propertyChangeSupport != null) {
					this.firePropertyChange("width", oldWidth, width);
					this.firePropertyChange("height", oldHeight, height);
				}

				return true;
			}
		} else {
			throw new IllegalArgumentException("negative size");
		}
	}

	public boolean setInnerSize(int width, int height) {
		return this.setSize(width + this.borderLeft + this.borderRight, height + this.borderTop + this.borderBottom);
	}

	public short getBorderTop() {
		return this.borderTop;
	}

	public short getBorderLeft() {
		return this.borderLeft;
	}

	public short getBorderBottom() {
		return this.borderBottom;
	}

	public short getBorderRight() {
		return this.borderRight;
	}

	public int getBorderHorizontal() {
		return this.borderLeft + this.borderRight;
	}

	public int getBorderVertical() {
		return this.borderTop + this.borderBottom;
	}

	public boolean setBorderSize(int top, int left, int bottom, int right) {
		if(top >= 0 && left >= 0 && bottom >= 0 && right >= 0) {
			if(this.borderTop == top && this.borderBottom == bottom && this.borderLeft == left && this.borderRight == right) {
				return false;
			} else {
				int innerWidth = this.getInnerWidth();
				int innerHeight = this.getInnerHeight();
				int deltaLeft = left - this.borderLeft;
				int deltaTop = top - this.borderTop;
				this.borderLeft = (short)left;
				this.borderTop = (short)top;
				this.borderRight = (short)right;
				this.borderBottom = (short)bottom;
				if(this.children != null && (deltaLeft != 0 || deltaTop != 0)) {
					int i = 0;

					for(int n = this.children.size(); i < n; ++i) {
						adjustChildPosition((Widget)this.children.get(i), deltaLeft, deltaTop);
					}
				}

				this.setInnerSize(innerWidth, innerHeight);
				this.borderChanged();
				return true;
			}
		} else {
			throw new IllegalArgumentException("negative border size");
		}
	}

	public boolean setBorderSize(int horizontal, int vertical) {
		return this.setBorderSize(vertical, horizontal, vertical, horizontal);
	}

	public boolean setBorderSize(int border) {
		return this.setBorderSize(border, border, border, border);
	}

	public boolean setBorderSize(Border border) {
		return border == null ? this.setBorderSize(0, 0, 0, 0) : this.setBorderSize(border.getBorderTop(), border.getBorderLeft(), border.getBorderBottom(), border.getBorderRight());
	}

	public int getMinWidth() {
		return Math.max(this.minWidth, this.borderLeft + this.borderRight);
	}

	public int getMinHeight() {
		return Math.max(this.minHeight, this.borderTop + this.borderBottom);
	}

	public void setMinSize(int width, int height) {
		if(width >= 0 && height >= 0) {
			this.minWidth = (short)Math.min(width, 32767);
			this.minHeight = (short)Math.min(height, 32767);
		} else {
			throw new IllegalArgumentException("negative size");
		}
	}

	public int getPreferredInnerWidth() {
		int right = this.getInnerX();
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				Widget child = (Widget)this.children.get(i);
				right = Math.max(right, child.getRight());
			}
		}

		return right - this.getInnerX();
	}

	public int getPreferredWidth() {
		int prefWidth = this.borderLeft + this.borderRight + this.getPreferredInnerWidth();
		Image bg = this.getBackground();
		if(bg != null) {
			prefWidth = Math.max(prefWidth, bg.getWidth());
		}

		return Math.max(this.minWidth, prefWidth);
	}

	public int getPreferredInnerHeight() {
		int bottom = this.getInnerY();
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				Widget child = (Widget)this.children.get(i);
				bottom = Math.max(bottom, child.getBottom());
			}
		}

		return bottom - this.getInnerY();
	}

	public int getPreferredHeight() {
		int prefHeight = this.borderTop + this.borderBottom + this.getPreferredInnerHeight();
		Image bg = this.getBackground();
		if(bg != null) {
			prefHeight = Math.max(prefHeight, bg.getHeight());
		}

		return Math.max(this.minHeight, prefHeight);
	}

	public int getMaxWidth() {
		return this.maxWidth;
	}

	public int getMaxHeight() {
		return this.maxHeight;
	}

	public void setMaxSize(int width, int height) {
		if(width >= 0 && height >= 0) {
			this.maxWidth = (short)Math.min(width, 32767);
			this.maxHeight = (short)Math.min(height, 32767);
		} else {
			throw new IllegalArgumentException("negative size");
		}
	}

	public static int computeSize(int min, int preferred, int max) {
		if(max > 0) {
			preferred = Math.min(preferred, max);
		}

		return Math.max(min, preferred);
	}

	public void adjustSize() {
		this.setSize(computeSize(this.getMinWidth(), this.getPreferredWidth(), this.getMaxWidth()), computeSize(this.getMinHeight(), this.getPreferredHeight(), this.getMaxHeight()));
		this.validateLayout();
	}

	public void invalidateLayout() {
		if(this.layoutInvalid < 3) {
			this.invalidateLayoutLocally();
			if(this.parent != null) {
				this.layoutInvalid = 3;
				this.parent.childInvalidateLayout(this);
			}
		}

	}

	public void validateLayout() {
		if(this.layoutInvalid != 0) {
			this.layoutInvalid = 0;
			this.layout();
		}

		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				((Widget)this.children.get(i)).validateLayout();
			}
		}

	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		if(theme == null) {
			throw new IllegalArgumentException("theme is null");
		} else {
			if(theme.length() > 0) {
				int slashIdx = theme.lastIndexOf(47);
				if(slashIdx > 0) {
					throw new IllegalArgumentException("\'/\' is only allowed as first character in theme name");
				}

				if(slashIdx < 0) {
					if(theme.indexOf(46) >= 0) {
						throw new IllegalArgumentException("\'.\' is only allowed for absolute theme paths");
					}
				} else if(theme.length() == 1) {
					throw new IllegalArgumentException("\'/\' requires a theme path");
				}

				int i = 0;

				for(int n = theme.length(); i < n; ++i) {
					char ch = theme.charAt(i);
					if(Character.isISOControl(ch) || ch == 42) {
						throw new IllegalArgumentException("invalid character \'" + TextUtil.toPrintableString(ch) + "\' in theme name");
					}
				}
			}

			this.theme = theme;
		}
	}

	public final String getThemePath() {
		return this.getThemePath(0).toString();
	}

	public boolean isClip() {
		return this.clip;
	}

	public void setClip(boolean clip) {
		this.clip = clip;
	}

	public boolean isFocusKeyEnabled() {
		return this.focusKeyEnabled;
	}

	public void setFocusKeyEnabled(boolean focusKeyEnabled) {
		this.focusKeyEnabled = focusKeyEnabled;
	}

	public Image getBackground() {
		return this.background;
	}

	public void setBackground(Image background) {
		this.background = background;
	}

	public Image getOverlay() {
		return this.overlay;
	}

	public void setOverlay(Image overlay) {
		this.overlay = overlay;
	}

	public MouseCursor getMouseCursor() {
		return this.mouseCursor;
	}

	public void setMouseCursor(MouseCursor mouseCursor) {
		this.mouseCursor = mouseCursor;
	}

	public final int getNumChildren() {
		return this.children != null ? this.children.size() : 0;
	}

	public final Widget getChild(int index) throws IndexOutOfBoundsException {
		if(this.children != null) {
			return (Widget)this.children.get(index);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void add(Widget child) {
		this.insertChild(child, this.getNumChildren());
	}

	public void insertChild(Widget child, int index) throws IndexOutOfBoundsException {
		if(child == null) {
			throw new IllegalArgumentException("child is null");
		} else if(child == this) {
			throw new IllegalArgumentException("can\'t add to self");
		} else if(child.parent != null) {
			throw new IllegalArgumentException("child widget already in tree");
		} else {
			if(this.children == null) {
				this.children = new ArrayList();
			}

			if(index >= 0 && index <= this.children.size()) {
				this.children.add(index, child);
				child.parent = this;
				GUI gui = this.getGUI();
				if(gui != null) {
					child.recursivelySetGUI(gui);
				}

				adjustChildPosition(child, this.posX + this.borderLeft, this.posY + this.borderTop);
				child.recursivelyEnabledChanged((GUI)null, this.enabled);
				if(gui != null) {
					child.recursivelyAddToGUI(gui);
				}

				if(this.themeManager != null) {
					child.applyTheme(this.themeManager);
				}

				try {
					this.childAdded(child);
				} catch (Exception exception5) {
					this.getLogger().log(Level.SEVERE, "Exception in childAdded()", exception5);
				}

			} else {
				throw new IndexOutOfBoundsException();
			}
		}
	}

	public final int getChildIndex(Widget child) {
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				if(this.children.get(i) == child) {
					return i;
				}
			}
		}

		return -1;
	}

	public boolean removeChild(Widget child) {
		int idx = this.getChildIndex(child);
		if(idx >= 0) {
			this.removeChild(idx);
			return true;
		} else {
			return false;
		}
	}

	public Widget removeChild(int index) throws IndexOutOfBoundsException {
		if(this.children != null) {
			Widget child = (Widget)this.children.remove(index);
			this.unparentChild(child);
			if(this.lastChildMouseOver == child) {
				this.lastChildMouseOver = null;
			}

			if(this.focusChild == child) {
				this.focusChild = null;
			}

			this.childRemoved(child);
			return child;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void removeAllChildren() {
		if(this.children != null) {
			this.focusChild = null;
			this.lastChildMouseOver = null;
			int gui = 0;

			for(int n = this.children.size(); gui < n; ++gui) {
				Widget child = (Widget)this.children.get(gui);
				this.unparentChild(child);
			}

			this.children.clear();
			if(this.hasOpenPopup) {
				GUI gUI4 = this.getGUI();

				assert gUI4 != null;

				this.recalcOpenPopups(gUI4);
			}

			this.allChildrenRemoved();
		}

	}

	public void destroy() {
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				((Widget)this.children.get(i)).destroy();
			}
		}

	}

	public boolean canAcceptKeyboardFocus() {
		return this.canAcceptKeyboardFocus;
	}

	public void setCanAcceptKeyboardFocus(boolean canAcceptKeyboardFocus) {
		this.canAcceptKeyboardFocus = canAcceptKeyboardFocus;
	}

	public boolean isDepthFocusTraversal() {
		return this.depthFocusTraversal;
	}

	public void setDepthFocusTraversal(boolean depthFocusTraversal) {
		this.depthFocusTraversal = depthFocusTraversal;
	}

	public boolean requestKeyboardFocus() {
		if(this.parent != null && this.visible) {
			if(this.parent.focusChild == this) {
				return true;
			} else {
				boolean clear = this.focusTransferStart();

				boolean z3;
				try {
					z3 = this.parent.requestKeyboardFocus(this);
				} finally {
					this.focusTransferClear(clear);
				}

				return z3;
			}
		} else {
			return false;
		}
	}

	public void giveupKeyboardFocus() {
		if(this.parent != null && this.parent.focusChild == this) {
			this.parent.requestKeyboardFocus((Widget)null);
		}

	}

	public boolean hasKeyboardFocus() {
		return this.parent != null ? this.parent.focusChild == this : false;
	}

	public boolean focusNextChild() {
		return this.moveFocus(true, 1);
	}

	public boolean focusPrevChild() {
		return this.moveFocus(true, -1);
	}

	public boolean focusFirstChild() {
		return this.moveFocus(false, 1);
	}

	public boolean focusLastChild() {
		return this.moveFocus(false, -1);
	}

	public AnimationState getAnimationState() {
		return this.animState;
	}

	public boolean hasSharedAnimationState() {
		return this.sharedAnimState;
	}

	public TintAnimator getTintAnimator() {
		return this.tintAnimator;
	}

	public void setTintAnimator(TintAnimator tintAnimator) {
		this.tintAnimator = tintAnimator;
	}

	public Object getTooltipContent() {
		return this.tooltipContent;
	}

	public void setTooltipContent(Object tooltipContent) {
		this.tooltipContent = tooltipContent;
		this.updateTooltip();
	}

	public InputMap getInputMap() {
		return this.inputMap;
	}

	public void setInputMap(InputMap inputMap) {
		this.inputMap = inputMap;
	}

	public ActionMap getActionMap() {
		return this.actionMap;
	}

	public ActionMap getOrCreateActionMap() {
		if(this.actionMap == null) {
			this.actionMap = new ActionMap();
		}

		return this.actionMap;
	}

	public void setActionMap(ActionMap actionMap) {
		this.actionMap = actionMap;
	}

	public Widget getWidgetAt(int x, int y) {
		Widget child = this.getChildAt(x, y);
		return child != null ? child.getWidgetAt(x, y) : this;
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		this.applyThemeBackground(themeInfo);
		this.applyThemeOverlay(themeInfo);
		this.applyThemeBorder(themeInfo);
		this.applyThemeMinSize(themeInfo);
		this.applyThemeMaxSize(themeInfo);
		this.applyThemeMouseCursor(themeInfo);
		this.applyThemeInputMap(themeInfo);
		this.applyThemeTooltip(themeInfo);
		this.invalidateLayout();
	}

	protected void applyThemeBackground(ThemeInfo themeInfo) {
		this.setBackground(themeInfo.getImage("background"));
	}

	protected void applyThemeOverlay(ThemeInfo themeInfo) {
		this.setOverlay(themeInfo.getImage("overlay"));
	}

	protected void applyThemeBorder(ThemeInfo themeInfo) {
		this.setBorderSize((Border)themeInfo.getParameterValue("border", false, Border.class));
	}

	protected void applyThemeMinSize(ThemeInfo themeInfo) {
		this.setMinSize(themeInfo.getParameter("minWidth", 0), themeInfo.getParameter("minHeight", 0));
	}

	protected void applyThemeMaxSize(ThemeInfo themeInfo) {
		this.setMaxSize(themeInfo.getParameter("maxWidth", 32767), themeInfo.getParameter("maxHeight", 32767));
	}

	protected void applyThemeMouseCursor(ThemeInfo themeInfo) {
		this.setMouseCursor(themeInfo.getMouseCursor("mouseCursor"));
	}

	protected void applyThemeInputMap(ThemeInfo themeInfo) {
		this.setInputMap((InputMap)themeInfo.getParameterValue("inputMap", false, InputMap.class));
	}

	protected void applyThemeTooltip(ThemeInfo themeInfo) {
		this.themeTooltipContent = themeInfo.getParameterValue("tooltip", false);
		if(this.tooltipContent == null) {
			this.updateTooltip();
		}

	}

	protected Object getThemeTooltipContent() {
		return this.themeTooltipContent;
	}

	protected Object getTooltipContentAt(int mouseX, int mouseY) {
		Object content = this.getTooltipContent();
		if(content == null) {
			content = this.getThemeTooltipContent();
		}

		return content;
	}

	protected void updateTooltip() {
		GUI gui = this.getGUI();
		if(gui != null) {
			gui.requestToolTipUpdate(this);
		}

	}

	protected void addActionMapping(String action, String methodName, Object... params) {
		this.getOrCreateActionMap().addMapping(action, (Object)this, (String)methodName, params, 1);
	}

	public void reapplyTheme() {
		if(this.themeManager != null) {
			this.applyTheme(this.themeManager);
		}

	}

	protected boolean isMouseInside(Event evt) {
		return this.isInside(evt.getMouseX(), evt.getMouseY());
	}

	protected boolean handleEvent(Event evt) {
		return evt.isKeyEvent() ? this.handleKeyEvent(evt) : false;
	}

	protected boolean handleKeyStrokeAction(String action, Event event) {
		return this.actionMap != null ? this.actionMap.invoke(action, event) : false;
	}

	protected void moveChild(int from, int to) {
		if(this.children == null) {
			throw new IndexOutOfBoundsException();
		} else if(to >= 0 && to < this.children.size()) {
			if(from >= 0 && from < this.children.size()) {
				Widget child = (Widget)this.children.remove(from);
				this.children.add(to, child);
			} else {
				throw new IndexOutOfBoundsException("from");
			}
		} else {
			throw new IndexOutOfBoundsException("to");
		}
	}

	protected boolean requestKeyboardFocus(Widget child) {
		if(child != null && child.parent != this) {
			throw new IllegalArgumentException("not a direct child");
		} else {
			if(this.focusChild != child) {
				if(child == null) {
					this.recursivelyChildFocusLost(this.focusChild);
					this.focusChild = null;
					this.keyboardFocusChildChanged((Widget)null);
				} else {
					boolean clear = this.focusTransferStart();

					try {
						FocusGainedCause cause = this.focusGainedCause;
						if(cause == null) {
							this.focusGainedCause = FocusGainedCause.CHILD_FOCUSED;
						}

						try {
							if(!this.requestKeyboardFocus()) {
								return false;
							}
						} finally {
							this.focusGainedCause = cause;
						}

						this.recursivelyChildFocusLost(this.focusChild);
						this.focusChild = child;
						this.keyboardFocusChildChanged(child);
						if(!child.sharedAnimState) {
							child.animState.setAnimationState(STATE_KEYBOARD_FOCUS, true);
						}

						cause = child.focusGainedCause;
						Widget[] fti = (Widget[])focusTransferInfo.get();
						child.keyboardFocusGained(cause != null ? cause : FocusGainedCause.MANUAL, fti != null ? fti[0] : null);
					} finally {
						this.focusTransferClear(clear);
					}
				}
			}

			if(!this.sharedAnimState) {
				this.animState.setAnimationState(STATE_HAS_FOCUSED_CHILD, this.focusChild != null);
			}

			return this.focusChild != null;
		}
	}

	protected void beforeRemoveFromGUI(GUI gui) {
	}

	protected void afterAddToGUI(GUI gui) {
	}

	protected void layout() {
	}

	protected void positionChanged() {
	}

	protected void sizeChanged() {
		this.invalidateLayoutLocally();
	}

	protected void borderChanged() {
		this.invalidateLayout();
	}

	protected void childInvalidateLayout(Widget child) {
		this.invalidateLayout();
	}

	protected void childAdded(Widget child) {
		this.invalidateLayout();
	}

	protected void childRemoved(Widget exChild) {
		this.invalidateLayout();
	}

	protected void allChildrenRemoved() {
		this.invalidateLayout();
	}

	protected void childVisibilityChanged(Widget child) {
	}

	protected void keyboardFocusChildChanged(Widget child) {
	}

	protected void keyboardFocusLost() {
	}

	protected void keyboardFocusGained() {
	}

	protected void keyboardFocusGained(FocusGainedCause cause, Widget previousWidget) {
		this.keyboardFocusGained();
	}

	protected void widgetDisabled() {
	}

	protected void paint(GUI gui) {
		this.paintBackground(gui);
		this.paintWidget(gui);
		this.paintChildren(gui);
		this.paintOverlay(gui);
	}

	protected void paintWidget(GUI gui) {
	}

	protected void paintBackground(GUI gui) {
		Image bgImage = this.getBackground();
		if(bgImage != null) {
			bgImage.draw(this.getAnimationState(), this.posX, this.posY, this.width, this.height);
		}

	}

	protected void paintOverlay(GUI gui) {
		Image ovImage = this.getOverlay();
		if(ovImage != null) {
			ovImage.draw(this.getAnimationState(), this.posX, this.posY, this.width, this.height);
		}

	}

	protected void paintChildren(GUI gui) {
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				Widget child = (Widget)this.children.get(i);
				if(child.visible) {
					child.drawWidget(gui);
				}
			}
		}

	}

	protected void paintChild(GUI gui, Widget child) {
		if(child.parent != this) {
			throw new IllegalArgumentException("can only render direct children");
		} else {
			child.drawWidget(gui);
		}
	}

	protected final void invalidateLayoutLocally() {
		if(this.layoutInvalid < 1) {
			this.layoutInvalid = 1;
			GUI gui = this.getGUI();
			if(gui != null) {
				gui.hasInvalidLayouts = true;
			}
		}

	}

	protected void layoutChildFullInnerArea(Widget child) {
		if(child.parent != this) {
			throw new IllegalArgumentException("can only layout direct children");
		} else {
			child.setPosition(this.getInnerX(), this.getInnerY());
			child.setSize(this.getInnerWidth(), this.getInnerHeight());
		}
	}

	protected void layoutChildrenFullInnerArea() {
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				this.layoutChildFullInnerArea((Widget)this.children.get(i));
			}
		}

	}

	protected List getKeyboardFocusOrder() {
		return this.children == null ? Collections.emptyList() : Collections.unmodifiableList(this.children);
	}

	private int collectFocusOrderList(ArrayList list) {
		int idx = -1;
		Iterator iterator4 = this.getKeyboardFocusOrder().iterator();

		while(iterator4.hasNext()) {
			Widget child = (Widget)iterator4.next();
			if(child.visible && child.isEnabled()) {
				if(child.canAcceptKeyboardFocus) {
					if(child == this.focusChild) {
						idx = list.size();
					}

					list.add(child);
				}

				if(child.depthFocusTraversal) {
					int subIdx = child.collectFocusOrderList(list);
					if(subIdx != -1) {
						idx = subIdx;
					}
				}
			}
		}

		return idx;
	}

	private boolean moveFocus(boolean relative, int dir) {
		ArrayList focusList = new ArrayList();
		int curIndex = this.collectFocusOrderList(focusList);
		if(focusList.isEmpty()) {
			return false;
		} else {
			if(dir < 0) {
				label62: {
					if(relative) {
						--curIndex;
						if(curIndex >= 0) {
							break label62;
						}
					}

					curIndex = focusList.size() - 1;
				}
			} else {
				label58: {
					if(relative) {
						++curIndex;
						if(curIndex < focusList.size()) {
							break label58;
						}
					}

					curIndex = 0;
				}
			}

			Widget widget = (Widget)focusList.get(curIndex);

			try {
				widget.focusGainedCause = FocusGainedCause.FOCUS_KEY;
				widget.requestKeyboardFocus((Widget)null);
				widget.requestKeyboardFocus();
			} finally {
				widget.focusGainedCause = null;
			}

			return true;
		}
	}

	private boolean focusTransferStart() {
		Widget[] fti = (Widget[])focusTransferInfo.get();
		if(fti != null) {
			return false;
		} else {
			Widget root = this.getRootWidget();

			Widget w;
			for(w = root; w.focusChild != null; w = w.focusChild) {
			}

			if(w == root) {
				w = null;
			}

			focusTransferInfo.set(new Widget[]{w});
			return true;
		}
	}

	private void focusTransferClear(boolean clear) {
		if(clear) {
			focusTransferInfo.set((Object)null);
		}

	}

	protected final Widget getChildAt(int x, int y) {
		if(this.children != null) {
			int i = this.children.size();

			while(i-- > 0) {
				Widget child = (Widget)this.children.get(i);
				if(child.visible && child.isInside(x, y)) {
					return child;
				}
			}
		}

		return null;
	}

	protected void updateTintAnimation() {
		this.tintAnimator.update();
	}

	protected final void firePropertyChange(PropertyChangeEvent evt) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.firePropertyChange(evt);
		}

	}

	protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	protected final void firePropertyChange(String propertyName, int oldValue, int newValue) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if(this.propertyChangeSupport != null) {
			this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	private void unparentChild(Widget child) {
		GUI gui = this.getGUI();
		if(child.hasOpenPopup) {
			assert gui != null;

			gui.closePopupFromWidgets(child);
		}

		this.recursivelyChildFocusLost(child);
		if(gui != null) {
			child.recursivelyRemoveFromGUI(gui);
		}

		child.recursivelyClearGUI(gui);
		child.parent = null;

		try {
			child.destroy();
		} catch (Exception exception4) {
			this.getLogger().log(Level.SEVERE, "Exception in destroy()", exception4);
		}

		adjustChildPosition(child, -this.posX, -this.posY);
		child.recursivelyEnabledChanged((GUI)null, child.locallyEnabled);
	}

	private void recursivelySetGUI(GUI gui) {
		assert this.guiInstance == null : "guiInstance must be null";

		this.guiInstance = gui;
		if(this.children != null) {
			int i = this.children.size();

			while(i-- > 0) {
				((Widget)this.children.get(i)).recursivelySetGUI(gui);
			}
		}

	}

	private void recursivelyAddToGUI(GUI gui) {
		assert this.guiInstance == gui : "guiInstance must be equal to gui";

		if(this.layoutInvalid != 0) {
			gui.hasInvalidLayouts = true;
		}

		if(!this.sharedAnimState) {
			this.animState.setGUI(gui);
		}

		try {
			this.afterAddToGUI(gui);
		} catch (Exception exception3) {
			this.getLogger().log(Level.SEVERE, "Exception in afterAddToGUI()", exception3);
		}

		if(this.children != null) {
			int i = this.children.size();

			while(i-- > 0) {
				((Widget)this.children.get(i)).recursivelyAddToGUI(gui);
			}
		}

	}

	private void recursivelyClearGUI(GUI gui) {
		assert this.guiInstance == gui : "guiInstance must be null";

		this.guiInstance = null;
		if(this.children != null) {
			int i = this.children.size();

			while(i-- > 0) {
				((Widget)this.children.get(i)).recursivelyClearGUI(gui);
			}
		}

	}

	private void recursivelyRemoveFromGUI(GUI gui) {
		assert this.guiInstance == gui : "guiInstance must be equal to gui";

		if(this.children != null) {
			int ex = this.children.size();

			while(ex-- > 0) {
				((Widget)this.children.get(ex)).recursivelyRemoveFromGUI(gui);
			}
		}

		this.focusChild = null;
		if(!this.sharedAnimState) {
			this.animState.setGUI((GUI)null);
		}

		try {
			this.beforeRemoveFromGUI(gui);
		} catch (Exception exception3) {
			this.getLogger().log(Level.SEVERE, "Exception in beforeRemoveFromGUI()", exception3);
		}

	}

	private void recursivelyChildFocusLost(Widget w) {
		while(w != null) {
			Widget next = w.focusChild;
			if(!w.sharedAnimState) {
				w.animState.setAnimationState(STATE_KEYBOARD_FOCUS, false);
			}

			try {
				w.keyboardFocusLost();
			} catch (Exception exception4) {
				this.getLogger().log(Level.SEVERE, "Exception in keyboardFocusLost()", exception4);
			}

			w.focusChild = null;
			w = next;
		}

	}

	private void recursivelyEnabledChanged(GUI gui, boolean enabled) {
		enabled &= this.locallyEnabled;
		if(this.enabled != enabled) {
			this.enabled = enabled;
			if(!this.sharedAnimState) {
				this.getAnimationState().setAnimationState(STATE_DISABLED, !enabled);
			}

			if(!enabled) {
				if(gui != null) {
					gui.widgetDisabled(this);
				}

				try {
					this.widgetDisabled();
				} catch (Exception exception7) {
					this.getLogger().log(Level.SEVERE, "Exception in widgetDisabled()", exception7);
				}

				try {
					this.giveupKeyboardFocus();
				} catch (Exception exception6) {
					this.getLogger().log(Level.SEVERE, "Exception in giveupKeyboardFocus()", exception6);
				}
			}

			try {
				this.firePropertyChange("enabled", !enabled, enabled);
			} catch (Exception exception5) {
				this.getLogger().log(Level.SEVERE, "Exception in firePropertyChange(\"enabled\")", exception5);
			}

			if(this.children != null) {
				int i = this.children.size();

				while(i-- > 0) {
					Widget child = (Widget)this.children.get(i);
					child.recursivelyEnabledChanged(gui, enabled);
				}
			}
		}

	}

	private void childHidden(Widget child) {
		if(this.focusChild == child) {
			this.recursivelyChildFocusLost(this.focusChild);
			this.focusChild = null;
		}

		if(this.lastChildMouseOver == child) {
			this.lastChildMouseOver = null;
		}

	}

	final void setOpenPopup(GUI gui, boolean hasOpenPopup) {
		if(this.hasOpenPopup != hasOpenPopup) {
			this.hasOpenPopup = hasOpenPopup;
			if(!this.sharedAnimState) {
				this.getAnimationState().setAnimationState(STATE_HAS_OPEN_POPUPS, hasOpenPopup);
			}

			if(this.parent != null) {
				if(hasOpenPopup) {
					this.parent.setOpenPopup(gui, true);
				} else {
					this.parent.recalcOpenPopups(gui);
				}
			}
		}

	}

	final void recalcOpenPopups(GUI gui) {
		if(gui.hasOpenPopups(this)) {
			this.setOpenPopup(gui, true);
		} else {
			if(this.children != null) {
				int i = this.children.size();

				while(i-- > 0) {
					if(((Widget)this.children.get(i)).hasOpenPopup) {
						this.setOpenPopup(gui, true);
						return;
					}
				}
			}

			this.setOpenPopup(gui, false);
		}
	}

	final void drawWidget(GUI gui) {
		if(this.tintAnimator != null && this.tintAnimator.hasTint()) {
			this.drawWidgetTint(gui);
		} else if(this.clip) {
			this.drawWidgetClip(gui);
		} else {
			this.paint(gui);
		}
	}

	private void drawWidgetTint(GUI gui) {
		if(this.tintAnimator.isFadeActive()) {
			this.updateTintAnimation();
		}

		Renderer renderer = gui.getRenderer();
		this.tintAnimator.paintWithTint(renderer);

		try {
			if(this.clip) {
				this.drawWidgetClip(gui);
			} else {
				this.paint(gui);
			}
		} finally {
			renderer.popGlobalTintColor();
		}

	}

	private void drawWidgetClip(GUI gui) {
		gui.clipEnter(this.posX, this.posY, this.width, this.height);

		try {
			this.paint(gui);
		} finally {
			gui.clipLeave();
		}

	}

	Widget getWidgetUnderMouse() {
		if(!this.visible) {
			return null;
		} else {
			Widget w;
			for(w = this; w.lastChildMouseOver != null && w.visible; w = w.lastChildMouseOver) {
			}

			return w;
		}
	}

	private static void adjustChildPosition(Widget child, int deltaX, int deltaY) {
		child.setPositionImpl(child.posX + deltaX, child.posY + deltaY);
	}

	final boolean setPositionImpl(int x, int y) {
		int deltaX = x - this.posX;
		int deltaY = y - this.posY;
		if(deltaX == 0 && deltaY == 0) {
			return false;
		} else {
			this.posX = x;
			this.posY = y;
			if(this.children != null) {
				int i = 0;

				for(int n = this.children.size(); i < n; ++i) {
					adjustChildPosition((Widget)this.children.get(i), deltaX, deltaY);
				}
			}

			this.positionChanged();
			if(this.propertyChangeSupport != null) {
				this.firePropertyChange("x", x - deltaX, x);
				this.firePropertyChange("y", y - deltaY, y);
			}

			return true;
		}
	}

	void applyTheme(ThemeManager themeManager) {
		this.themeManager = themeManager;
		String themePath = this.getThemePath();
		if(themePath.length() == 0) {
			if(this.children != null) {
				int i11 = 0;

				for(int i12 = this.children.size(); i11 < i12; ++i11) {
					((Widget)this.children.get(i11)).applyTheme(themeManager);
				}
			}

		} else {
			DebugHook hook = DebugHook.getDebugHook();
			hook.beforeApplyTheme(this);
			ThemeInfo themeInfo = null;

			try {
				themeInfo = themeManager.findThemeInfo(themePath);
				if(themeInfo != null && this.theme.length() > 0) {
					try {
						this.applyTheme(themeInfo);
					} catch (Exception exception9) {
						this.getLogger().log(Level.SEVERE, "Exception in applyTheme()", exception9);
					}
				}
			} finally {
				hook.afterApplyTheme(this);
			}

			this.applyThemeToChildren(themeManager, themeInfo, hook);
		}
	}

	public static boolean isAbsoluteTheme(String theme) {
		return theme.length() > 1 && theme.charAt(0) == 47;
	}

	private void applyThemeImpl(ThemeManager themeManager, ThemeInfo themeInfo, DebugHook hook) {
		this.themeManager = themeManager;
		if(this.theme.length() > 0) {
			hook.beforeApplyTheme(this);

			try {
				if(isAbsoluteTheme(this.theme)) {
					themeInfo = themeManager.findThemeInfo(this.theme.substring(1));
				} else {
					themeInfo = themeInfo.getChildTheme(this.theme);
				}

				if(themeInfo != null) {
					try {
						this.applyTheme(themeInfo);
					} catch (Exception exception8) {
						this.getLogger().log(Level.SEVERE, "Exception in applyTheme()", exception8);
					}
				}
			} finally {
				hook.afterApplyTheme(this);
			}
		}

		this.applyThemeToChildren(themeManager, themeInfo, hook);
	}

	private void applyThemeToChildren(ThemeManager themeManager, ThemeInfo themeInfo, DebugHook hook) {
		if(this.children != null && themeInfo != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				Widget child = (Widget)this.children.get(i);
				child.applyThemeImpl(themeManager, themeInfo, hook);
			}
		}

	}

	private StringBuilder getThemePath(int length) {
		length += this.theme.length();
		boolean abs = isAbsoluteTheme(this.theme);
		StringBuilder sb;
		if(this.parent != null && !abs) {
			sb = this.parent.getThemePath(length + 1);
			if(this.theme.length() > 0 && sb.length() > 0) {
				sb.append('.');
			}
		} else {
			sb = new StringBuilder(length);
		}

		return abs ? sb.append(this.theme.substring(1)) : sb.append(this.theme);
	}

	Widget routeMouseEvent(Event evt) {
		assert !evt.isMouseDragEvent();

		if(this.children != null) {
			int i = this.children.size();

			label284:
			while(true) {
				Widget child;
				do {
					do {
						do {
							if(i-- <= 0) {
								break label284;
							}

							child = (Widget)this.children.get(i);
						} while(!child.visible);
					} while(!child.isMouseInside(evt));
				} while(!this.setMouseOverChild(child, evt));

				if(evt.getType() == Event.Type.MOUSE_ENTERED || evt.getType() == Event.Type.MOUSE_EXITED) {
					return child;
				}

				Widget result = child.routeMouseEvent(evt);
				if(result != null) {
					if(evt.getType() == Event.Type.MOUSE_BTNDOWN && this.focusChild != child) {
						try {
							child.focusGainedCause = FocusGainedCause.MOUSE_BTNDOWN;
							if(child.isEnabled() && child.canAcceptKeyboardFocus()) {
								this.requestKeyboardFocus(child);
							} else {
								this.requestKeyboardFocus((Widget)null);
							}
						} finally {
							child.focusGainedCause = null;
						}
					}

					return result;
				}
			}
		}

		if(evt.getType() == Event.Type.MOUSE_BTNDOWN && this.isEnabled() && this.canAcceptKeyboardFocus()) {
			try {
				this.focusGainedCause = FocusGainedCause.MOUSE_BTNDOWN;
				if(this.focusChild == null) {
					this.requestKeyboardFocus();
				} else {
					this.requestKeyboardFocus((Widget)null);
				}
			} finally {
				this.focusGainedCause = null;
			}
		}

		if(evt.getType() != Event.Type.MOUSE_WHEEL) {
			this.setMouseOverChild((Widget)null, evt);
		}

		return !this.isEnabled() && isMouseAction(evt) ? this : (this.handleEvent(evt) ? this : null);
	}

	static boolean isMouseAction(Event evt) {
		Event.Type type = evt.getType();
		return type == Event.Type.MOUSE_BTNDOWN || type == Event.Type.MOUSE_BTNUP || type == Event.Type.MOUSE_CLICKED || type == Event.Type.MOUSE_DRAGGED;
	}

	void routePopupEvent(Event evt) {
		this.handleEvent(evt);
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				((Widget)this.children.get(i)).routePopupEvent(evt);
			}
		}

	}

	static boolean getSafeBooleanProperty(String name) {
		try {
			return Boolean.getBoolean(name);
		} catch (AccessControlException accessControlException2) {
			return false;
		}
	}

	private boolean handleKeyEvent(Event evt) {
		if(this.children != null) {
			if(this.focusKeyEnabled && evt.isKeyEvent() && evt.getKeyCode() == 15 && (evt.getModifiers() & 1590) == 0) {
				this.handleFocusKeyEvent(evt);
				return true;
			}

			if(this.focusChild != null && this.focusChild.isVisible() && this.focusChild.handleEvent(evt)) {
				return true;
			}
		}

		if(this.inputMap != null) {
			String action = this.inputMap.mapEvent(evt);
			if(action != null) {
				if(this.handleKeyStrokeAction(action, evt)) {
					return true;
				}

				if(WARN_ON_UNHANDLED_ACTION) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unhandled action \'\'{0}\'\' for class \'\'{1}\'\'", new Object[]{action, this.getClass().getName()});
				}
			}
		}

		return false;
	}

	private void handleFocusKeyEvent(Event evt) {
		if(evt.isKeyPressedEvent()) {
			if((evt.getModifiers() & 9) != 0) {
				this.focusPrevChild();
			} else {
				this.focusNextChild();
			}
		}

	}

	boolean setMouseOverChild(Widget child, Event evt) {
		if(this.lastChildMouseOver != child) {
			if(child != null) {
				Widget result = child.routeMouseEvent(evt.createSubEvent(Event.Type.MOUSE_ENTERED));
				if(result == null) {
					return false;
				}
			}

			if(this.lastChildMouseOver != null) {
				this.lastChildMouseOver.routeMouseEvent(evt.createSubEvent(Event.Type.MOUSE_EXITED));
			}

			this.lastChildMouseOver = child;
		}

		return true;
	}

	void collectLayoutLoop(ArrayList result) {
		if(this.layoutInvalid != 0) {
			result.add(this);
		}

		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				((Widget)this.children.get(i)).collectLayoutLoop(result);
			}
		}

	}

	private PropertyChangeSupport createPropertyChangeSupport() {
		if(this.propertyChangeSupport == null) {
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		}

		return this.propertyChangeSupport;
	}

	private Logger getLogger() {
		return Logger.getLogger(Widget.class.getName());
	}
}
