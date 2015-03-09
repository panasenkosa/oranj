
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.oranj.dbprocs.constants.VIEW_ALL_COLL_TYPES;
import org.oranj.dbprocs.dbtypes.BasePlsqlType;
import org.oranj.dbprocs.constants.SQL_VIEW;
import org.oranj.dbprocs.constants.VIEW_ALL_TYPES;
import org.oranj.dbprocs.constants.VIEW_ALL_TYPE_ATTRS;
import org.oranj.dbprocs.dbtypes.PLSQL_TYPE;
import org.oranj.utils.StringUtils;

public class OraTypeField {
	
	BasePlsqlType dataType;
	int				fieldIndex;
	String			fieldName;

	
	public static List<OraTypeField> getTypeFields(String owner,
			String typeName, OracleHelper oracleHelper) throws SQLException {
		
		List<OraTypeField> result = new ArrayList<OraTypeField>();

		PreparedStatement pstmt = oracleHelper.getConnection().prepareStatement(SQL_VIEW.SELECT_TYPE_ATTRIBUTES);
		pstmt.setString(1, owner.toUpperCase());
		pstmt.setString(2, typeName.toUpperCase());
		
		ResultSet rset = pstmt.executeQuery();
		
		while (rset.next()) {
			OraTypeField newField = new OraTypeField(rset);
			result.add(newField);
		}
		rset.close();
		//TODO: reuse prepared statement?
		pstmt.close();		
		
		return result;
	}

	private OraTypeField(ResultSet rs) throws SQLException {

		fieldIndex = rs.getInt(VIEW_ALL_TYPE_ATTRS.COL_ATTR_NO);
		fieldName  = rs.getString(VIEW_ALL_TYPE_ATTRS.COL_ATTR_NAME);

		PLSQL_TYPE plsqlType = decodeBaseType(
					 rs.getString(VIEW_ALL_TYPES.COL_TYPECODE),
				/*
				possible values -
					COLLECTION
					LCR$_PROCEDURE_RECORD
					LCR$_ROW_RECORD
					XMLTYPE
					LCR$_DDL_RECORD
					OBJECT
					ANYDATA
					ANYDATASET
					ANYTYPE
				*/

				rs.getString(VIEW_ALL_COLL_TYPES.COL_COLL_TYPE));
				/*
				possible values -
					VARYING ARRAY
					TABLE
				 */

		dataType = plsqlType.getTypeInstance();
		dataType.setOwner(rs.getString(VIEW_ALL_TYPE_ATTRS.COL_ATTR_TYPE_OWNER));
		dataType.setName(rs.getString(VIEW_ALL_TYPE_ATTRS.COL_ATTR_TYPE_NAME));

		if (dataType.isArray()) {
			PLSQL_TYPE subPlSqlType = PLSQL_TYPE.SCALAR;
			if (!StringUtils.isEmpty(rs.getString(VIEW_ALL_COLL_TYPES.COL_ELEM_TYPE_OWNER)))
				subPlSqlType = PLSQL_TYPE.OBJECT;

			BasePlsqlType	subDataType =  subPlSqlType.getTypeInstance();
			subDataType.setOwner(rs.getString(VIEW_ALL_COLL_TYPES.COL_ELEM_TYPE_OWNER));
			subDataType.setName(rs.getString(VIEW_ALL_COLL_TYPES.COL_ELEM_TYPE_NAME));
			dataType.setSubElementType(subDataType);
		}	
	}
	
	private static PLSQL_TYPE decodeBaseType(String typeCode, String collType) {
		
		if (StringUtils.isEmpty(typeCode))
			return PLSQL_TYPE.SCALAR;
		
		if ("OBJECT".equals(typeCode))
			return PLSQL_TYPE.OBJECT;
		
		if ("COLLECTION".equals(typeCode)) {
			if ("TABLE".equals(collType))
				return PLSQL_TYPE.NESTED_TABLE;
			else if ("VARYING ARRAY".equals(collType))
				return PLSQL_TYPE.VARRAY;			
		}
					
		//TODO: throw exception
		return PLSQL_TYPE.SCALAR;
	}

	
	public BasePlsqlType getDataType() {
		return dataType;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	

}
