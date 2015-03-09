
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

package org.oranj.mappings;

import java.lang.reflect.Method;

import org.oranj.convert.AbstractConverter;
import org.oranj.convert.ValueConverter;
import org.oranj.exceptions.InvalidMappingException;

public class ValueTypeMapping extends TypeMapping{

	private Method toOracleMethod;	
	private Method fromOracleMethod;
	private Method bindMethod;
	private Method updateMethod;
	
	public ValueTypeMapping(String className, String sqlTypeName) throws InvalidMappingException {
		super(className, null, sqlTypeName);
	}

	@Override
	public TYPE_MAPPING getMappingType() {
		return TYPE_MAPPING.VALUE;
	}

	@Override
	public AbstractConverter getValueConverter(ArgumentMapping argumentMapping)
			throws InvalidMappingException{
		return new ValueConverter(this);
	}

	public Method getToOracleMethod() {
		return toOracleMethod;
	}

	public void setToOracleMethod(Method toOracleMethod) {
		this.toOracleMethod = toOracleMethod;
	}

	public Method getFromOracleMethod() {
		return fromOracleMethod;
	}

	public void setFromOracleMethod(Method fromOracleMethod) {
		this.fromOracleMethod = fromOracleMethod;
	}

	public Method getBindMethod() {
		return bindMethod;
	}

	public void setBindMethod(Method bindMethod) {
		this.bindMethod = bindMethod;
	}

	public Method getUpdateMethod() {
		return updateMethod;
	}

	public void setUpdateMethod(Method updateMethod) {
		this.updateMethod = updateMethod;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!super.equals(obj))
			return false;
		ValueTypeMapping other = (ValueTypeMapping) obj;
		if (bindMethod == null) {
			if (other.bindMethod != null)
				return false;
		} else if (!bindMethod.equals(other.bindMethod))
			return false;
		if (fromOracleMethod == null) {
			if (other.fromOracleMethod != null)
				return false;
		} else if (!fromOracleMethod.equals(other.fromOracleMethod))
			return false;
		if (toOracleMethod == null) {
			if (other.toOracleMethod != null)
				return false;
		} else if (!toOracleMethod.equals(other.toOracleMethod))
			return false;
		if (updateMethod == null) {
			if (other.updateMethod != null)
				return false;
		} else if (!updateMethod.equals(other.updateMethod))
			return false;
		return true;
	}

	
}
