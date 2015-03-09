
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

package org.oranj.convert.standards;

import java.sql.Connection;
import java.sql.SQLException;

import org.oranj.exceptions.DBObjectConversionException;

public class DateConversions {

	public static Object utilDateFromDate(Object value)
			throws DBObjectConversionException {				
		
		if (value instanceof java.sql.Date)
			return value;
				
		if (value instanceof java.sql.Timestamp) {
			java.sql.Timestamp timestamp = (java.sql.Timestamp)value;
			return new java.util.Date(timestamp.getTime());
		}			
		
		if (value instanceof oracle.sql.DATE) {
			oracle.sql.DATE oracleDate = (oracle.sql.DATE)value;
			return oracleDate.timestampValue();
		}			
		
		if (value instanceof oracle.sql.TIMESTAMP) {
			oracle.sql.TIMESTAMP oracleTimestamp = (oracle.sql.TIMESTAMP)value;
			try {
				return oracleTimestamp.timestampValue();
			} catch (SQLException e) {
				throw new DBObjectConversionException("Error casting oracle.sql.TIMESTAMP value", e);
			}
		}			
			throw new DBObjectConversionException(
					"Can't convert Date type from oracle, unknown class");
	}


	public static Object utilDateToDate(Object value, Connection connection)
			throws DBObjectConversionException {

		java.util.Date date = (java.util.Date)value;
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(date.getTime());
		return sqlTimestamp;
	}
	
}
