package cn.lightfish.describer;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;
@Getter
public class CallExpr implements Node {
    private final String name;
    private final Node args;

    public CallExpr(String name, Node args) {
        this.name = name;
        this.args = args;
    }


    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
    @Override
    public String toString() {
        return MessageFormat.format( "{0}{1}",name,Objects.toString(args));
    }
}