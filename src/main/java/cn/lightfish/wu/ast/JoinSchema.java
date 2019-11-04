package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JoinSchema extends Schema {
    private final List<Schema> schemas;
    private final Node condition;
    private final boolean correlation;

    public JoinSchema(Op op, List<Schema> schemas, Node condition, boolean correlation) {
        super(op);
        this.schemas = schemas;
        this.condition = condition;
        this.correlation = correlation;
    }

    @Override
    public List<FieldSchema> fields() {
        ArrayList<FieldSchema> list = new ArrayList<>();
        for (Schema schema : schemas) {
            list.addAll(schema.fields());
        }
        return list;
    }

    public List<Schema> getSchemas() {
        return schemas;
    }
}
