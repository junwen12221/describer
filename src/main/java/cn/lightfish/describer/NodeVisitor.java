package cn.lightfish.describer;

import cn.lightfish.describer.literal.DecimalLiteral;
import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.describer.literal.IntegerLiteral;
import cn.lightfish.describer.literal.StringLiteral;

public interface NodeVisitor {

    void visit(Bind bind);
    void endVisit(Bind bind);

    void visit(CallExpr call);

    void endVisit(CallExpr call);

    void visit(IdLiteral id);

    void endVisit(IdLiteral id);

    void visit(ParenthesesExpr parenthesesExpr);

    void endVisit(ParenthesesExpr parenthesesExpr);

    void visit(IntegerLiteral numberLiteral);

    void endVisit(IntegerLiteral numberLiteral);

    void visit(StringLiteral stringLiteral);

    void endVisit(StringLiteral stringLiteral);

    void visit(DecimalLiteral decimalLiteral);

    void endVisit(DecimalLiteral decimalLiteral);
}