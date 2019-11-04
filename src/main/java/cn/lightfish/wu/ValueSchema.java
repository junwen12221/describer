package cn.lightfish.wu;

import cn.lightfish.wu.ast.FieldSchema;
import cn.lightfish.wu.ast.Schema;
import cn.lightfish.wu.ast.Values;

import java.util.Collections;
import java.util.List;

public class ValueSchema extends Schema {
    private final Values values;
    private final List<FieldSchema> fieldSchemas;

    public ValueSchema(Values values, List<FieldSchema> fieldSchemas) {
        super(Op.SCHEMA);
        this.values = values;
        this.fieldSchemas = fieldSchemas;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(fieldSchemas);
    }
}