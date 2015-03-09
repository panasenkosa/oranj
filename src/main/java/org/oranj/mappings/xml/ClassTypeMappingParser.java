
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.ClassTypeMapping;
import org.oranj.mappings.FieldGroup;
import org.oranj.mappings.FieldMapping;
import org.oranj.mappings.MappingHelper;


public class ClassTypeMappingParser {

	public static void parse(Document document, MappingHelper helper) throws InitConfigurationException {
		
		NodeList nodes = document.getElementsByTagName("class-mapping");
		
		for (int i=0; i<nodes.getLength(); i++) {
			
			Element node = (Element)nodes.item(i);
			String className = node.getAttribute("class");
			String sqlTypeName = node.getAttribute("oracle-type");
			String owner = node.getAttribute("owner");
			String fieldMappingAttr = node.getAttribute("field-mapping");

			ClassTypeMapping mapping = new ClassTypeMapping(className, owner,
					sqlTypeName, helper.getOracleHelper());

			//naming by user-defined mapping
			if (fieldMappingAttr==null || "custom".equals(fieldMappingAttr)) {
				NodeList subNodes = node.getElementsByTagName("field");
				for (int j=0; j<subNodes.getLength(); j++) {

					Element subNode = (Element) subNodes.item(j);
					String fieldName = subNode.getAttribute("name");
					Field field;

					try {
						field = Utils.findClassField(mapping.getMappingClass(),
								fieldName);
					} catch (NoSuchFieldException e) {
						throw new InvalidMappingException("No field \"" + fieldName
								+ "\" in class \"" + className + "\"");
					}

					FieldMapping fieldMapping;
					try {
						fieldMapping = new FieldMapping(subNode);
					} catch (InitConfigurationException e) {
						throw e;
						//todo log bad field in className mapping
					}
					mapping.addMappingField(field, fieldMapping);
				}
			}
			//naming by one-to-one coincidence of oracle type field names and java field names
				else {

				String includeFields = node.getAttribute("include-fields");
				String excludeFields = node.getAttribute("exclude-fields");

				List<String> includeFieldsList = new ArrayList<String>();
				if (includeFields!=null && includeFields.length()>0) {
					includeFieldsList =  Arrays.asList(includeFields.replaceAll(" ", "").split(","));
				}
				List<String> excludeFieldsList = new ArrayList<String>();
				if (excludeFields!=null && excludeFields.length()>0) {
					excludeFieldsList =  Arrays.asList(excludeFields.replaceAll(" ", "").split(","));
				}

				Field[] classFields = mapping.getMappingClass().getDeclaredFields();
				for (Field field : classFields) {
					if (excludeFieldsList.contains(field.getName())) {
						continue;
					}
					if (includeFieldsList.size()>0 && !includeFieldsList.contains(field.getName())) {
						continue;
					}
					try {
						FieldMapping fieldMapping = new FieldMapping(field);
						mapping.addMappingField(field, fieldMapping);
					} catch (InvalidMappingException e) {
						//it's ok, not all fields must have setters and getters
						//TODO: log info about such fields
					}
				}
			}



			NodeList typeMethods = node.getElementsByTagName("oracle-type");
			for (int j=0; j<typeMethods.getLength(); j++) {
				Element subNode = (Element) typeMethods.item(j);
				DBObjectMappingParser.parseType(subNode, mapping, helper.getOracleHelper());
			}


			NodeList subNodes = node.getElementsByTagName("property-group");
			for (int j=0; j<subNodes.getLength(); j++) {
				Element subNode = (Element) subNodes.item(j);	
				FieldGroup group = parseFieldsGroup(subNode, mapping);	
				mapping.addFieldGroup(group.getName(), group);
			}
			mapping.defineDefaultGroup();
			mapping.inverseSkipGroups();
			helper.addMapping(mapping);
						
		}
				
	}	
	
	private static FieldGroup parseFieldsGroup(Element groupNode, ClassTypeMapping mapping) throws InitConfigurationException {
				
		FieldGroup group = new FieldGroup(groupNode);						
		NodeList fieldNodes = groupNode.getElementsByTagName("property");	
		
		for (int j=0; j<fieldNodes.getLength(); j++) {			
			Element fieldNode = (Element) fieldNodes.item(j);
			FieldGroup.GroupElement element = null;
			try {
				element = new FieldGroup.GroupElement(fieldNode, mapping);
			} catch (InitConfigurationException e) {
				throw e;
				//log bad fieldgroup element in mapping
			}
			group.getElements().put(element.getFieldMapping().getField(), element);
		}
		
		return group;

	}
}
