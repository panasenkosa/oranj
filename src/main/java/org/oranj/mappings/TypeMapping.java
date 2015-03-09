
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


import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.Utils;
import org.oranj.convert.AbstractConverter;
import org.oranj.exceptions.InvalidMappingException;

public abstract class TypeMapping {	
	
	protected Class       mappingClass;		
	protected String      mappingClassName;		
	protected String      sqlTypeName;
	protected String      owner;
	
	public TypeMapping(String className, String owner, String sqlTypeName) throws InvalidMappingException  {
		mappingClassName = className;		
		this.sqlTypeName = sqlTypeName;
		this.owner = owner;
		try {
			mappingClass = Utils.clasForName(className);
		} catch (ClassNotFoundException e) {
			throw new InvalidMappingException
				("Bad class name \"" + className + "\" to \"" + sqlTypeName + "\" mapping ", e);	
		}
	}	

	
	public abstract TYPE_MAPPING getMappingType();
	
	public abstract AbstractConverter getValueConverter(ArgumentMapping argumentMapping)
			throws InitConfigurationException;
		
	public void completeMapping(MappingHelper mappingHelper)
			throws InitConfigurationException{}	

	public Class getMappingClass() {
		return mappingClass;
	}

	public String getMappingClassName() {
		return mappingClassName;
	}	
	
	public String getSqlTypeName() {
		return sqlTypeName;
	}

	public String getOwner() {
		return owner;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeMapping other = (TypeMapping) obj;
		if (mappingClass == null) {
			if (other.mappingClass != null)
				return false;
		} else if (!mappingClass.equals(other.mappingClass))
			return false;
		if (sqlTypeName == null) {
			if (other.sqlTypeName != null)
				return false;
		} else if (!sqlTypeName.equals(other.sqlTypeName))
			return false;
		return true;
	}



	public static class MappingKey {
		
		private Class	 mappingClass;
		private String 	 sqlTypeName;
		private String 	 owner;

		public MappingKey(TypeMapping typeMapping) {

			this.mappingClass = typeMapping.getMappingClass();
			this.sqlTypeName = typeMapping.getSqlTypeName();
			this.owner = typeMapping.getOwner();

		}

		public MappingKey(Class mappingClass, String owner, String sqlTypeName) {
			
			this.mappingClass = mappingClass;
			this.sqlTypeName = sqlTypeName;
			this.owner = owner;
			
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof MappingKey)) return false;

			MappingKey that = (MappingKey) o;

			if (!mappingClass.equals(that.mappingClass)) return false;
			if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
			if (!sqlTypeName.equals(that.sqlTypeName)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = mappingClass.hashCode();
			result = 31 * result + sqlTypeName.hashCode();
			result = 31 * result + (owner != null ? owner.hashCode() : 0);
			return result;
		}
	}

}
