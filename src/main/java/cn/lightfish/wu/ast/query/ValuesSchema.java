package cn.lightfish.wu.ast.query;

import cn.lightfish.wu.Op;
import cn.lightfish.wu.ast.base.NodeVisitor;
import cn.lightfish.wu.ast.base.Schema;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class ValuesSchema extends Schema {
    private final List<Object> values;
    private final List<FieldSchema> fieldNames;

    public ValuesSchema(List<FieldSchema> fieldNames, List<Object> values) {
        super(Op.VALUES);
        this.fieldNames = fieldNames;
        this.values = values;
    }

    public ValuesSchema(List<FieldSchema> fieldNames, Object... values) {
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