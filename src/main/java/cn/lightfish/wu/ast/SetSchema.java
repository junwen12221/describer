package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class SetSchema extends Schema {
    final List<Schema> schemas;

    public SetSchema(Op op, Schema... schemas) {
        super(op);
        this.schemas = Arrays.asList(schemas);
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schemas.get(0).fields());
    }
}