package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Node;
import cn.lightfish.wu.ast.base.Schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FilterSchema extends Schema {
    private final Schema schema;
    private final List<Node> exprs;

    public FilterSchema(Schema schema, Node... exprs) {
        super(Op.FILTER);
        this.schema = schema;
        this.exprs = Arrays.asList(exprs);
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public Schema getSchema() {
        return schema;
    }

    public List<Node> getExpr() {
        return exprs;
    }
}
