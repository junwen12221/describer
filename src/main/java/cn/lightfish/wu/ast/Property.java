package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.List;

@Data
public class Property extends Node {
    final List<String> value;

    public Property(List<String> value) {
        super(Op.PROPERTY);
        this.value = value;
    }

}