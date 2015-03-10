
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

import java.io.*;
import java.lang.reflect.Method;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.exceptions.ParseXMLException;
import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;
import org.oranj.exceptions.InvalidMappingException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLParsersHelper {


	public static Document parseXMLFile(ServletContext servletContext, String configFileName) throws ParseXMLException{
		InputStream inStream = null;
		if (servletContext!=null) {
			inStream = servletContext.getResourceAsStream(configFileName);
		}  else {
			File file = new File(configFileName);
			try {
				inStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new ParseXMLException("Configuration file "
						+ configFileName + " not found!");
			}
		}
		String errorMessage = "Error loading \"" + configFileName + "\" oranj configuration file";

		Document document = null;

		if (inStream != null) {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;

			try {
				builder = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new ParseXMLException(errorMessage, e);
			}
			try {
				document = builder.parse(inStream);
			} catch (SAXException e) {
				throw new ParseXMLException(errorMessage, e);
			} catch (IOException e) {
				throw new ParseXMLException(errorMessage, e);
			}
		}
		else {
			throw new ParseXMLException("Configuration file "
					+ configFileName + " not found!");
		}
		return document;
	}

	public static Class getClassByName(String className) throws InvalidMappingException {
		
		if (StringUtils.isEmpty(className))
			return null;		
		
		try {
			Class  clazz = Utils.clasForName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException(e);
		}		
	}
	
	
	public static Method getMethodByName(String methodName, Class... arguments) throws InvalidMappingException {
		
		if (StringUtils.isEmpty(methodName))
			return null;		

			Method method;
			try {
				method = Utils.findMethodByFullName(methodName, arguments);
				return method;
			}  catch (ClassNotFoundException e) {
				throw new InvalidMappingException(e);
			} catch (NoSuchMethodException e) {
				throw new InvalidMappingException(e);
			}
			
	
	}	
	
	public static void checkMethodReturnType(Method method, Class clazz) throws InvalidMappingException {
		
		if (method==null) return;
		
		Class returnClass = method.getReturnType();
		if (!returnClass.getName().equals(clazz.getName())) {
			throw new InvalidMappingException("method " + method.getName() + 
					" return type does not match " + clazz.getName());						
		}			
	}
}
