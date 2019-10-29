package cn.lightfish.rsqlBuilder;

import cn.lightfish.describer.Node;

public interface DotAble extends Node {
    <T> T dot(String o);
}