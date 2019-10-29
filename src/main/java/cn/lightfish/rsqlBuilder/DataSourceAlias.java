//package cn.lightfish.rsqlBuilder;
//
//public class DataSourceAlias implements DataSource, DotAble {
//    String alias;
//    DataSource dataSource;
//
//    public DataSourceAlias(String alias, DataSource dataSource) {
//        this.alias = alias;
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public String toString() {
//        return "DataSourceAlias{" +
//                dataSource + " as " + alias +
//                '}';
//    }
//
//
//    @Override
//    public String dot(MemberFunction o) {
//        return null;
//    }
//
//    @Override
//    public ColumnObject getColumn(String columnName) {
//        return dataSource.getColumn(columnName);
//    }
//
//    @Override
//    public <T> T dot(String o) {
//        return (T) getColumn(o);
//    }
//}