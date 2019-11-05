package cn.lightfish.describer;

import cn.lightfish.describer.literal.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

public class ExprTreeVisitor implements NodeVisitor {
    Map<EvalNodeVisitor.FunctionSig, Builder> map = new HashMap<>();

    ArrayDeque<Node> stack = new ArrayDeque<>();

    public ExprTreeVisitor() {
        map.put(new EvalNodeVisitor.FunctionSig("+", "(int,int)"), (exprs) -> {
            IntegerLiteral one = cast(exprs.get(0));
            IntegerLiteral two = cast(exprs.get(1));
            return new IntegerLiteral(one.getNumber().add(two.getNumber()));
        });
        map.put(new EvalNodeVisitor.FunctionSig(".", "(int,int)"), (exprs) -> {
            exprs.get(0).accept(this);
            Node ret = stack.pop();
            CallExpr callExpr = (CallExpr) exprs.get(1);
            callExpr.getArgs().getExprs().add(0, ret);
            return callExpr;
        });
    }

    private <T> T cast(Node node) {
        return (T) (node);
    }

    @Override
    public void visit(Bind bind) {
        bind.expr.accept(this);
    }

    @Override
    public void endVisit(Bind bind) {
        bind.expr = stack.pop();
    }

    @Override
    public void visit(CallExpr call) {
        call.getArgs().accept(this);
    }

    @Override
    public void endVisit(CallExpr call) {
        String name = call.getName();
        String type = getType();
        EvalNodeVisitor.FunctionSig functionSig = new EvalNodeVisitor.FunctionSig(name, type);
        Builder function = map.get(functionSig);
        ParenthesesExpr pop = (ParenthesesExpr) stack.pop();
        stack.push(function.eval(pop.getExprs()));
    }

    public String getType() {
        Node peek = stack.peek();
        return getType(peek);
    }

    private String getType(Node peek) {
        if (peek == null) {
            return "()";
        } else if (peek instanceof ParenthesesExpr) {
            ParenthesesExpr peek1 = (ParenthesesExpr) peek;
            StringBuilder sb = new StringBuilder("(");
            List<Node> exprs = peek1.getExprs();
            for (int i = 0; i < exprs.size(); i++) {
                Node expr = exprs.get(i);
                if (expr instanceof StringLiteral) {
                    sb.append("str");
                } else if (expr instanceof IntegerLiteral) {
                    sb.append("int");
                } else if (expr instanceof DecimalLiteral) {
                    sb.append("double");
                } else {
                    sb.append(expr.getClass().getSimpleName());
                }
                if (i != exprs.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        } else if (peek instanceof IdLiteral) {
            return "ID";
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void visit(IdLiteral id) {

    }

    @Override
    public void endVisit(IdLiteral id) {
        stack.push(id);
    }

    @Override
    public void visit(ParenthesesExpr parenthesesExpr) {


    }

    @Override
    public void endVisit(ParenthesesExpr parenthesesExpr) {
        List<Node> exprs = parenthesesExpr.getExprs();
        for (Node expr : exprs) {
            expr.accept(this);
        }
        ArrayList<Node> list = new ArrayList<>(exprs.size());
        for (int i = 0; i < exprs.size(); i++) {
            list.add(stack.pop());
        }
        stack.push(new ParenthesesExpr(list));
    }

    @Override
    public void visit(IntegerLiteral numberLiteral) {

    }

    @Override
    public void endVisit(IntegerLiteral numberLiteral) {
        stack.push(numberLiteral);
    }

    @Override
    public void visit(StringLiteral stringLiteral) {

    }

    @Override
    public void endVisit(StringLiteral stringLiteral) {
        stack.push(stringLiteral);
    }

    @Override
    public void visit(DecimalLiteral decimalLiteral) {

    }

    @Override
    public void endVisit(DecimalLiteral decimalLiteral) {
        stack.push(decimalLiteral);
    }

    @Override
    public void visit(PropertyLiteral propertyLiteral) {

    }

    @Override
    public void endVisit(PropertyLiteral propertyLiteral) {
        stack.push(propertyLiteral.copy());
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class FunctionSig {
        String name;
        String type;
    }
}