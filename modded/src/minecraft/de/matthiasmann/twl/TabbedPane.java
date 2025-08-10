package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;
import de.matthiasmann.twl.model.HasCallback;

import java.util.ArrayList;

public class TabbedPane extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_FIRST_TAB = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("firstTab");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_LAST_TAB = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("lastTab");
	private final ArrayList tabs = new ArrayList();
	private final BoxLayout tabBox = new BoxLayout();
	private final Widget tabBoxClip = new Widget();
	private final TabbedPane.Container container = new TabbedPane.Container((TabbedPane.Container)null);
	final TabbedPane.Container innerContainer = new TabbedPane.Container((TabbedPane.Container)null);
	DialogLayout scrollControlls;
	Button btnScrollLeft;
	Button btnScrollRight;
	boolean scrollTabs;
	int tabScrollPosition;
	TabbedPane.TabPosition tabPosition = TabbedPane.TabPosition.TOP;
	TabbedPane.Tab activeTab;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition;

	public TabbedPane() {
		this.tabBox.setTheme("tabbox");
		this.tabBoxClip.setTheme("");
		this.innerContainer.setTheme("");
		this.innerContainer.setClip(true);
		this.tabBoxClip.add(this.tabBox);
		this.container.add(this.innerContainer);
		super.insertChild(this.container, 0);
		super.insertChild(this.tabBoxClip, 1);
		this.addActionMapping("nextTab", "cycleTabs", new Object[]{1});
		this.addActionMapping("prevTab", "cycleTabs", new Object[]{-1});
	}

	public TabbedPane.TabPosition getTabPosition() {
		return this.tabPosition;
	}

	public void setTabPosition(TabbedPane.TabPosition tabPosition) {
		if(tabPosition == null) {
			throw new NullPointerException("tabPosition");
		} else {
			if(this.tabPosition != tabPosition) {
				this.tabPosition = tabPosition;
				this.tabBox.setDirection(tabPosition.horz ? BoxLayout.Direction.HORIZONTAL : BoxLayout.Direction.VERTICAL);
				this.invalidateLayout();
			}

		}
	}

	public boolean isScrollTabs() {
		return this.scrollTabs;
	}

	public void setScrollTabs(boolean scrollTabs) {
		if(this.scrollTabs != scrollTabs) {
			this.scrollTabs = scrollTabs;
			if(this.scrollControlls == null && scrollTabs) {
				this.createScrollControlls();
			}

			this.tabBoxClip.setClip(scrollTabs);
			if(this.scrollControlls != null) {
				this.scrollControlls.setVisible(scrollTabs);
			}

			this.invalidateLayout();
		}

	}

	public TabbedPane.Tab addTab(String title, Widget pane) {
		TabbedPane.Tab tab = new TabbedPane.Tab();
		tab.setTitle(title);
		tab.setPane(pane);
		this.tabBox.add(tab.button);
		this.tabs.add(tab);
		if(this.tabs.size() == 1) {
			this.setActiveTab(tab);
		}

		this.updateTabStates();
		return tab;
	}

	public TabbedPane.Tab getActiveTab() {
		return this.activeTab;
	}

	public void setActiveTab(TabbedPane.Tab tab) {
		if(tab != null) {
			this.validateTab(tab);
		}

		if(this.activeTab != tab) {
			TabbedPane.Tab prevTab = this.activeTab;
			this.activeTab = tab;
			if(prevTab != null) {
				prevTab.doCallback();
			}

			if(tab != null) {
				tab.doCallback();
			}

			if(this.scrollTabs) {
				int pos;
				int end;
				int size;
				if(this.tabPosition.horz) {
					pos = tab.button.getX() - this.tabBox.getX();
					end = tab.button.getWidth() + pos;
					size = this.tabBoxClip.getWidth();
				} else {
					pos = tab.button.getY() - this.tabBox.getY();
					end = tab.button.getHeight() + pos;
					size = this.tabBoxClip.getHeight();
				}

				int border = (size + 19) / 20;
				pos -= border;
				end += border;
				if(pos < this.tabScrollPosition) {
					this.setScrollPos(pos);
				} else if(end > this.tabScrollPosition + size) {
					this.setScrollPos(end - size);
				}
			}
		}

	}

	public void removeTab(TabbedPane.Tab tab) {
		this.validateTab(tab);
		int idx = tab == this.activeTab ? this.tabs.indexOf(tab) : -1;
		tab.setPane((Widget)null);
		this.tabBox.removeChild(tab.button);
		this.tabs.remove(tab);
		if(idx >= 0 && !this.tabs.isEmpty()) {
			this.setActiveTab((TabbedPane.Tab)this.tabs.get(Math.min(this.tabs.size() - 1, idx)));
		}

		this.updateTabStates();
	}

	public void removeAllTabs() {
		this.innerContainer.removeAllChildren();
		this.tabBox.removeAllChildren();
		this.tabs.clear();
		this.activeTab = null;
	}

	public void cycleTabs(int direction) {
		if(!this.tabs.isEmpty()) {
			int idx = this.tabs.indexOf(this.activeTab);
			if(idx < 0) {
				idx = 0;
			} else {
				idx += direction;
				idx %= this.tabs.size();
				idx += this.tabs.size();
				idx %= this.tabs.size();
			}

			this.setActiveTab((TabbedPane.Tab)this.tabs.get(idx));
		}

	}

	public int getMinWidth() {
		int minWidth;
		if(this.tabPosition.horz) {
			int tabBoxWidth;
			if(this.scrollTabs) {
				tabBoxWidth = this.tabBox.getBorderHorizontal() + BoxLayout.computeMinWidthVertical(this.tabBox) + this.scrollControlls.getPreferredWidth();
			} else {
				tabBoxWidth = this.tabBox.getMinWidth();
			}

			minWidth = Math.max(this.container.getMinWidth(), tabBoxWidth);
		} else {
			minWidth = this.container.getMinWidth() + this.tabBox.getMinWidth();
		}

		return Math.max(super.getMinWidth(), minWidth + this.getBorderHorizontal());
	}

	public int getMinHeight() {
		int minHeight;
		if(this.tabPosition.horz) {
			minHeight = this.container.getMinHeight() + this.tabBox.getMinHeight();
		} else {
			minHeight = Math.max(this.container.getMinHeight(), this.tabBox.getMinHeight());
		}

		return Math.max(super.getMinHeight(), minHeight + this.getBorderVertical());
	}

	public int getPreferredInnerWidth() {
		if(this.tabPosition.horz) {
			int tabBoxWidth;
			if(this.scrollTabs) {
				tabBoxWidth = this.tabBox.getBorderHorizontal() + BoxLayout.computePreferredWidthVertical(this.tabBox) + this.scrollControlls.getPreferredWidth();
			} else {
				tabBoxWidth = this.tabBox.getPreferredWidth();
			}

			return Math.max(this.container.getPreferredWidth(), tabBoxWidth);
		} else {
			return this.container.getPreferredWidth() + this.tabBox.getPreferredWidth();
		}
	}

	public int getPreferredInnerHeight() {
		return this.tabPosition.horz ? this.container.getPreferredHeight() + this.tabBox.getPreferredHeight() : Math.max(this.container.getPreferredHeight(), this.tabBox.getPreferredHeight());
	}

	protected void layout() {
		int scrollCtrlsWidth = 0;
		int scrollCtrlsHeight = 0;
		int tabBoxWidth = this.tabBox.getPreferredWidth();
		int tabBoxHeight = this.tabBox.getPreferredHeight();
		if(this.scrollTabs) {
			scrollCtrlsWidth = this.scrollControlls.getPreferredWidth();
			scrollCtrlsHeight = this.scrollControlls.getPreferredHeight();
		}

		if(this.tabPosition.horz) {
			tabBoxHeight = Math.max(scrollCtrlsHeight, tabBoxHeight);
		} else {
			tabBoxWidth = Math.max(scrollCtrlsWidth, tabBoxWidth);
		}

		this.tabBox.setSize(tabBoxWidth, tabBoxHeight);
		switch($SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition()[this.tabPosition.ordinal()]) {
		case 1:
			this.tabBoxClip.setPosition(this.getInnerX(), this.getInnerY());
			this.tabBoxClip.setSize(this.getInnerWidth() - scrollCtrlsWidth, tabBoxHeight);
			this.container.setSize(this.getInnerWidth(), this.getInnerHeight() - tabBoxHeight);
			this.container.setPosition(this.getInnerX(), this.tabBoxClip.getBottom());
			break;
		case 2:
			this.tabBoxClip.setPosition(this.getInnerX(), this.getInnerY());
			this.tabBoxClip.setSize(tabBoxWidth, this.getInnerHeight() - scrollCtrlsHeight);
			this.container.setSize(this.getInnerWidth() - tabBoxWidth, this.getInnerHeight());
			this.container.setPosition(this.tabBoxClip.getRight(), this.getInnerY());
			break;
		case 3:
			this.tabBoxClip.setPosition(this.getInnerX() - tabBoxWidth, this.getInnerY());
			this.tabBoxClip.setSize(tabBoxWidth, this.getInnerHeight() - scrollCtrlsHeight);
			this.container.setSize(this.getInnerWidth() - tabBoxWidth, this.getInnerHeight());
			this.container.setPosition(this.getInnerX(), this.getInnerY());
			break;
		case 4:
			this.tabBoxClip.setPosition(this.getInnerX(), this.getInnerY() - tabBoxHeight);
			this.tabBoxClip.setSize(this.getInnerWidth() - scrollCtrlsWidth, tabBoxHeight);
			this.container.setSize(this.getInnerWidth(), this.getInnerHeight() - tabBoxHeight);
			this.container.setPosition(this.getInnerX(), this.getInnerY());
		}

		if(this.scrollControlls != null) {
			if(this.tabPosition.horz) {
				this.scrollControlls.setPosition(this.tabBoxClip.getRight(), this.tabBoxClip.getY());
				this.scrollControlls.setSize(scrollCtrlsWidth, tabBoxHeight);
			} else {
				this.scrollControlls.setPosition(this.tabBoxClip.getX(), this.tabBoxClip.getBottom());
				this.scrollControlls.setSize(tabBoxWidth, scrollCtrlsHeight);
			}

			this.setScrollPos(this.tabScrollPosition);
		}

	}

	private void createScrollControlls() {
		this.scrollControlls = new DialogLayout();
		this.scrollControlls.setTheme("scrollControls");
		this.btnScrollLeft = new Button();
		this.btnScrollLeft.setTheme("scrollLeft");
		this.btnScrollLeft.addCallback(new TabbedPane.CB(-1));
		this.btnScrollRight = new Button();
		this.btnScrollRight.setTheme("scrollRight");
		this.btnScrollRight.addCallback(new TabbedPane.CB(1));
		DialogLayout.Group horz = this.scrollControlls.createSequentialGroup().addWidget(this.btnScrollLeft).addGap("scrollButtons").addWidget(this.btnScrollRight);
		DialogLayout.Group vert = this.scrollControlls.createParallelGroup().addWidget(this.btnScrollLeft).addWidget(this.btnScrollRight);
		this.scrollControlls.setHorizontalGroup(horz);
		this.scrollControlls.setVerticalGroup(vert);
		super.insertChild(this.scrollControlls, 2);
	}

	void scrollTabs(int dir) {
		dir *= Math.max(1, this.tabBoxClip.getWidth() / 10);
		this.setScrollPos(this.tabScrollPosition + dir);
	}

	private void setScrollPos(int pos) {
		int maxPos;
		if(this.tabPosition.horz) {
			maxPos = this.tabBox.getWidth() - this.tabBoxClip.getWidth();
		} else {
			maxPos = this.tabBox.getHeight() - this.tabBoxClip.getHeight();
		}

		pos = Math.max(0, Math.min(pos, maxPos));
		this.tabScrollPosition = pos;
		if(this.tabPosition.horz) {
			this.tabBox.setPosition(this.tabBoxClip.getX() - pos, this.tabBoxClip.getY());
		} else {
			this.tabBox.setPosition(this.tabBoxClip.getX(), this.tabBoxClip.getY() - pos);
		}

		if(this.scrollControlls != null) {
			this.btnScrollLeft.setEnabled(pos > 0);
			this.btnScrollRight.setEnabled(pos < maxPos);
		}

	}

	public void insertChild(Widget child, int index) {
		throw new UnsupportedOperationException("use addTab/removeTab");
	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException("use addTab/removeTab");
	}

	public Widget removeChild(int index) {
		throw new UnsupportedOperationException("use addTab/removeTab");
	}

	protected void updateTabStates() {
		int i = 0;

		for(int n = this.tabs.size(); i < n; ++i) {
			TabbedPane.Tab tab = (TabbedPane.Tab)this.tabs.get(i);
			AnimationState animationState = tab.button.getAnimationState();
			animationState.setAnimationState(STATE_FIRST_TAB, i == 0);
			animationState.setAnimationState(STATE_LAST_TAB, i == n - 1);
		}

	}

	private void validateTab(TabbedPane.Tab tab) {
		if(tab.button.getParent() != this.tabBox) {
			throw new IllegalArgumentException("Invalid tab");
		}
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition;
		if($SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition != null) {
			return i10000;
		} else {
			int[] i0 = new int[TabbedPane.TabPosition.values().length];

			try {
				i0[TabbedPane.TabPosition.BOTTOM.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[TabbedPane.TabPosition.LEFT.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[TabbedPane.TabPosition.RIGHT.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[TabbedPane.TabPosition.TOP.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$TabbedPane$TabPosition = i0;
			return i0;
		}
	}

	private class CB implements Runnable {
		final int dir;

		public CB(int dir) {
			this.dir = dir;
		}

		public void run() {
			TabbedPane.this.scrollTabs(this.dir);
		}
	}

	private static class Container extends Widget {
		private Container() {
		}

		public int getMinWidth() {
			return Math.max(super.getMinWidth(), this.getBorderHorizontal() + BoxLayout.computeMinWidthVertical(this));
		}

		public int getMinHeight() {
			return Math.max(super.getMinHeight(), this.getBorderVertical() + BoxLayout.computeMinHeightHorizontal(this));
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

		Container(TabbedPane.Container tabbedPane$Container1) {
			this();
		}
	}

	public class Tab extends HasCallback implements BooleanModel {
		final ToggleButton button = new ToggleButton(this);
		Widget pane;

		Tab() {
			this.button.setTheme("tabbutton");
		}

		public boolean getValue() {
			return TabbedPane.this.activeTab == this;
		}

		public void setValue(boolean value) {
			if(value) {
				TabbedPane.this.setActiveTab(this);
			}

		}

		public Widget getPane() {
			return this.pane;
		}

		public void setPane(Widget pane) {
			if(this.pane != pane) {
				if(this.pane != null) {
					TabbedPane.this.innerContainer.removeChild(this.pane);
				}

				this.pane = pane;
				if(pane != null) {
					pane.setVisible(this.getValue());
					TabbedPane.this.innerContainer.add(pane);
				}
			}

		}

		public TabbedPane.Tab setTitle(String title) {
			this.button.setText(title);
			return this;
		}

		public TabbedPane.Tab setTheme(String theme) {
			this.button.setTheme(theme);
			return this;
		}

		protected void doCallback() {
			if(this.pane != null) {
				this.pane.setVisible(this.getValue());
			}

			super.doCallback();
		}
	}

	public static enum TabPosition {
		TOP(true),
		LEFT(false),
		RIGHT(true),
		BOTTOM(false);

		final boolean horz;

		private TabPosition(boolean horz) {
			this.horz = horz;
		}
	}
}
