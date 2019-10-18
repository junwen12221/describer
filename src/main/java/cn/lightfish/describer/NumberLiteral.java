package cn.lightfish.describer;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;
@Getter
public class NumberLiteral implements Literal {
    private Number number;

    public NumberLiteral(Number number) {
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