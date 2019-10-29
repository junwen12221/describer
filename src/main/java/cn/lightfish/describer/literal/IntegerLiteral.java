package cn.lightfish.describer.literal;

import cn.lightfish.describer.NodeVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Objects;

@EqualsAndHashCode
@Getter
public class IntegerLiteral implements Literal {
    private BigInteger number;

    public IntegerLiteral(BigInteger number) {
        this.number = number;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public IntegerLiteral copy() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toString(number);
    }
}