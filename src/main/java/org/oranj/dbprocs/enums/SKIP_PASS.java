
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

public enum SKIP_PASS {

	SKIP("skip"), PASS("pass");

	private String name;
	
	private static HashMap<String, SKIP_PASS> map = new HashMap<String, SKIP_PASS>();
	
	static {
		for (SKIP_PASS value : SKIP_PASS.values())
			map.put(value.getName(), value);
	}	
	
	SKIP_PASS(String name) {
		this.name = name;
		
	}

	public String getName() {
		return name;
	}	
	
	public static SKIP_PASS get(String name) {
		return map.get(name);
	}	
}
