package cn.lightfish.describer;

import cn.lightfish.describer.leaf.DecimalLiteral;
import cn.lightfish.describer.leaf.Id;
import cn.lightfish.describer.leaf.IntegerLiteral;
import cn.lightfish.describer.leaf.StringLiteral;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

public class EvalNodeVisitor implements NodeVisitor {
    Map<FunctionSig, Builder> map = new HashMap<>();

    BuilderContext context = new BuilderContext();
    ArrayDeque<Node> stack = new ArrayDeque<>();

    public EvalNodeVisitor() {
        map.put(new FunctionSig("+", "(int,int)"), new Builder() {
            @Override
            public Node eval(BuilderContext builder, List<Node> exprs) {
                IntegerLiteral one = cast(exprs.get(0));
                IntegerLiteral two = cast(exprs.get(1));
                return new IntegerLiteral(one.getNumber().add(two.getNumber()));
            }
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
        FunctionSig functionSig = new FunctionSig(name, type);
        Builder function = map.get(functionSig);
        ParenthesesExpr pop = (ParenthesesExpr) stack.pop();
        stack.push(function.eval(context, pop.getExprs()));
    }

    public String getType() {
        Node peek = stack.peek();
        return getType(peek);
    }

    private String getType(Node peek) {
        if (peek == null) {
            return "()";
        } else {
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
                    sb.append(getType(expr));
                }
                if (i != exprs.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    @Override
    public void visit(Id id) {

    }

    @Override
    public void endVisit(Id id) {
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

    @EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class FunctionSig {
        String name;
        String type;
    }
}