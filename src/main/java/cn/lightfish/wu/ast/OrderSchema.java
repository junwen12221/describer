package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;
import org.apache.calcite.util.Pair;

import java.util.Collections;
import java.util.List;

@Data
public class OrderSchema extends Schema {
    private final Schema schema;
    private final List<org.apache.calcite.util.Pair<Identifier, Direction>> orders;

    public OrderSchema(Schema schema, List<Pair<Identifier, Direction>> fields) {
        super(Op.ORDER);
        this.schema = schema;
        this.orders = fields;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public Schema getSchema() {
        return schema;
    }
}
