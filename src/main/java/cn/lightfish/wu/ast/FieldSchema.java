package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

@Data
public class FieldSchema extends Node {
    final String id;
    final String type;

    public FieldSchema(String id, String type) {
        super(Op.FIELD_SCHEMA);
        this.id = id;
        this.type = type;
    }
}
