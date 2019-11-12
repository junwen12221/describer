package cn.lightfish.wu;

import cn.lightfish.wu.ast.base.Schema;
import cn.lightfish.wu.ast.query.FieldSchema;
import cn.lightfish.wu.ast.query.ValuesSchema;

import java.util.Collections;
import java.util.List;

public class ValueSchema extends Schema {
    private final ValuesSchema values;
    private final List<FieldSchema> fieldSchemas;

    public ValueSchema(ValuesSchema values, List<FieldSchema> fieldSchemas) {
        super(Op.SCHEMA);
        this.values = values;
        this.fieldSchemas = fieldSchemas;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(fieldSchemas);
    }
}