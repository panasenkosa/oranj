
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

package org.oranj.mappings.xml;


import org.oranj.dbprocs.OracleHelper;
import org.oranj.dbprocs.enums.OBJECT;
import org.oranj.exceptions.InitConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.oranj.dbprocs.MappedObject;
import org.oranj.dbprocs.OraObject;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.ArgumentMapping;
import org.oranj.mappings.ClassTypeMapping;
import org.oranj.mappings.MethodMapping;

public class DBObjectMappingParser {

	public static void parsePackage(Document document, OracleHelper oracleHelper) throws InitConfigurationException {
		
		NodeList nodes = document.getElementsByTagName("oracle-package");
		
		for (int i=0; i<nodes.getLength(); i++) {
			
			Element node = (Element)nodes.item(i);
			String className = node.getAttribute("class");
			String owner = node.getAttribute("owner").toUpperCase();
			String objectName = node.getAttribute("name").toUpperCase();
		
			OraObject oraObject = oracleHelper.findOrLoadOraObject(owner, objectName, OBJECT.PACKAGE);
			MappedObject mappedObject = new MappedObject(oraObject, className, null);
			oracleHelper.addMappedObject(mappedObject);
			
			NodeList subNodes = node.getElementsByTagName("procedure");			
			for (int j=0; j<subNodes.getLength(); j++) {				
				Element subNode = (Element) subNodes.item(j);	
				parseMethod(mappedObject, subNode);				
			}			
			mappedObject.checkCompatibility();
		}				
	}

	public static void parseType(Element node, ClassTypeMapping typeMapping, OracleHelper oracleHelper) throws InitConfigurationException{

			OraObject oraObject = oracleHelper.findOrLoadOraObject(typeMapping.getOwner(), typeMapping.getSqlTypeName(), OBJECT.TYPE);
			String className = node.getAttribute("class");
			MappedObject mappedObject = new MappedObject(oraObject, className, typeMapping.getMappingClassName());
			oracleHelper.addMappedObject(mappedObject);

			NodeList subNodes = node.getElementsByTagName("procedure");
			for (int j=0; j<subNodes.getLength(); j++) {
				Element subNode = (Element) subNodes.item(j);
				parseMethod(mappedObject, subNode);
			}
			mappedObject.checkCompatibility();

	}


	private static void parseMethod(MappedObject mappedObject, Element methodNode) throws InitConfigurationException{
		
		String javaMethodName = methodNode.getAttribute("java-name");
		String dbProcedureName = methodNode.getAttribute("oracle-name").toUpperCase();
		String inGroup = methodNode.getAttribute("in-group");
		String outGroup = methodNode.getAttribute("out-group");
	
		MethodMapping methodMapping = new MethodMapping(
				javaMethodName, dbProcedureName, inGroup, outGroup);
		
		NodeList argumentNodes = methodNode.getElementsByTagName("argument");	
		
		for (int j=0; j<argumentNodes.getLength(); j++) {
			
			Element argumentNode = (Element) argumentNodes.item(j);						
			ArgumentMapping argumentMapping;
			try {
				argumentMapping = new ArgumentMapping(argumentNode);			
				int position = Integer.valueOf(argumentNode.getAttribute("position"));			
				argumentMapping.setPosition(position);
				argumentMapping.setArgumentClass(argumentNode.getAttribute("class"));
			} catch (NumberFormatException e) {
				throw new InvalidMappingException("Bad position attribute " +
						" value a argument mapping in method \"" + javaMethodName + "\"", e);
			} catch (InitConfigurationException e) {
				throw e;
				//log bad argument mapping in method
			} catch (ClassNotFoundException e) {
				throw new InitConfigurationException
					("Bad argument mapping in method \"" + javaMethodName + "\"", e);
			}			
			
			methodMapping.addMappedArgument(argumentMapping);
		}
		
		NodeList returnNodes = methodNode.getElementsByTagName("return");
		if (returnNodes.getLength()>0) {
			Element returnNode = (Element) returnNodes.item(0);
			ArgumentMapping argumentMapping = new ArgumentMapping(returnNode);
			try {
				argumentMapping.setArgumentClass(returnNode.getAttribute("class"));
			} catch (ClassNotFoundException e) {
				throw new InitConfigurationException
				("Bad return value mapping in method \"" + javaMethodName + "\"", e);
			}				
			methodMapping.setReturnResult(argumentMapping);
		}		
		
		try {
			methodMapping.findJavaMethod(mappedObject.getProxyInterface());
		} catch (SecurityException e) {
			throw new InvalidMappingException("Could not find appropriate java method \"" 
					+ javaMethodName + "\" for \"" + dbProcedureName + "\" oracle procedure");
		} catch (NoSuchMethodException e) {
			throw new InvalidMappingException("Could not find appropriate java method \"" 
					+ javaMethodName + "\" for \"" + dbProcedureName + "\" oracle procedure");
		}
		mappedObject.bindMappedProcedure(methodMapping);

	}	
}
