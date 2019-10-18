package cn.lightfish.describer;

import java.util.List;

public interface Builder {
    Node eval(BuilderContext builder, List<Node> exprs);
}