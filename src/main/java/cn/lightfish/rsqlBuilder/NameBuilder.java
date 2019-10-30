package cn.lightfish.rsqlBuilder;

import cn.lightfish.describer.CallExpr;
import cn.lightfish.describer.Node;
import cn.lightfish.describer.ParenthesesExpr;
import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.describer.literal.PropertyLiteral;
import cn.lightfish.rsqlBuilder.schema.SchemaMatcher;

import java.util.*;

public class NameBuilder extends CopyNodeVisitor {

    private final SchemaMatcher schemaMatcher;
    private final Map<String, Node> variables;

    public NameBuilder() {
        this(null, Collections.emptyMap());
    }

    public NameBuilder(SchemaMatcher schemaMatcher) {
        this(schemaMatcher, Collections.emptyMap());
    }

    public NameBuilder(SchemaMatcher schemaMatcher, Map<String, Node> variables) {
        this.schemaMatcher = schemaMatcher;
        this.variables = variables;
    }

    @Override
    public void endVisit(CallExpr call) {
        List<Node> exprs = call.getArgs().getExprs();
        switch (call.getName().toUpperCase()) {
            case "DOT": {
                dot(exprs);
                return;
            }
            default: {
                int size = exprs.size();
                ArrayDeque list = new ArrayDeque(size);
                for (int i = 0; i < size; i++) {
                    list.push(stack.pop());
                }
                stack.push(new CallExpr(call.getName(), new ParenthesesExpr(new ArrayList<>(list))));
            }
        }
    }

    private void dot(List<Node> exprs) {
        if (exprs.size() == 2) {
            Node second = stack.pop();
            Node pop = stack.pop();


            if (pop instanceof DotAble) {
                DotAble first = (DotAble) pop;
                stack.push(first.dot(((IdLiteral) second).getId()));
                return;
            }

            if (pop instanceof IdLiteral) {
                Node var = getVariables(((IdLiteral) pop).getId());
                if (var instanceof DotAble) {
                    IdLiteral second1 = (IdLiteral) second;
                    stack.push(((DotAble) var).dot(second1.getId()));
                    return;
                }
            }
            if (second instanceof IdLiteral && pop instanceof IdLiteral) {
                stack.push(new PropertyLiteral(Arrays.asList(((IdLiteral) pop).getId(), ((IdLiteral) second).getId())));
                return;
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void endVisit(IdLiteral id) {
        stack.push(resloveName(id));
    }

    public <T extends Node> T getVariables(String pop) {
        return (T) variables.get(pop);
    }

    public Node resloveName(IdLiteral name) {
        if (schemaMatcher != null) {
            Node schemaObject = schemaMatcher.getSchemaObject(name);
            if (schemaObject != null) {
                return schemaObject;
            }
        }
        Node o = variables.get(name.getId());
        if (o != null) {
            return o;
        } else {
            return name;
        }
    }
}