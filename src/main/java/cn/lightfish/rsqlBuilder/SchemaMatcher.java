package cn.lightfish.rsqlBuilder;

import cn.lightfish.describer.Node;
import cn.lightfish.describer.literal.IdLiteral;

import java.util.HashMap;
import java.util.Map;

public class SchemaMatcher {

    final Map<String, Map<String, Map<String, Node>>> map = new HashMap<>();

    public void addSchema(String schema, String table, String column) {
        Map<String, Map<String, Node>> stringHashMapMap1 = map.computeIfAbsent(schema.toLowerCase(), s -> new HashMap<>());
        if (table != null) {
            Map<String, Node> map = stringHashMapMap1.computeIfAbsent(table.toLowerCase(), (s) -> new HashMap<>());
            if (column != null) {
                map.put(column.toLowerCase(), null);
            }
        }

    }


    public Node getSchemaObject(IdLiteral string) {
        String s = string.getId().toLowerCase();
        Map<String, Map<String, Node>> stringMapMap = map.get(s);
        if (stringMapMap != null) {
            return new SchemaObject(s, stringMapMap);
        }
        return string.copy();
    }
}