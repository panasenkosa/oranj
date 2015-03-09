
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

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;
import org.w3c.dom.Element;

import org.oranj.dbprocs.enums.COLLECTION_MERGING;
import org.oranj.mappings.annotations.DBArgument;
import org.oranj.mappings.annotations.DBProcedure;
import org.oranj.mappings.annotations.DBTypeField;

import java.lang.reflect.Field;

public class ArgumentMapping {
	
	String			   dbName;
	Class			   argumentClass;
	
	int    			   position = -1;
	
	String 			   elementClassName;
	Class			   elementClass;
	COLLECTION_MERGING collectionMerging;	

	String			   inGroupName;
	String			   outGroupName;
	FieldGroup		   inGroup;
	FieldGroup		   outGroup;
	
	public ArgumentMapping() {}
	
	public ArgumentMapping(DBArgument argumentAnnotation) throws InitConfigurationException {
		
		dbName = argumentAnnotation.name().toUpperCase();
		try {
			setElementClass(argumentAnnotation.elementClass());
		} catch (ClassNotFoundException e) {
			throw new InitConfigurationException(
					"Bad collection argument element class \"" + argumentAnnotation.elementClass() + 
					"\" in @DBArgument \"" + dbName + "\" annotation");
		}
		inGroupName = argumentAnnotation.inPropertyGroup();
		outGroupName = argumentAnnotation.outPropertyGroup();
		collectionMerging = argumentAnnotation.collectionMerging();
	}	
	
	public ArgumentMapping(DBProcedure methodAnnotation) throws InitConfigurationException  {
		
		dbName = null; 
		try {
			setElementClass(methodAnnotation.returnElementClass());
		} catch (ClassNotFoundException e) {
			throw new InitConfigurationException(
						"Bad return element class \"" + methodAnnotation.returnElementClass() + 
						"\" in + @DBProcedure \"" + methodAnnotation.name() + "\"");
		}
		inGroupName = methodAnnotation.inPropertyGroup();
		outGroupName = methodAnnotation.outPropertyGroup();
		collectionMerging = COLLECTION_MERGING.RELOAD;
	}	
	
	public ArgumentMapping(Element xmlNode) throws InitConfigurationException  {
		dbName = xmlNode.getAttribute("oracle-name").toUpperCase();
		if (dbName==null) {
			dbName = xmlNode.getAttribute("name").toUpperCase();
		}
		try {
			String elementClassName = xmlNode.getAttribute("element-class");
			setElementClass(elementClassName);
		} catch (ClassNotFoundException e) {
			throw new InitConfigurationException(
					"Bad element class \"" + elementClassName + 
						"\" in \"" + dbName + "\" field mapping");
		}
		String collectionMergingStr = xmlNode.getAttribute("collection-merging");
		collectionMerging = COLLECTION_MERGING.get(collectionMergingStr);		
		inGroupName  = xmlNode.getAttribute("in-group");
		outGroupName = xmlNode.getAttribute("out-group");				
	}

	//one-to-one coincidince oracle to java field
	public ArgumentMapping(Field field) throws InitConfigurationException  {
		dbName = field.getName().toUpperCase(); ;
	}

	public ArgumentMapping(DBTypeField	fieldAnnotation) throws InitConfigurationException   {
		dbName = fieldAnnotation.name().toUpperCase();		
		try {
			setElementClass(fieldAnnotation.elementClass());
		} catch (ClassNotFoundException e) {
			throw new InitConfigurationException(
					"Bad element class \"" + fieldAnnotation.elementClass() + 
					"\" in DBTypeField  \"" + dbName + "\" annotation");
		}
		collectionMerging = fieldAnnotation.collectionMerging();	
	}
	
	void setElementClass(String elementClassName) throws ClassNotFoundException  {
		this.elementClassName = elementClassName;		
		if (!StringUtils.isEmpty(elementClassName))
				elementClass = Utils.clasForName(elementClassName);
	
	}
	
	public void setInGroup(FieldGroup inGroup) {
		this.inGroup = inGroup;
	}

	public void setOutGroup(FieldGroup outGroup) {
		this.outGroup = outGroup;
	}
	
	public void setArgumentClass(Class argumentClass) {
		this.argumentClass = argumentClass;
	}
	
	public void setArgumentClass(String className) throws ClassNotFoundException {			
		argumentClass = Utils.clasForName(className);				
	}		

	public Class getArgumentClass() {
		return argumentClass;
	}	
		
	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public String getDbName() {
		return dbName;
	}

	public Class getElementClass() {
		return elementClass;
	}

	public COLLECTION_MERGING getCollectionMerging() {
		return collectionMerging;
	}

	public String getInGroupName() {
		return inGroupName;
	}

	public String getOutGroupName() {
		return outGroupName;
	}

	public FieldGroup getInGroup() {
		return inGroup;
	}

	public FieldGroup getOutGroup() {
		return outGroup;
	}

	public void setInGroupName(String inGroupName) {
		this.inGroupName = inGroupName;
	}

	public void setOutGroupName(String outGroupName) {
		this.outGroupName = outGroupName;
	}	
	
	
	
	
}
