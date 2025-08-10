package de.matthiasmann.twl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DialogLayout extends Widget {
	public static final int SMALL_GAP = -1;
	public static final int MEDIUM_GAP = -2;
	public static final int LARGE_GAP = -3;
	public static final int DEFAULT_GAP = -4;
	private static final boolean DEBUG_LAYOUT_GROUPS = Widget.getSafeBooleanProperty("debugLayoutGroups");
	protected Dimension smallGap;
	protected Dimension mediumGap;
	protected Dimension largeGap;
	protected Dimension defaultGap;
	protected ParameterMap namedGaps;
	protected boolean addDefaultGaps = true;
	protected boolean includeInvisibleWidgets = true;
	protected boolean redoDefaultGaps;
	protected boolean isPrepared;
	protected boolean blockInvalidateLayoutTree;
	protected boolean warnOnIncomplete;
	private DialogLayout.Group horz;
	private DialogLayout.Group vert;
	Throwable debugStackTrace;
	final HashMap widgetSprings = new HashMap();
	static final int AXIS_X = 0;
	static final int AXIS_Y = 1;
	static final DialogLayout.Gap NO_GAP = new DialogLayout.Gap(0, 0, 32767);

	public DialogLayout() {
		this.collectDebugStack();
	}

	public DialogLayout.Group getHorizontalGroup() {
		return this.horz;
	}

	public void setHorizontalGroup(DialogLayout.Group g) {
		if(g != null) {
			g.checkGroup(this);
		}

		this.horz = g;
		this.collectDebugStack();
		this.layoutGroupsChanged();
	}

	public DialogLayout.Group getVerticalGroup() {
		return this.vert;
	}

	public void setVerticalGroup(DialogLayout.Group g) {
		if(g != null) {
			g.checkGroup(this);
		}

		this.vert = g;
		this.collectDebugStack();
		this.layoutGroupsChanged();
	}

	public Dimension getSmallGap() {
		return this.smallGap;
	}

	public void setSmallGap(Dimension smallGap) {
		this.smallGap = smallGap;
		this.maybeInvalidateLayoutTree();
	}

	public Dimension getMediumGap() {
		return this.mediumGap;
	}

	public void setMediumGap(Dimension mediumGap) {
		this.mediumGap = mediumGap;
		this.maybeInvalidateLayoutTree();
	}

	public Dimension getLargeGap() {
		return this.largeGap;
	}

	public void setLargeGap(Dimension largeGap) {
		this.largeGap = largeGap;
		this.maybeInvalidateLayoutTree();
	}

	public Dimension getDefaultGap() {
		return this.defaultGap;
	}

	public void setDefaultGap(Dimension defaultGap) {
		this.defaultGap = defaultGap;
		this.maybeInvalidateLayoutTree();
	}

	public boolean isAddDefaultGaps() {
		return this.addDefaultGaps;
	}

	public void setAddDefaultGaps(boolean addDefaultGaps) {
		this.addDefaultGaps = addDefaultGaps;
	}

	public void removeDefaultGaps() {
		if(this.horz != null && this.vert != null) {
			this.horz.removeDefaultGaps();
			this.vert.removeDefaultGaps();
			this.maybeInvalidateLayoutTree();
		}

	}

	public void addDefaultGaps() {
		if(this.horz != null && this.vert != null) {
			this.horz.addDefaultGap();
			this.vert.addDefaultGap();
			this.maybeInvalidateLayoutTree();
		}

	}

	public boolean isIncludeInvisibleWidgets() {
		return this.includeInvisibleWidgets;
	}

	public void setIncludeInvisibleWidgets(boolean includeInvisibleWidgets) {
		if(this.includeInvisibleWidgets != includeInvisibleWidgets) {
			this.includeInvisibleWidgets = includeInvisibleWidgets;
			this.layoutGroupsChanged();
		}

	}

	private void collectDebugStack() {
		this.warnOnIncomplete = true;
		if(DEBUG_LAYOUT_GROUPS) {
			this.debugStackTrace = (new Throwable("DialogLayout created/used here")).fillInStackTrace();
		}

	}

	private void warnOnIncomplete() {
		this.warnOnIncomplete = false;
		getLogger().log(Level.WARNING, "Dialog layout has incomplete state", this.debugStackTrace);
	}

	static Logger getLogger() {
		return Logger.getLogger(DialogLayout.class.getName());
	}

	protected void applyThemeDialogLayout(ThemeInfo themeInfo) {
		try {
			this.blockInvalidateLayoutTree = true;
			this.setSmallGap((Dimension)themeInfo.getParameterValue("smallGap", true, Dimension.class, Dimension.ZERO));
			this.setMediumGap((Dimension)themeInfo.getParameterValue("mediumGap", true, Dimension.class, Dimension.ZERO));
			this.setLargeGap((Dimension)themeInfo.getParameterValue("largeGap", true, Dimension.class, Dimension.ZERO));
			this.setDefaultGap((Dimension)themeInfo.getParameterValue("defaultGap", true, Dimension.class, Dimension.ZERO));
			this.namedGaps = themeInfo.getParameterMap("namedGaps");
		} finally {
			this.blockInvalidateLayoutTree = false;
		}

		this.invalidateLayout();
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeDialogLayout(themeInfo);
	}

	public int getMinWidth() {
		if(this.horz != null) {
			this.prepare();
			return this.horz.getMinSize(0) + this.getBorderHorizontal();
		} else {
			return super.getMinWidth();
		}
	}

	public int getMinHeight() {
		if(this.vert != null) {
			this.prepare();
			return this.vert.getMinSize(1) + this.getBorderVertical();
		} else {
			return super.getMinHeight();
		}
	}

	public int getPreferredInnerWidth() {
		if(this.horz != null) {
			this.prepare();
			return this.horz.getPrefSize(0);
		} else {
			return super.getPreferredInnerWidth();
		}
	}

	public int getPreferredInnerHeight() {
		if(this.vert != null) {
			this.prepare();
			return this.vert.getPrefSize(1);
		} else {
			return super.getPreferredInnerHeight();
		}
	}

	public void adjustSize() {
		if(this.horz != null && this.vert != null) {
			this.prepare();
			int minWidth = this.horz.getMinSize(0);
			int minHeight = this.vert.getMinSize(1);
			int prefWidth = this.horz.getPrefSize(0);
			int prefHeight = this.vert.getPrefSize(1);
			int maxWidth = this.getMaxWidth();
			int maxHeight = this.getMaxHeight();
			this.setInnerSize(computeSize(minWidth, prefWidth, maxWidth), computeSize(minHeight, prefHeight, maxHeight));
			this.doLayout();
		}

	}

	protected void layout() {
		if(this.horz != null && this.vert != null) {
			this.prepare();
			this.doLayout();
		} else if(this.warnOnIncomplete) {
			this.warnOnIncomplete();
		}

	}

	protected void prepare() {
		if(this.redoDefaultGaps) {
			if(this.addDefaultGaps) {
				try {
					this.blockInvalidateLayoutTree = true;
					this.removeDefaultGaps();
					this.addDefaultGaps();
				} finally {
					this.blockInvalidateLayoutTree = false;
				}
			}

			this.redoDefaultGaps = false;
			this.isPrepared = false;
		}

		if(!this.isPrepared) {
			Iterator iterator2 = this.widgetSprings.values().iterator();

			while(true) {
				DialogLayout.WidgetSpring s;
				do {
					if(!iterator2.hasNext()) {
						this.isPrepared = true;
						return;
					}

					s = (DialogLayout.WidgetSpring)iterator2.next();
				} while(!this.includeInvisibleWidgets && !s.w.isVisible());

				s.prepare();
			}
		}
	}

	protected void doLayout() {
		this.horz.setSize(0, this.getInnerX(), this.getInnerWidth());
		this.vert.setSize(1, this.getInnerY(), this.getInnerHeight());

		try {
			Iterator iterator2 = this.widgetSprings.values().iterator();

			while(true) {
				DialogLayout.WidgetSpring ex;
				do {
					if(!iterator2.hasNext()) {
						return;
					}

					ex = (DialogLayout.WidgetSpring)iterator2.next();
				} while(!this.includeInvisibleWidgets && !ex.w.isVisible());

				ex.apply();
			}
		} catch (IllegalStateException illegalStateException3) {
			if(this.debugStackTrace != null && illegalStateException3.getCause() == null) {
				illegalStateException3.initCause(this.debugStackTrace);
			}

			throw illegalStateException3;
		}
	}

	public void invalidateLayout() {
		this.isPrepared = false;
		super.invalidateLayout();
	}

	protected void paintWidget(GUI gui) {
		this.isPrepared = false;
	}

	protected void sizeChanged() {
		this.isPrepared = false;
		super.sizeChanged();
	}

	protected void afterAddToGUI(GUI gui) {
		this.isPrepared = false;
		super.afterAddToGUI(gui);
	}

	public DialogLayout.Group createParallelGroup() {
		return new DialogLayout.ParallelGroup();
	}

	public DialogLayout.Group createParallelGroup(Widget... widgets) {
		return this.createParallelGroup().addWidgets(widgets);
	}

	public DialogLayout.Group createParallelGroup(DialogLayout.Group... groups) {
		return this.createParallelGroup().addGroups(groups);
	}

	public DialogLayout.Group createSequentialGroup() {
		return new DialogLayout.SequentialGroup();
	}

	public DialogLayout.Group createSequentialGroup(Widget... widgets) {
		return this.createSequentialGroup().addWidgets(widgets);
	}

	public DialogLayout.Group createSequentialGroup(DialogLayout.Group... groups) {
		return this.createSequentialGroup().addGroups(groups);
	}

	public void insertChild(Widget child, int index) throws IndexOutOfBoundsException {
		super.insertChild(child, index);
		this.widgetSprings.put(child, new DialogLayout.WidgetSpring(child));
	}

	public void removeAllChildren() {
		super.removeAllChildren();
		this.widgetSprings.clear();
		this.recheckWidgets();
		this.layoutGroupsChanged();
	}

	public Widget removeChild(int index) throws IndexOutOfBoundsException {
		Widget widget = super.removeChild(index);
		this.widgetSprings.remove(widget);
		this.recheckWidgets();
		this.layoutGroupsChanged();
		return widget;
	}

	public boolean setWidgetAlignment(Widget widget, Alignment alignment) {
		if(widget == null) {
			throw new NullPointerException("widget");
		} else if(alignment == null) {
			throw new NullPointerException("alignment");
		} else {
			DialogLayout.WidgetSpring ws = (DialogLayout.WidgetSpring)this.widgetSprings.get(widget);
			if(ws != null) {
				assert widget.getParent() == this;

				ws.alignment = alignment;
				return true;
			} else {
				return false;
			}
		}
	}

	protected void recheckWidgets() {
		if(this.horz != null) {
			this.horz.recheckWidgets();
		}

		if(this.vert != null) {
			this.vert.recheckWidgets();
		}

	}

	protected void layoutGroupsChanged() {
		this.redoDefaultGaps = true;
		this.maybeInvalidateLayoutTree();
	}

	protected void maybeInvalidateLayoutTree() {
		if(this.horz != null && this.vert != null && !this.blockInvalidateLayoutTree) {
			this.invalidateLayout();
		}

	}

	protected void childVisibilityChanged(Widget child) {
		if(!this.includeInvisibleWidgets) {
			this.layoutGroupsChanged();
		}

	}

	void removeChild(DialogLayout.WidgetSpring widgetSpring) {
		Widget widget = widgetSpring.w;
		int idx = this.getChildIndex(widget);

		assert idx >= 0;

		super.removeChild(idx);
		this.widgetSprings.remove(widget);
	}

	public static class Gap {
		public final int min;
		public final int preferred;
		public final int max;

		public Gap() {
			this(0, 0, 32767);
		}

		public Gap(int size) {
			this(size, size, size);
		}

		public Gap(int min, int preferred) {
			this(min, preferred, 32767);
		}

		public Gap(int min, int preferred, int max) {
			if(min < 0) {
				throw new IllegalArgumentException("min");
			} else if(preferred < min) {
				throw new IllegalArgumentException("preferred");
			} else if(max >= 0 && (max <= 0 || max >= preferred)) {
				this.min = min;
				this.preferred = preferred;
				this.max = max;
			} else {
				throw new IllegalArgumentException("max");
			}
		}
	}

	private class GapSpring extends DialogLayout.Spring {
		final int min;
		final int pref;
		final int max;
		final boolean isDefault;

		GapSpring(int min, int pref, int max, boolean isDefault) {
			this.convertConstant(0, min);
			this.convertConstant(0, pref);
			this.convertConstant(0, max);
			this.min = min;
			this.pref = pref;
			this.max = max;
			this.isDefault = isDefault;
		}

		int getMinSize(int axis) {
			return this.convertConstant(axis, this.min);
		}

		int getPrefSize(int axis) {
			return this.convertConstant(axis, this.pref);
		}

		int getMaxSize(int axis) {
			return this.convertConstant(axis, this.max);
		}

		void setSize(int axis, int pos, int size) {
		}

		private int convertConstant(int axis, int value) {
			if(value >= 0) {
				return value;
			} else {
				Dimension dim;
				switch(value) {
				case -4:
					dim = DialogLayout.this.defaultGap;
					break;
				case -3:
					dim = DialogLayout.this.largeGap;
					break;
				case -2:
					dim = DialogLayout.this.mediumGap;
					break;
				case -1:
					dim = DialogLayout.this.smallGap;
					break;
				default:
					throw new IllegalArgumentException("Invalid gap size: " + value);
				}

				return dim == null ? 0 : (axis == 0 ? dim.getX() : dim.getY());
			}
		}
	}

	public abstract class Group extends DialogLayout.Spring {
		final ArrayList springs = new ArrayList();
		boolean alreadyAdded;

		void checkGroup(DialogLayout owner) {
			if(DialogLayout.this != owner) {
				throw new IllegalArgumentException("Can\'t add group from different layout");
			} else if(this.alreadyAdded) {
				throw new IllegalArgumentException("Group already added to another group");
			}
		}

		public DialogLayout.Group addGroup(DialogLayout.Group g) {
			g.checkGroup(DialogLayout.this);
			g.alreadyAdded = true;
			this.addSpring(g);
			return this;
		}

		public DialogLayout.Group addGroups(DialogLayout.Group... groups) {
			DialogLayout.Group[] dialogLayout$Group5 = groups;
			int i4 = groups.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				DialogLayout.Group g = dialogLayout$Group5[i3];
				this.addGroup(g);
			}

			return this;
		}

		public DialogLayout.Group addWidget(Widget w) {
			if(w.getParent() != DialogLayout.this) {
				DialogLayout.this.add(w);
			}

			DialogLayout.WidgetSpring s = (DialogLayout.WidgetSpring)DialogLayout.this.widgetSprings.get(w);
			if(s == null) {
				throw new IllegalStateException("WidgetSpring for Widget not found: " + w);
			} else {
				this.addSpring(s);
				return this;
			}
		}

		public DialogLayout.Group addWidget(Widget w, Alignment alignment) {
			this.addWidget(w);
			DialogLayout.this.setWidgetAlignment(w, alignment);
			return this;
		}

		public DialogLayout.Group addWidgets(Widget... widgets) {
			Widget[] widget5 = widgets;
			int i4 = widgets.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				Widget w = widget5[i3];
				this.addWidget(w);
			}

			return this;
		}

		public DialogLayout.Group addWidgetsWithGap(String gapName, Widget... widgets) {
			de.matthiasmann.twl.renderer.AnimationState.StateKey stateNotFirst = de.matthiasmann.twl.renderer.AnimationState.StateKey.get(gapName.concat("NotFirst"));
			de.matthiasmann.twl.renderer.AnimationState.StateKey stateNotLast = de.matthiasmann.twl.renderer.AnimationState.StateKey.get(gapName.concat("NotLast"));
			int i = 0;

			for(int n = widgets.length; i < n; ++i) {
				if(i > 0) {
					this.addGap(gapName);
				}

				Widget w = widgets[i];
				this.addWidget(w);
				AnimationState as = w.getAnimationState();
				as.setAnimationState(stateNotFirst, i > 0);
				as.setAnimationState(stateNotLast, i < n - 1);
			}

			return this;
		}

		public DialogLayout.Group addGap(int min, int pref, int max) {
			this.addSpring(DialogLayout.this.new GapSpring(min, pref, max, false));
			return this;
		}

		public DialogLayout.Group addGap(int size) {
			this.addSpring(DialogLayout.this.new GapSpring(size, size, size, false));
			return this;
		}

		public DialogLayout.Group addMinGap(int minSize) {
			this.addSpring(DialogLayout.this.new GapSpring(minSize, minSize, 32767, false));
			return this;
		}

		public DialogLayout.Group addGap() {
			this.addSpring(DialogLayout.this.new GapSpring(0, 0, 32767, false));
			return this;
		}

		public DialogLayout.Group addGap(String name) {
			if(name.length() == 0) {
				throw new IllegalArgumentException("name");
			} else {
				this.addSpring(DialogLayout.this.new NamedGapSpring(name));
				return this;
			}
		}

		public void removeDefaultGaps() {
			int i = this.springs.size();

			while(i-- > 0) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(s instanceof DialogLayout.GapSpring) {
					if(((DialogLayout.GapSpring)s).isDefault) {
						this.springs.remove(i);
					}
				} else if(s instanceof DialogLayout.Group) {
					((DialogLayout.Group)s).removeDefaultGaps();
				}
			}

		}

		public void addDefaultGap() {
			for(int i = 0; i < this.springs.size(); ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(s instanceof DialogLayout.Group) {
					((DialogLayout.Group)s).addDefaultGap();
				}
			}

		}

		public boolean removeGroup(DialogLayout.Group g, boolean removeWidgets) {
			for(int i = 0; i < this.springs.size(); ++i) {
				if(this.springs.get(i) == g) {
					this.springs.remove(i);
					if(removeWidgets) {
						g.removeWidgets();
						DialogLayout.this.recheckWidgets();
					}

					DialogLayout.this.layoutGroupsChanged();
					return true;
				}
			}

			return false;
		}

		public void clear(boolean removeWidgets) {
			if(removeWidgets) {
				this.removeWidgets();
			}

			this.springs.clear();
			if(removeWidgets) {
				DialogLayout.this.recheckWidgets();
			}

			DialogLayout.this.layoutGroupsChanged();
		}

		void addSpring(DialogLayout.Spring s) {
			this.springs.add(s);
			DialogLayout.this.layoutGroupsChanged();
		}

		void recheckWidgets() {
			int i = this.springs.size();

			while(i-- > 0) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(s instanceof DialogLayout.WidgetSpring) {
					if(!DialogLayout.this.widgetSprings.containsKey(((DialogLayout.WidgetSpring)s).w)) {
						this.springs.remove(i);
					}
				} else if(s instanceof DialogLayout.Group) {
					((DialogLayout.Group)s).recheckWidgets();
				}
			}

		}

		void removeWidgets() {
			int i = this.springs.size();

			while(i-- > 0) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(s instanceof DialogLayout.WidgetSpring) {
					DialogLayout.this.removeChild((DialogLayout.WidgetSpring)s);
				} else if(s instanceof DialogLayout.Group) {
					((DialogLayout.Group)s).removeWidgets();
				}
			}

		}
	}

	private class NamedGapSpring extends DialogLayout.Spring {
		final String name;

		public NamedGapSpring(String name) {
			this.name = name;
		}

		int getMaxSize(int axis) {
			return this.getGap().max;
		}

		int getMinSize(int axis) {
			return this.getGap().min;
		}

		int getPrefSize(int axis) {
			return this.getGap().preferred;
		}

		void setSize(int axis, int pos, int size) {
		}

		private DialogLayout.Gap getGap() {
			return DialogLayout.this.namedGaps != null ? (DialogLayout.Gap)DialogLayout.this.namedGaps.getParameterValue(this.name, true, DialogLayout.Gap.class, DialogLayout.NO_GAP) : DialogLayout.NO_GAP;
		}
	}

	class ParallelGroup extends DialogLayout.Group {
		ParallelGroup() {
			super();
		}

		int getMinSize(int axis) {
			int size = 0;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					size = Math.max(size, s.getMinSize(axis));
				}
			}

			return size;
		}

		int getPrefSize(int axis) {
			int size = 0;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					size = Math.max(size, s.getPrefSize(axis));
				}
			}

			return size;
		}

		int getMaxSize(int axis) {
			int size = 0;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					size = Math.max(size, s.getMaxSize(axis));
				}
			}

			return size;
		}

		void setSize(int axis, int pos, int size) {
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					s.setSize(axis, pos, size);
				}
			}

		}

		public DialogLayout.Group addGap() {
			DialogLayout.getLogger().log(Level.WARNING, "Useless call to addGap() on ParallelGroup", new Throwable());
			return this;
		}
	}

	class SequentialGroup extends DialogLayout.Group {
		SequentialGroup() {
			super();
		}

		int getMinSize(int axis) {
			int size = 0;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					size += s.getMinSize(axis);
				}
			}

			return size;
		}

		int getPrefSize(int axis) {
			int size = 0;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					size += s.getPrefSize(axis);
				}
			}

			return size;
		}

		int getMaxSize(int axis) {
			int size = 0;
			boolean hasMax = false;
			int i = 0;

			for(int n = this.springs.size(); i < n; ++i) {
				DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
				if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
					int max = s.getMaxSize(axis);
					if(max > 0) {
						size += max;
						hasMax = true;
					} else {
						size += s.getPrefSize(axis);
					}
				}
			}

			return hasMax ? size : 0;
		}

		public void addDefaultGap() {
			if(this.springs.size() > 1) {
				boolean wasGap = true;

				for(int i = 0; i < this.springs.size(); ++i) {
					DialogLayout.Spring s = (DialogLayout.Spring)this.springs.get(i);
					if(DialogLayout.this.includeInvisibleWidgets || s.isVisible()) {
						boolean isGap = s instanceof DialogLayout.GapSpring || s instanceof DialogLayout.NamedGapSpring;
						if(!isGap && !wasGap) {
							this.springs.add(i++, DialogLayout.this.new GapSpring(-4, -4, -4, true));
						}

						wasGap = isGap;
					}
				}
			}

			super.addDefaultGap();
		}

		void setSize(int axis, int pos, int size) {
			int prefSize = this.getPrefSize(axis);
			DialogLayout.Spring s;
			if(size == prefSize) {
				Iterator iterator6 = this.springs.iterator();

				while(true) {
					do {
						if(!iterator6.hasNext()) {
							return;
						}

						s = (DialogLayout.Spring)iterator6.next();
					} while(!DialogLayout.this.includeInvisibleWidgets && !s.isVisible());

					int spref = s.getPrefSize(axis);
					s.setSize(axis, pos, spref);
					pos += spref;
				}
			} else if(this.springs.size() == 1) {
				s = (DialogLayout.Spring)this.springs.get(0);
				s.setSize(axis, pos, size);
			} else if(this.springs.size() > 1) {
				this.setSizeNonPref(axis, pos, size, prefSize);
			}

		}

		private void setSizeNonPref(int axis, int pos, int size, int prefSize) {
			int delta = size - prefSize;
			boolean useMin = delta < 0;
			if(useMin) {
				delta = -delta;
			}

			DialogLayout.SpringDelta[] deltas = new DialogLayout.SpringDelta[this.springs.size()];
			int resizeable = 0;

			int s;
			int ssize;
			for(s = 0; s < this.springs.size(); ++s) {
				DialogLayout.Spring rest = (DialogLayout.Spring)this.springs.get(s);
				if(DialogLayout.this.includeInvisibleWidgets || rest.isVisible()) {
					ssize = useMin ? rest.getPrefSize(axis) - rest.getMinSize(axis) : rest.getMaxSize(axis) - rest.getPrefSize(axis);
					if(ssize > 0) {
						deltas[resizeable++] = new DialogLayout.SpringDelta(s, ssize);
					}
				}
			}

			if(resizeable > 0) {
				if(resizeable > 1) {
					Arrays.sort(deltas, 0, resizeable);
				}

				s = delta / resizeable;
				int i16 = delta - s * resizeable;
				int[] i19 = new int[this.springs.size()];

				int i;
				int ssize1;
				for(i = 0; i < resizeable; ++i) {
					DialogLayout.SpringDelta s1 = deltas[i];
					if(i + 1 == resizeable) {
						s += i16;
					}

					ssize1 = Math.min(s1.delta, s);
					delta -= ssize1;
					if(ssize1 != s && i + 1 < resizeable) {
						int remaining = resizeable - i - 1;
						s = delta / remaining;
						i16 = delta - s * remaining;
					}

					if(useMin) {
						ssize1 = -ssize1;
					}

					i19[s1.idx] = ssize1;
				}

				for(i = 0; i < this.springs.size(); ++i) {
					DialogLayout.Spring dialogLayout$Spring20 = (DialogLayout.Spring)this.springs.get(i);
					if(DialogLayout.this.includeInvisibleWidgets || dialogLayout$Spring20.isVisible()) {
						ssize1 = dialogLayout$Spring20.getPrefSize(axis) + i19[i];
						dialogLayout$Spring20.setSize(axis, pos, ssize1);
						pos += ssize1;
					}
				}

			} else {
				Iterator iterator17 = this.springs.iterator();

				while(true) {
					DialogLayout.Spring dialogLayout$Spring18;
					do {
						if(!iterator17.hasNext()) {
							return;
						}

						dialogLayout$Spring18 = (DialogLayout.Spring)iterator17.next();
					} while(!DialogLayout.this.includeInvisibleWidgets && !dialogLayout$Spring18.isVisible());

					if(useMin) {
						ssize = dialogLayout$Spring18.getMinSize(axis);
					} else {
						ssize = dialogLayout$Spring18.getMaxSize(axis);
						if(ssize == 0) {
							ssize = dialogLayout$Spring18.getPrefSize(axis);
						}
					}

					dialogLayout$Spring18.setSize(axis, pos, ssize);
					pos += ssize;
				}
			}
		}
	}

	abstract static class Spring {
		abstract int getMinSize(int i1);

		abstract int getPrefSize(int i1);

		abstract int getMaxSize(int i1);

		abstract void setSize(int i1, int i2, int i3);

		void collectAllSprings(HashSet result) {
			result.add(this);
		}

		boolean isVisible() {
			return true;
		}
	}

	static class SpringDelta implements Comparable {
		final int idx;
		final int delta;

		SpringDelta(int idx, int delta) {
			this.idx = idx;
			this.delta = delta;
		}

		public int compareTo(DialogLayout.SpringDelta o) {
			return this.delta - o.delta;
		}

		public int compareTo(Object object1) {
			return this.compareTo((DialogLayout.SpringDelta)object1);
		}
	}

	private static class WidgetSpring extends DialogLayout.Spring {
		final Widget w;
		Alignment alignment;
		int x;
		int y;
		int width;
		int height;
		int minWidth;
		int minHeight;
		int maxWidth;
		int maxHeight;
		int prefWidth;
		int prefHeight;
		int flags;

		WidgetSpring(Widget w) {
			this.w = w;
			this.alignment = Alignment.FILL;
		}

		void prepare() {
			this.x = this.w.getX();
			this.y = this.w.getY();
			this.width = this.w.getWidth();
			this.height = this.w.getHeight();
			this.minWidth = this.w.getMinWidth();
			this.minHeight = this.w.getMinHeight();
			this.maxWidth = this.w.getMaxWidth();
			this.maxHeight = this.w.getMaxHeight();
			this.prefWidth = DialogLayout.computeSize(this.minWidth, this.w.getPreferredWidth(), this.maxWidth);
			this.prefHeight = DialogLayout.computeSize(this.minHeight, this.w.getPreferredHeight(), this.maxHeight);
			this.flags = 0;
		}

		int getMinSize(int axis) {
			switch(axis) {
			case 0:
				return this.minWidth;
			case 1:
				return this.minHeight;
			default:
				throw new IllegalArgumentException("axis");
			}
		}

		int getPrefSize(int axis) {
			switch(axis) {
			case 0:
				return this.prefWidth;
			case 1:
				return this.prefHeight;
			default:
				throw new IllegalArgumentException("axis");
			}
		}

		int getMaxSize(int axis) {
			switch(axis) {
			case 0:
				return this.maxWidth;
			case 1:
				return this.maxHeight;
			default:
				throw new IllegalArgumentException("axis");
			}
		}

		void setSize(int axis, int pos, int size) {
			this.flags |= 1 << axis;
			switch(axis) {
			case 0:
				this.x = pos;
				this.width = size;
				break;
			case 1:
				this.y = pos;
				this.height = size;
				break;
			default:
				throw new IllegalArgumentException("axis");
			}

		}

		void apply() {
			if(this.flags != 3) {
				this.invalidState();
			}

			if(this.alignment != Alignment.FILL) {
				int newWidth = Math.min(this.width, this.prefWidth);
				int newHeight = Math.min(this.height, this.prefHeight);
				this.w.setPosition(this.x + this.alignment.computePositionX(this.width, newWidth), this.y + this.alignment.computePositionY(this.height, newHeight));
				this.w.setSize(newWidth, newHeight);
			} else {
				this.w.setPosition(this.x, this.y);
				this.w.setSize(this.width, this.height);
			}

		}

		boolean isVisible() {
			return this.w.isVisible();
		}

		void invalidState() {
			StringBuilder sb = new StringBuilder();
			sb.append("Widget ").append(this.w).append(" with theme ").append(this.w.getTheme()).append(" is not part of the following groups:");
			if((this.flags & 1) == 0) {
				sb.append(" horizontal");
			}

			if((this.flags & 2) == 0) {
				sb.append(" vertical");
			}

			throw new IllegalStateException(sb.toString());
		}
	}
}
