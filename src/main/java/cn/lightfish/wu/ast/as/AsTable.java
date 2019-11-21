package cn.lightfish.wu.ast.as;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import cn.lightfish.wu.ast.query.FieldType;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class AsTable extends Schema {
    private final Schema schema;
    private final String alias;

    public AsTable(Schema schema, String alias) {
        super(Op.AS_TABLE);
        this.schema = schema;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public List<FieldType> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}

