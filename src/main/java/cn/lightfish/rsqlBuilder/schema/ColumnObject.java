package cn.lightfish.rsqlBuilder.schema;

import cn.lightfish.describer.Node;
import cn.lightfish.describer.NodeVisitor;

public class ColumnObject implements Node {
    private final String schema;
    private final String tableName;
    private final String columnName;

    public ColumnObject(String schema, String tableName, String columnName) {
        this.schema = schema;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "ColumnObject{" + schema +
                "." + tableName + '.' + columnName +
                '}';
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public void accept(NodeVisitor visitor) {

    }

    @Override
    public ColumnObject copy() {
        return this;
    }
}