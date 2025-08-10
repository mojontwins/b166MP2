package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;

public class PositionAnimatedPanel extends Widget {
	private final Widget animatedWidget;
	private MouseSensitiveRectangle rect;
	private PositionAnimatedPanel.Direction direction = PositionAnimatedPanel.Direction.TOP;
	private int moveSpeedIn;
	private int moveSpeedOut;
	private int auraSizeX;
	private int auraSizeY;
	private boolean forceVisible;
	private boolean forceJumps;
	private BooleanModel forceVisibleModel;
	private Runnable forceVisibleModelCallback;

	public PositionAnimatedPanel(Widget animatedWidget) {
		if(animatedWidget == null) {
			throw new NullPointerException("animatedWidget");
		} else {
			this.animatedWidget = animatedWidget;
			this.setClip(true);
			this.add(animatedWidget);
		}
	}

	public PositionAnimatedPanel.Direction getHideDirection() {
		return this.direction;
	}

	public void setHideDirection(PositionAnimatedPanel.Direction direction) {
		if(direction == null) {
			throw new NullPointerException("direction");
		} else {
			this.direction = direction;
		}
	}

	public int getMoveSpeedIn() {
		return this.moveSpeedIn;
	}

	public void setMoveSpeedIn(int moveSpeedIn) {
		this.moveSpeedIn = moveSpeedIn;
	}

	public int getMoveSpeedOut() {
		return this.moveSpeedOut;
	}

	public void setMoveSpeedOut(int moveSpeedOut) {
		this.moveSpeedOut = moveSpeedOut;
	}

	public int getAuraSizeX() {
		return this.auraSizeX;
	}

	public void setAuraSizeX(int auraSizeX) {
		this.auraSizeX = auraSizeX;
	}

	public int getAuraSizeY() {
		return this.auraSizeY;
	}

	public void setAuraSizeY(int auraSizeY) {
		this.auraSizeY = auraSizeY;
	}

	public boolean isForceVisible() {
		return this.forceVisible;
	}

	public void setForceVisible(boolean forceVisible) {
		this.forceVisible = forceVisible;
		if(this.forceVisibleModel != null) {
			this.forceVisibleModel.setValue(forceVisible);
		}

	}

	public boolean isForceVisibleJumps() {
		return this.forceJumps;
	}

	public void setForceVisibleJumps(boolean forceJumps) {
		this.forceJumps = forceJumps;
	}

	public BooleanModel getForceVisibleModel() {
		return this.forceVisibleModel;
	}

	public void setForceVisibleModel(BooleanModel forceVisibleModel) {
		if(this.forceVisibleModel != forceVisibleModel) {
			if(this.forceVisibleModel != null) {
				this.forceVisibleModel.removeCallback(this.forceVisibleModelCallback);
			}

			this.forceVisibleModel = forceVisibleModel;
			if(forceVisibleModel != null) {
				if(this.forceVisibleModelCallback == null) {
					this.forceVisibleModelCallback = new PositionAnimatedPanel.ForceVisibleModelCallback();
				}

				forceVisibleModel.addCallback(this.forceVisibleModelCallback);
				this.syncWithForceVisibleModel();
			}
		}

	}

	public boolean isHidden() {
		int x = this.animatedWidget.getX();
		int y = this.animatedWidget.getY();
		return x == this.getInnerX() + this.direction.x * this.animatedWidget.getWidth() && y == this.getInnerY() + this.direction.y * this.animatedWidget.getHeight();
	}

	public int getMinWidth() {
		return Math.max(super.getMinWidth(), this.animatedWidget.getMinWidth() + this.getBorderHorizontal());
	}

	public int getMinHeight() {
		return Math.max(super.getMinHeight(), this.animatedWidget.getMinHeight() + this.getBorderVertical());
	}

	public int getPreferredInnerWidth() {
		return this.animatedWidget.getPreferredWidth();
	}

	public int getPreferredInnerHeight() {
		return this.animatedWidget.getPreferredHeight();
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.setHideDirection((PositionAnimatedPanel.Direction)themeInfo.getParameter("hidedirection", PositionAnimatedPanel.Direction.TOP));
		this.setMoveSpeedIn(themeInfo.getParameter("speed.in", 2));
		this.setMoveSpeedOut(themeInfo.getParameter("speed.out", 1));
		this.setAuraSizeX(themeInfo.getParameter("aura.width", 50));
		this.setAuraSizeY(themeInfo.getParameter("aura.height", 50));
		this.setForceVisibleJumps(themeInfo.getParameter("forceVisibleJumps", false));
	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.rect = gui.createMouseSenitiveRectangle();
		this.setRectSize();
	}

	protected void beforeRemoveFromGUI(GUI gui) {
		super.beforeRemoveFromGUI(gui);
		this.rect = null;
	}

	protected void layout() {
		this.animatedWidget.setSize(this.getInnerWidth(), this.getInnerHeight());
		this.setRectSize();
	}

	protected void positionChanged() {
		this.setRectSize();
	}

	protected void paint(GUI gui) {
		if(this.rect != null) {
			int x = this.getInnerX();
			int y = this.getInnerY();
			boolean forceOpen = this.forceVisible || this.hasOpenPopups();
			if(forceOpen && this.forceJumps) {
				this.animatedWidget.setPosition(x, y);
			} else if(!forceOpen && !this.rect.isMouseOver()) {
				this.animatedWidget.setPosition(this.calcPosOut(this.animatedWidget.getX(), x, this.direction.x * this.animatedWidget.getWidth()), this.calcPosOut(this.animatedWidget.getY(), y, this.direction.y * this.animatedWidget.getHeight()));
			} else {
				this.animatedWidget.setPosition(this.calcPosIn(this.animatedWidget.getX(), x, this.direction.x), this.calcPosIn(this.animatedWidget.getY(), y, this.direction.y));
			}
		}

		super.paint(gui);
	}

	private void setRectSize() {
		if(this.rect != null) {
			this.rect.setXYWH(this.getX() - this.auraSizeX, this.getY() - this.auraSizeY, this.getWidth() + 2 * this.auraSizeX, this.getHeight() + 2 * this.auraSizeY);
		}

	}

	private int calcPosIn(int cur, int org, int dir) {
		return dir < 0 ? Math.min(org, cur + this.moveSpeedIn) : Math.max(org, cur - this.moveSpeedIn);
	}

	private int calcPosOut(int cur, int org, int dist) {
		return dist < 0 ? Math.max(org + dist, cur - this.moveSpeedIn) : Math.min(org + dist, cur + this.moveSpeedIn);
	}

	void syncWithForceVisibleModel() {
		this.setForceVisible(this.forceVisibleModel.getValue());
	}

	public static enum Direction {
		TOP(0, -1),
		LEFT(-1, 0),
		BOTTOM(0, 1),
		RIGHT(1, 0);

		final int x;
		final int y;

		private Direction(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	class ForceVisibleModelCallback implements Runnable {
		public void run() {
			PositionAnimatedPanel.this.syncWithForceVisibleModel();
		}
	}
}
