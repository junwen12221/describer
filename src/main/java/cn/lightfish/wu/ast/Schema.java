package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.List;

@Data
public abstract class Schema extends Node {
    public Schema(Op op) {
        super(op);
    }

    public abstract List<FieldSchema> fields();

}
