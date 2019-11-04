package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

@Data
public class Literal extends Node {
    final Object value;

    public Literal(Object value) {
        super(Op.LITERAL);
        this.value = value;
    }
}
