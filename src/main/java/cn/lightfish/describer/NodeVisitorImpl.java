package cn.lightfish.describer;

import cn.lightfish.describer.literal.*;

import java.util.List;

public class NodeVisitorImpl implements NodeVisitor {
    final StringBuilder sb = new StringBuilder();

    @Override
    public void visit(Bind bind) {

    }

    @Override
    public void endVisit(Bind bind) {

    }

    @Override
    public void visit(CallExpr call) {
        sb.append(call.getName().toLowerCase());
        call.getArgs().accept(this);
    }

    @Override
    public void endVisit(CallExpr call) {

    }

    @Override
    public void visit(IdLiteral id) {
        //sb.append("new Identifier(\"").append(id.getId()).append("\")");
        sb.append("\"").append(id.getId()).append("\"");
    }

    @Override
    public void endVisit(IdLiteral id) {

    }

    @Override
    public void visit(ParenthesesExpr parenthesesExpr) {
        sb.append("(");
        List<Node> exprs = parenthesesExpr.getExprs();
        for (Node expr : exprs.subList(0, exprs.size() - 1)) {
            expr.accept(this);
            sb.append(",");
        }
        exprs.get(exprs.size() - 1).accept(this);
        sb.append(")");
    }

    @Override
    public void endVisit(ParenthesesExpr parenthesesExpr) {

    }

    @Override
    public void visit(IntegerLiteral numberLiteral) {
//        sb.append("new Literal(BigInteger.valueOf(").append(numberLiteral.getNumber()).append("))");
        sb.append("new Literal(").append(numberLiteral.getNumber()).append(")");
    }

    @Override
    public void endVisit(IntegerLiteral numberLiteral) {

    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        sb.append("new Literal(\"").append(stringLiteral.getString()).append("\")");
    }

    @Override
    public void endVisit(StringLiteral stringLiteral) {

    }

    @Override
    public void visit(DecimalLiteral decimalLiteral) {
        sb.append("new Literal(").append(decimalLiteral.getNumber().toString()).append(")");
    }

    @Override
    public void endVisit(DecimalLiteral decimalLiteral) {

    }

    @Override
    public void visit(PropertyLiteral propertyLiteral) {
        sb.append("new Property(Arrays.asList(");
        sb.append(String.join(",", "\"" + propertyLiteral.getValue() + "\""));
        sb.append("))");
    }

    @Override
    public void endVisit(PropertyLiteral propertyLiteral) {

    }


    public String getSb() {
        return sb.toString();
    }
}