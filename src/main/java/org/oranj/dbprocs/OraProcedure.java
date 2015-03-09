
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

import java.util.ArrayList;
import java.util.List;

import org.oranj.dbprocs.enums.OBJECT;
import org.oranj.utils.StringUtils;
import org.oranj.dbprocs.enums.ARGUMENT;

public class OraProcedure {

	private OraObject 		oracleObject;
	private String      	name;
	
	private OraArgument 	returnType;

	private List<OraArgument>	arguments = new ArrayList<OraArgument>();			
	
	private String      callableSql;
	//ref out cursor "?" index in CallableStatement
	private int         cursorBindIndex = 0;
	
	boolean isStatic = true;
	
	public OraProcedure(DictProcedure sourceProcedure, OraObject oraObject) {
		
		this.name = sourceProcedure.getObjectName();

		DictArgument currentRecord = sourceProcedure.getArguments().get(0);
		oracleObject = oraObject;	
				
		//exceptional case, procedure without arguments
		if (currentRecord.getPosition()==1 && currentRecord.getSequence()==0)
			{}
		else {
			
			while (currentRecord != null) {
				//SELF argument not included
				if (oracleObject.getObjectType()== OBJECT.TYPE &&
						"SELF".equals(currentRecord.argumentName)) {
					isStatic = false;
				} else {
					OraArgument currentArgument  = new OraArgument (currentRecord);
					currentArgument.setProcedure(this);
					//function must have DictUserArgumentsRecord with field "position"=0
					if (currentRecord.getSequence()==1 && currentRecord.getPosition()==0)
						returnType = currentArgument;
					else
						arguments.add(currentArgument);								
				}
				currentRecord = currentRecord.nextZeroLevelRecord();
			}			
		}

		
		defineBindIndexes();
		defineCallableSql();
	}					

	
	public boolean isFunction() {
		return (returnType!=null);
	}

	public OraObject getOracleObject() {
		return oracleObject;
	}

	public void setOracleObject(OraObject oracleObject) {
		this.oracleObject = oracleObject;
	}
	
	public String getFullName() {			
		return StringUtils.dotStrings(oracleObject.getFullName(), name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OraArgument getReturnType() {
		return returnType;
	}

	public List<OraArgument> getArguments() {
		return arguments;
	}

	private void defineBindIndexes() {
		
		int currentBindIndex = 1;
		int currentCursorOutIndex = 1;		
		
		if (isFunction()) 			
			currentCursorOutIndex++;		
					
		if (!isStatic) {			
			currentBindIndex++;
			currentCursorOutIndex++;
		}			
		
		for (OraArgument argument : arguments) 
			if (argument.getInout().equals(ARGUMENT.IN_OUT) &&
					argument.getDataType().isOutableType()) 						
				argument.bindIndex = currentBindIndex++;
		
		for (OraArgument argument : arguments) 
			if (argument.getInout().equals(ARGUMENT.IN) ||
					!argument.getDataType().isOutableType()) 						
				argument.bindIndex = currentBindIndex++;
		
		for (OraArgument argument : arguments) 
			if (!argument.getInout().equals(ARGUMENT.IN) &&
					argument.getDataType().isOutableType()) 						
				argument.outCursorIndex = currentCursorOutIndex++;
		
		if (currentCursorOutIndex>1) 
			cursorBindIndex = currentBindIndex;
			
	}
	
	/*
	 * All returnable values (function result, out object types or collections)
	 * returns as cursor kinda
	 * select functionResult, modifiedObject1, modifiedObject2, outCollection1... from dual
	 */	
	private void defineCallableSql() {
		
		StringBuffer result = new StringBuffer("");
		result.append(getDeclareSql());
		result.append(getDefineVariablesSql());
		result.append(getCallSql());
		result.append(getOutCursorSelectSql());
		result.append("end" + END_LF);
		callableSql = result.toString();
	}
	
	private static String RESULT_VARIABLE = "result ";
	private static String OBJECT_VARIABLE = "objectVariable";
	private static String END_LF = ";\n";
	
	private String getDeclareSql() {
		StringBuffer result = new StringBuffer("declare ");
		if (isFunction()) 
			result.append(RESULT_VARIABLE + getReturnType().getDataType().getDeclareName() + END_LF);
		if (!isStatic) 
			result.append(OBJECT_VARIABLE + " " + oracleObject.getFullName() + END_LF);
		for (OraArgument argument : getArguments())			
				result.append(argument.getDeclareSql());				
		return result.toString();
	}
	
	private String getDefineVariablesSql() {
		StringBuffer result = new StringBuffer("begin ");
		if (!isStatic) 
			result.append(OBJECT_VARIABLE + " := ?;\n");	
		for (OraArgument argument : getArguments())			
			result.append(argument.getDefineVariablesSql());		
		return result.toString();
	}
	
	private String getCallSql() {
		StringBuffer result = new StringBuffer("");
		if (isFunction()) 
			result.append(RESULT_VARIABLE + " := ");
		if (isStatic)
			result.append(getFullName());
		else
			result.append(OBJECT_VARIABLE + "." + name);
		StringBuffer commaSeparated = new StringBuffer("");
		if (arguments.size()>0) {			
			result.append("(");			
			for (OraArgument argument : arguments) 
				commaAppend(commaSeparated, argument.getCallParamsSql());						
			result.append(commaSeparated.toString());
			result.append(")");							
		}
		result.append(END_LF);
		return result.toString();
	}
	

	private void commaAppend(StringBuffer source, String appendix) {
		if (StringUtils.isEmpty(appendix)) return;
		if (source.length()>0)
			source.append(",");
		source.append(appendix);
	}
	
	private String getOutCursorSelectSql() {
		
		if (cursorBindIndex==0) return "";	
		StringBuffer result = new StringBuffer("");
		
		result.append("open ? for select ");
		
		StringBuffer commaSeparated = new StringBuffer("");
		if (isFunction()) 
			commaAppend(commaSeparated, RESULT_VARIABLE);				
		if (!isStatic) 
			commaAppend(commaSeparated, OBJECT_VARIABLE);			
		for (OraArgument argument : arguments) 
			commaAppend(commaSeparated, argument.getSelectSql());
			
		result.append(commaSeparated.toString());
		result.append(" from dual " + END_LF);
		return result.toString();		
	}


	public String getCallableSql() {
		return callableSql;
	}

	public int getCursorBindIndex() {
		return cursorBindIndex;
	}	
	
	
	
	
}
