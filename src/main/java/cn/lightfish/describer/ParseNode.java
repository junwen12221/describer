package cn.lightfish.describer;

public interface ParseNode {
    void accept(NodeVisitor visitor);

    <T extends ParseNode> T copy();
}