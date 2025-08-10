package de.matthiasmann.twl;

public class AnimationState implements de.matthiasmann.twl.renderer.AnimationState {
	private final AnimationState parent;
	private AnimationState.State[] stateTable;
	private GUI gui;

	public AnimationState(AnimationState parent) {
		this.parent = parent;
		this.stateTable = new AnimationState.State[16];
	}

	public AnimationState() {
		this((AnimationState)null);
	}

	public void setGUI(GUI gui) {
		this.gui = gui;
		long curTime = this.getCurrentTime();
		AnimationState.State[] animationState$State7 = this.stateTable;
		int i6 = this.stateTable.length;

		for(int i5 = 0; i5 < i6; ++i5) {
			AnimationState.State s = animationState$State7[i5];
			if(s != null) {
				s.lastChangedTime = curTime;
			}
		}

	}

	public int getAnimationTime(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		AnimationState.State state = this.getState(stateKey);
		return state != null ? (int)Math.min(2147483647L, this.getCurrentTime() - state.lastChangedTime) : (this.parent != null ? this.parent.getAnimationTime(stateKey) : (int)this.getCurrentTime() & Integer.MAX_VALUE);
	}

	public boolean getAnimationState(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		AnimationState.State state = this.getState(stateKey);
		return state != null ? state.active : (this.parent != null ? this.parent.getAnimationState(stateKey) : false);
	}

	public boolean getShouldAnimateState(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		AnimationState.State state = this.getState(stateKey);
		return state != null ? state.shouldAnimate : (this.parent != null ? this.parent.getShouldAnimateState(stateKey) : false);
	}

	/** @deprecated */
	@Deprecated
	public void setAnimationState(String stateName, boolean active) {
		this.setAnimationState(de.matthiasmann.twl.renderer.AnimationState.StateKey.get(stateName), active);
	}

	public void setAnimationState(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey, boolean active) {
		AnimationState.State state = this.getOrCreate(stateKey);
		if(state.active != active) {
			state.active = active;
			state.lastChangedTime = this.getCurrentTime();
			state.shouldAnimate = true;
		}

	}

	/** @deprecated */
	@Deprecated
	public void resetAnimationTime(String stateName) {
		this.resetAnimationTime(de.matthiasmann.twl.renderer.AnimationState.StateKey.get(stateName));
	}

	public void resetAnimationTime(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		AnimationState.State state = this.getOrCreate(stateKey);
		state.lastChangedTime = this.getCurrentTime();
		state.shouldAnimate = true;
	}

	/** @deprecated */
	@Deprecated
	public void dontAnimate(String stateName) {
		this.dontAnimate(de.matthiasmann.twl.renderer.AnimationState.StateKey.get(stateName));
	}

	public void dontAnimate(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		AnimationState.State state = this.getState(stateKey);
		if(state != null) {
			state.shouldAnimate = false;
		}

	}

	private AnimationState.State getState(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		int id = stateKey.getID();
		return id < this.stateTable.length ? this.stateTable[id] : null;
	}

	private AnimationState.State getOrCreate(de.matthiasmann.twl.renderer.AnimationState.StateKey stateKey) {
		int id = stateKey.getID();
		if(id < this.stateTable.length) {
			AnimationState.State state = this.stateTable[id];
			if(state != null) {
				return state;
			}
		}

		return this.createState(id);
	}

	private AnimationState.State createState(int id) {
		if(id >= this.stateTable.length) {
			AnimationState.State[] state = new AnimationState.State[id + 1];
			System.arraycopy(this.stateTable, 0, state, 0, this.stateTable.length);
			this.stateTable = state;
		}

		AnimationState.State state1 = new AnimationState.State();
		state1.lastChangedTime = this.getCurrentTime();
		this.stateTable[id] = state1;
		return state1;
	}

	private long getCurrentTime() {
		return this.gui != null ? this.gui.curTime : 0L;
	}

	static final class State {
		long lastChangedTime;
		boolean active;
		boolean shouldAnimate;
	}
}
