
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

package org.oranj.dbprocs;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.ArgumentMapping;
import org.oranj.mappings.MappingHelper;
import org.oranj.convert.AbstractConverter;
import org.oranj.dbprocs.enums.ARGUMENT;

public class MappedArgument {

	OraArgument			baseArgument;
	ArgumentMapping argumentMapping;
	
	AbstractConverter 	valueConverter;	
	
	MappedArgument(OraArgument baseArgument, ArgumentMapping argumentMapping) {
		this.baseArgument = baseArgument;
		this.argumentMapping = argumentMapping;		
	}	
	
	void createValueConverter(MappingHelper helper) throws InitConfigurationException {
		valueConverter = helper.getConverter(baseArgument.getDataType(), argumentMapping);		
	}

	public AbstractConverter getValueConverter() {
		return valueConverter;
	}
	
	public int getBindIndex() {
		return baseArgument.bindIndex;
	}

	public int getOutCursorIndex() {
		return baseArgument.outCursorIndex;
	}

	public int getJavaArgumentIndex() {
		return argumentMapping.getPosition();
	}			
	
	public ARGUMENT getInout() {
		return baseArgument.inout;
	}
	
	public String getName() {
		return baseArgument.name;
	}	
}
