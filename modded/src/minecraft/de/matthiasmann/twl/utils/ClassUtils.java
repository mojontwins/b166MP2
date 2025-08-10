package de.matthiasmann.twl.utils;

import java.util.HashMap;

public class ClassUtils {
	private static final HashMap primitiveTypeMap = new HashMap();

	static {
		primitiveTypeMap.put(Boolean.TYPE, Boolean.class);
		primitiveTypeMap.put(Byte.TYPE, Byte.class);
		primitiveTypeMap.put(Short.TYPE, Short.class);
		primitiveTypeMap.put(Character.TYPE, Character.class);
		primitiveTypeMap.put(Integer.TYPE, Integer.class);
		primitiveTypeMap.put(Long.TYPE, Long.class);
		primitiveTypeMap.put(Float.TYPE, Float.class);
		primitiveTypeMap.put(Double.TYPE, Double.class);
	}

	public static Class mapPrimitiveToWrapper(Class clazz) {
		Class mappedClass = (Class)primitiveTypeMap.get(clazz);
		return mappedClass != null ? mappedClass : clazz;
	}

	public static boolean isParamCompatible(Class type, Object obj) {
		if(obj == null && !type.isPrimitive()) {
			return true;
		} else {
			type = mapPrimitiveToWrapper(type);
			return type.isInstance(obj);
		}
	}

	public static boolean isParamsCompatible(Class[] types, Object[] params) {
		if(types.length != params.length) {
			return false;
		} else {
			for(int i = 0; i < types.length; ++i) {
				if(!isParamCompatible(types[i], params[i])) {
					return false;
				}
			}

			return true;
		}
	}
}
