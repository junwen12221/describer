package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class FromSchema extends Schema {
    private String[] names;

    public FromSchema(String... names) {
        super(Op.FROM);
        this.names = names;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.emptyList();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
