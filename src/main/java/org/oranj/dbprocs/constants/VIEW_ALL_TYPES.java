package org.oranj.dbprocs.constants;

/**
 * Oracle view ALL_TYPES constants
 *
     OWNER	VARCHAR2(30)	 	    Owner of the type
     TYPE_NAME	VARCHAR2(30)	 	Name of the type
     TYPE_OID	RAW(16)	 	        Object identifier (OID) of the type
     TYPECODE	VARCHAR2(30)	 	Typecode of the type
     ATTRIBUTES	NUMBER	 	        Number of attributes (if any) in the type
     METHODS	NUMBER	 	        Number of methods (if any) in the type
     PREDEFINED	VARCHAR2(3)	 	    Indicates whether the type is a predefined type (YES) or not (NO)
     INCOMPLETE	VARCHAR2(3)	 	    Indicates whether the type is an incomplete type (YES) or not (NO)
     FINAL	VARCHAR2(3)	 	        Indicates whether the type is a final type (YES) or not (NO)
     INSTANTIABLE	VARCHAR2(3)	 	Indicates whether the type is an instantiable type (YES) or not (NO)
     SUPERTYPE_OWNER	VARCHAR2(30)	 	Owner of the supertype (NULL if type is not a subtype)
     SUPERTYPE_NAME	VARCHAR2(30)	    	Name of the supertype (NULL if type is not a subtype)
     LOCAL_ATTRIBUTES	NUMBER	 	        Number of local (not inherited) attributes (if any) in the subtype
     LOCAL_METHODS	NUMBER	 	            Number of local (not inherited) methods (if any) in the subtype
     TYPEID	RAW(16)	 	                    Type ID value of the type
 *
 */


public class VIEW_ALL_TYPES {

    public static final String VIEW_NAME         = "ALL_TYPES";
    public static final String COL_OWNER         = "OWNER";
    public static final String COL_TYPE_NAME     = "TYPE_NAME";
    public static final String COL_TYPECODE      = "TYPECODE";

    private VIEW_ALL_TYPES() {
    }
}
