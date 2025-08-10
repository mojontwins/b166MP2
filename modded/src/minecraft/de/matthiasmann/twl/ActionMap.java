package de.matthiasmann.twl;

import de.matthiasmann.twl.utils.ClassUtils;
import de.matthiasmann.twl.utils.HashEntry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionMap {
	public static final int FLAG_ON_PRESSED = 1;
	public static final int FLAG_ON_RELEASE = 2;
	public static final int FLAG_ON_REPEAT = 4;
	private ActionMap.Mapping[] mappings = new ActionMap.Mapping[16];
	private int numMappings;

	public boolean invoke(String action, Event event) {
		ActionMap.Mapping mapping = (ActionMap.Mapping)HashEntry.get(this.mappings, action);
		if(mapping != null) {
			mapping.call(event);
			return true;
		} else {
			return false;
		}
	}

	public void addMapping(String action, Object target, String methodName, Object[] params, int flags) throws IllegalArgumentException {
		if(action == null) {
			throw new NullPointerException("action");
		} else {
			Method[] method9;
			int i8 = (method9 = target.getClass().getMethods()).length;

			for(int i7 = 0; i7 < i8; ++i7) {
				Method m = method9[i7];
				if(m.getName().equals(methodName) && !Modifier.isStatic(m.getModifiers()) && ClassUtils.isParamsCompatible(m.getParameterTypes(), params)) {
					this.addMappingImpl(action, target, m, params, flags);
					return;
				}
			}

			throw new IllegalArgumentException("Can\'t find matching method: " + methodName);
		}
	}

	public void addMapping(String action, Class targetClass, String methodName, Object[] params, int flags) throws IllegalArgumentException {
		if(action == null) {
			throw new NullPointerException("action");
		} else {
			Method[] method9;
			int i8 = (method9 = targetClass.getMethods()).length;

			for(int i7 = 0; i7 < i8; ++i7) {
				Method m = method9[i7];
				if(m.getName().equals(methodName) && Modifier.isStatic(m.getModifiers()) && ClassUtils.isParamsCompatible(m.getParameterTypes(), params)) {
					this.addMappingImpl(action, (Object)null, m, params, flags);
					return;
				}
			}

			throw new IllegalArgumentException("Can\'t find matching method: " + methodName);
		}
	}

	public void addMapping(String action, Object target, Method method, Object[] params, int flags) {
		if(action == null) {
			throw new NullPointerException("action");
		} else if(!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalArgumentException("Method is not public");
		} else if(target == null && !Modifier.isStatic(method.getModifiers())) {
			throw new IllegalArgumentException("Method is not static but target is null");
		} else if(target != null && method.getDeclaringClass().isInstance(target)) {
			throw new IllegalArgumentException("method does not belong to target");
		} else if(!ClassUtils.isParamsCompatible(method.getParameterTypes(), params)) {
			throw new IllegalArgumentException("Paramters don\'t match method");
		} else {
			this.addMappingImpl(action, target, method, params, flags);
		}
	}

	public void addMapping(Object target) {
		Method[] method5;
		int i4 = (method5 = target.getClass().getMethods()).length;

		for(int i3 = 0; i3 < i4; ++i3) {
			Method m = method5[i3];
			ActionMap.Action action = (ActionMap.Action)m.getAnnotation(ActionMap.Action.class);
			if(action != null) {
				if(m.getParameterTypes().length > 0) {
					throw new UnsupportedOperationException("automatic binding of actions not supported for methods with parameters");
				}

				String name = m.getName();
				if(action.name().length() > 0) {
					name = action.name();
				}

				int flags = (action.onPressed() ? 1 : 0) | (action.onRelease() ? 2 : 0) | (action.onRepeat() ? 4 : 0);
				this.addMappingImpl(name, target, m, (Object[])null, flags);
			}
		}

	}

	protected void addMappingImpl(String action, Object target, Method method, Object[] params, int flags) {
		this.mappings = (ActionMap.Mapping[])HashEntry.maybeResizeTable(this.mappings, this.numMappings++);
		HashEntry.insertEntry(this.mappings, new ActionMap.Mapping(action, target, method, params, flags));
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Action {
		String name() default "";

		boolean onPressed() default true;

		boolean onRelease() default false;

		boolean onRepeat() default true;
	}

	static class Mapping extends HashEntry {
		final Object target;
		final Method method;
		final Object[] params;
		final int flags;

		Mapping(String key, Object target, Method method, Object[] params, int flags) {
			super(key);
			this.target = target;
			this.method = method;
			this.params = params;
			this.flags = flags;
		}

		void call(Event e) {
			Event.Type type = e.getType();
			if(type == Event.Type.KEY_RELEASED && (this.flags & 2) != 0 || type == Event.Type.KEY_PRESSED && (this.flags & 1) != 0 && (!e.isKeyRepeated() || (this.flags & 4) != 0)) {
				try {
					this.method.invoke(this.target, this.params);
				} catch (Exception exception4) {
					Logger.getLogger(ActionMap.class.getName()).log(Level.SEVERE, "Exception while invoking action handler", exception4);
				}
			}

		}
	}
}
