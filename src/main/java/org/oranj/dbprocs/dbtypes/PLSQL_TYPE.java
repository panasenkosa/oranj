
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

import java.util.HashMap;

public enum PLSQL_TYPE {

	SCALAR(		 "SCALAR", 			TypeScalar.class),
	REF_CURSOR(  "REF CURSOR", 		TypeRefCursor.class),
	NESTED_TABLE("TABLE", 			TypeNestedTable.class),
	HASH_TABLE(  "PL/SQL TABLE",  	TypeHashTable.class),
	RECORD(      "PL/SQL RECORD",	TypeRecord.class),
	OBJECT(      "OBJECT", 			TypeObject.class),
	VARRAY(      "VARRAY", 			TypeVarray.class);
	
	private static HashMap<String, PLSQL_TYPE> map = new HashMap<String, PLSQL_TYPE>();
	
	static {
		for (PLSQL_TYPE value : PLSQL_TYPE.values())
			map.put(value.getName(), value);
	}	
	
	public static BasePlsqlType getTypeInstanceByName(String typeName) {
		PLSQL_TYPE plsqlType = map.get(typeName);
		if (plsqlType==null)
				plsqlType = SCALAR;
		return plsqlType.getTypeInstance();
	}
	
	private String name;
	private Class<? extends BasePlsqlType>  typeClass;
	
	PLSQL_TYPE(String name, Class<? extends BasePlsqlType> clazz) {
		this.name = name;
		this.typeClass = clazz;
	}

	public String getName() {
		return name;
	}
	
	public BasePlsqlType getTypeInstance() {
		try {
			BasePlsqlType newType = typeClass.newInstance();
			newType.setOracleTypeCode(getName());
			return newType;
		} catch (Exception e) {			
			return null;
		}
	}
		
}
