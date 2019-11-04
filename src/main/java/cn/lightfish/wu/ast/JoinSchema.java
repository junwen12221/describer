package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class JoinSchema extends Schema {
    private final Schema schema;
    private final JoinType type;
    private final Node condition;

    public JoinSchema(Schema schema, JoinType type, Node condition) {
        super(Op.JOIN);
        this.schema = schema;
        this.type = type;
        this.condition = condition;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public Schema getSchema() {
        return schema;
    }

    public enum JoinType {
        INNER,
        LEFT,
        RIGHT,
        FULL,
        SEMI,
        ANTI
    }
}
