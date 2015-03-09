
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


public class TypeRefCursor extends BasePlsqlType {

	private boolean isWeak;
	
	@Override
	public void buildType(DictArgument baseRecord) {
		
		setOracleTypeCode(sourceDictRecord.getDataType());
		
		if (sourceDictRecord.nextRecord()==null) {
			setWeak(true);
			return;
		}
		int currentDataLevel = sourceDictRecord.getDataLevel();
		int nextDataLevel = sourceDictRecord.nextRecord().getDataLevel();
		if (nextDataLevel==currentDataLevel+1) {
			setWeak(false);
			setUsualSubElementType();		
		} else
			setWeak(true);
		
	}	
	
	public boolean isWeak() {
		return isWeak;
	}

	public void setWeak(boolean isWeak) {
		this.isWeak = isWeak;
	}

	@Override
	public PLSQL_TYPE getSuperType() {		
		return PLSQL_TYPE.REF_CURSOR;
	}

	@Override
	public boolean hasSubElement() {
		return true;
	}	
	
	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isOutableType() {
		return true;
	}	

	@Override
	public boolean isSupported() {
		return false;
	}
	
}
