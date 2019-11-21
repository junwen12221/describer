package cn.lightfish.wu.ast.as;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import cn.lightfish.wu.ast.query.FieldType;

import java.util.Collections;
import java.util.List;

public class AsSchema extends Schema {
    private final Schema schema;
    private final List<FieldType> fields;

    public AsSchema(Schema schema, List<FieldType> fields) {
        super(Op.AS_TABLE);
        this.schema = schema;
        this.fields = fields;
    }

    public Schema getSchema() {
        return schema;
    }


    @Override
    public List<FieldType> fields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}

