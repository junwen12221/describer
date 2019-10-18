package cn.lightfish.describer;

public interface NodeVisitor {

    void visit(Bind bind);
    void endVisit(Bind bind);

    void visit(CallExpr call);
    void endVisit(CallExpr bind);

    void visit(Id id);
    void endVisit(Id bind);

    void visit(ParenthesesExpr parenthesesExpr);
    void endVisit(ParenthesesExpr bind);

    void visit(NumberLiteral numberLiteral);
    void endVisit(NumberLiteral bind);

    void visit(StringLiteral stringLiteral);
    void endVisit(StringLiteral bind);
}