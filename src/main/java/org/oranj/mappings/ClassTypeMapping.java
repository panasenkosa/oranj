
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import oracle.sql.StructDescriptor;
import org.oranj.convert.ClassConverter;
import org.oranj.dbprocs.OraTypeField;
import org.oranj.dbprocs.OracleHelper;
import org.oranj.dbprocs.enums.SKIP_PASS;
import org.oranj.exceptions.InitConfigurationException;
import org.oranj.utils.StringUtils;
import org.oranj.utils.Utils;
import org.oranj.convert.AbstractConverter;
import org.oranj.exceptions.InvalidMappingException;

/*
 * mapping of java class to oracle object type
 */
public class ClassTypeMapping extends TypeMapping{
	
	private StructDescriptor		 descriptor;
	private List<OraTypeField> 		 oracleFields;

	private HashMap<Field, FieldMapping> fields = new HashMap<Field, FieldMapping>();	
	
	public ClassTypeMapping(String className, String owner, String sqlTypeName,
			OracleHelper oracleHelper) throws InvalidMappingException, InitConfigurationException {
		
		super(className, (owner==null ? oracleHelper.getSchemaName() : owner),  sqlTypeName);
		this.mappingClassName = className;
		try {
			oracleFields = OraTypeField.getTypeFields(owner, sqlTypeName, oracleHelper);
		} catch (SQLException e) {
			throw new InvalidMappingException("Error loading \"" + owner + "\"."
					+ sqlTypeName + "\" metadata from oracle owner, sqlTypeName",e);
		}		
		descriptor = oracleHelper.getStructDescriptor(StringUtils.dotStrings(this.getOwner(), this.getSqlTypeName()));
																		
	}
					
	public void addMappingField(Field field, FieldMapping mapping)
			throws InvalidMappingException {
		try {
			OraTypeField oraField = findOraField(mapping.dbName);	
			mapping.setField(field);
			mapping.setIdxInStruct(oraField.getFieldIndex());								
			mapping.setSqlTypeName(oraField.getDataType().getFullName());
			mapping.setGetMethod(Utils.findGetterMethod(field));
			mapping.setSetMethod(Utils.findSetterMethod(field));					
			mapping.setArgumentClass(field.getType());
			fields.put(field, mapping);
			
		} catch (NoSuchMethodException e) {
			throw new InvalidMappingException
				("No public seter or getter for \"" + field.getName() + 
				 "\" field of \"" + field.getDeclaringClass().getName() + "\" class", e);
		}		
	}	

	private HashMap<String, FieldGroup> groups = new HashMap<String, FieldGroup>();	
	private FieldGroup defaultGroup;


	public TYPE_MAPPING getMappingType() {		
		return TYPE_MAPPING.CLASS;
	}

	public HashMap<Field, FieldMapping> getFields() {
		return fields;
	}

	public StructDescriptor getDescriptor() {
		return descriptor;
	}
	
	/*
	 * If default group is not defined by user it consists of all mapped fields
	 */
	public void defineDefaultGroup() {
		if (defaultGroup!=null)
			return;
		FieldGroup group = new FieldGroup();
		group.setBehaviour(SKIP_PASS.PASS);
		group.setDefault(true);
		for (FieldMapping fieldMapping : fields.values()) {
			FieldGroup.GroupElement element = new FieldGroup.GroupElement();
			element.setFieldMapping(fieldMapping);
			group.getElements().put(fieldMapping.getField(), element);			
		}
		defaultGroup = group;
	}
	
	/*
	 * Field groups which defined to SKIP behaviour transform to field groups
	 * with PASS behaviour
	 */
	public void inverseSkipGroups() {
		for (FieldGroup group : groups.values()) {
			if (group.getBehaviour().equals(SKIP_PASS.SKIP)) {
				group.setBehaviour(SKIP_PASS.PASS);
				HashMap<Field, FieldGroup.GroupElement> 	elements = new HashMap<Field, FieldGroup.GroupElement>();
				for (FieldMapping fieldMapping : fields.values()) {
					if (!group.getElements().keySet().contains(fieldMapping.getField())) {
						FieldGroup.GroupElement element = new FieldGroup.GroupElement();
						element.setFieldMapping(fieldMapping);
						elements.put(fieldMapping.getField(), element);
					}							
				}
				group.setElements(elements);
			}
		}
	}
	
	public void addFieldGroup(String groupName, FieldGroup group) {
		if (group.isDefault())
			this.defaultGroup = group;
		groups.put(groupName, group);
	}

	@Override
	public AbstractConverter getValueConverter(ArgumentMapping argumentMapping) 
			throws InitConfigurationException 		{
		return new ClassConverter(this, argumentMapping);
	}

	/*
	 * If groupName parameter is empty default group is returned
	 */
	public FieldGroup getFieldGroup(String groupName) throws InvalidMappingException {		
		FieldGroup result = null;
		if (!StringUtils.isEmpty(groupName)) {
			result = groups.get(groupName);
			if (result==null) 
				throw new InvalidMappingException(
						"There is no property-group with name " + groupName + " in "
						+ getMappingClassName() + " class mapping");
		} else 
			result = defaultGroup;		
		return result;
	}
	
	/*
	 * If property 'group-name' of element is defined we have to set reference
	 *  to field group of another class type mapping 
	 */
	private void completeFieldGroups(MappingHelper mappingHelper)
			throws InvalidMappingException {
		
		for (FieldGroup group : groups.values()) {
			for (FieldGroup.GroupElement element : group.getElements().values()) {				
				TypeMapping mapping = element.getFieldMapping().getTypeMapping();				 
				if (mapping instanceof ClassTypeMapping) {
					FieldGroup subGroup = ((ClassTypeMapping) mapping)
							.getFieldGroup(element.getGroupName());
					element.setGroup(subGroup);
				}
			}
		}
	}
	
	/*
	 * Loading type mappings of fields, it have to be processed by second pass, because
	 * during initial definition of class mapping some mappings can be not defined yet 
	 */
	@Override
	public void completeMapping(MappingHelper mappingHelper) 
			throws InitConfigurationException{						
						
		for (FieldMapping fieldMapping : fields.values()) {		
			OraTypeField oraField = findOraField(fieldMapping.dbName);
			fieldMapping.setPlsqlType(oraField.getDataType());	
			try {
				fieldMapping.typeMapping = mappingHelper.getMapping(fieldMapping, oraField.getDataType());
			}  catch (InvalidMappingException e) {
				//TODO: log this message
				//throw new InvalidMappingException("Field " + fieldMapping.getField().getName() +
				//		" of java class " + this.getMappingClass().getName() + " is not compatible" +
				//		" with field " + fieldMapping.dbName + " of oracle object type " + this.sqlTypeName, e);				
				throw e;
			}
		}				
		completeFieldGroups(mappingHelper);
	}
	
	//find apropriate orace object type field
	private OraTypeField findOraField(String dbName) throws InvalidMappingException{
		
		for (OraTypeField oraField : oracleFields) {			
			if (oraField.getFieldName().equals(dbName))
				return oraField;
		}
		
		throw new InvalidMappingException("There is no field " + dbName +
				" in " + sqlTypeName + " oracle object type");
	}

	public String getOwner() {
		return owner;
	}


}
