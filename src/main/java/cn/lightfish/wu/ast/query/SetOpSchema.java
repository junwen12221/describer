package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class SetOpSchema extends Schema {
    final List<Schema> schemas;

    public SetOpSchema(Op op, Schema... schemas) {
        super(op);
        this.schemas = Arrays.asList(schemas);
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schemas.get(0).fields());
    }
}