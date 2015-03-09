
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

import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.dbprocs.enums.ARGUMENT;


public class OraArgument {
	
	BasePlsqlType dataType;
	String  		        name;	

	//index of "?" in CallableStatement
	int         bindIndex = 0;
	//column index in out ref cursor 
	int         outCursorIndex = 0;
	
	ARGUMENT inout;
	
	private OraProcedure procedure;

	
	/*
	 * Composite arguments are arranged in a tree.
	 */	
	public OraArgument(DictArgument baseRecord) {
		name = baseRecord.getArgumentName();
		inout = ARGUMENT.get(baseRecord.getInout());
		dataType = BasePlsqlType.getTypeInstance(baseRecord);									
	}

	
	public BasePlsqlType getDataType() {
		return dataType;
	}

	public String getName() {
		return name;
	}

	public OraProcedure getProcedure() {
		return procedure;
	}

	public void setProcedure(OraProcedure procedure) {
		this.procedure = procedure;
	}

	public ARGUMENT getInout() {
		return inout;
	}

	public int getBindIndex() {
		return bindIndex;
	}

	public int getOutCursorIndex() {
		return outCursorIndex;
	}		

	public String getDeclareSql() {
		if (inout==ARGUMENT.IN || !dataType.isOutableType())
			return "";		
		return getName() + " " + getDataType().getFullName() + ";\n";
	}
	
	public String getDefineVariablesSql() {
		if (dataType.isOutableType() && inout==ARGUMENT.IN_OUT) 
			return getName() + " := ?;\n";						
		return "";
	}	

	public String getCallParamsSql() {
		if (inout==ARGUMENT.IN) return "?";
		if (dataType.isOutableType())
			return getName();					
		return "?";		
	}	
	
	public String getSelectSql()  {
		if (inout==ARGUMENT.IN) return "";
		if (dataType.isOutableType()) 
			return getName();
		return "";		
	}	

}
