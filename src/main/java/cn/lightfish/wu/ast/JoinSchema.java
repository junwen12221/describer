package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JoinSchema extends Schema {
    private final List<Schema> schemas;
    private final Node condition;

    public JoinSchema(Op op, List<Schema> schemas, Node condition) {
        super(op);
        this.schemas = schemas;
        this.condition = condition;
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