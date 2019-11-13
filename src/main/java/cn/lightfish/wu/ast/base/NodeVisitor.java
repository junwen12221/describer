package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.as.AsSchema;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.modify.ModifyTable;
import cn.lightfish.wu.ast.query.*;

public interface NodeVisitor {
    void visit(MapSchema mapSchema);

    void visit(JoinSchema joinSchema);

    void visit(GroupSchema groupSchema);

    void visit(LimitSchema limitSchema);

    void visit(FromSchema fromSchema);

    void visit(SetOpSchema setOpSchema);

    void visit(FieldSchema fieldSchema);

    void visit(Literal literal);

    void visit(OrderSchema orderSchema);

    void visit(Identifier identifier);

    void visit(Expr expr);

    void visit(Property property);

    void visit(ValuesSchema valuesSchema);

    void visit(CorJoinSchema corJoinSchema);

    void visit(AsTable asTable);

    void visit(AggregateCall aggregateCall);

    void visit(AsSchema asSchema);

    void visit(FilterSchema filterSchema);

    void visit(ModifyTable modifyTable);

    void visit(DistinctSchema distinctSchema);

    void visit(ProjectSchema projectSchema);
}