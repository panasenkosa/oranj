
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

import java.lang.reflect.Field;
import java.util.HashMap;

import org.oranj.dbprocs.enums.SKIP_PASS;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.StringUtils;
import org.w3c.dom.Element;


public class FieldGroup {

	private 	String 	name;				
	private 	HashMap<Field, GroupElement> 	elements = new HashMap<Field, GroupElement>();
	private     boolean   isDefault;
	private SKIP_PASS behaviour;
	
	public FieldGroup() {}
	
	public FieldGroup(Element groupNode) {
		name = groupNode.getAttribute("name");
		isDefault = StringUtils.stringToBoolean(groupNode.getAttribute("default"));
		behaviour = SKIP_PASS.get(groupNode.getAttribute("behaviour"));		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setElements(HashMap<Field, GroupElement> elements) {
		this.elements = elements;
	}
	public HashMap<Field, GroupElement> getElements() {
		return elements;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}	

	public SKIP_PASS getBehaviour() {
		return behaviour;
	}
	public void setBehaviour(SKIP_PASS behaviour) {
		this.behaviour = behaviour;
	}


	public static class GroupElement{
		
		private String 		   fieldName;
		private String	 	   groupName;
		private FieldMapping   fieldMapping;
		private FieldGroup     group;		
		
		public GroupElement() {}
		
		public GroupElement(Element elementNode, ClassTypeMapping mapping) throws InitConfigurationException {
			fieldName = elementNode.getAttribute("name");
			groupName = elementNode.getAttribute("property-group");						
			Field field = null;
			String errorMessage = "Error creating \"" + groupName + "\" field group";
				try {
					field = mapping.getMappingClass().getDeclaredField(fieldName);
				} catch (SecurityException e) {
					throw new InitConfigurationException(errorMessage, e);
				} catch (NoSuchFieldException e) {
					throw new InitConfigurationException(errorMessage, e);
				}

			fieldMapping = mapping.getFields().get(field);
			if (fieldMapping==null)
				throw new InitConfigurationException(
						"Field " + fieldName + " is included in property-group, \"" + groupName 
						+ "\" but not mapped at all ");						
		}
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}		
		public FieldMapping getFieldMapping() {
			return fieldMapping;
		}
		public void setFieldMapping(FieldMapping fieldMapping) {
			this.fieldMapping = fieldMapping;
		}
		public FieldGroup getGroup() {
			return group;
		}
		public void setGroup(FieldGroup group) {
			this.group = group;
		}		
	}	
}
