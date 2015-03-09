
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

package org.oranj.dbprocs;

import java.util.ArrayList;
import java.util.List;

public class DictProcedure {
	
	String 	 objectName;
	int		 subProgramId;

	List<DictArgument> arguments = new ArrayList<DictArgument>();
	
	DictObject dictObject;

	public DictProcedure(DictArgument firstArgument) {
		objectName = firstArgument.getObjectName();
		subProgramId = firstArgument.getSubprogramId();
	}	
	
	public String getObjectName() {
		return objectName;
	}

	public int getSubProgramId() {
		return subProgramId;
	}
	
	public void addArgument(DictArgument argument) {
		this.arguments.add(argument);
		argument.setProcedure(this);
	}		

	public List<DictArgument> getArguments() {
		return arguments;
	}

	public void setDictObject(DictObject dictObject) {
		this.dictObject = dictObject;
	}
	
}
