
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.oranj.dbprocs.MappedObject;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.MappingHelper;
import org.oranj.mappings.annotations.ObjectMappingLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.oranj.exceptions.InvalidMappingException;
import org.oranj.mappings.annotations.ClassMappingLoader;

public class MappingsLoader {
	
	private MappingHelper helper;
	
	public MappingsLoader(MappingHelper helper) {
		this.helper = helper;
	}
	
	//loading all kind of mappings from xml configuration file
	private void loadXMLMapping(ServletContext servletContext, String fileName, DocumentBuilder builder)
			throws InitConfigurationException {

		InputStream inStream = servletContext.getResourceAsStream(fileName);
		//File file = new File(fileName);
		//if (file.exists()){
		if (inStream != null) {
				Document document;
				try {
					//document = builder.parsePackage(fileName);
					document = builder.parse(inStream);
					ValueTypeMappingParser.parse(document, helper);
					ClassTypeMappingParser.parse(document, helper);
					DBObjectMappingParser.parsePackage(document, helper.getOracleHelper());
				} catch (SAXException e) {
					throw new InitConfigurationException("Error in parsing \"" 
							+ fileName + "\" xml mapping file", e);
				} catch (IOException e) {
					throw new InitConfigurationException("Error in parsing \"" 
							+ fileName + "\" xml mapping file", e);
				} 

		} else
			throw new InitConfigurationException("XML mapping resource file "
					+ fileName + " not found!");
	}
	
	//entry point for loading all mappings
	public void loadMappings(ServletContext servletContext,
							 Set<String> resourceFiles,
							 Set<String> annotatedTypes,
							 Set<String> annotatedProxies)
				throws InitConfigurationException {
		
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = fact.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new InitConfigurationException(e);
		}
		
		//loading all kinds of mappings from resource files 
		for (String fileName : resourceFiles) 
				loadXMLMapping(servletContext, fileName, builder);

		//loading class to object type mappings from annotations
		for (String className : annotatedTypes) 
				ClassMappingLoader.loadMapping(className, helper);			
		

		helper.completeMappings();


		//loading java interface to db-object mappings from annotations
		for (String className : annotatedProxies) 
				ObjectMappingLoader.loadMapping(className, helper.getOracleHelper());

		
		//finishing db-object proxies creating
		for (MappedObject mappedPackage : helper.getOracleHelper().getMappedObjects().values())
			try {
				mappedPackage.createValueConverters(helper);
			} catch (InvalidMappingException e) {
				//log error creating value converter for mappedPackage
				throw e;
			}
			
	}
				
	
}
