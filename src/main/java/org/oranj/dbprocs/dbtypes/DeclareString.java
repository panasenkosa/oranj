
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

import java.util.HashSet;
import java.util.Set;

public class DeclareString {
	
	private static Set<String> charTypes = new HashSet<String>();
	
	private static String CHAR_DECLARE = "VARCHAR2(4000)";
	
	static {
		charTypes.add("VARCHAR2");
		charTypes.add("VARCHAR");
		charTypes.add("CHAR");
		charTypes.add("CHARACTER");
		charTypes.add("STRING");	
	}

	public static String getDeclareString(String typeName) {
		
		if (charTypes.contains(typeName))
			return CHAR_DECLARE;
		return typeName;
	}
}
