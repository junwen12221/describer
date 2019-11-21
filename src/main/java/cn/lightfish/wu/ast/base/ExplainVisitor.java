package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.as.AsSchema;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.modify.ModifyTable;
import cn.lightfish.wu.ast.query.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class ExplainVisitor implements NodeVisitor {
    final StringBuilder sb = new StringBuilder();

    @Override
    public void visit(MapSchema mapSchema) {
        Schema schema = mapSchema.getSchema();
        List<Node> expr = mapSchema.getExpr();
    }


    @Override
    public void visit(GroupSchema groupSchema) {

    }

    @Override
    public void visit(LimitSchema limitSchema) {

    }

    @Override
    public void visit(FromSchema fromSchema) {

    }

    @Override
    public void visit(SetOpSchema setOpSchema) {

    }

    @Override
    public void visit(FieldType fieldSchema) {
        String id = fieldSchema.getId();
        String type = fieldSchema.getType();
        sb.append(MessageFormat.format("fieldType({0},{1})", id, type));
    }

    @Override
    public void visit(Literal literal) {
        sb.append("literal(");
        Object value = literal.getValue();
        String target;
        if (value instanceof String) {
            target = "'" + value + "'";
        } else {
            target = Objects.toString(value);
        }
        sb.append(target);
        sb.append(")");
    }

    @Override
    public void visit(OrderSchema orderSchema) {

    }

    @Override
    public void visit(Identifier identifier) {

    }

    @Override
    public void visit(Expr expr) {

    }


    @Override
    public void visit(ValuesSchema valuesSchema) {
        sb.append("valuesSchema(");
        sb.append("fields(");
        joinNode((List) valuesSchema.getFieldNames());
        sb.append(")");
        sb.append(",");
        sb.append("values(");
        joinNode((List) valuesSchema.getValues());
        sb.append(")");
        sb.append(")");
    }

    private void joinNode(List<Node> fieldNames) {
        if (fieldNames.isEmpty()) {
            return;
        }
        int size = fieldNames.size();
        for (int i = 0; i < size - 1; i++) {
            fieldNames.get(i).accept(this);
            sb.append(",");
        }
        fieldNames.get(size - 1).accept(this);
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

    }

    @Override
    public void visit(CorrelateSchema correlate) {

    }

    public String getSb() {
        return sb.toString();
    }
}