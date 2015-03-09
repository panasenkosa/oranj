
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import org.oranj.exceptions.InitConfigurationException;
import org.oranj.convert.AbstractConverter;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.ArgumentMapping;
import org.oranj.mappings.MappingHelper;
import org.oranj.mappings.MethodMapping;

public class MappedProcedure {

	OraProcedure			baseProcedure;
	
	List<MappedArgument> arguments = new ArrayList<MappedArgument>();
	
	MappedArgument	returnType;
	
	Method      	javaMethod;
	MappedObject 	mappedObject;
	
	ArgumentMapping selfArgumentMapping;
	
	AbstractConverter baseValueConverter;	

	MappedProcedure(MethodMapping method, OraProcedure baseProcedure, MappedObject mappedObject) throws InvalidMappingException {
		this.baseProcedure = baseProcedure;
		this.javaMethod = method.getJavaMethod();
		
		selfArgumentMapping = new ArgumentMapping();
		selfArgumentMapping.setInGroupName(method.getInGroup());
		selfArgumentMapping.setOutGroupName(method.getOutGroup());
		this.mappedObject = mappedObject;
		selfArgumentMapping.setArgumentClass(mappedObject.typeMappingClass);

		if (baseProcedure.isFunction())  {
			if (method.getReturnResult()==null) 
				throw new InvalidMappingException("Return type of \""
						+ baseProcedure.getFullName() + "\" stored procedure not mapped");
			returnType = new MappedArgument(baseProcedure.getReturnType(), method.getReturnResult());			
		}
			
		for (OraArgument argument : baseProcedure.getArguments()) {
			ArgumentMapping argumentMapping = method.findArgumentByName(argument.getName());
			if (argumentMapping==null)
				throw new InvalidMappingException("Argument \"" + argument.getName() + 
						"\" of db-procedure \"" + baseProcedure.getFullName() + "\" not mapped");
			MappedArgument mappedArgument = new MappedArgument(argument, argumentMapping);  
			arguments.add(mappedArgument);
		}
	}
	
	void setMappedObject(MappedObject mappedObject) {
		this.mappedObject = mappedObject;
	}
	
	public boolean isFunction() {
		return baseProcedure.isFunction();
	}
	
	void createValueConverters(MappingHelper helper) throws InvalidMappingException, InitConfigurationException {
		if (mappedObject.isObjectType())
			baseValueConverter = helper.getConverter(
					mappedObject.oracleObject.getPlsqlType(), selfArgumentMapping);
		if (isFunction()) 
			returnType.createValueConverter(helper);
		for (MappedArgument argument : arguments)
			argument.createValueConverter(helper);		
	}
		
	public String getCallableSql() {
		return baseProcedure.getCallableSql();
	}
	
	public boolean isStatic() {
		return baseProcedure.isStatic;
	}
	
	public int getCursorBindIndex() {
		return baseProcedure.getCursorBindIndex();
	}

	public MappedArgument getReturnType() {
		return returnType;
	}

	public List<MappedArgument> getArguments() {
		return arguments;
	}

	public AbstractConverter getBaseValueConverter() {
		return baseValueConverter;
	}
	
	
}
