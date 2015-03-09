
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

import java.lang.reflect.Method;
import java.sql.CallableStatement;

import org.oranj.mappings.MappingHelper;
import org.oranj.mappings.ValueTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.oranj.exceptions.InvalidMappingException;


public class ValueTypeMappingParser {

	public static void parse(Document document, MappingHelper helper) throws InvalidMappingException {
		
		NodeList nodes = document.getElementsByTagName("value-mapping");
		
		for (int i=0; i<nodes.getLength(); i++) {
			
			Element node = (Element)nodes.item(i);
			String className = node.getAttribute("class");			
			NodeList subNodes = node.getElementsByTagName("sql-type");
			
			for (int j=0; j<subNodes.getLength(); j++) {
				
				Element subNode = (Element) subNodes.item(j);
				String sqlTypeName = subNode.getAttribute("name");
				ValueTypeMapping mapping = new ValueTypeMapping(className, sqlTypeName);
				
				String toOracleMethodName = subNode.getAttribute("to-oracle-method");
				Method toOracleMethod = XMLParsersHelper.getMethodByName(toOracleMethodName, 
								Object.class, java.sql.Connection.class);				
				mapping.setToOracleMethod(toOracleMethod);
				
				String fromOracleMethodName = subNode.getAttribute("from-oracle-method");
				Method fromOracleMethod = XMLParsersHelper.getMethodByName(
						fromOracleMethodName, Object.class);				
				mapping.setFromOracleMethod(fromOracleMethod);				
				
				String bindMethodName = subNode.getAttribute("bind-method");
				Method bindMethod = XMLParsersHelper.getMethodByName(
						bindMethodName, CallableStatement.class, int.class, Object.class);				
				mapping.setBindMethod(bindMethod);				
				
				String updateMethodName = subNode.getAttribute("update-method");
				Method updateMethod = XMLParsersHelper.getMethodByName(
						updateMethodName, Object.class, Object.class);				
				mapping.setUpdateMethod(updateMethod);	
				
				helper.addMapping(mapping);				
			}					
		}				
	}
	
}
