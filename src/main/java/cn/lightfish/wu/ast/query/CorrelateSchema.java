package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CorrelateSchema extends Schema {
    private Schema from;

    public CorrelateSchema(Schema from) {
        super(Op.CORRELATE);
        this.from = from;
    }

    @Override
    public List<FieldSchema> fields() {
        return from.fields();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getAlias() {
        return from.getAlias();
    }
}