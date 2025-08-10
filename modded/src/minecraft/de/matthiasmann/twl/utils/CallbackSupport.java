package de.matthiasmann.twl.utils;

import de.matthiasmann.twl.CallbackWithReason;

import java.lang.reflect.Array;

public class CallbackSupport {
	static final boolean $assertionsDisabled = !CallbackSupport.class.desiredAssertionStatus();

	private static void checkNotNull(Object callback) {
		if(callback == null) {
			throw new NullPointerException("callback");
		}
	}

	public static Object[] addCallbackToList(Object[] curList, Object callback, Class clazz) {
		checkNotNull(callback);
		int curLength = curList == null ? 0 : curList.length;
		Object[] newList = (Object[])Array.newInstance(clazz, curLength + 1);
		if(curLength > 0) {
			System.arraycopy(curList, 0, newList, 0, curLength);
		}

		newList[curLength] = callback;
		return newList;
	}

	public static int findCallbackPosition(Object[] list, Object callback) {
		checkNotNull(callback);
		if(list != null) {
			int i = 0;

			for(int n = list.length; i < n; ++i) {
				if(list[i] == callback) {
					return i;
				}
			}
		}

		return -1;
	}

	public static Object[] removeCallbackFromList(Object[] curList, int index) {
		int curLength = curList.length;
		if($assertionsDisabled || index >= 0 && index < curLength) {
			if(curLength == 1) {
				return null;
			} else {
				int newLength = curLength - 1;
				Object[] newList = (Object[])Array.newInstance(curList.getClass().getComponentType(), newLength);
				System.arraycopy(curList, 0, newList, 0, index);
				System.arraycopy(curList, index + 1, newList, index, newLength - index);
				return newList;
			}
		} else {
			throw new AssertionError();
		}
	}

	public static Object[] removeCallbackFromList(Object[] curList, Object callback) {
		int idx = findCallbackPosition(curList, callback);
		if(idx >= 0) {
			curList = removeCallbackFromList(curList, idx);
		}

		return curList;
	}

	public static void fireCallbacks(Runnable[] callbacks) {
		if(callbacks != null) {
			Runnable[] runnable4 = callbacks;
			int i3 = callbacks.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				Runnable cb = runnable4[i2];
				cb.run();
			}
		}

	}

	public static void fireCallbacks(CallbackWithReason[] callbacks, Enum reason) {
		if(callbacks != null) {
			CallbackWithReason[] callbackWithReason5 = callbacks;
			int i4 = callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				CallbackWithReason cb = callbackWithReason5[i3];
				cb.callback(reason);
			}
		}

	}
}
