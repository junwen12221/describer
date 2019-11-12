package cn.lightfish.wu.ast.modify;

import cn.lightfish.describer.Node;

import java.util.List;

public class MergeStatement {
    List<Node> sources;
    Node booleanExpression;
    List<Assign> assigns;
    List<cn.lightfish.wu.ast.base.Node> values;
}