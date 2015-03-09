
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



public enum ARGUMENT {

	IN("IN"), OUT("OUT"), IN_OUT("IN/OUT");
	
	private String name;
	
	private static HashMap<String, ARGUMENT> map = new HashMap<String, ARGUMENT>();
	
	static {
		for (ARGUMENT value : ARGUMENT.values())
			map.put(value.getName(), value);
	}	
	
	ARGUMENT(String name) {
		this.name = name;
		
	}

	public String getName() {
		return name;
	}	
	
	public static ARGUMENT get(String name) {
		return map.get(name);
	}

		
}
