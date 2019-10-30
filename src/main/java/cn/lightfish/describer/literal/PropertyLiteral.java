package cn.lightfish.describer.literal;

import cn.lightfish.describer.NodeVisitor;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class PropertyLiteral implements Literal {
    List<String> value;

    public PropertyLiteral(List<String> value) {
        this.value = new ArrayList<>(value);
    }

    @Override
    public String toString() {
        return String.join(".", value);
    }

    @Override
    public void accept(NodeVisitor visitor) {

    }

    @Override
    public PropertyLiteral copy() {
        return new PropertyLiteral(value);
    }
}