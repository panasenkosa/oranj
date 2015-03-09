package org.oranj.dbprocs.constants;

public class SQL_VIEW {

    public static final String SELECT_TYPE_ATTRIBUTES =
            "SELECT UTA.ATTR_NO, " +
                    "UTA.ATTR_NAME, " +
                    "UTA.ATTR_TYPE_OWNER, " +
                    "UTA.ATTR_TYPE_NAME," +
                    "UTY.TYPECODE, " +
                    "UCT.COLL_TYPE, " +
                    "UCT.ELEM_TYPE_OWNER, " +
                    "UCT.ELEM_TYPE_NAME " +
                    "FROM ALL_TYPE_ATTRS UTA " +
                    "LEFT JOIN ALL_TYPES UTY ON UTA.ATTR_TYPE_OWNER=UTY.OWNER AND UTA.ATTR_TYPE_NAME=UTY.TYPE_NAME " +
                    "LEFT JOIN ALL_COLL_TYPES UCT ON UTA.ATTR_TYPE_OWNER=UCT.OWNER " +
                    "AND UTA.ATTR_TYPE_NAME=UCT.TYPE_NAME " +
                    "WHERE UTA.OWNER=? AND UTA.TYPE_NAME=? ORDER BY ATTR_NO";


    public static final String SELECT_PROCEDURE_ARGUMENTS =
                "SELECT SUBPROGRAM_ID, OBJECT_NAME, ARGUMENT_NAME, POSITION, " +
                        "SEQUENCE, DATA_LEVEL, DATA_TYPE, TYPE_OWNER, TYPE_NAME, " +
                        "TYPE_SUBNAME, PLS_TYPE, IN_OUT " +
                        "FROM ALL_ARGUMENTS " +
                        "WHERE OWNER=? AND PACKAGE_NAME=? ORDER BY SUBPROGRAM_ID, SEQUENCE";

    private SQL_VIEW() {
    }
}
