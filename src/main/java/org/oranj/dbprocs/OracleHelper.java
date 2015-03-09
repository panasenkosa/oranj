
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;
import oracle.sql.ArrayDescriptor;
import oracle.sql.StructDescriptor;

import org.oranj.dbprocs.enums.OBJECT;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.utils.StringUtils;

public class OracleHelper {
	
	private Connection    connection;	
	private String        schemaName;	
	private DataSource    dataSource;
	
	public OracleHelper(DataSource ds) throws InitConfigurationException {
		this.dataSource = ds;
		try {
			connection = dataSource.getConnection();
			schemaName = ((OracleConnection) connection).getCurrentSchema();
		} catch (SQLException e) {
			throw new InitConfigurationException("Error obtaining oracle connection", e);
		}						
	}		
	
	private HashMap<String, OraObject> oracleObjects = new HashMap<String, OraObject>();
	private HashMap<Class, MappedObject>  mappedObjects = new HashMap<Class, MappedObject>();
	
	public Connection getConnection() throws SQLException {
		if (connection!=null && !connection.isClosed()) 
			return connection;
		connection = dataSource.getConnection();
		return connection;
	}	
	
	public void closeConnection() throws SQLException {
		if (connection!=null && !connection.isClosed()) 
			connection.close();
	}	
	
	private HashMap<String, ArrayDescriptor> arrayDescriptors = new HashMap<String, ArrayDescriptor>();
		
	public ArrayDescriptor getArrayDescriptor(String typeName) throws InitConfigurationException {
		ArrayDescriptor result = arrayDescriptors.get(typeName);
		if (result==null) {
			try {
				result = ArrayDescriptor.createDescriptor(typeName, getConnection());
			} catch (SQLException e) {
				throw new InitConfigurationException("Error obtaining \"" + typeName
						+ "\" oracle ARRAY descriptor", e);
			}						
			arrayDescriptors.put(typeName, result);
		}	
		return result;
	}	
	
	private HashMap<String, StructDescriptor> structDescriptors = new HashMap<String, StructDescriptor>();
		
	public StructDescriptor getStructDescriptor(String typeName) throws InitConfigurationException {
		StructDescriptor result = structDescriptors.get(typeName);
		if (result==null) {
			try {
				result = StructDescriptor.createDescriptor(typeName, getConnection());
			} catch (SQLException e) {
				throw new InitConfigurationException("Error obtaining \"" + typeName
						+ "\" oracle STRUCT descriptor", e);
			}						
			structDescriptors.put(typeName, result);
		}	
		return result;
	}		
	
	public void addOraObject(OraObject pack) {
		this.oracleObjects.put(pack.getFullName(), pack);
	}
	
	public void addMappedObject(MappedObject pack) {
		this.mappedObjects.put(pack.getProxyInterface(), pack);
	}
	
	public OraObject getOraObject(String name) {
		return this.oracleObjects.get(name);
	}
	
	public MappedObject getMappedObject(Class clazz) {
		return this.mappedObjects.get(clazz);
	}			
	
	public HashMap<Class, MappedObject> getMappedObjects() {
		return mappedObjects;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String fullObjectName(String owner, String name) throws InvalidMappingException{
		String fullObjectName = null;

		if (StringUtils.isEmpty(owner))
			fullObjectName = StringUtils.dotStrings(schemaName, name.toUpperCase());
		else
			fullObjectName = StringUtils.dotStrings(owner.toUpperCase(), name.toUpperCase());
					
		return fullObjectName;
	}

	public OraObject findOrLoadOraObject(String owner, String name,
			OBJECT objectType) throws InvalidMappingException {
		String fullName = fullObjectName(owner, name);
		OraObject oraObject = getOraObject(fullName);
		if (oraObject==null)
			oraObject = new OraObject(name, owner, this, objectType);
		addOraObject(oraObject);
		return oraObject;
	}	
	
}
