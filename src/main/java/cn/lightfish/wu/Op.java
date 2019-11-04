package cn.lightfish.wu;

public enum Op {
    //relational operators
    FROM, MAP, FILTER, LIMIT, ORDER, GROUP, VALUES, DISTINCT,

    JOIN,

    // types
    SCHEMA, SCALAR_TYPE, FIELD_SCHEMA, AS_TABLE,

    //atoms
    LITERAL, IDENTIFIER,

    //debug
    DESCRIBE, DUMP,

    // operators
    DOT, EQ, NE, GT, LT, GTE, LTE, PLUS, MINUS, AND, OR, NOT, AS_COLUMNNAME,

    //aggregateCall
    COUNT_STAR,
    COUNT,
    MIN,
    MAX,
    LAST_VALUE,
    ANY_VALUE,
    FIRST_VALUE,
    NTH_VALUE,
    LEAD,
    LAG,
    NTILE,
    SINGLE_VALUE,
    AVG,
    STDDEV_POP,
    REGR_COUNT,
    REGR_SXX,
    REGR_SYY,
    COVAR_POP,
    COVAR_SAMP,
    STDDEV_SAMP,
    STDDEV,
    VAR_POP,
    VAR_SAMP,
    VARIANCE,
    BIT_AND,
    BIT_OR,
}