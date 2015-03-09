
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

import java.util.HashMap;

import org.oranj.dbprocs.OracleHelper;
import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.convert.AbstractConverter;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.TypeMapping.MappingKey;

public class MappingHelper {			
			
	private OracleHelper oracleHelper;
	
	private HashMap<MappingKey,  TypeMapping> 		 mappings = new HashMap<MappingKey, TypeMapping>();	
	
	public MappingHelper(OracleHelper oracleHelper)
			throws InitConfigurationException {
		this.oracleHelper = oracleHelper;		
	}		
	
	public AbstractConverter getConverter(BasePlsqlType plsqlType,ArgumentMapping argumentMapping)
			throws InitConfigurationException {
		TypeMapping mapping = getMapping(argumentMapping, plsqlType);
		return mapping.getValueConverter(argumentMapping);
	}	
	
	public TypeMapping getMapping(ArgumentMapping argumentMapping,
			BasePlsqlType sqlType) throws InitConfigurationException {
		
		if (!sqlType.isSupported())
			throw new InvalidMappingException("Type " + sqlType.getOracleTypeCode()
					+ " not supported");
		
		if (sqlType.isArray())
			return new ArrayTypeMapping(argumentMapping, sqlType, this);		

		return getSimpleMapping(argumentMapping.getArgumentClass(), sqlType.getOwner(), sqlType.getName());
	}
	
	public TypeMapping getSimpleMapping(Class clazz, String owner, String sqlTypeName)
			throws InvalidMappingException {
		MappingKey key = new MappingKey(clazz, owner, sqlTypeName);
		TypeMapping result = mappings.get(key);
		if (result==null) 
			throw new InvalidMappingException("There is no mapping for class " + clazz.getName() + 
					" and sql type " + sqlTypeName);
		return result;		
	}
	
	public void addMapping(TypeMapping mapping) {
		MappingKey key = new MappingKey(mapping);
		mappings.put(key, mapping);
	}		

	public void completeMappings() throws InitConfigurationException {
		for (TypeMapping mapping : mappings.values())
			mapping.completeMapping(this);		
	}			
	
	public OracleHelper getOracleHelper() {
		return oracleHelper;
	}

	public HashMap<MappingKey, TypeMapping> getMappings() {
		return mappings;
	}
	
	
	
}

	

