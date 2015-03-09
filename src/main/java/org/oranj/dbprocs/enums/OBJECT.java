
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

package org.oranj.dbprocs.enums;

import java.util.HashMap;


public enum OBJECT {
	
	PACKAGE("package"), TYPE("type"); 
	
	private String name;
	
	private static HashMap<String, OBJECT> map = new HashMap<String, OBJECT>();
	
	static {
		for (OBJECT value : OBJECT.values())
			map.put(value.getName(), value);
	}	
	
	OBJECT(String name) {
		this.name = name;
		
	}

	public String getName() {
		return name;
	}	
	
	public static OBJECT get(String name) {
		return map.get(name);
	}
}
