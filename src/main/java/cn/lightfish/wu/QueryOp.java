package cn.lightfish.wu;

import cn.lightfish.rsqlBuilder.DesBuilder;
import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.Direction;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.base.*;
import cn.lightfish.wu.ast.query.*;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.CorrelationId;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlAggFunction;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Holder;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.builder;

public class QueryOp {
    private final DesBuilder relBuilder;
    private final Deque<Map<String, Holder<RexCorrelVariable>>> correlMap = new ArrayDeque<>();
    private final Map<String, RelNode> aliasMap = new HashMap<>();
    private final Map<String, SqlAggFunction> sqlAggFunctionMap = new HashMap<>();
    private final Map<String, SqlOperator> sqlOperatorMap = new HashMap<>();

    public QueryOp(DesBuilder relBuilder) {
        this.relBuilder = relBuilder;

        Map<String, SqlAggFunction> functionMap = this.sqlAggFunctionMap;
        functionMap.put("avg", SqlStdOperatorTable.AVG);
        functionMap.put("count", SqlStdOperatorTable.COUNT);
        functionMap.put("first", SqlStdOperatorTable.FIRST_VALUE);
        functionMap.put("last", SqlStdOperatorTable.LAST_VALUE);
        functionMap.put("max", SqlStdOperatorTable.MAX);
        functionMap.put("min", SqlStdOperatorTable.MIN);

        Map<String, SqlOperator> sqlOperatorMap = this.sqlOperatorMap;
        sqlOperatorMap.put("eq", SqlStdOperatorTable.EQUALS);
        sqlOperatorMap.put("ne", SqlStdOperatorTable.NOT_EQUALS);
        sqlOperatorMap.put("gt", SqlStdOperatorTable.GREATER_THAN);
        sqlOperatorMap.put("gte", SqlStdOperatorTable.GREATER_THAN_OR_EQUAL);
        sqlOperatorMap.put("lt", SqlStdOperatorTable.LESS_THAN);
        sqlOperatorMap.put("lte", SqlStdOperatorTable.LESS_THAN_OR_EQUAL);
        sqlOperatorMap.put("and", SqlStdOperatorTable.AND);
        sqlOperatorMap.put("or", SqlStdOperatorTable.OR);
        sqlOperatorMap.put("not", SqlStdOperatorTable.NOT);
        sqlOperatorMap.put("plus", SqlStdOperatorTable.NOT);
        sqlOperatorMap.put("minus", SqlStdOperatorTable.MINUS);
        sqlOperatorMap.put("dot", SqlStdOperatorTable.DOT);

        sqlOperatorMap.put("lcase", SqlStdOperatorTable.LOWER);
        sqlOperatorMap.put("lower", SqlStdOperatorTable.LOWER);

        sqlOperatorMap.put("ucase", SqlStdOperatorTable.UPPER);
        sqlOperatorMap.put("upper", SqlStdOperatorTable.UPPER);

        sqlOperatorMap.put("round", SqlStdOperatorTable.ROUND);

        sqlOperatorMap.put("isnull", SqlStdOperatorTable.IS_NULL);
    }

    private SqlOperator op(String op) {
        SqlOperator sqlOperator = sqlOperatorMap.get(op);
        if (sqlOperator == null) {
            throw new AssertionError("unknown: " + op);
        }
        return sqlOperator;
    }

    private List<RelNode> handle(List<Schema> inputs) {
        return inputs.stream().map(this::handle).collect(Collectors.toList());
    }

    public RelNode complie(Schema root) {
        return handle(root);
    }

