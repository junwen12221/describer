package cn.lightfish.wu;

import cn.lightfish.rsqlBuilder.RowExpressionBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstTest {
    private final RelBuilder relBuilder;

    public AstTest() {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema.add("db1", new ReflectiveSchema(new RowExpressionBuilder.Db1()));
        final FrameworkConfig config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema).build();
        this.relBuilder = RelBuilder.create(config);
        Ast.Schema from = Ast.from("db1", "travelrecord");
        Ast.Schema from2 = Ast.from("db1", "travelrecord");
        Ast.Schema id = Ast.select(from, Ast.as(Ast.plus(Ast.id("id"), Ast.literal(1)), "id"));
        id = Ast.filter(id, Ast.eq(Ast.id("id"), Ast.literal(1)));
        id = Ast.select(id, Ast.as(Ast.id("id"), "id"));
//        id = Ast.as(id, Ast.fieldType("id2", "Int"));
        RelNode handle = handle(id);
        System.out.print(RelOptUtil.toString(handle));
    }

    public static void main(String[] args) {
        new AstTest();
    }

    private static SqlOperator op(Ast.Op op) {
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

    private RelNode handle(Ast.Node id) {
        relBuilder.clear();
        switch (id.getOp()) {
            case PROGRAM:
                break;
            case FROM: {
                Ast.FromSchema id1 = (Ast.FromSchema) id;
                relBuilder.scan(id1.getNames());
                return relBuilder.peek();
            }
            case MAP: {
                Ast.MapSchema id1 = (Ast.MapSchema) id;
                relBuilder.push(handle(id1.getSchema()));
                List<RexNode> maps = new ArrayList<>();
                List<String> alias = new ArrayList<>();
                for (Ast.AsNode mapping : id1.getExpr()) {
                    maps.add(toRex(mapping.getExpr()));
                    alias.add(mapping.getAlias());
                }
                relBuilder.projectNamed(maps, alias, true);
                return relBuilder.peek();
            }
            case FILTER:
                Ast.FilterSchema id1 = (Ast.FilterSchema) id;
                relBuilder.push(handle(id1.getSchema()));
                relBuilder.filter(toRex(id1.getExpr()));
                return relBuilder.peek();
            case LIMIT:
                break;
            case ORDER:
                break;
            case GROUP:
                break;
            case VALUES:
                break;
            case DISTINCT: {
//                Ast.FromSchema id1 = (Ast.FromSchema) id;
//                relBuilder.distinct().scan(id1.getNames());
//                return relBuilder.peek();
//                break;
            }
            case JOIN: {
//                Ast.FilterSchema id1 = (Ast.JoinSchema) id;
//                relBuilder.push(handle(id1.getSchema()));
//                relBuilder.filter(toRex(id1.getExpr()));
//                return relBuilder.peek();
//                break;
            }
            case SCHEMA:
                break;
            case SCALAR_TYPE:
                break;
            case FIELD_SCHEMA:
                break;
            case AS_SCHEMA: {
//                Ast.AsSchema id1 = (Ast.AsSchema) id;
//                relBuilder.push(handle(id1.getSchema()));
//                List<Ast.FieldSchema> fields = id1.getFields();
//                relBuilder.join
                break;
            }
            case LITERAL:
                break;
            case IDENTIFIER:
                break;
            case DESCRIBE:
                break;
            case DUMP:
                break;
            case DOT:
                break;
            case EQ:
                break;
            case NE:
                break;
            case GT:
                break;
            case LT:
                break;
            case GTE:
                break;
            case LTE:
                break;
            case PLUS:
                break;
            case MINUS:
                break;
            case AND:
                break;
            case OR:
                break;
            case NOT:
                break;
            case AS:
                break;
        }
        throw new UnsupportedOperationException();
    }

    private RexNode toRex(Ast.Node node) {
        if (node instanceof Ast.Expr) {
            Ast.Expr node1 = (Ast.Expr) node;
            return this.relBuilder.call(op(node.getOp()), toRex(Arrays.asList(node1.left, node1.right)));
        }
        switch (node.getOp()) {
            case IDENTIFIER: {
                Ast.Identifier node1 = (Ast.Identifier) node;
                return relBuilder.field(node1.getValue());
            }
            case LITERAL: {
                Ast.Literal node1 = (Ast.Literal) node;
                return relBuilder.literal(node1.getValue());
            }
        }
        throw new UnsupportedOperationException();
    }

    private ImmutableList<RexNode> toRex(Iterable<Ast.Node> operands) {
        final ImmutableList.Builder<RexNode> builder = ImmutableList.builder();
        for (Ast.Node operand : operands) {
            builder.add(toRex(operand));
        }
        return builder.build();
    }

}