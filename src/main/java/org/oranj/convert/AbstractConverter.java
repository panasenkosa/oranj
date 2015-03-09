
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

package org.oranj.convert;

import java.sql.Connection;

import org.oranj.exceptions.DBObjectConversionException;

public interface AbstractConverter {	
	
	public abstract Object javaToOracle(Object value, Connection connection) throws DBObjectConversionException;
	public abstract Object oracleToJava(Object value) throws DBObjectConversionException;
	public abstract Object updateInstance(Object value, Object oldValue) throws DBObjectConversionException;
	
}
