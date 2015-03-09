
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

package org.oranj.exceptions;

public class InitConfigurationException extends OranjException {

	public InitConfigurationException(String message, Throwable e) {
		super(message, e);		
	}

	public InitConfigurationException(String message) {
		super(message);
	}
	
	public InitConfigurationException(Throwable e) {
		super(e);		
	}	
	
}
