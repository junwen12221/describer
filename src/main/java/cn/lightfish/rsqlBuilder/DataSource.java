package cn.lightfish.rsqlBuilder;

import cn.lightfish.rsqlBuilder.schema.ColumnObject;

public interface DataSource {
    ColumnObject getColumn(String columnName);
}