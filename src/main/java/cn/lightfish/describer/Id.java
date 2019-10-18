package cn.lightfish.describer;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
@Getter
public class Id implements Node{
    List<String> id;

    public Id(String id) {
        this.id = Collections.singletonList(id);
    }

    public Id(List<String> id) {
        this.id = id;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public String toString() {
        return String.join(".", id);
    }
}