package de.matthiasmann.twl;

import java.util.Collection;
import java.util.Iterator;

public class DebugHook {
	private static ThreadLocal tls = new ThreadLocal() {
		protected DebugHook initialValue() {
			return new DebugHook();
		}

		protected Object initialValue() {
			return this.initialValue();
		}
	};

	public static DebugHook getDebugHook() {
		return (DebugHook)tls.get();
	}

	public static DebugHook installHook(DebugHook hook) {
		if(hook == null) {
			throw new NullPointerException("hook");
		} else {
			DebugHook old = (DebugHook)tls.get();
			tls.set(hook);
			return old;
		}
	}

	public void beforeApplyTheme(Widget widget) {
	}

	public void afterApplyTheme(Widget widget) {
	}

	public void missingTheme(String themePath) {
		System.err.println("Could not find theme: " + themePath);
	}

	public void missingChildTheme(ThemeInfo parent, String theme) {
		System.err.println("Missing child theme \"" + theme + "\" for \"" + parent.getThemePath() + "\"");
	}

	public void missingParameter(ParameterMap map, String paramName, String parentDescription, Class dataType) {
		StringBuilder sb = (new StringBuilder("Parameter \"")).append(paramName).append("\" ");
		if(dataType != null) {
			sb.append("of type ");
			if(dataType.isEnum()) {
				sb.append("enum ");
			}

			sb.append('\"').append(dataType.getSimpleName()).append('\"');
		}

		sb.append(" not set");
		if(map instanceof ThemeInfo) {
			sb.append(" for \"").append(((ThemeInfo)map).getThemePath()).append("\"");
		} else {
			sb.append(parentDescription);
		}

		System.err.println(sb.toString());
	}

	public void wrongParameterType(ParameterMap map, String paramName, Class expectedType, Class foundType, String parentDescription) {
		System.err.println("Parameter \"" + paramName + "\" is a " + foundType.getSimpleName() + " expected a " + expectedType.getSimpleName() + parentDescription);
	}

	public void wrongParameterType(ParameterList map, int idx, Class expectedType, Class foundType, String parentDescription) {
		System.err.println("Parameter at index " + idx + " is a " + foundType.getSimpleName() + " expected a " + expectedType.getSimpleName() + parentDescription);
	}

	public void replacingWithDifferentType(ParameterMap map, String paramName, Class oldType, Class newType, String parentDescription) {
		System.err.println("Paramter \"" + paramName + "\" of type " + oldType + " is replaced with type " + newType + parentDescription);
	}

	public void missingImage(String name) {
		System.err.println("Could not find image: " + name);
	}

	public void guiLayoutValidated(int iterations, Collection loop) {
		if(loop != null) {
			System.err.println("WARNING: layout loop detected - printing");
			int index = 1;

			for(Iterator iterator5 = loop.iterator(); iterator5.hasNext(); ++index) {
				Widget w = (Widget)iterator5.next();
				System.err.println(index + ": " + w);
			}
		}

	}
}
