package org.oranj.dbprocs.constants;

/**
 *  Oracle view ALL_COLL_TYPES constants
 *  ALL_COLL_TYPES describes all named collection types (varrays and nested tables) accessible to the current user.
 *
     OWNER	VARCHAR2(30)	NOT NULL	Owner of the collection
     TYPE_NAME	VARCHAR2(30)	NOT NULL	Name of the collection
     COLL_TYPE	VARCHAR2(30)	NOT NULL	Description of the collection, such as VARYING ARRAY, [nested] TABLE
     UPPER_BOUND	NUMBER	 	            For varrays only, maximum size
     ELEM_TYPE_MOD	VARCHAR2(7)	 	        Type modifier of the element
     ELEM_TYPE_OWNER	VARCHAR2(30)	 	Owner of the type upon which the collection is based. This value is useful primarily in the case of a user-defined type.
     ELEM_TYPE_NAME	VARCHAR2(30)	 	    Name of the datatype or user-defined type upon which the collection is based
     LENGTH	NUMBER	 	                    Length of CHAR elements or maximum length of VARCHAR or VARCHAR2 elements
     PRECISION	NUMBER	 	                Decimal precision of NUMBER or DECIMAL elements; binary precision of FLOAT elements
     SCALE	NUMBER	 	                    Scale of NUMBER or DECIMAL elements
     CHARACTER_SET_NAME	VARCHAR2(44)	 	Name of the character set (CHAR_CS | NCHAR_CS)
     ELEM_STORAGE	VARCHAR2(7)	 	        Obsolete column
     NULLS_STORED	VARCHAR2(3)	        	Obsolete column
 */

public class VIEW_ALL_COLL_TYPES {

    public static final String VIEW_NAME         = "ALL_COLL_TYPES";
    public static final String COL_OWNER         = "OWNER";
    public static final String COL_COLL_TYPE     = "COLL_TYPE";
    public static final String COL_ELEM_TYPE_OWNER    = "ELEM_TYPE_OWNER";
    public static final String COL_ELEM_TYPE_NAME     = "ELEM_TYPE_NAME";

    private VIEW_ALL_COLL_TYPES() {
    }
}
