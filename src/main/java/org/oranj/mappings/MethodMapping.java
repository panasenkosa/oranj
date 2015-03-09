
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

package org.oranj.mappings;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.annotations.DBProcedure;

public class MethodMapping {

	private List<ArgumentMapping> arguments = new ArrayList<ArgumentMapping>();
	private Method					javaMethod;
	private String					dbProcedureName;
	private String					inGroup;
	private String					outGroup;
	
	private String			javaMethodName;
	
	private ArgumentMapping		    returnResult;
	
	public MethodMapping(DBProcedure methodAnnotation, Method javaMethod) {
		this.dbProcedureName = methodAnnotation.name().toUpperCase();
		this.inGroup = methodAnnotation.inPropertyGroup();
		this.outGroup = methodAnnotation.outPropertyGroup();
		this.javaMethod = javaMethod;		
	}
	
	public MethodMapping(Method javaMethod, String dbProcedureName, String inGroup, String outGroup) {
		this.dbProcedureName = dbProcedureName;
		this.inGroup = inGroup;
		this.outGroup = outGroup;
		this.javaMethod = javaMethod;
	}	
	
	public MethodMapping(String javaMethodName, String dbProcedureName, String inGroup, String outGroup) {
		this.dbProcedureName = dbProcedureName;
		this.inGroup = inGroup;
		this.outGroup = outGroup;
		this.javaMethodName = javaMethodName;
	}
	
	public void setReturnResult(ArgumentMapping argument) {
		returnResult = argument;
		argument.position = -1;
		argument.dbName = null;	
	}
	
	public void addMappedArgument(ArgumentMapping argument) {		
		this.arguments.add(argument);
	}
	
	public void findJavaMethod(Class proxyInterface) throws InvalidMappingException, SecurityException, NoSuchMethodException{
		
		int argumentsCount = arguments.size();			
		//TODO: check zero-size array
		Object methodArgumentClasses = Array.newInstance(Class.class, argumentsCount);
		for (int i=0;i<argumentsCount;i++) {
			ArgumentMapping arg = findArgumentByPosition(i);
			if (arg==null)
				throw new InvalidMappingException("Argument with position "
						+ i + " in procedure \"" + dbProcedureName + "\" mapping missed, interface \""
						+ proxyInterface.getName() + "\" mapping");
			Array.set(methodArgumentClasses, i, arg.getArgumentClass());
		}		
		this.javaMethod = proxyInterface.getDeclaredMethod(javaMethodName, (Class[])methodArgumentClasses);
				
	}
	
	public ArgumentMapping findArgumentByPosition(int position) {
		for (ArgumentMapping argument : arguments) {
			if (argument.position==position)
				return argument;
		}
		return null;
	}
	
	public ArgumentMapping findArgumentByName(String name) {
		for (ArgumentMapping argument : arguments) {
			if (name.equalsIgnoreCase(argument.dbName))					
				return argument;
		}
		return null;
	}		
	
	
	
	public ArgumentMapping getReturnResult() {
		return returnResult;
	}

	public Method getJavaMethod() {
		return javaMethod;
	}

	public String getDbProcedureName() {
		return dbProcedureName;
	}

	public String getInGroup() {
		return inGroup;
	}

	public String getOutGroup() {
		return outGroup;
	}
}
