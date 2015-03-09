
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
import java.util.HashMap;

import org.oranj.dbprocs.enums.OBJECT;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.MappingHelper;
import org.oranj.mappings.MethodMapping;
import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;
import org.oranj.exceptions.InvalidMappingException;

public class MappedObject {

	OraObject oracleObject;
	
	//interface wich methods are proxy for oracle object (type/package) methods
	Class proxyInterface;
	
	//class mapped to this oracle object type
	Class typeMappingClass;
		
	OracleHelper oracleHelper;
	
	HashMap<Method, MappedProcedure> procedures = new HashMap<Method, MappedProcedure>();
	
	public MappedObject(OraObject oracleObject, String proxyInterfaceName, String typeMappingClassName) throws InvalidMappingException{
		
		try {
			proxyInterface = Utils.clasForName(proxyInterfaceName);
			if (oracleObject.getObjectType().equals(OBJECT.TYPE)) {
				if (StringUtils.isEmpty(typeMappingClassName))
					throw new InvalidMappingException(
							"Object proxy mapping \"" + proxyInterfaceName
									+ "\", base-class property must be defined");
				typeMappingClass = Utils.clasForName(typeMappingClassName);
			}							
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException(e);
		}			
		
		this.oracleObject = oracleObject;
		if (!proxyInterface.isInterface())
			throw new InvalidMappingException(
					"Only interface can be db-object proxy, \""
							+ proxyInterface.getName() + "\" isn't interface");
	}
	
	public void bindMappedProcedure(MethodMapping mappedMethod)  throws InvalidMappingException{
						
		//searching procedure in oracle package
		OraProcedure findedProcedure = null;
		for (OraProcedure oraProcedure : oracleObject.procedures) {
			
			//by name
			if (!mappedMethod.getDbProcedureName().equalsIgnoreCase(oraProcedure.getName()))
				continue;
			
			//by function or not function
			if ((!Utils.isFunction(mappedMethod.getJavaMethod()) && oraProcedure.isFunction()) ||
					(Utils.isFunction(mappedMethod.getJavaMethod()) && !oraProcedure.isFunction()))
				continue;
			
			//by arguments count
			int argumentsCount = mappedMethod.getJavaMethod().getParameterTypes().length;
			if (argumentsCount!=oraProcedure.getArguments().size())
				continue;
			
			//by arguments name
			for (OraArgument argument :oraProcedure.getArguments()) 
				if (mappedMethod.findArgumentByName(argument.getName())==null)
					continue;
			
			findedProcedure = oraProcedure;
		}
		
		if (findedProcedure==null) 
			throw new  InvalidMappingException("There is no procedure \""
					+ mappedMethod.getDbProcedureName() + "\" in db object \""
					+ oracleObject.getFullName() + "\" satisfying mapping");
		
		
		MappedProcedure procedure = new MappedProcedure(mappedMethod, findedProcedure, this);				
		procedures.put(procedure.javaMethod, procedure);
		
	}
	
	public void createValueConverters(MappingHelper helper)
		throws InitConfigurationException {
				
		for (MappedProcedure procedure : procedures.values()) 
			procedure.createValueConverters(helper);
		
	}
	
	public void checkCompatibility() throws  InvalidMappingException {
		
		Method[] sourceMethods = proxyInterface.getMethods();
		for (Method method : sourceMethods) {
			if (!Utils.isObjectClassMethod(method)) {	
				
				MappedProcedure mappedProcedure = null;
				for (MappedProcedure procedure : procedures.values()) {
					if (method.equals(procedure.javaMethod))
						mappedProcedure = procedure;							
				}
				
				if 	(mappedProcedure==null) {
					throw new InvalidMappingException(
							"There is no mapping for method \"" + method.getName() + "\" of \""
							 + proxyInterface.getName() + "\" db-object proxy interface");
				}								
			}
		}		
	}	
	
	public MappedProcedure getProcedure(Method method) {
		return procedures.get(method);
	}
	
	public Class getTypeMappingClass() {
		return typeMappingClass;
	}

	public Class getProxyInterface() {
		return proxyInterface;
	}
		

	public boolean isObjectType() {
		return oracleObject.getObjectType().equals(OBJECT.TYPE);
	}
	
}
