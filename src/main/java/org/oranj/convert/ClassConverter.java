
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

package org.oranj.convert;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.sql.STRUCT;

import org.oranj.exceptions.DBObjectConversionException;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.ArgumentMapping;
import org.oranj.mappings.ClassTypeMapping;
import org.oranj.mappings.FieldGroup;
import org.oranj.mappings.FieldMapping;

public class ClassConverter implements AbstractConverter{
		
	ClassTypeMapping 	 mapping;
	ArgumentMapping		 argumentMapping;
	
	List<FieldConverter> inFieldConverters  = new ArrayList<FieldConverter>();
	List<FieldConverter> outFieldConverters = new ArrayList<FieldConverter>();
	
	static int IN 	= 1;
	static int OUT  = 2;
	
	public ClassConverter(ClassTypeMapping mapping, ArgumentMapping argumentMapping)
			throws InitConfigurationException{
		this.mapping = mapping;
		this.argumentMapping = argumentMapping;		
		loadFieldConverters (argumentMapping.getInGroupName(),  inFieldConverters, IN);				
		loadFieldConverters (argumentMapping.getOutGroupName(), outFieldConverters, OUT);

	}

	private void loadFieldConverters(String groupName,
			List<FieldConverter> fieldConverters, int direction)
			throws InitConfigurationException {
		
		FieldGroup group = mapping.getFieldGroup(groupName);
		for (FieldGroup.GroupElement element : group.getElements().values()) {
			FieldConverter converter = new FieldConverter(element, direction);
			fieldConverters.add(converter);
		}
		
	}

	public Object javaToOracle(Object value, Connection connection) throws DBObjectConversionException{
		
		if (value==null) return value;
		
		int size;
		try {
			size = mapping.getDescriptor().getMetaData().getColumnCount();
		} catch (SQLException e1) {
			throw new DBObjectConversionException("Error getting \"" + mapping.getSqlTypeName() +
					"\" STRUCT descriptor metadata");
		}
	
		Object array = Array.newInstance(Object.class, size);
		
		for (FieldConverter fieldConverter : inFieldConverters) {
			
			FieldMapping fieldMapping = fieldConverter.getMapping();
			Object fieldValue;
			
			String errorMessage = "Error when calling getter method for "
				+ fieldMapping.getField().getName() + " field of " + mapping.getMappingClass() + " class";
			try {
				fieldValue = fieldMapping.getGetMethod().invoke(value);
			} catch (IllegalAccessException e) {
				throw new DBObjectConversionException(errorMessage, e);
			} catch (InvocationTargetException e) {
				throw new DBObjectConversionException(errorMessage, e);
			}

			if (fieldValue!=null) {
				//metadata index starts from 1, but all arrays from 0
				int idx = fieldMapping.getIdxInStruct()-1;						
				Object attrValue = fieldConverter.getConverter().javaToOracle(fieldValue, connection);
			    Array.set(array, idx, attrValue);					
			}					
		}	
		
		oracle.sql.STRUCT struct;
		try {
			struct = new STRUCT (mapping.getDescriptor(), connection, (Object[])array);
		} catch (SQLException e) {
			throw new DBObjectConversionException
				("Error creating \"" + mapping.getSqlTypeName() + "\" oracle STRUCT");
		}

		return struct;
	}

	public Object oracleToJava(Object value) throws DBObjectConversionException{
		return updateInstance(value, null);
	}
	
	/*
	 * In the case of in/out stored procedure agrument,
	 * there will be object update after returing from oracle
	 */
	public Object updateInstance(Object value, Object oldValue) throws DBObjectConversionException{
		
		if (value==null) return value;
		
		Object instance = null;
		Class baseClass = mapping.getMappingClass();
		
		String errorMessage = "Error in new instance of '" + baseClass.getName() + "' creation";		
		if (oldValue==null) {

			try {
				instance = baseClass.newInstance();
			} catch (InstantiationException e) {
				throw new DBObjectConversionException(errorMessage, e);
			} catch (IllegalAccessException e) {
				throw new DBObjectConversionException(errorMessage, e);
			}
			
		} else
			instance = oldValue;

		STRUCT struct = (oracle.sql.STRUCT) value;
		Object[] attrValues;
		try {
			attrValues = struct.getAttributes();
		} catch (SQLException e1) {
			throw new DBObjectConversionException
			("Error getting \"" + mapping.getSqlTypeName() + "\" oracle STRUCT attributes");
		}
		
		for (FieldConverter fieldConverter : outFieldConverters) {
			
			FieldMapping fieldMapping = fieldConverter.getMapping();
			//metadata index starts from 1, but all arrays from 0
			int idx = fieldMapping.getIdxInStruct()-1;		
			Object attrValue = null;
			if (attrValues[idx] != null) 
				attrValue = fieldConverter.getConverter().oracleToJava(attrValues[idx]);		
					
			
			errorMessage = "Error calling setter method for \""
				+ fieldMapping.getField().getName() + "\" field of \"" + baseClass.getName() + "\" class";
				//TODO: primitives can't set null, need to handle this case
			if (attrValue == null
					&& fieldMapping.getField().getType().isPrimitive()) {
			} else
				try {
					fieldMapping.getSetMethod().invoke(instance, attrValue);
				} catch (IllegalAccessException e) {
					throw new DBObjectConversionException(errorMessage, e);
				} catch (InvocationTargetException e) {
					throw new DBObjectConversionException(errorMessage, e);
				}
			
		}			
			
		return instance;
	}

}

	