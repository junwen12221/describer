package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class GroupSchema extends Schema {
    private final Schema schema;
    private final List<Node> keys;
    private final List<AggregateCall> exprs;

    public GroupSchema(Schema schema, List<Node> keys, List<AggregateCall> exprs) {
        super(Op.GROUP);
        this.schema = schema;
        this.keys = keys;
        this.exprs = exprs;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public Schema getSchema() {
        return schema;
    }
}
