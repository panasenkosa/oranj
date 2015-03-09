package org.oranj.dbprocs.constants;

/**
 * Oracle view ALL_TYPES_ATTRS constants
     OWNER	VARCHAR2(30)	 	    Owner of the type
     TYPE_NAME	VARCHAR2(30)	 	Name of the type
     ATTR_NAME	VARCHAR2(30)	 	Name of the attribute
     ATTR_TYPE_MOD	VARCHAR2(7)	 	Type modifier of the attribute:
     REF
     POINTER
     ATTR_TYPE_OWNER	VARCHAR2(30)	 	Owner of the type of the attribute
     ATTR_TYPE_NAME	VARCHAR2(30)	 	    Name of the type of the attribute
     LENGTH	NUMBER	 	                    Length of the CHAR attribute, or maximum
                                                length of the VARCHAR or VARCHAR2 attribute.
     PRECISION	NUMBER	 	                Decimal precision of the NUMBER or DECIMAL attribute,
                                                or binary precision of the FLOAT attribute.
     SCALE	NUMBER	 	                    Scale of the NUMBER or DECIMAL attribute
     CHARACTER_SET _NAME	VARCHAR2(44)	Character set name of the attribute (CHAR_CS or NCHAR_CS)
     ATTR_NO	NUMBER	 	                Syntactical order number or position of the attribute as specified
                                                in the type specification or CREATE TYPE statement (not to be used as an ID number)
     INHERITED	VARCHAR2(3)	 	            Indicates whether the attribute is inherited from a supertype (YES) or not (NO)
 */
public class VIEW_ALL_TYPE_ATTRS {

    public static final String VIEW_NAME              = "ALL_TYPES_ATTRS";
    public static final String COL_TYPE_NAME          = "TYPE_NAME";
    public static final String COL_ATTR_NAME          = "ATTR_NAME";
    public static final String COL_ATTR_NO            = "ATTR_NO";
    public static final String COL_ATTR_TYPE_OWNER    = "ATTR_TYPE_OWNER";
    public static final String COL_ATTR_TYPE_NAME     = "ATTR_TYPE_NAME";

    private VIEW_ALL_TYPE_ATTRS() {
    }


}
