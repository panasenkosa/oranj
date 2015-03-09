
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

package org.oranj.mappings.annotations;

import java.lang.reflect.Field;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.ClassTypeMapping;
import org.oranj.mappings.FieldMapping;
import org.oranj.mappings.MappingHelper;
import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;

/*
	Loading mapping configuration of java class to oracle object type or oracle package defined by annotations.
 */

public class ClassMappingLoader {

	public static void loadMapping(String className, MappingHelper helper) throws InitConfigurationException {
		Class mappingClass = null;
		try {
			mappingClass = Utils.clasForName(className);
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException(e);
		}
		
		DBType oracleTypeName = (DBType)mappingClass.getAnnotation(DBType.class);
		if (oracleTypeName == null || StringUtils.isEmpty(oracleTypeName.name()))
			throw new InvalidMappingException("Class \"" + className
					+ "\" havn't @DBType annotation");		
		String sqlTypeName = oracleTypeName.name().toUpperCase();
		String owner = oracleTypeName.owner().toUpperCase();
		
		ClassTypeMapping mapping = new ClassTypeMapping(className, owner,
				sqlTypeName, helper.getOracleHelper());
		
		for (Field field : mapping.getMappingClass().getDeclaredFields()) {
			DBTypeField dbTypeField = field.getAnnotation(DBTypeField.class);
			//some fields can be not mapped, it's ok
			if (dbTypeField==null || dbTypeField.name()==null || dbTypeField.name().length()==0) {
				
			} else {
				FieldMapping fieldMapping = null;
				try {
					fieldMapping = new FieldMapping(dbTypeField);
				} catch (InitConfigurationException e) {
					//TODO: log bad field annotation in DBType annotated mapping
					throw e;
				}
				mapping.addMappingField(field, fieldMapping);
			}				
		}		
		mapping.defineDefaultGroup();
		helper.addMapping(mapping);	
	}	
}
