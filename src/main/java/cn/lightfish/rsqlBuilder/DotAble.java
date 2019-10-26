package cn.lightfish.rsqlBuilder;

public interface DotAble {
    default <T> T dot(Object o) {
        if (o instanceof String) {
            return dot((String) o);
        }
        return dot((MemberFunction) o);
    }

    <T> T dot(String o);

    <T> T dot(MemberFunction o);
}