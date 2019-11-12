package cn.lightfish.wu.ast.modify;

import cn.lightfish.describer.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Assign {
    String identifier;
    Node expr;
}