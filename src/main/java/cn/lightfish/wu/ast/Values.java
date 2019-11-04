package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Values extends Node {
    private final List<Node> values;

    public Values(List<Node> values) {
        super(Op.VALUES);
        this.values = values;
    }

    public Values(Node... values) {
        this(Arrays.asList(values));
    }
}