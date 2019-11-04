package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;

import java.util.Collections;
import java.util.List;

public class AsSchema extends Schema {
    private final Schema schema;
    private final List<FieldSchema> fields;

    public AsSchema(Schema schema, List<FieldSchema> fields) {
        super(Op.AS_TABLE);
        this.schema = schema;
        this.fields = fields;
    }

    public Schema getSchema() {
        return schema;
    }


    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(fields);
    }
}

