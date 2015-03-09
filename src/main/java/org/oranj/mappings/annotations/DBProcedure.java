
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

/*
 * Mapping of oracle stored procedure or oracle object type method
 * to java interface method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})      
public @interface DBProcedure {
	public String name();
	public String returnElementClass() default "";
	public String inPropertyGroup() default "";
	public String outPropertyGroup() default "";
	public String returnInPropertyGroup() default "";
	public String returnOutPropertyGroup() default "";	
}