    public RelNode handle(Schema input) {
        relBuilder.clear();
        try {
            switch (input.getOp()) {
                case FROM:
                    return from((FromSchema) input);
                case MAP:
                    return map((MapSchema) input);
                case FILTER:
                    return filter((FilterSchema) input);
                case LIMIT:
                    return limit((LimitSchema) input);
                case ORDER:
                    return order((OrderSchema) input);
                case GROUP:
                    return group((GroupSchema) input);
                case VALUES:
                    return values((ValuesSchema) input);
                case DISTINCT:
                    return distinct((DistinctSchema) input);
                case UNION_ALL:
                case UNION_DISTINCT:
                case EXCEPT_ALL:
                case EXCEPT_DISTINCT:
                    return setSchema((SetOpSchema) input);
                case LEFT_JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case SEMI_JOIN:
                case ANTI_JOIN:
                case INNER_JOIN:
                    return join((JoinSchema) input);
                case CORRELATE_INNER_JOIN:
                case CORRELATE_LEFT_JOIN:
                    return correlateJoin((CorJoinSchema) input);
                case AS_TABLE:
                    return asTable((AsTable) input);
                case PROJECT:
                    return project((ProjectSchema) input);
                default:
            }
        } finally {
            relBuilder.clear();
        }
        throw new UnsupportedOperationException();
    }


    private RelNode project(ProjectSchema input) {
        RelNode origin = handle(input.getSchema());
        List<String> alias = input.getAlias();
        relBuilder.push(origin);
        relBuilder.projectNamed(relBuilder.fields(), alias, true);
        return relBuilder.build();
    }

    private RelNode correlateJoin(CorJoinSchema input) {
        List<AsTable> schemas = input.getSchemas();
        Map<String, Holder<RexCorrelVariable>> map;
        correlMap.push(map = new HashMap<>());
        try {
            for (AsTable schema : schemas) {
                RelNode relNode = handle(schema);
                relBuilder.push(relNode);
                Holder<RexCorrelVariable> of = Holder.of(null);
                relBuilder.variable(of);
                map.put(schema.getAlias(), of);
            }
            Set<CorrelationId> variablesSet = map.values().stream().map(i -> i.get().id).collect(Collectors.toSet());
            return relBuilder.join(joinOp(input.getOp()), toRex(input.getCondition()), variablesSet).build();
        } finally {
            correlMap.pop();
        }
    }

    private RelNode join(JoinSchema input) {
        return relBuilder.pushAll(handle(input.getSchemas())).join(joinOp(input.getOp()), toRex(input.getCondition())).build();
    }

