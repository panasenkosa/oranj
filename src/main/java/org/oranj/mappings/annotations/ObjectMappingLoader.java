
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

import java.lang.reflect.Method;

import org.oranj.dbprocs.MappedObject;
import org.oranj.dbprocs.OraObject;
import org.oranj.dbprocs.OracleHelper;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.ArgumentMapping;
import org.oranj.mappings.MethodMapping;
import org.oranj.utils.Utils;

public class ObjectMappingLoader {

	public static void loadMapping(String className,
			OracleHelper helper)  throws InitConfigurationException{
		
		Class annotatedInterface;
		try {
			annotatedInterface = Utils.clasForName(className);
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException(e);
		}
		
		DBObject dbObjectAnnotation = (DBObject) annotatedInterface.getAnnotation(DBObject.class);
		if (dbObjectAnnotation==null)
			throw new InvalidMappingException
				("Interface \"" + className + "\" havn't @DBObject annotation");
		
		OraObject oraObject = helper.findOrLoadOraObject(dbObjectAnnotation
				.owner().toUpperCase(), dbObjectAnnotation.name().toUpperCase(), dbObjectAnnotation.type());
		MappedObject mappedObject = new MappedObject(oraObject, className, dbObjectAnnotation.baseClass());								
		helper.addMappedObject(mappedObject);		
		
		Method[] sourceMethods = annotatedInterface.getMethods();
		
		for (Method method : sourceMethods) {
			if (!Utils.isObjectClassMethod(method)) {	
				DBProcedure methodAnnotation = method.getAnnotation(DBProcedure.class);
				if (methodAnnotation == null)
					throw new InvalidMappingException("Method \""
							+ method.getName() + "\" of db-object proxy interface \"" + className
							+ "\" havn't @DBProcedure annotation");
				
				MethodMapping mappedMethod = new MethodMapping(methodAnnotation, method);
	
				if (Utils.isFunction(method)) {					
					ArgumentMapping returnArgument = null;
					try {
						returnArgument = new ArgumentMapping(methodAnnotation);
					} catch (InitConfigurationException e) {
						//log bad return value mapping for method.getName
						throw e;
					}					
					returnArgument.setArgumentClass(method.getReturnType());
					mappedMethod.setReturnResult(returnArgument);
				}
				
				for (int i=0;i<method.getParameterTypes().length;i++) {
					DBArgument argumentAnnotation = Utils.getParameterAnnotation(method, i);
					ArgumentMapping argument = null;
					try {
						argument = new ArgumentMapping(argumentAnnotation);
					} catch (InitConfigurationException e) {
						//log bad argument mapping for method.getName
						throw e;
					}
					argument.setArgumentClass(method.getParameterTypes()[i]);
					argument.setPosition(i);
					mappedMethod.addMappedArgument(argument);
				}
				mappedObject.bindMappedProcedure(mappedMethod);
											
			}			
		}	
		mappedObject.checkCompatibility();
	}
	
}
