package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.Direction;
import cn.lightfish.wu.ast.as.AsSchema;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.modify.ModifyTable;
import cn.lightfish.wu.ast.query.*;
import org.apache.calcite.avatica.util.ByteString;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ExplainVisitor implements NodeVisitor {
    final StringBuilder sb = new StringBuilder();

    @Override
    public void visit(MapSchema mapSchema) {
        Schema schema = mapSchema.getSchema();
        List<Expr> expr = mapSchema.getExpr();
        sb.append("map(");
        schema.accept(this);
        sb.append(",");
        joinNode(expr);
        sb.append(")");
    }


    @Override
    public void visit(GroupSchema groupSchema) {
        List<AggregateCall> exprs = groupSchema.getExprs();
        List<GroupItem> keys = groupSchema.getKeys();


        sb.append("group(");
        {
            Schema schema = groupSchema.getSchema();
            schema.accept(this);
        }
        sb.append(",");
        {
            sb.append("keys(");
            groupKey(keys);
            sb.append(")");
            sb.append(",");
            sb.append("aggregating(");

            int size = exprs.size();
            int lastIndex = exprs.size() - 1;
            for (int i = 0; i < size; i++) {
                AggregateCall call = exprs.get(i);


                sb.append("call(");
                String function = call.getFunction();
                sb.append(function);
                sb.append(",");
                String alias = call.getAlias();//null
                sb.append("null");
                List<Expr> operands = call.getOperands();
                joinNode(operands);
                sb.append(",");
                Boolean distinct = call.getDistinct();
                sb.append("/*distinct*/").append(distinct).append(",");
                Boolean approximate = call.getApproximate();
                sb.append("/*approximate*/").append(approximate).append(",");
                Boolean ignoreNulls = call.getIgnoreNulls();
                sb.append("/*ignoreNulls*/").append(ignoreNulls).append(",");
                Expr filter = call.getFilter();
                sb.append("/*filter*/");
                filter.accept(this);
                sb.append(",");
                sb.append("/*orderKeys*/");
                List<OrderItem> orderKeys = call.getOrderKeys();
                orderKeys(orderKeys);
                if (i != lastIndex) {
                    sb.append(",");
                }
            }

            sb.append(")");
        }
        sb.append(")");
    }

    private void orderKeys(List<OrderItem> orderKeys) {
        int size = orderKeys.size();
        int lastIndex = orderKeys.size() - 1;
        for (int i = 0; i < size; i++) {
            sb.append("order(");
            OrderItem orderItem = orderKeys.get(i);
            Identifier columnName = orderItem.getColumnName();
            sb.append(columnName.getValue());
            Direction direction = orderItem.getDirection();
            sb.append(",");
            sb.append(direction.name());
            sb.append(")");
            if (i != lastIndex) {
                sb.append(",");
            }
        }
    }

    private void groupKey(List<GroupItem> keys) {
        int size = keys.size();
        int lastIndex = keys.size() - 1;
        for (int i = 0; i < size; i++) {
            GroupItem key = keys.get(i);
            Op op = key.getOp();
            if (op == Op.REGULAR) {
                sb.append("regular(");
                joinNode(key.getExprs());
                sb.append(")");
            } else {
                throw new UnsupportedOperationException();
            }
            if (i != lastIndex) {
                sb.append(",");
            }
        }
    }

    @Override
    public void visit(LimitSchema limitSchema) {

    }

    @Override
    public void visit(FromSchema fromSchema) {
        sb.append("from(");
        sb.append(fromSchema.getNames().stream().map(i -> i.getValue()).collect(Collectors.joining(",")));
        sb.append(")");
    }

    @Override
    public void visit(SetOpSchema setOpSchema) {
        Op op = setOpSchema.getOp();
        sb.append(op.getFun()).append("(");
        joinNode(setOpSchema.getSchemas());
        sb.append(")");
    }

    @Override
    public void visit(FieldType fieldSchema) {
        String id = fieldSchema.getId();
        String type = fieldSchema.getType();
        sb.append(MessageFormat.format("fieldType({0},{1})", id, type));
    }

    @Override
    public void visit(Literal literal) {
        Object value = literal.getValue();
        String target;
        if (value instanceof String) {
            target = "literal('" + value + "')";
        } else if (value instanceof byte[]) {
            byte[] value1 = (byte[]) value;
            ByteString byteString = new ByteString(value1);
            target = "literal(X'" + byteString.toString() + "')";
        } else if (value instanceof Number) {
            target = "literal(" + value + ")";
        } else if (value instanceof LocalDate) {
            target = "dateLiteral(" + (value) + ")";
        } else if (value instanceof LocalDateTime) {
            target = "timestampLiteral(" + (value) + ")";
        } else if (value instanceof LocalTime) {
            target = "timeLiteral(" + (value) + ")";
        } else {
            target = "literal(" + value + ")";
        }
        sb.append(target);
    }

    @Override
    public void visit(OrderSchema orderSchema) {

    }

    @Override
    public void visit(Identifier identifier) {
        String value = identifier.getValue();
        sb.append("id(").append(value).append(")");
    }

    @Override
    public void visit(Expr expr) {
        if (expr instanceof Fun) {
            Fun fun = (Fun) expr;
            sb.append(fun.getFunctionName());
        }
        sb.append("(");
        joinNode(expr.getNodes());
        sb.append(")");
    }


    @Override
    public void visit(ValuesSchema valuesSchema) {
        sb.append("valuesSchema(");
        sb.append("fields(");
        joinNode(valuesSchema.getFieldNames());
        sb.append(")");
        sb.append(",");
        sb.append("values(");
        joinNode(valuesSchema.getValues());
        sb.append(")");
        sb.append(")");
    }

    private void joinNode(List fieldNames) {
        if (fieldNames.isEmpty()) {
            return;
        }
        int size = fieldNames.size();
        for (int i = 0; i < size - 1; i++) {
            Node o = (Node) fieldNames.get(i);
            o.accept(this);
            sb.append(",");
        }
        Node o = (Node) fieldNames.get(size - 1);
        o.accept(this);
    }

    @Override
    public void visit(JoinSchema corJoinSchema) {

    }

    @Override
    public void visit(AsTable asTable) {

    }

    @Override
    public void visit(AggregateCall aggregateCall) {

    }

    @Override
    public void visit(AsSchema asSchema) {

    }

    @Override
    public void visit(FilterSchema filterSchema) {

    }

    @Override
    public void visit(ModifyTable modifyTable) {

    }

    @Override
    public void visit(DistinctSchema distinctSchema) {

    }

    @Override
    public void visit(ProjectSchema projectSchema) {
        List<String> columnNames = projectSchema.getColumnNames();
        sb.append("project(");
        projectSchema.getSchema().accept(this);
        sb.append(",");
        sb.append(columnNames.stream().map(i -> "'" + i + "'").collect(Collectors.joining(",")));
        sb.append(")");
    }

    @Override
    public void visit(CorrelateSchema correlate) {

    }

    public String getSb() {
        return sb.toString();
    }
}