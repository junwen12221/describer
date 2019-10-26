package cn.lightfish.rsqlBuilder;

public interface DataSource {
    ColumnObject getColumn(String columnName);
}