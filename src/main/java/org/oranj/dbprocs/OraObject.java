
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

import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.dbprocs.enums.OBJECT;
import org.oranj.utils.StringUtils;
import org.oranj.dbprocs.dbtypes.PLSQL_TYPE;
import org.oranj.exceptions.InvalidMappingException;


public class OraObject {

	List<OraProcedure> procedures = new ArrayList<OraProcedure>();
	
	//oracle package or oracle object type
	private OBJECT objectType;
		
	private BasePlsqlType plsqlType;
	
	private String name;

	private String owner;
	
	public OraObject(String objectName, String owner, OracleHelper oracleHelper, OBJECT objectType) throws InvalidMappingException {
		
		this(new DictObject(objectName, owner, oracleHelper), objectType);
		
	}
	
	private OraObject(DictObject sourceObject, OBJECT objectType) throws InvalidMappingException {
		
		this.name  = sourceObject.getObjectName();
		this.owner = sourceObject.getOwner();
		this.objectType = objectType;
		if (this.objectType.equals(OBJECT.TYPE)) {
			this.plsqlType = PLSQL_TYPE.OBJECT.getTypeInstance();
			plsqlType.setName(sourceObject.getObjectName());
			plsqlType.setOwner(sourceObject.getOwner());
		}

		for (DictProcedure sourceProcedure : sourceObject.getProcedures()) {
			OraProcedure newProcedure = new OraProcedure(sourceProcedure, this);
			procedures.add(newProcedure);
		}
	}

	//schema.name
	public String getFullName() {
		return StringUtils.dotStrings(getOwner(), getName());
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public OBJECT getObjectType() {
		return objectType;
	}

	public BasePlsqlType getPlsqlType() {
		return plsqlType;
	}

	
}
