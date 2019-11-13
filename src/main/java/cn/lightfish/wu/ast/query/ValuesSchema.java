package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.Node;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class ValuesSchema extends Schema {
    private final List<Node> values;
    private final List<FieldSchema> fieldNames;

    public ValuesSchema(List<FieldSchema> fieldNames, List<Node> values) {
        super(Op.VALUES);
        this.fieldNames = fieldNames;
        this.values = values;
    }

    public ValuesSchema(List<FieldSchema> fieldNames, Node... values) {
        this(fieldNames, Arrays.asList(values));
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(fieldNames);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}