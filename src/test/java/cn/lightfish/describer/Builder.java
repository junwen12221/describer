package cn.lightfish.describer;

import java.util.List;

public interface Builder {
    ParseNode eval(List<ParseNode> exprs);
}