package cn.lightfish.describer;

import cn.lightfish.rsqlBuilder.DotCallResolver;
import cn.lightfish.wu.Ast;
import cn.lightfish.wu.ast.Identifier;
import cn.lightfish.wu.ast.Literal;
import cn.lightfish.wu.ast.Schema;

import java.math.BigInteger;


public class BuilderTest3 extends Ast {
    public static void main(String[] args) {
        Describer describer = new Describer("(from(db1,travelrecord) asTable t)" +
                ".filter (t.id = 1 or a.id = 2)\n" +
                ".select(t.id,t.user_id)");
        Node primary1 = describer.expression();
        Node primary = processDotCall(primary1);
        NodeVisitorImpl nodeVisitor = new NodeVisitorImpl();
        primary.accept(nodeVisitor);
        System.out.println(nodeVisitor.getSb());
        Schema select = select(filter(as(from("db1", "travelrecord"), "t"), or(eq(dot("t", "id"), new Literal(BigInteger.valueOf(1))), eq(dot("a", "id"), new Literal(BigInteger.valueOf(2))))), dot("t", "id"), dot("t", "user_id"));

    }

    private static cn.lightfish.wu.ast.Node dot(String t, String id) {
        return dot(new Identifier(t), new Identifier(id));
    }


    private static Node processDotCall(Node primary) {
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        return primary;
    }
}