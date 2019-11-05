package cn.lightfish.wu;

public enum Op {
    //SET OPERATORS
    UNION, UNION__ALL, EXCEPT, EXCEPT_ALL, INTERSECT, INTERSECT_ALL,

    //relational operators
    FROM, MAP, FILTER, LIMIT, ORDER, GROUP, VALUES, DISTINCT,
    INNER_JOIN,

    LEFT_JOIN,

    CORRELATE_INNER_JOIN,

    CORRELATE_LEFT_JOIN,

    RIGHT_JOIN,

    FULL_JOIN,

    SEMI_JOIN,

    ANTI_JOIN,

    // types
    SCHEMA, SCALAR_TYPE, FIELD_SCHEMA, AS_TABLE,

    //atoms
    LITERAL, IDENTIFIER, PROPERTY,

    //debug
    DESCRIBE, DUMP,

    // operators
    DOT, EQ, NE, GT, LT, GTE, LTE, PLUS, MINUS, AND, OR, NOT, AS_COLUMNNAME, CAST,

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