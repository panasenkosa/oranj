
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

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.FieldGroup;
import org.oranj.mappings.FieldMapping;

public class FieldConverter {
	
	FieldMapping			  mapping;
	AbstractConverter 		  converter;	
	
	FieldConverter(FieldGroup.GroupElement groupElement, int direction)
			throws InitConfigurationException {
		
		this.mapping = groupElement.getFieldMapping();
		
		String inGroupName = null;
		String outGroupName = null;
		
		if (direction==ClassConverter.IN)
			inGroupName = groupElement.getGroupName();
		else
			outGroupName = groupElement.getGroupName();
		
		mapping.setInGroupName(inGroupName);
		mapping.setOutGroupName(outGroupName);
		
		converter = mapping.getTypeMapping().getValueConverter(mapping);
		
	}

	public FieldMapping getMapping() {
		return mapping;
	}

	public AbstractConverter getConverter() {
		return converter;
	}
	
	
}
