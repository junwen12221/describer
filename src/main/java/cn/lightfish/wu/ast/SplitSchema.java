package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class SplitSchema extends Schema {
    final Schema schema;
    final List<Node> conditions;
    final List<String> aliasList;

    public SplitSchema(Schema schema, List<Node> conditions, List<String> aliasList) {
        super(Op.SPLIT);
        this.schema = schema;
        this.conditions = conditions;
        this.aliasList = aliasList;
    }

    @Override
    public List<FieldSchema> fields() {
        return Collections.unmodifiableList(schema.fields());
    }

    public List<String> getAliasList() {
        return aliasList;
    }
}