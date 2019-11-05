package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;

import java.util.Collections;
import java.util.List;

public class DistinctSchema extends Schema {
    private final Schema schema;

    public DistinctSchema(Schema schema) {
        super(Op.DISTINCT);
        this.schema = schema;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public Schema getSchema() {
        return schema;
    }
}

   
