
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

public class TypeScalar extends BasePlsqlType {
	
	public void buildType(DictArgument baseRecord)	{
		super.buildType(baseRecord);
		setName(sourceDictRecord.getPlsType());
	}	
	
	@Override
	public PLSQL_TYPE getSuperType() {		
		return PLSQL_TYPE.SCALAR;
	}

	@Override
	public boolean hasSubElement() {
		return false;
	}	
	
	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isOutableType() {
		return false;
	}	
	
	@Override
	public boolean isSupported() {
		return true;
	}	
}
