package cn.lightfish.describer;

import cn.lightfish.describer.leaf.DecimalLiteral;
import cn.lightfish.describer.leaf.Id;
import cn.lightfish.describer.leaf.IntegerLiteral;
import cn.lightfish.describer.leaf.StringLiteral;

public interface NodeVisitor {

    void visit(Bind bind);
    void endVisit(Bind bind);

    void visit(CallExpr call);

    void endVisit(CallExpr call);

    void visit(Id id);

    void endVisit(Id id);

    void visit(ParenthesesExpr parenthesesExpr);

    void endVisit(ParenthesesExpr parenthesesExpr);

    void visit(IntegerLiteral numberLiteral);

    void endVisit(IntegerLiteral numberLiteral);

    void visit(StringLiteral stringLiteral);

    void endVisit(StringLiteral stringLiteral);

    void visit(DecimalLiteral decimalLiteral);

    void endVisit(DecimalLiteral decimalLiteral);
}