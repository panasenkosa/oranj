
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

package org.oranj;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.oranj.dbprocs.OracleHelper;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.exceptions.OranjException;
import org.oranj.mappings.MappingHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.oranj.exceptions.ProxyCreationException;
import org.oranj.mappings.xml.MappingsLoader;

public class OranjProxyFactory {
	
	
	private OracleHelper oracleHelper;
			
	private DataSource 		dataSource;

	/**
	 * Creates a new oranj Configuration instance.
	 */
	public OranjProxyFactory() {

	}

	/**
	 * Creates a new oranj Configuration instance.
	 * The instance initialization includes loading of all user-defined
	 * type and interface mappings from the xml-configuration files and annotations.
	 * The mappings are checked for completeness and correctness.
	 * @param ds				data source, this argument value overwrites <data-source> configuration element value
	 * @throws SQLException
	 * @throws org.oranj.exceptions.InitConfigurationException
	 */
	public OranjProxyFactory(DataSource ds) throws OranjException {
		this.dataSource = ds;
	}	

	
	/**
	 * Creates a new instance of the oracle package proxy
	 * @param  clazz	interface mapped to the required oracle package
	 * @return proxy instance
	 * @throws ProxyCreationException
	 */
	public Object getPackageProxy(Class clazz) throws OranjException {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, 
				new DBCallHandler(clazz, oracleHelper, null));		
	}
	
	/**
	 * Create a new instance of the oracle object type proxy for the java class instance mapped to this type
	 * @param clazz	interface mapped to the required oracle object type
	 * @param baseObject instance of the entity class, proxy base
	 * @return proxy instance
	 * @throws ProxyCreationException
	 */
	public Object getObjectTypeProxy(Class clazz, Object baseObject) throws  ProxyCreationException {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, 
				new DBCallHandler(clazz, oracleHelper, baseObject));
	}

	public void loadConfig(String configFileName) throws InitConfigurationException {
		loadConfig(null, configFileName);
	}
	public void loadConfig(ServletContext servletContext, String configFileName) throws InitConfigurationException {
		
		Set<String> resourceFiles = new HashSet<String>();
		Set<String> annotatedTypes = new HashSet<String>();		
		Set<String> annotatedProxies = new HashSet<String>();
		
		String errorMessage = "Error loading \"" + configFileName + "\" oranj configuration file";

		InputStream inStream = null;
		if (servletContext!=null) {
			inStream = servletContext.getResourceAsStream(configFileName);
		}  else {
			File file = new File(configFileName);
			try {
				inStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new InitConfigurationException("Configuration file "
						+ configFileName + " not found!");
			}
		}

		if (inStream != null) {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;

				try {
					builder = fact.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new InitConfigurationException(errorMessage, e);
				}
				Document document = null;
				try {
					document = builder.parse(inStream);
				} catch (SAXException e) {
					throw new InitConfigurationException(errorMessage, e);
				} catch (IOException e) {
					throw new InitConfigurationException(errorMessage, e);
				}

				if (this.dataSource==null)
					throw new InitConfigurationException("No data source!");

				//loading resource file names and annotated class names
				NodeList nodes = document.getElementsByTagName("mappings");
				for (int i=0; i<nodes.getLength(); i++) {
					Element node = (Element)nodes.item(i);
					NodeList subNodes = node.getElementsByTagName("file");
					for (int j=0; j<subNodes.getLength(); j++) {						
						Element subNode = (Element) subNodes.item(j);
						resourceFiles.add(subNode.getAttribute("name"));
					}
					subNodes = node.getElementsByTagName("annotated-type");
					for (int j=0; j<subNodes.getLength(); j++) {						
						Element subNode = (Element) subNodes.item(j);
						annotatedTypes.add(subNode.getAttribute("class"));
					}	
					subNodes = node.getElementsByTagName("annotated-object");
					for (int j=0; j<subNodes.getLength(); j++) {						
						Element subNode = (Element) subNodes.item(j);
						annotatedProxies.add(subNode.getAttribute("class"));
					}					
				}				

		} else {
			throw new InitConfigurationException("Configuration file "
					+ configFileName + " not found!");
		}		

		//try {
			oracleHelper = new OracleHelper(this.getDataSource());
			MappingHelper mappingHelper = new MappingHelper(oracleHelper);
			MappingsLoader loader = new MappingsLoader(mappingHelper);
			loader.loadMappings(servletContext, resourceFiles, annotatedTypes, annotatedProxies);
			
		//}
		/*
		finally {
			try {
				if (oracleHelper.getConnection()!=null)
					oracleHelper.closeConnection();
			} catch (SQLException e) {}
		}*/

	}

	
	/**
	 * Returns data source object currently using by the library
	 * @return	data source
	 */
	public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