    private JoinRelType joinOp(Op op) {
        switch (op) {
            case INNER_JOIN:
                return JoinRelType.INNER;
            case LEFT_JOIN:
                return JoinRelType.LEFT;
            case RIGHT_JOIN:
                return JoinRelType.RIGHT;
            case FULL_JOIN:
                return JoinRelType.FULL;
            case SEMI_JOIN:
                return JoinRelType.SEMI;
            case ANTI_JOIN:
                return JoinRelType.ANTI;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private RelNode setSchema(SetOpSchema input) {
        int size = input.getSchemas().size();
        RelBuilder relBuilder = this.relBuilder.pushAll(handle(input.getSchemas()));
        switch (input.getOp()) {
            case UNION_DISTINCT:
                return relBuilder.union(false, size).build();
            case UNION_ALL:
                return relBuilder.union(true, size).build();
            case EXCEPT_DISTINCT:
                return relBuilder.minus(false, size).build();
            case EXCEPT_ALL:
                return relBuilder.minus(true, size).build();
            case INTERSECT_DISTINCT:
                return relBuilder.intersect(false, size).build();
            case INTERSECT_ALL:
                return relBuilder.intersect(true, size).build();
            default:
                throw new UnsupportedOperationException();

        }
    }

    private RelNode asTable(AsTable input) {
        RelNode build = relBuilder.push(handle(input.getSchema())).as(input.getAlias()).build();
        aliasMap.put(input.getAlias(), build);
        return build;
    }

    private RelNode group(GroupSchema input) {
        return relBuilder.push(handle(input.getSchema()))
                .aggregate(relBuilder.groupKey(toRex(input.getKeys())), toAggregateCall(input.getExprs()))
                .build();
    }

    private int toRex(List<GroupItem> keys) {
        return 0;
    }

    private List<RelBuilder.AggCall> toAggregateCall(List<AggregateCall> exprs) {
        return exprs.stream().map(this::toAggregateCall).collect(Collectors.toList());
    }

    private RelBuilder.AggCall toAggregateCall(AggregateCall expr) {
        return relBuilder.aggregateCall(toSqlAggFunction(expr.getFunction()),
                toRex(expr.getOperands() == null ? Collections.emptyList() : expr.getOperands()))
                .as(expr.getAlias())
                .sort(expr.getOrderKeys() == null ? Collections.emptyList() : toSortRex(expr.getOrderKeys()))
                .distinct(expr.getDistinct() == Boolean.TRUE)
                .approximate(expr.getApproximate() == Boolean.TRUE)
                .ignoreNulls(expr.getIgnoreNulls() == Boolean.TRUE)
                .filter(expr.getFilter() == null ? null : toRex(expr.getFilter()));
    }

    private SqlAggFunction toSqlAggFunction(String op) {
        SqlAggFunction sqlAggFunction = sqlAggFunctionMap.get(op);
        if (sqlAggFunction == null) {
            throw new UnsupportedOperationException();
        }
        return sqlAggFunction;
    }

    private RelNode from(FromSchema input) {
        RelNode build = relBuilder.scan(input.getNames()).build();
        aliasMap.put(input.getNames()[1], build);
        return build;
    }

    private RelNode map(MapSchema input) {
        RelNode handle = handle(input.getSchema());
        relBuilder.push(handle);
        List<RexNode> nodes = toRex(input.getExpr());
        relBuilder.push(handle);
        relBuilder.project(nodes);
        return relBuilder.build();
    }

    private RelNode filter(FilterSchema input) {
        return relBuilder.push(handle(input.getSchema())).filter(toRex(input.getExpr())).build();
    }

    private RelNode values(ValuesSchema input) {
        return relBuilder.values2(toType(input.getFieldNames()), (input.getValues()).toArray(new Object[0])).build();
    }

    private RelNode distinct(DistinctSchema input) {
        RelNode handle = handle(input.getSchema());
        relBuilder.push(handle);
        relBuilder.distinct();
        return relBuilder.peek();
    }

    private RelNode order(OrderSchema input) {
        return relBuilder.push(handle(input.getSchema())).sort(toSortRex(input.getOrders())).build();
    }

    private List<RexNode> toSortRex(List<OrderItem> orders) {
        final List<RexNode> nodes = new ArrayList<>();
        for (OrderItem field : orders) {
            toSortRex(nodes, field);
        }
        return nodes;
    }

    private RelNode limit(LimitSchema input) {
        relBuilder.push(handle(input.getSchema()));
        Number offset = (Number) input.getOffset().getValue();
        Number limit = (Number) input.getLimit().getValue();
        relBuilder.limit(offset.intValue(), limit.intValue());
        return relBuilder.build();
    }

    private void toSortRex(List<RexNode> nodes, OrderItem pair) {
        if (pair.getColumnName().isStar()) {
            for (RexNode node : relBuilder.fields()) {
                if (pair.getDirection() == Direction.DESC) {
                    node = relBuilder.desc(node);
                }
                nodes.add(node);
            }
        } else {
            RexNode node = toRex(pair.getColumnName());
            if (pair.getDirection() == Direction.DESC) {
                node = relBuilder.desc(node);
            }
            nodes.add(node);
        }
    }

    public RexNode toRex(Node node) {
        switch (node.getOp()) {
            case IDENTIFIER: {
                Identifier node1 = (Identifier) node;
                String value = node1.getValue();
                if (value.startsWith("$")) {
                    return relBuilder.field(Integer.parseInt(node1.getValue().substring(1, value.length())));
                } else {
                    RelNode relNode = aliasMap.getOrDefault(value, null);
                    if (relNode != null) {
                        relBuilder.push(relNode);
                        String value1 = node1.getValue();
                        return relBuilder.field(value, value1);
                    }
                    return relBuilder.field(value);
                }
            }
            case PROPERTY: {
                Property node1 = (Property) node;
                List<String> value = node1.getValue();
                if (value.size() == 2 && !correlMap.isEmpty()) {
                    String tableName = value.get(0);
                    Optional<Map<String, Holder<RexCorrelVariable>>> first = correlMap.stream().filter(i -> i.get(tableName) != null).findFirst();
                    if (first.isPresent()) {
                        Map<String, Holder<RexCorrelVariable>> stringHolderMap = first.get();
                        Holder<RexCorrelVariable> correlVariableHolder = stringHolderMap.get(tableName);
                        return relBuilder.field(correlVariableHolder.get(), value.get(1));
                    }
                }
                if (value.size() == 2) {
                    int size = correlMap.size();
                    for (int i = 0; i < size; i++) {
                        RexNode field = relBuilder.field(i, value.get(0), value.get(1));
                        if (field != null) {
                            return field;
                        }
                    }
                }
                throw new UnsupportedOperationException();
            }
            case LITERAL: {
                Literal node1 = (Literal) node;
                return relBuilder.literal(node1.getValue());
            }
            default: {
                if (node instanceof Expr) {
                    Expr node1 = (Expr) node;
                    if (node1.op == Op.AS_COLUMNNAME) {
                        Identifier id = (Identifier) node1.getNodes().get(1);
                        return this.relBuilder.alias(toRex(node1.getNodes().get(0)), id.getValue());

                    } else if (node.op == Op.DOT) {
                        Identifier tableName = (Identifier) node1.getNodes().get(0);
                        Identifier fieldName = (Identifier) node1.getNodes().get(1);
                        RelNode relNode = aliasMap.getOrDefault(tableName.getValue(), null);
                        if (relNode != null) {
                            relBuilder.push(relNode);
                            return relBuilder.field(fieldName.getValue());
                        } else {
                            return relBuilder.field(fieldName.getValue());
                        }
                    } else if (node.op == Op.FUN) {
                        Fun node2 = (Fun) node;
                        return this.relBuilder.call(op(node2.getFunctionName()), toRex(node1.getNodes()));
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    private List<RexNode> toRex(Iterable<Node> operands) {
        final ImmutableList.Builder<RexNode> builder = builder();
        for (Node operand : operands) {
            builder.add(toRex(operand));
        }
        return builder.build();
    }

    private RelDataType toType(String typeText) {
        final RelDataTypeFactory typeFactory = relBuilder.getTypeFactory();
        switch (typeText) {
            case "boolean":
                return typeFactory.createSqlType(SqlTypeName.BOOLEAN);
            case "int":
                return typeFactory.createSqlType(SqlTypeName.INTEGER);
            case "float":
                return typeFactory.createSqlType(SqlTypeName.FLOAT);
            case "double":
                return typeFactory.createSqlType(SqlTypeName.DOUBLE);
            case "long":
                return typeFactory.createSqlType(SqlTypeName.BIGINT);
            case "date":
                return typeFactory.createSqlType(SqlTypeName.DATE);
            case "time":
                return typeFactory.createSqlType(SqlTypeName.TIME);
            case "timestamp":
                return typeFactory.createSqlType(SqlTypeName.TIMESTAMP);
            case "binary":
                return typeFactory.createSqlType(SqlTypeName.VARBINARY);
            case "String":
            case "string":
                return typeFactory.createSqlType(SqlTypeName.VARCHAR);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private RelDataType toType(List<FieldSchema> fieldSchemaList) {
        final RelDataTypeFactory typeFactory = relBuilder.getTypeFactory();
        final RelDataTypeFactory.Builder builder = typeFactory.builder();
        for (FieldSchema fieldSchema : fieldSchemaList) {
            builder.add(fieldSchema.getId(), toType(fieldSchema.getType()));
        }
        return builder.build();
    }
}