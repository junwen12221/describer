package cn.lightfish.wu.ast.base;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Expr extends Node {
    List<Node> nodes;

    public Expr(Op op, Node... nodes) {
        super(op);
        this.nodes = Arrays.asList(nodes);
    }
}