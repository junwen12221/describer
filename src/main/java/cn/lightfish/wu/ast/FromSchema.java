package cn.lightfish.wu.ast;

import cn.lightfish.wu.Op;
import lombok.Data;

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
        throw new UnsupportedOperationException();
    }
}

