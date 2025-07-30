package com.mojontwins.utils;

import java.lang.reflect.Field;

public class MinecraftUtils {
	
	public static Object getPrivateValue(Class<?> class1, Object obj, int i) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field field = class1.getDeclaredFields()[i];
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException illegalaccessexception) {
			System.out.println ("An impossible error has occured! " + illegalaccessexception);
			return null;
		}
	}

	public static Object getPrivateValue(Class<Thread> class1, Object obj, String s) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field field = class1.getDeclaredField(s);
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException illegalaccessexception) {
			System.out.println ("An impossible error has occured! " + illegalaccessexception);
			return null;
		}
	}
}
