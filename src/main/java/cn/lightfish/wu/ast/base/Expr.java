package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Expr extends Node {
    List<Node> nodes;

    public Expr(Op op, List<Node> nodes) {
        super(op);
        this.nodes = nodes;
    }

    public Expr(Op op, Node... nodes) {
        this(op, Arrays.asList(nodes));
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return op + "(" +
                nodes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) +
                ')';
    }
}