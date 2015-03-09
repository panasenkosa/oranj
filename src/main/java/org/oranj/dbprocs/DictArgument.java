
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

import org.oranj.dbprocs.constants.VIEW_ALL_ARGUMENTS;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
	Parameter of oracle package procedure or oracle object type.
	Metadata loaded from oracle system view ALL_ARGUMENTS in the DictObject.loadProcedures method

 */

public class DictArgument {

	int     subprogramId;
	String  objectName;
	String 	argumentName;
	int    	position;
	int     sequence;
	int     dataLevel;
	String  dataType;
	String  typeOwner;
	String  typeName;
	String  typeSubName;
	String  plsType;
	String  inout;
	
	private DictProcedure procedure;
	
	public DictArgument(ResultSet rs) throws SQLException {
				
		subprogramId = rs.getInt(VIEW_ALL_ARGUMENTS.COL_SUBPROGRAM_ID);
		objectName = rs.getString(VIEW_ALL_ARGUMENTS.COL_OBJECT_NAME);
		argumentName = rs.getString(VIEW_ALL_ARGUMENTS.COL_ARGUMENT_NAME);
		position = rs.getInt(VIEW_ALL_ARGUMENTS.COL_POSITION);
		sequence = rs.getInt(VIEW_ALL_ARGUMENTS.COL_SEQUENCE);
		dataLevel = rs.getInt(VIEW_ALL_ARGUMENTS.COL_DATA_LEVEL);
		dataType = rs.getString(VIEW_ALL_ARGUMENTS.COL_DATA_TYPE);
		typeOwner = rs.getString(VIEW_ALL_ARGUMENTS.COL_TYPE_OWNER);
		//TODO: subname exists only in context of oracle package, not supported yet
		typeSubName = rs.getString(VIEW_ALL_ARGUMENTS.COL_TYPE_SUBNAME);
		typeName = rs.getString(VIEW_ALL_ARGUMENTS.COL_TYPE_NAME);
		plsType = rs.getString(VIEW_ALL_ARGUMENTS.COL_PLS_TYPE);
		inout = rs.getString(VIEW_ALL_ARGUMENTS.COL_IN_OUT);
		
	}
	
	public DictArgument nextSameLevelRecord() {
		int level = this.dataLevel;
		DictArgument result = nextRecord();
		while (result!=null && result.dataLevel!=level) {
			//type finished...
			if (result.dataLevel<level)
				result=null;
			else
				result = result.nextRecord();
		}
		return result;
	}
	
	public DictArgument nextZeroLevelRecord() {
		DictArgument result = nextRecord();
		while (result!=null && result.dataLevel!=0) {
			result = result.nextRecord();
		}
		return result;
	}
	
	public DictArgument nextRecord() {
		if (this.sequence<procedure.getArguments().size())
			return procedure.getArguments().get(this.sequence);
		else 
			return null;
	}

	public int getSubprogramId() {
		return subprogramId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setProcedure(DictProcedure procedure) {
		this.procedure = procedure;
	}

	public int getPosition() {
		return position;
	}

	public int getSequence() {
		return sequence;
	}

	public String getArgumentName() {
		return argumentName;
	}

	public int getDataLevel() {
		return dataLevel;
	}

	public String getDataType() {
		return dataType;
	}

	public String getTypeOwner() {
		return typeOwner;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTypeSubName() {
		return typeSubName;
	}

	public String getPlsType() {
		return plsType;
	}

	public String getInout() {
		return inout;
	}		
	
}
