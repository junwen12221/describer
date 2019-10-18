package cn.lightfish.describer;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ParenthesesExpr implements Node {

    private List<Node> exprs;
    public ParenthesesExpr(Node... exprs){
    this.exprs = Arrays.asList(exprs);
    }
    public ParenthesesExpr(List<Node> exprs) {
        this.exprs = exprs;
    }
    public ParenthesesExpr(Node exprs) {
        this.exprs = Collections.singletonList(exprs);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
    @Override
    public String toString() {
        return MessageFormat.format( "({0})",  exprs.stream().map(i->Objects.toString(i)).collect(Collectors.joining(",")));
    }
}