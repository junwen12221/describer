package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;

import java.util.Collections;
import java.util.List;

public class MapSchema extends Schema {
    private final Schema schema;
    private final List<Node> expr;

    public MapSchema(Schema schema, List<Node> expr) {
        super(Op.MAP);
        this.schema = schema;
        this.expr = expr;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public List<Node> getExpr() {
        return expr;
    }

    public Schema getSchema() {
        return schema;
    }
}