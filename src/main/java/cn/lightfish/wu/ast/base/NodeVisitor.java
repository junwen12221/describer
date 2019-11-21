package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.as.AsSchema;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.modify.ModifyTable;
import cn.lightfish.wu.ast.query.*;

public interface NodeVisitor {
    void visit(MapSchema mapSchema);

    void visit(GroupSchema groupSchema);

    void visit(LimitSchema limitSchema);

    void visit(FromSchema fromSchema);

    void visit(SetOpSchema setOpSchema);

    void visit(FieldType fieldSchema);

    void visit(Literal literal);

    void visit(OrderSchema orderSchema);

    void visit(Identifier identifier);

    void visit(Expr expr);

    void visit(ValuesSchema valuesSchema);

    void visit(JoinSchema corJoinSchema);

    void visit(AsTable asTable);

    void visit(AggregateCall aggregateCall);

    void visit(AsSchema asSchema);

    void visit(FilterSchema filterSchema);

    void visit(ModifyTable modifyTable);

    void visit(DistinctSchema distinctSchema);

    void visit(ProjectSchema projectSchema);

    void visit(CorrelateSchema correlate);
}