package cn.lightfish.describer;

import java.util.List;

public interface Builder {
    Node eval(List<Node> exprs);
}