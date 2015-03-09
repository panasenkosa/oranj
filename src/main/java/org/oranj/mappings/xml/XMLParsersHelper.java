
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

package org.oranj.mappings.xml;

import java.lang.reflect.Method;

import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;
import org.oranj.exceptions.InvalidMappingException;

public class XMLParsersHelper {

	public static Class getClassByName(String className) throws InvalidMappingException {
		
		if (StringUtils.isEmpty(className))
			return null;		
		
		try {
			Class  clazz = Utils.clasForName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException(e);
		}		
	}
	
	
	public static Method getMethodByName(String methodName, Class... arguments) throws InvalidMappingException {
		
		if (StringUtils.isEmpty(methodName))
			return null;		

			Method method;
			try {
				method = Utils.findMethodByFullName(methodName, arguments);
				return method;
			}  catch (ClassNotFoundException e) {
				throw new InvalidMappingException(e);
			} catch (NoSuchMethodException e) {
				throw new InvalidMappingException(e);
			}
			
	
	}	
	
	public static void checkMethodReturnType(Method method, Class clazz) throws InvalidMappingException {
		
		if (method==null) return;
		
		Class returnClass = method.getReturnType();
		if (!returnClass.getName().equals(clazz.getName())) {
			throw new InvalidMappingException("method " + method.getName() + 
					" return type does not match " + clazz.getName());						
		}			
	}
}
