package cn.lightfish.describer;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;

@Getter
public class Bind implements Node {
    String name;
    Node expr;

    public Bind(String name, Node expr) {
        this.name = name;
        this.expr = expr;
    }


    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public Bind copy() {
        return new Bind(name, expr.copy());
    }

    @Override
    public String toString() {
        return MessageFormat.format( "let {0} = {1};",Objects.toString(name), Objects.toString(expr));
    }
}