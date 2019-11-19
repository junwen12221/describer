package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Node;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
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

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}