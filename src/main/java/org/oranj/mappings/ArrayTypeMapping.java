
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

import oracle.sql.ArrayDescriptor;

import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.Utils;
import org.oranj.convert.AbstractConverter;
import org.oranj.convert.ArrayConverter;
import org.oranj.exceptions.InvalidMappingException;

public class ArrayTypeMapping extends TypeMapping{

	private ArrayDescriptor  arrayDescriptor;	
	private ArgumentMapping  argumentMapping;
	private TypeMapping      baseMapping;
	
	public ArrayTypeMapping(ArgumentMapping argumentMapping, BasePlsqlType sqlType,
			MappingHelper mappingHelper) throws InitConfigurationException {
		
		super(argumentMapping.getArgumentClass().getName(), sqlType.getOwner(), sqlType.getFullName());
		
		this.argumentMapping = argumentMapping;
		if (!Utils.isArrayOrCollection(argumentMapping.getArgumentClass()))
			throw new InvalidMappingException("Oracle type \"" + sqlType.getFullName() + "\" and \"" +
					argumentMapping.getArgumentClass().getName() + "\" are not compatible, because "  
					+ " only collection or array class can be mapped to oracle collection");
		
		if (argumentMapping.getElementClass()==null)
			throw new InvalidMappingException(
					"Collection element class not defined for \""
							+ sqlType.getFullName() + "\" oracle collection type mapping");
		
		arrayDescriptor = mappingHelper.getOracleHelper().getArrayDescriptor(sqlType.getFullName());
		try {
			baseMapping = mappingHelper.getSimpleMapping(argumentMapping
					.getElementClass(), sqlType.getSubElementType().getOwner(), sqlType.getSubElementType().getName());
		} catch(InitConfigurationException e) {
			//TODO: this to log
			//	throw new InvalidMappingException("Java collection \"" + argumentMapping.getArgumentClass().getName() +					
			//			"\" and oracle collection \"" + sqlType.getExactName() + "\" are not compatible", e);			
			throw e;
		}				
	}

	@Override
	public TYPE_MAPPING getMappingType() {
		return TYPE_MAPPING.ARRAY;
	}

	@Override
	public AbstractConverter getValueConverter(ArgumentMapping argumentMapping)
			throws InitConfigurationException{
		return new ArrayConverter(this);
	}

	public ArrayDescriptor getArrayDescriptor() {
		return arrayDescriptor;
	}

	public ArgumentMapping getArgumentMapping() {
		return argumentMapping;
	}

	public TypeMapping getBaseMapping() {
		return baseMapping;
	}
	
}
