
/*
 * Copyright (C) 2010 Sergey Panasenko
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

package org.oranj.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.annotations.DBArgument;

public class Utils {
	
	public static Field findClassField(Class clazz, String fieldName) throws NoSuchFieldException {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			if (clazz.getSuperclass()!=null)
				return (findClassField(clazz.getSuperclass(), fieldName));
			else
				throw new NoSuchFieldException();
		}		
		return field;
	}
	
	public static DBArgument 	getParameterAnnotation(Method method, int idx) throws InvalidMappingException {		
		Annotation[][] annots = method.getParameterAnnotations();
		for (Annotation annotation : annots[idx]) {
			if (annotation instanceof DBArgument) {
				DBArgument result = (DBArgument)annotation;
				if (result!=null)
					return result;
				else
					break;						
			}
		}
		throw new InvalidMappingException ("No annotation for " + (new Integer(idx)).toString()
				+ " parameter of " + method.getName() + " method");
	}	
	
	public static boolean isObjectClassMethod(Method method) {	
		Object baseObject = new Object();
		for (Method baseMethod : baseObject.getClass().getMethods())
			if (baseMethod.equals(method))
				return true;		
		return false;
	}
	
	
	public static boolean isFunction(Method method) {
		String returnType = method.getReturnType().getName();
		return (!returnType.equals("void"));
	}	

	/*
	 * Class.forName() works buggy for scalar type so we do all manually
	 */
	public static Class clasForName(String className)
			throws ClassNotFoundException {
		if (className.equals("boolean")) {
			return boolean.class;
		} else if (className.equals("byte")) {
			return byte.class;
		} else if (className.equals("short")) {
			return short.class;
		} else if (className.equals("int")) {
			return int.class;
		} else if (className.equals("long")) {
			return long.class;
		} else if (className.equals("float")) {
			return float.class;
		} else if (className.equals("double")) {
			return double.class;
		} else if (className.equals("char")) {
			return char.class;
		} else if (className.equals("void")) {
			return void.class;
		}else {
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		}
	}
	
	
	private static String generateGetter(Field field) {
		return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}	
	
	private static String generateSetter(Field field) {
		return "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}	
	
	private static String generateBooleanGetter(Field field) {
		if ("is".equals(field.getName().substring(0, 2))) {
			if (field.getName().substring(2, 1).toUpperCase().equals(field.getName().substring(2, 1)))
				return field.getName();				
							
		}	
		return "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);		
	}	
	
	private static String generateBooleanSetter(Field field) {
		if ("is".equals(field.getName().substring(0, 2))) {
			if (field.getName().substring(2, 1).toUpperCase().equals(field.getName().substring(2, 1)))
				return "set" + field.getName().substring(2, 1).toUpperCase() + field.getName().substring(3);			
		}
		return generateSetter(field);
	}		
	
	/*
	 * To search setter method for class field
	 */
	public static Method findSetterMethod(Field field)
			throws SecurityException, NoSuchMethodException {
		Class clazz = field.getDeclaringClass();
		String setterName = Utils.generateSetter(field);
		Method setterMethod = null;
		try {
			setterMethod = clazz.getMethod(setterName, field.getType());
		} catch (NoSuchMethodException e) {
			
			if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {				
				setterName = Utils.generateBooleanSetter(field);
				setterMethod = clazz.getMethod(setterName, field.getType());
			} 
			else
			throw new NoSuchMethodException("No setter method for " + field.getName() + " field");		
		}
		return setterMethod;
	}
	/*
	 * To search getter method for class field
	 */
	public static Method findGetterMethod(Field field)
			throws SecurityException, NoSuchMethodException {
		String getterName = Utils.generateGetter(field);
		Class clazz = field.getDeclaringClass();
		Method getterMethod = null;
		try {
			getterMethod = clazz.getMethod(getterName);
		} catch (NoSuchMethodException e) {
			if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {				
				getterName = Utils.generateBooleanGetter(field);
				getterMethod = clazz.getMethod(getterName);			
			} else
			throw new NoSuchMethodException("No getter method for " + field.getName() + " field");			
		}
		
		if (!field.getType().getName().equals(getterMethod.getReturnType().getName()))
			throw new NoSuchMethodException("Bad getter return type");
		return getterMethod;
	}	
	
	/*
	 * Search method by full-name format "packageName.className.methodName"
	 */				
	public static Method findMethodByFullName(String methodFullName, Class... clazzes) throws ClassNotFoundException,  NoSuchMethodException 
				{
		int lastDotIndex = methodFullName.lastIndexOf('.');
		String methodName = methodFullName.substring(lastDotIndex+1);
		String className = methodFullName.substring(0, lastDotIndex);
		Class clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		Method method = clazz.getMethod(methodName, clazzes);		
		return method;
	}

	
	public static boolean isArrayOrCollection(Class clazz) {
		if (clazz.isArray())
			return true;
		return Collection.class.isAssignableFrom(clazz);
	}
	
	
}
