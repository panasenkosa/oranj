
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

package org.oranj.dbprocs.dbtypes;


import org.oranj.dbprocs.DictArgument;
import org.oranj.utils.StringUtils;

public abstract class BasePlsqlType {
	
	//abstract (common) sql type name {PL/SQL RECORD, NUMBER, OBJECT,...}
	//correspondents to ALL_TYPES.TYPECODE oracle view column
	private   String 	   oracleTypeCode;
	//oracle schema
	private   String	   owner;
	//exact oracle type name {NUMBER, VARCHAR2, MY_SCHEMA.MY_TYPE, ...}
	private   String       name;

	//sub sql type - type of collection element or cursor element
	private   BasePlsqlType   subElementType;
	//source record in USER_ARGUMENTS view on which this object instance based
	protected DictArgument sourceDictRecord;
		
	public abstract PLSQL_TYPE getSuperType();
	public abstract boolean hasSubElement();
	public abstract boolean isArray();
	public abstract boolean isOutableType();
	public abstract boolean isSupported();

	protected void buildType(DictArgument baseRecord)	{
		sourceDictRecord = baseRecord;
		setOwner(baseRecord.getTypeOwner());
		setName(baseRecord.getTypeName());
		setOracleTypeCode(baseRecord.getDataType());
	}
	
	protected void setUsualSubElementType() {		
		this.subElementType = getTypeInstance(sourceDictRecord.nextRecord());								
	}
		
	public static BasePlsqlType getTypeInstance(DictArgument baseRecord) {
		BasePlsqlType instance = PLSQL_TYPE.getTypeInstanceByName(baseRecord.getDataType());				
		instance.buildType(baseRecord);
		return instance;
	}
	public String getOracleTypeCode() {
		return oracleTypeCode;
	}
	public void setOracleTypeCode(String oracleTypeCode) {
		this.oracleTypeCode = oracleTypeCode;
	}
	
	public String getDeclareName() {
		return DeclareString.getDeclareString(getFullName());
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return StringUtils.dotStrings(getOwner(), getName());
	}

	public BasePlsqlType getSubElementType() {
		return subElementType;
	}
	public void setSubElementType(BasePlsqlType subElementType) {
		this.subElementType = subElementType;
	}

		
	
	
}
