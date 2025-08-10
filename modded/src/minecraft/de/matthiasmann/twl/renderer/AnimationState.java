package de.matthiasmann.twl.renderer;

import java.util.HashMap;

public interface AnimationState {
	int getAnimationTime(AnimationState.StateKey animationState$StateKey1);

	boolean getAnimationState(AnimationState.StateKey animationState$StateKey1);

	boolean getShouldAnimateState(AnimationState.StateKey animationState$StateKey1);

	public static final class StateKey {
		private final String name;
		private final int id;
		private static final HashMap keys = new HashMap();

		private StateKey(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public int getID() {
			return this.id;
		}

		public boolean equals(Object obj) {
			if(obj instanceof AnimationState.StateKey) {
				AnimationState.StateKey other = (AnimationState.StateKey)obj;
				return this.id == other.id;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return this.id;
		}

		public static synchronized AnimationState.StateKey get(String name) {
			if(name.length() == 0) {
				throw new IllegalArgumentException("name");
			} else {
				AnimationState.StateKey key = (AnimationState.StateKey)keys.get(name);
				if(key == null) {
					key = new AnimationState.StateKey(name, keys.size());
					keys.put(name, key);
				}

				return key;
			}
		}

		public static synchronized int getNumStateKeys() {
			return keys.size();
		}
	}
}
