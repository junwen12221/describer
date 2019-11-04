package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;

import java.util.Collections;
import java.util.List;

public class AsTable extends Schema {
    private final Schema schema;
    private final String alias;

    public AsTable(Schema schema, String alias) {
        super(Op.AS_TABLE);
        this.schema = schema;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }
}

