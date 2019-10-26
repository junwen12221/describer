package cn.lightfish.rsqlBuilder;

import java.util.HashMap;
import java.util.Map;

public class SchemaMatcher {

    final Map<String, Map<String, Map<String, Object>>> map = new HashMap<>();

    public void addSchema(String schema, String table, String column) {
        Map<String, Map<String, Object>> stringHashMapMap1 = map.computeIfAbsent(schema.toLowerCase(), s -> new HashMap<>());
        if (table != null) {
            Map<String, Object> map = stringHashMapMap1.computeIfAbsent(table.toLowerCase(), (s) -> new HashMap<>());
            if (column != null) {
                map.put(column.toLowerCase(), null);
            }
        }

    }


    public Object getSchemaObject(String string) {
        string = string.toLowerCase();
        Map<String, Map<String, Object>> stringMapMap = map.get(string.toLowerCase());
        if (stringMapMap != null) {
            return new SchemaObject(string, stringMapMap);
        }
        return string;
    }
}