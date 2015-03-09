
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import oracle.sql.ARRAY;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.ArrayTypeMapping;
import org.oranj.dbprocs.enums.COLLECTION_MERGING;
import org.oranj.exceptions.DBObjectConversionException;

public class ArrayConverter implements AbstractConverter{
	
	private static Class defaultCollectionClass = ArrayList.class;
	
	private  AbstractConverter  elementValueConverter;		
	private ArrayTypeMapping mapping;
	
	public ArrayConverter(ArrayTypeMapping mapping) 
			throws InitConfigurationException {
		this.mapping = mapping;
		elementValueConverter = mapping.getBaseMapping().getValueConverter(mapping.getArgumentMapping());
	}
	
	public Object javaToOracle(Object value, Connection connection) throws DBObjectConversionException{
		
		if (value==null) return value;
		
		Object array;
		
		if (!mapping.getArgumentMapping().getArgumentClass().isArray()) {
			array = ((Collection)value).toArray();
		} else
			array = value;
			
		int size = Array.getLength(array);		
		Object resultArray = Array.newInstance(Object.class, size);
		
		int idx = 0;
		while (idx<size) {
			Object currentValue = Array.get(array, idx);
			Object newValue = null;
			if(currentValue!=null) {
				newValue = elementValueConverter.javaToOracle(currentValue, connection);
			}
			Array.set(resultArray, idx, newValue);	
			idx++;
		}
		
		ARRAY result;
		try {
			result = new ARRAY(mapping.getArrayDescriptor() , connection, (Object[]) resultArray);
		} catch (SQLException e) {
			throw new DBObjectConversionException
				("Error creating oracle ARRAY \"" + mapping.getSqlTypeName() +"\"", e);
		}
		
		return result;
	}

	/*
	 * In the case of in/out stored procedure agrument,
	 * there will be object update after returing from oracle
	 */
	public Object updateInstance(Object value, Object oldValue) throws DBObjectConversionException{
		
		if (value==null) return value;
		
		Object result;
		
		oracle.sql.ARRAY array = (oracle.sql.ARRAY)value;
		Object[] values;
		try {
			values = (Object[])array.getArray();
		} catch (SQLException e) {
			throw new DBObjectConversionException
				("Error getting oracle ARRAY \"" + mapping.getSqlTypeName() +"\"", e);
		}
				
		Class javaClass = mapping.getArgumentMapping().getArgumentClass();
		Class elementClass = mapping.getArgumentMapping().getElementClass();

		String errorMessage = "Error in instantiating collection \"" + javaClass.getName()+ "\"";
		try {
			if (javaClass.isArray())
				result = Array.newInstance(elementClass, values.length);
			else if (javaClass.isInterface())
				result = defaultCollectionClass.newInstance();
			else
				result = javaClass.newInstance();
		} catch (InstantiationException e1) {
			throw new DBObjectConversionException(
					errorMessage, e1);
		} catch (IllegalAccessException e1) {
			throw new DBObjectConversionException(
					errorMessage, e1);
		}

		int idx=0;
		while (idx<values.length) {
			
			Object currentValue = values[idx];
			Object newValue = null;
			if (currentValue!=null)
				newValue = elementValueConverter.oracleToJava(currentValue);
			
			if (javaClass.isArray())	
				Array.set(result, idx, newValue);						
			else
				((Collection)result).add(newValue);
			
			idx++;
		}		

		if (oldValue!=null) {
			if (javaClass.isArray()) 
				result = refreshOldArray(oldValue, result);
			 else
				result = refreshOldCollection((Collection)oldValue, (Collection)result, values);
				 
		}
		
		return result;
	}

	public Object oracleToJava(Object value)
			throws DBObjectConversionException {
		return updateInstance(value, null);
	}

	private Object refreshOldArray(Object oldArray, Object newArray) throws DBObjectConversionException {
		
		int oldSize = Array.getLength(oldArray);
		int newSize = Array.getLength(newArray);
		if (newSize>oldSize)
			throw new DBObjectConversionException("Array size of out parameter greater than source array size");
		int idx = 0;
		while (idx<oldSize) {
			Object newValue = null;
			if (idx<newSize) {
				newValue = Array.get(newArray, idx);
			}
			Array.set(oldArray, idx, newValue);
			idx++;
		}
		
		return oldArray;
	}
	
	private Object refreshOldCollection(Collection oldCollection, 
						Collection newCollection, Object[] sourceObjects) throws DBObjectConversionException{
			
		COLLECTION_MERGING collectionMerging = mapping.getArgumentMapping().getCollectionMerging();
		
		if (collectionMerging==COLLECTION_MERGING.APPEND) {
			oldCollection.addAll((Collection)newCollection);
		} else if (collectionMerging==COLLECTION_MERGING.RELOAD) {
			oldCollection.clear();
			oldCollection.addAll((Collection)newCollection);
			
		} else if (collectionMerging==COLLECTION_MERGING.UPDATE) {			
			//TODO: second time conversion, no need really
			for (Object sourceObject : sourceObjects) {
				boolean foundedEquals = false;				
				Object newValue = null;
				if (sourceObject!=null)
					newValue = elementValueConverter.oracleToJava(sourceObject);
				if (newValue!=null) {
					for (Object oldValue : oldCollection) {
						if (newValue.equals(oldValue)) {
							elementValueConverter.updateInstance(sourceObject, oldValue);
							foundedEquals = true;
							break;
						}
					}
					if (!foundedEquals) 
						oldCollection.add(newValue);					
				}
				
			}			
		}					
		return oldCollection;
		
	}		
	
}
