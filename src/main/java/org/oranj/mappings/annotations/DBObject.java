
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

package org.oranj.mappings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.oranj.dbprocs.enums.OBJECT;

/*
 * Mapping of oracle object type or package to java interface
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})      
public @interface DBObject {
	public String name();
	public String owner() default "";
	public String baseClass() default "";
	public OBJECT type() default OBJECT.PACKAGE;
}
