package cn.lightfish.rsqlBuilder;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectName implements DataSource {
    private final Map<Integer, String> out;
    private final DataSource pop;

    public ProjectName(Map<Integer, String> out, DataSource pop) {
        this.out = out;
        this.pop = pop;
    }

    @Override
    public ColumnObject getColumn(String columnName) {
        for (Map.Entry<Integer, String> integerStringEntry : out.entrySet()) {
            if (integerStringEntry.getValue().equalsIgnoreCase(columnName)) {
                String value = integerStringEntry.getValue();
                ColumnObject column = pop.getColumn(value);
                return new ColumnObject(column.getSchema(), column.getTableName(), columnName);
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        String collect = out.entrySet().stream().sorted(Comparator.comparing(x -> x.getKey())).map(i -> i.getKey() + ":" + i.getValue()).collect(Collectors.joining(","));
        return "ProjectName{" + collect + ";" + pop + "}";
    }
}