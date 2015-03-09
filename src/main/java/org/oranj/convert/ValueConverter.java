
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.oranj.mappings.ValueTypeMapping;
import org.oranj.exceptions.DBObjectConversionException;

public class ValueConverter implements AbstractConverter{

	ValueTypeMapping mapping;
	
	public ValueConverter(ValueTypeMapping mapping) {
		this.mapping = mapping;
	}

	public Object javaToOracle(Object value, Connection connection) throws DBObjectConversionException{
		
		if (value==null) return value;
		
		Method valueMethod = ((ValueTypeMapping)mapping).getToOracleMethod();
		
		if (valueMethod!=null) {
			String errorMessage = "Error when calling custom conversion method of "
				+ ((ValueTypeMapping) mapping).getClass().getName() + " class";			
			try {
				value = valueMethod.invoke(null, value, connection);
			} catch (IllegalAccessException e) {
				throw new DBObjectConversionException(errorMessage, e);
			} catch (InvocationTargetException e) {
				throw new DBObjectConversionException(errorMessage, e);
			}
						
		}							
		
		return value;
	}

	public Object oracleToJava(Object value) throws DBObjectConversionException{		
	
		if (value==null) return value;
		
		Method valueMethod = ((ValueTypeMapping)mapping).getFromOracleMethod();
			
		if (valueMethod!=null) {
			String errorMessage = "Error when calling custom conversion method of "
				+ ((ValueTypeMapping) mapping).getClass().getName() + " class";						
			try {
				value = valueMethod.invoke(null, value);
			} catch (IllegalAccessException e) {
				throw new DBObjectConversionException(errorMessage, e);
			} catch (InvocationTargetException e) {
				throw new DBObjectConversionException(errorMessage, e);
			}
		
		}		
		return value;
	}

	public Object updateInstance(Object value, Object oldValue)
			throws DBObjectConversionException {
		return oracleToJava(value);
	}	
}
