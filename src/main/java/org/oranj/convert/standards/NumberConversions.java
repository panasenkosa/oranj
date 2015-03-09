
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;

public class NumberConversions {

	
	public static Object bigDecimalToBoolean(Object value) {

		return (((BigDecimal) value).intValue() == 1);
	}

	public static Object booleanToBigDecimal(Object value, Connection connection){

		Boolean booleanValue = (Boolean)value;
		if (booleanValue)
			return new BigDecimal(1);
		else
			return new BigDecimal(0);
	}	
	
	public static Object intToBigDecimal(Object value, Connection connection) {		
		if (value instanceof Byte) 
			return new BigDecimal((Byte)value);
		if (value instanceof Short) 
			return new BigDecimal((Short)value);
		return new BigDecimal((Integer)value);
	}
	
	public static Object longToBigDecimal(Object value, Connection connection) {		
		return new BigDecimal((Long)value);
	}	
	
	public static Object doubleToBigDecimal(Object value, Connection connection) {	
		if (value instanceof Float) 
			return new BigDecimal((Float)value);	
		return new BigDecimal((Double)value);
	}	
	
	public static Object bigIntegerToBigDecimal(Object value, Connection connection) {		
		return new BigDecimal((BigInteger)value);
	}	
	
	public static Object bigDecimalToByte(Object value) {		
		return ((BigDecimal)value).byteValue();
	}
	
	public static Object bigDecimalToShort(Object value) {		
		return ((BigDecimal)value).shortValue();
	}
	
	public static Object bigDecimalToInt(Object value) {		
		return ((BigDecimal)value).intValue();
	}	
	
	public static Object bigDecimalToLong(Object value) {		
		return ((BigDecimal)value).longValue();
	}
	
	public static Object bigDecimalToFloat(Object value) {		
		return ((BigDecimal)value).floatValue();
	}
	
	public static Object bigDecimalToDouble(Object value) {		
		return ((BigDecimal)value).doubleValue();
	}	
	
	public static Object bigDecimalToBigInteger(Object value) {		
		return  ((BigDecimal)value).toBigInteger();
	}		

	//testing-purpose mappings	
	public static Object INTEGER_int(Object value) {		
		return  ((BigDecimal)value).intValue();
	}	

	public static Object int_INTEGER(Object value, Connection connection) {		
		return new BigDecimal((Integer)value);
	}	
	
	public static Object BINARYINTEGER_int(Object value) {		
		return  ((BigDecimal)value).intValue();
	}	

	public static Object int_BINARYINTEGER(Object value, Connection connection) {		
		return new BigDecimal((Integer)value);
	}
	
	public static Object float_BINARYFLOAT(Object value, Connection connection) {		
		return value;
	}
	
	public static Object BINARYFLOAT_float(Object value) {		
		//binary_float can pass as BigDecimal or Float
		if (value instanceof Float) 
			return ((Float)value).floatValue();		
		return  ((BigDecimal)value).floatValue();
	}	
	
	public static Object double_BINARYDOUBLE(Object value, Connection connection) {		
		return value;
	}
	
	public static Object BINARYDOUBLE_double(Object value) {		
		//binary_double can pass as BigDecimal or Double
		if (value instanceof Double) 
			return ((Double)value).doubleValue();		
		return  ((BigDecimal)value).doubleValue();
	}	
}
