package cn.lightfish.rsqlBuilder;


import cn.lightfish.describer.Node;
import cn.lightfish.describer.NodeVisitor;

import java.util.Map;

public class SchemaObject implements DotAble, Node {
    private final String schema;
    private final Map<String, Map<String, Node>> tables;

    public SchemaObject(String string, Map<String, Map<String, Node>> stringMapMap) {
        this.schema = string;
        this.tables = stringMapMap;
    }


    public TableObject dotAttribute(String o) {
        o = o.toLowerCase();
        Map<String, Node> map = tables.get(o);
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
    public void accept(NodeVisitor visitor) {

    }

    @Override
    public SchemaObject copy() {
        return this;
    }
}