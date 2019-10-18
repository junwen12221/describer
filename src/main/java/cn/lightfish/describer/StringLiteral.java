package cn.lightfish.describer;

import lombok.Getter;

import java.util.Objects;
@Getter
public class StringLiteral implements Literal{
  final   String string;

    public StringLiteral(String string) {
        this.string = string;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public String toString() {
        return Objects.toString(string);
    }
}