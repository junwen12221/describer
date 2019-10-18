package cn.lightfish.describer.literal;

import cn.lightfish.describer.NodeVisitor;
import lombok.Getter;

import java.util.Objects;

@Getter
public class IdLiteral implements Literal {
    String id;

    public IdLiteral(String id) {
        this.id = id;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public String toString() {
        return Objects.toString(id);
    }
}