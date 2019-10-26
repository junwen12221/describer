package cn.lightfish.rsqlBuilder;

public class AliasColumnIndex {
    private final Integer second;
    private final String first;

    public AliasColumnIndex(Integer second, String first) {
        this.second = second;
        this.first = first;
    }

    public Integer getIndex() {
        return second;
    }

    public String getAlias() {
        return first;
    }
}