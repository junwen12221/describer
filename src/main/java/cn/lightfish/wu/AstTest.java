package cn.lightfish.wu;

import cn.lightfish.rsqlBuilder.RowExpressionBuilder;
import cn.lightfish.wu.ast.*;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.CorrelationId;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlAggFunction;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Holder;
import org.apache.calcite.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.builder;

public class AstTest {
    private final RelBuilder relBuilder;
    private final Deque<Map<String, Holder<RexCorrelVariable>>> correlMap = new ArrayDeque<>();

    public AstTest() throws Exception {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema.add("DB1", new ReflectiveSchema(new RowExpressionBuilder.Db1()));
        final FrameworkConfig config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema).build();

        PlannerImpl planner = new PlannerImpl(config);
        SqlNode parse = planner.parse("select (select count(t.id) from  db1.TRAVELRECORD as t    where t.id not between 1 and 2 and t.user_id = t2.id or ((not exists (select t.user_id from  db1.TRAVELRECORD as t3  where t3.id = 4 or t2.user_id = 1))) ) from db1.TRAVELRECORD as t2");
        SqlNode validate = planner.validate(parse);
        RelNode convert = planner.convert(validate);
        String s = RelOptUtil.toString(convert);
        System.out.println(s);
        this.relBuilder = RelBuilder.create(config);
    }

    public static void main(String[] args) throws Exception {
        new AstTest();
    }

    private static SqlOperator op(Op op) {
        switch (op) {
            case EQ:
                return SqlStdOperatorTable.EQUALS;
            case NE:
                return SqlStdOperatorTable.NOT_EQUALS;
            case GT:
                return SqlStdOperatorTable.GREATER_THAN;
            case GTE:
                return SqlStdOperatorTable.GREATER_THAN_OR_EQUAL;
            case LT:
                return SqlStdOperatorTable.LESS_THAN;
            case LTE:
                return SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
            case AND:
                return SqlStdOperatorTable.AND;
            case OR:
                return SqlStdOperatorTable.OR;
            case NOT:
                return SqlStdOperatorTable.NOT;
            case PLUS:
                return SqlStdOperatorTable.PLUS;
            case MINUS:
                return SqlStdOperatorTable.MINUS;
            case DOT:
                return SqlStdOperatorTable.DOT;
            default:
                throw new AssertionError("unknown: " + op);
        }
    }

    private List<RelNode> handle(List<Schema> inputs) {
        return inputs.stream().map(this::handle).collect(Collectors.toList());
    }

    public RelNode handle(Schema input) {
        relBuilder.clear();
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
            case UNION:
                return setSchema((SetSchema) input);
            case LEFT_JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case SEMI_JOIN:
            case ANTI_JOIN:
            case INNER_JOIN: {
                return join((JoinSchema) input);
            }
            case CORRELATE_INNER_JOIN:
            case CORRELATE_LEFT_JOIN: {
                return correlateJoin((CorJoinSchema) input);
            }
            case AS_TABLE: {
                return asTable(input);
            }
            default:
        }
        throw new UnsupportedOperationException();
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

    private RelNode setSchema(SetSchema input) {
        int size = input.getSchemas().size();
        RelBuilder relBuilder = this.relBuilder.pushAll(handle(input.getSchemas()));
        switch (input.getOp()) {
            case UNION:
                return relBuilder.union(false, size).build();
            case UNION__ALL:
                return relBuilder.union(true, size).build();
            case EXCEPT:
                return relBuilder.minus(false, size).build();
            case EXCEPT_ALL:
                return relBuilder.minus(true, size).build();
            case INTERSECT:
                return relBuilder.intersect(false, size).build();
            case INTERSECT_ALL:
                return relBuilder.intersect(true, size).build();
            default:
                throw new UnsupportedOperationException();

        }
    }

    private RelNode asTable(Schema input) {
        return relBuilder.push(handle(input)).as(((AsTable) input).getAlias()).build();
    }

    private RelNode group(GroupSchema input) {
        return relBuilder.push(handle(input.getSchema()))
                .aggregate(relBuilder.groupKey(toRex(input.getKeys())), toAggregateCall(input.getExprs()))
                .build();
    }

    private List<RelBuilder.AggCall> toAggregateCall(List<AggregateCall> exprs) {
        return exprs.stream().map(this::toAggregateCall).collect(Collectors.toList());
    }

    private RelBuilder.AggCall toAggregateCall(AggregateCall expr) {
        return relBuilder.aggregateCall(toSqlAggFunction(expr.getOp()),
                toRex(expr.getOperands() == null ? Collections.emptyList() : expr.getOperands()))
                .as(expr.getAlias())
                .sort(expr.getOrderKeys() == null ? Collections.emptyList() : toSortRex(expr.getOrderKeys()))
                .distinct(expr.isDistinct())
                .approximate(expr.isApproximate())
                .ignoreNulls(expr.isIgnoreNulls());
    }

    private SqlAggFunction toSqlAggFunction(Op op) {
        switch (op) {
            case COUNT:
                return SqlStdOperatorTable.COUNT;
            case MIN:
                return SqlStdOperatorTable.MIN;
            case MAX:
                return SqlStdOperatorTable.MAX;
            case LAST_VALUE:
                return SqlStdOperatorTable.LAST_VALUE;
            case ANY_VALUE:
                return SqlStdOperatorTable.ANY_VALUE;
            case FIRST_VALUE:
                return SqlStdOperatorTable.FIRST_VALUE;
            case NTH_VALUE:
                return SqlStdOperatorTable.NTH_VALUE;
            case LEAD:
                return SqlStdOperatorTable.LEAD;
            case LAG:
                return SqlStdOperatorTable.LAG;
            case NTILE:
                return SqlStdOperatorTable.NTILE;
            case SINGLE_VALUE:
                return SqlStdOperatorTable.SINGLE_VALUE;
            case AVG:
                return SqlStdOperatorTable.AVG;
            case STDDEV_POP:
                return SqlStdOperatorTable.STDDEV_POP;
            case REGR_COUNT:
                return SqlStdOperatorTable.REGR_COUNT;
            case REGR_SXX:
                return SqlStdOperatorTable.REGR_SXX;
            case REGR_SYY:
                return SqlStdOperatorTable.REGR_SYY;
            case COVAR_POP:
                return SqlStdOperatorTable.COVAR_POP;
            case COVAR_SAMP:
                return SqlStdOperatorTable.COVAR_SAMP;
            case STDDEV_SAMP:
                return SqlStdOperatorTable.STDDEV_SAMP;
            case STDDEV:
                return SqlStdOperatorTable.STDDEV;
            case VAR_POP:
                return SqlStdOperatorTable.VAR_POP;
            case VAR_SAMP:
                return SqlStdOperatorTable.VAR_SAMP;
            case VARIANCE:
                return SqlStdOperatorTable.VARIANCE;
            case BIT_AND:
                return SqlStdOperatorTable.BIT_AND;
            case BIT_OR:
                return SqlStdOperatorTable.BIT_OR;
            default:
        }
        throw new UnsupportedOperationException();
    }

    private RelNode from(FromSchema input) {
        relBuilder.scan(input.getNames());
        return relBuilder.build();
    }

    private RelNode map(MapSchema input) {
        return relBuilder.push(handle(input.getSchema())).project(toRex(input.getExpr())).build();
    }

    private RelNode filter(FilterSchema input) {
        return relBuilder.push(handle(input.getSchema())).filter(toRex(input.getExpr())).build();
    }

    private RelNode values(ValuesSchema input) {
        return relBuilder.values(toType(input.getFieldNames()), toRex(input.getValues()).toArray(new RelNode[0])).build();
    }

    private RelNode distinct(DistinctSchema input) {
        return relBuilder.push(handle(input.getSchema())).distinct().build();
    }

    private RelNode order(OrderSchema input) {
        return relBuilder.push(handle(input.getSchema())).sort(toSortRex(input.getOrders())).build();
    }

    private List<RexNode> toSortRex(List<Pair<Identifier, Direction>> orders) {
        final List<RexNode> nodes = new ArrayList<>();
        for (Pair<Identifier, Direction> field : orders) {
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

    private void toSortRex(List<RexNode> nodes, Pair<Identifier, Direction> pair) {
        if (pair.left.isStar()) {
            for (RexNode node : relBuilder.fields()) {
                if (pair.right == Direction.DESC) {
                    node = relBuilder.desc(node);
                }
                nodes.add(node);
            }
        } else {
            RexNode node = toRex(pair.left);
            if (pair.right == Direction.DESC) {
                node = relBuilder.desc(node);
            }
            nodes.add(node);
        }
    }

    private RexNode toRex(Node node) {
        if (node instanceof Expr) {
            Expr node1 = (Expr) node;
            if (node1.op != Op.AS_COLUMNNAME) {
                return this.relBuilder.call(op(node.getOp()), toRex(node1.getNodes()));
            } else {
                Identifier id = (Identifier) node1.getNodes().get(1);
                return this.relBuilder.alias(toRex(node1.getNodes().get(0)), id.getValue());
            }
        }
        switch (node.getOp()) {
            case IDENTIFIER: {
                Identifier node1 = (Identifier) node;
                String value = node1.getValue();
                if (value.startsWith("$")) {
                    return relBuilder.field(Integer.parseInt(node1.getValue().substring(1, value.length())));
                } else {
                    return relBuilder.field(node1.getValue());
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
                return typeFactory.createSqlType(SqlTypeName.REAL);
            default:
                return typeFactory.createSqlType(SqlTypeName.VARCHAR);
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