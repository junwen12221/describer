package cn.lightfish.describer;

public interface Node {
    void accept(NodeVisitor visitor);
}