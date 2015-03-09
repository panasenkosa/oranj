
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

package org.oranj.mappings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.annotations.DBTypeField;
import org.w3c.dom.Element;

public class FieldMapping extends ArgumentMapping{		
	
		Method getMethod;
		Method setMethod;
		//column index in the oracle.sql.STRUCT.getOracleAttributes()
		int    idxInStruct;
		String sqlTypeName;	
		Field  field;
		
		BasePlsqlType plsqlType;
		
		TypeMapping 	typeMapping;
		
		public FieldMapping(Element xmlNode) throws InitConfigurationException {
			super(xmlNode);
		}
		
		public FieldMapping(DBTypeField fieldAnnotation) throws InitConfigurationException  {
			super(fieldAnnotation);
		}

		public FieldMapping(Field field) throws InitConfigurationException  {
			super(field);
		}

		public Method getGetMethod() {
			return getMethod;
		}
		public void setGetMethod(Method getMethod) {
			this.getMethod = getMethod;
		}
		public Method getSetMethod() {
			return setMethod;
		}
		public void setSetMethod(Method setMethod) {
			this.setMethod = setMethod;
		}
		public int getIdxInStruct() {
			return idxInStruct;
		}
		public void setIdxInStruct(int idxInStruct) {
			this.idxInStruct = idxInStruct;
		}
		public String getSqlTypeName() {
			return sqlTypeName;
		}
		public void setSqlTypeName(String sqlTypeName) {
			this.sqlTypeName = sqlTypeName;
		}		
		public Field getField() {
			return field;
		}
		public void setField(Field field) {
			this.field = field;
		}

		public TypeMapping getTypeMapping() {
			return typeMapping;
		}

		public void setTypeMapping(TypeMapping typeMapping) {
			this.typeMapping = typeMapping;
		}

		public BasePlsqlType getPlsqlType() {
			return plsqlType;
		}

		public void setPlsqlType(BasePlsqlType plsqlType) {
			this.plsqlType = plsqlType;
		}	
		
		
		
}
