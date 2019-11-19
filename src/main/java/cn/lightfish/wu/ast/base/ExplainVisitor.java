package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.as.AsSchema;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.modify.ModifyTable;
import cn.lightfish.wu.ast.query.*;

import java.util.List;

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
    public void visit(FieldSchema fieldSchema) {
        String id = fieldSchema.getId();
        String type = fieldSchema.getType();

    }

    @Override
    public void visit(Literal literal) {

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
    public void visit(Property property) {

    }

    @Override
    public void visit(ValuesSchema valuesSchema) {

        List<FieldSchema> fieldNames = valuesSchema.getFieldNames();
        fieldNames.forEach(fieldName -> fieldName.accept(this));


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

}