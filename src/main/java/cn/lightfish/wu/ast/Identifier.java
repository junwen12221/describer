package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Objects;

@Data
public class Identifier extends Node {
    final String value;

    public Identifier(String value) {
        super(Op.IDENTIFIER);
        this.value = value;
    }

    public boolean isStar() {
        return "*".equalsIgnoreCase(Objects.toString(value));
    }
}


