package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CorJoinSchema extends Schema {
    private final List<AsTable> schemas;
    private final Node condition;

    public CorJoinSchema(Op op, List<AsTable> schemas, Node condition) {
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

    public List<AsTable> getSchemas() {
        return schemas;
    }
}