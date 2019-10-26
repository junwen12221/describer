package cn.lightfish.rsqlBuilder;

import java.util.ArrayList;

public class Project implements DataSource {
    private final ArrayList<Object> out;
    private final DataSource dataSource;

    public Project(ArrayList<Object> out, DataSource dataSource) {
        this.out = out;
        this.dataSource = dataSource;
    }

    @Override
    public ColumnObject getColumn(String columnName) {
        return dataSource.getColumn(columnName);
    }
}