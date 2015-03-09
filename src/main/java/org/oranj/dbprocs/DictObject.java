
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.oranj.dbprocs.constants.SQL_VIEW;
import org.oranj.exceptions.InvalidMappingException;
import org.oranj.utils.StringUtils;

public class DictObject {
	
	String 		  objectName;
	String		  owner;
	
	OracleHelper  oracleHelper;
	
	List<DictProcedure> procedures = new ArrayList<DictProcedure>();

	
	public DictObject(String objectName, String owner, OracleHelper oracleHelper) throws InvalidMappingException {
		this.objectName = objectName;	
		this.owner = owner;
		//TODO: possible error, owner not set
		if (StringUtils.isEmpty(owner) || owner.equalsIgnoreCase(oracleHelper.getSchemaName())) {
			this.owner = oracleHelper.getSchemaName();
		} 		
		this.oracleHelper = oracleHelper;
		try {
			loadProcedures();
		} catch (SQLException e) {
			throw new InvalidMappingException("Error loading metadata from oracle system dictionaries for \"" 
					+ this.owner + "\"." + this.objectName + "\"", e);
		}
	}

	
	private void loadProcedures() throws SQLException {
		
		String stmtString = SQL_VIEW.SELECT_PROCEDURE_ARGUMENTS;
		PreparedStatement pstmt = oracleHelper.getConnection().prepareStatement(stmtString);
		pstmt.setString(1, this.owner.toUpperCase());
		pstmt.setString(2, this.objectName.toUpperCase());

		ResultSet rset = pstmt.executeQuery();
		
		List<DictArgument> allPackageArguments = new ArrayList<DictArgument>();
		
		while (rset.next()) {
			DictArgument newArgument = new DictArgument(rset);
			allPackageArguments.add(newArgument);
		}
		rset.close();
		pstmt.close();
		int currentSubProgramId = -1;
		DictProcedure currentProcedure = null;
		for (DictArgument argument : allPackageArguments) {
			if (currentSubProgramId != argument.getSubprogramId()) {
				currentProcedure = new DictProcedure(argument);
				currentProcedure.setDictObject(this);
				procedures.add(currentProcedure);
				currentSubProgramId = currentProcedure.getSubProgramId();
			}
			currentProcedure.addArgument(argument);
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public String getOwner() {
		return owner;
	}

	public List<DictProcedure> getProcedures() {
		return procedures;
	}	
	
	
}
