package cn.lightfish.wu;

import java.util.Collections;
import java.util.List;

public class ValueSchema extends Ast.Schema {
    private final Ast.Values values;
    private final List<Ast.FieldSchema> fieldSchemas;

    public ValueSchema(Ast.Values values, List<Ast.FieldSchema> fieldSchemas) {
        super(Ast.Op.SCHEMA);
        this.values = values;
        this.fieldSchemas = fieldSchemas;
    }

    @Override
    public List<Ast.FieldSchema> fields() {
        return Collections.unmodifiableList(fieldSchemas);
    }
}