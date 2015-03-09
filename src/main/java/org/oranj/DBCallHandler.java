
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import org.oranj.dbprocs.MappedArgument;
import org.oranj.dbprocs.MappedObject;
import org.oranj.dbprocs.MappedProcedure;
import org.oranj.dbprocs.OracleHelper;
import org.oranj.dbprocs.enums.ARGUMENT;
import org.oranj.exceptions.DBObjectConversionException;
import org.oranj.exceptions.ProxyCreationException;

public class DBCallHandler implements InvocationHandler {

	private MappedObject  mappedObject;
	private OracleHelper  oracleHelper;
	private Object		  baseObject;
	
	public DBCallHandler(Class proxyInterface, OracleHelper helper, Object baseObject) throws ProxyCreationException {
		this.oracleHelper = helper;
		this.mappedObject = oracleHelper.getMappedObject(proxyInterface);
		this.baseObject = baseObject;		
		if (mappedObject==null) 
			throw new ProxyCreationException("There is no db-object mapping for "
					+ proxyInterface.getName() + " interface");
		if (mappedObject.isObjectType() && baseObject==null)
			throw new ProxyCreationException("Proxy for oracle object type can't be base on null object");	
		if (mappedObject.isObjectType() && 
				!mappedObject.getTypeMappingClass().isAssignableFrom(baseObject.getClass()))
			throw new ProxyCreationException(baseObject.getClass()
					+ " class not assignable to " + mappedObject.getTypeMappingClass().getName());
	}
	
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws DBObjectConversionException
			 {
		
		Object result = null;		
		MappedProcedure procedure = mappedObject.getProcedure(arg1);
		CallableStatement stmt = null;
		
		try {		
			stmt = oracleHelper.getConnection().prepareCall(procedure.getCallableSql());
			
			bindStatement(stmt, procedure, arg2);
			stmt.execute();
			//if there are any returnable values...
			if (procedure.getCursorBindIndex()>0) {			
								
				int baseObjectReturnIndex = 1;
				ResultSet cursor = ((OracleCallableStatement)stmt).getCursor(procedure.getCursorBindIndex());
				cursor.next();
				if (procedure.isFunction()) {
					//function result always has index = 1
					Object functionResult = cursor.getObject(1);
					baseObjectReturnIndex = 2;
					result = procedure.getReturnType().getValueConverter().oracleToJava(functionResult);
				}
				if (!procedure.isStatic()) {
					procedure.getBaseValueConverter()
							.updateInstance(cursor.getObject(baseObjectReturnIndex),baseObject);
				}
				for (MappedArgument argument : procedure.getArguments()) {
					//stored procedure argument was defined as out or in/out
					if (argument.getOutCursorIndex()>0) {
						Object newValue = cursor.getObject(argument.getOutCursorIndex());
						argument.getValueConverter()
							.updateInstance(newValue, arg2[argument.getJavaArgumentIndex()]);
					}
				}
				cursor.close();								
			}			
		} catch (SQLException e) {
			throw new DBObjectConversionException(e);
		} finally {		
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {}
			}
			try {
				oracleHelper.closeConnection();
			} catch (SQLException e) {	} 
			
		}			
		return result;
	}	
	
	private void bindStatement(CallableStatement stmt, MappedProcedure procedure, Object[] params) throws SQLException, DBObjectConversionException {
		
		if (procedure.getCursorBindIndex()>0) {			
			stmt.registerOutParameter(procedure.getCursorBindIndex(), OracleTypes.CURSOR);
		}		
		
		if (!procedure.isStatic()) {
			Object value = procedure.getBaseValueConverter().javaToOracle(baseObject, stmt.getConnection());
			stmt.setObject(1, value);
		}
		
		for (MappedArgument argument : procedure.getArguments() ) {
			int idx = argument.getJavaArgumentIndex();
			if (argument.getBindIndex()>0) {
				if (params[idx]==null || argument.getInout()==ARGUMENT.OUT) {
					stmt.setObject(argument.getBindIndex(), null);
				} else {		
					Object value = argument.getValueConverter().javaToOracle(
							params[idx], stmt.getConnection());
					stmt.setObject(argument.getBindIndex(), value);
				}					
			}					
		}

	}
}
