package cn.lightfish.wu;

import cn.lightfish.rsqlBuilder.RowExpressionBuilder;
import cn.lightfish.wu.ast.*;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlAggFunction;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.builder;

public class AstTest {
    private final RelBuilder relBuilder;

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
            default:
                throw new AssertionError("unknown: " + op);
        }
    }

    private RelNode handle(Schema input) {
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
                break;
            case DISTINCT: {
                return distinct((DistinctSchema) input);
            }
            case JOIN: {
                relBuilder.correlate()
            }
            case SCHEMA:
                break;
            case SCALAR_TYPE:
                break;
            case FIELD_SCHEMA:
                break;
            case AS_TABLE: {
                return asTable(input);
            }
            case DESCRIBE:
                break;
            case DUMP:
                break;
            case DOT:
                break;
        }
        throw new UnsupportedOperationException();
    }

    private RelNode asTable(Schema input) {
        return relBuilder.push(handle(input)).as(((AsTable) input).getAlias()).peek();
    }

    private RelNode group(GroupSchema input) {
        return relBuilder.push(handle(input.getSchema()))
                .aggregate(relBuilder.groupKey(toRex(input.getKeys())), toAggregateCall(input.getExprs()))
                .peek();
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
        return null;
    }

    private RelNode from(FromSchema input) {
        relBuilder.scan(input.getNames());
        return relBuilder.peek();
    }

    private RelNode map(MapSchema input) {
        return relBuilder.push(handle(input.getSchema())).project(toRex(input.getExpr())).peek();
    }

    private RelNode filter(FilterSchema input) {
        return relBuilder.push(handle(input.getSchema())).filter(toRex(input.getExpr())).peek();
    }

    private RelNode distinct(DistinctSchema input) {
        return relBuilder.push(handle(input.getSchema())).distinct().peek();
    }

    private RelNode order(OrderSchema input) {
        return relBuilder.push(handle(input.getSchema())).sort(toSortRex(input.getOrders())).peek();
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
        return relBuilder.peek();
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

    private SqlOperator fun(Op op) {
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
            default:
                throw new AssertionError("unknown: " + op);
        }
    }
}