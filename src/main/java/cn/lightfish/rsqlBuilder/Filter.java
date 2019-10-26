package cn.lightfish.rsqlBuilder;

public class Filter implements MemberFunction, DataSource {
    private final Object condition;
    private final DataSource dataSource;

    public Filter(Object condition, DataSource dataSource) {
        this.condition = condition;
        this.dataSource = dataSource;
    }

    @Override
    public ColumnObject getColumn(String columnName) {
        return dataSource.getColumn(columnName);
    }
}