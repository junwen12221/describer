package cn.lightfish.rsqlBuilder;


import java.util.Map;

public class SchemaObject implements DotAble {
    private final String schema;
    private final Map<String, Map<String, Object>> tables;

    public SchemaObject(String string, Map<String, Map<String, Object>> stringMapMap) {
        this.schema = string;
        this.tables = stringMapMap;
    }


    public TableObject dotAttribute(String o) {
        o = o.toLowerCase();
        Map<String, Object> map = tables.get(o);
        return new TableObject(schema, o, map);
    }

    @Override
    public String toString() {
        return "SchemaObject{" + schema + "}";
    }

    @Override
    public <T> T dot(String o) {
        return (T) dotAttribute(o);
    }

    @Override
    public <T> T dot(MemberFunction o) {
        return dot(o);
    }
}