package cn.lightfish.describer.leaf;

import cn.lightfish.describer.NodeVisitor;

import java.math.BigDecimal;
import java.util.Objects;

public class DecimalLiteral implements Literal {
    private BigDecimal number;

    public DecimalLiteral(BigDecimal number) {
        this.number = number;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public String toString() {
        return Objects.toString(number);
    }
}