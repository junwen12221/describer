package cn.lightfish.rsqlBuilder;

import cn.lightfish.describer.*;
import cn.lightfish.describer.literal.DecimalLiteral;
import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.describer.literal.IntegerLiteral;
import cn.lightfish.describer.literal.StringLiteral;

import java.util.*;

public class RexBuilder implements NodeVisitor {

    private final Map<String, Object> variables;
    private final Deque<Object> stack = new ArrayDeque<>();
    private SchemaMatcher schemaMatcher;

    public RexBuilder() {
        this.variables = new HashMap<>();
    }

    public RexBuilder(SchemaMatcher schemaMatcher) {
        this(schemaMatcher, new HashMap<>());
    }

    public RexBuilder(SchemaMatcher schemaMatcher, Map<String, Object> variables) {
        this.schemaMatcher = schemaMatcher;
        this.variables = variables;
    }

    @Override
    public void visit(Bind bind) {
        bind.getExpr().accept(this);
    }

    @Override
    public void endVisit(Bind bind) {

    }

    @Override
    public void visit(CallExpr call) {
        String name = call.getName();
        List<Node> args = call.getArgs().getExprs();
        for (Node c : args) {
            c.accept(this);
        }
    }

    @Override
    public void endVisit(CallExpr call) {
        List<Node> exprs = call.getArgs().getExprs();
        switch (call.getName().toUpperCase()) {
            case "DOT": {
                dot(exprs);
                return;
            }
            case "AS": {
                as(exprs);
                return;
            }
            case "FILTER": {
                Object condition = stack.pop();
                DataSource t = (DataSource) stack.pop();
                stack.push(filter(t, condition));
                return;
            }
            case "MAP": {
                int size = exprs.size();
                ArrayList<Object> out = new ArrayList<>();
                for (int i = 0; i < size - 1; i++) {
                    out.add(i, stack.pop());
                }
                stack.push(map(out, (DataSource) stack.pop()));
                return;
            }
            case "FROM": {
                if (exprs.isEmpty()) {
                    stack.push(new From());
                    return;
                }
                return;
            }
            case "PROJECTINDEX": {
                int size = exprs.size();
                Map<Integer, String> map = new HashMap<>();
                for (int i = 0; i < size - 1; i++) {
                    AliasColumnIndex pop = (AliasColumnIndex) stack.pop();
                    map.put(pop.getIndex(), pop.getAlias());
                }
                stack.push(new ProjectName(map, (DataSource) stack.pop()));
                return;
            }
            default: {
                System.out.println();
            }
        }
    }

    private DataSource map(ArrayList<Object> out, DataSource dataSource) {
        return new Project(out, dataSource);
    }

    private DataSource filter(DataSource t, Object condition) {
        return new Filter(condition, t);
    }

    private void as(List<Node> exprs) {
        if (exprs.size() == 2) {
            String first = (String) stack.pop();
            Object second = stack.pop();

            String f = first;
            if (second instanceof DataSource) {
                DataSourceAlias dataSourceAlias = new DataSourceAlias(first, (DataSource) second);
                variables.put(first, dataSourceAlias);
                stack.push(dataSourceAlias);
                return;
            } else {
                stack.push(new AliasColumnIndex(((Number) second).intValue(), first));
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

    private void dot(List<Node> exprs) {
        if (exprs.size() == 2) {
            Object second = stack.pop();
            Object pop = stack.pop();


            if (pop instanceof DotAble) {
                DotAble first = (DotAble) pop;
                stack.push(first.dot(second));
                return;
            }

            if (pop instanceof String) {
                Object var = variables.get(pop);
                if (var != null && var instanceof DotAble) {
                    stack.push(((DotAble) var).dot(second));
                    return;
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(IdLiteral id) {

    }

    @Override
    public void endVisit(IdLiteral id) {
        if (schemaMatcher != null) {
            Object schemaObject = schemaMatcher.getSchemaObject(id.getId().toLowerCase());
            if (schemaObject != null) {
                stack.push(schemaObject);
                return;
            }
        }
        Object o = variables.get(id.getId());
        if (o != null) {
            stack.push(o);
            return;
        }
        stack.push(id.getId());
    }

    @Override
    public void visit(ParenthesesExpr parenthesesExpr) {
        List<Node> exprs = parenthesesExpr.getExprs();
        for (Node expr : exprs) {
            expr.accept(this);
        }

    }

    @Override
    public void endVisit(ParenthesesExpr parenthesesExpr) {

    }

    @Override
    public void visit(IntegerLiteral numberLiteral) {
        stack.push(numberLiteral.getNumber());
    }

    @Override
    public void endVisit(IntegerLiteral numberLiteral) {

    }

    @Override
    public void visit(StringLiteral stringLiteral) {

    }

    @Override
    public void endVisit(StringLiteral stringLiteral) {
        stack.push(stringLiteral.getString());
    }

    @Override
    public void visit(DecimalLiteral decimalLiteral) {
        stack.push(decimalLiteral.getNumber());
    }

    @Override
    public void endVisit(DecimalLiteral decimalLiteral) {

    }

    public Object getStack() {
        return stack.peek();
    }
}