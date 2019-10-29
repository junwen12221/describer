package cn.lightfish.describer;

import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.rsqlBuilder.DotCallResolver;
import cn.lightfish.rsqlBuilder.RexBuilder;
import cn.lightfish.rsqlBuilder.SchemaMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class BuilderTest {

    @Test
    public void id() throws IOException {
        Describer describer = new Describer("treavelrecord");
        Node primary = describer.primary();
        RexBuilder rexBuilder = new RexBuilder();
        primary.accept(rexBuilder);
        Assert.assertEquals(new IdLiteral("treavelrecord"), rexBuilder.getStack());
    }

    @Test
    public void schema() throws IOException {
        Describer describer = new Describer("db1");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", null, null);
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("SchemaObject{db1}", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void table() throws IOException {
        Describer describer = new Describer("db1.travelrecord");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", null);
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("TableObject{db1.travelrecord}", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void column() throws IOException {
        Describer describer = new Describer("db1.travelrecord.id");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("ColumnObject{db1.travelrecord.id}", Objects.toString(rexBuilder.getStack()));
    }


    @Test
    public void dotCallResolver() throws IOException {
        Describer describer = new Describer("from().map() ");
        Node primary = describer.expression();
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        Assert.assertTrue("map(from())".equalsIgnoreCase(Objects.toString(callResolver.getStack())));
    }

    @Test
    public void dotCallResolver2() throws IOException {
        Describer describer = new Describer("from(1).map(2) ");
        Node primary = describer.expression();
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        Assert.assertTrue("map(from(1),2)".equalsIgnoreCase(Objects.toString(callResolver.getStack())));
    }

    @Test
    public void project() throws IOException {
        Describer describer = new Describer("(let t = db1.travelrecord).project(t.id as id,t.id as id2)");
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");

        //////////////////////
        RexBuilder rexBuilder = getRexBuilder(describer, schemaMatcher);

        Assert.assertEquals("project((TableObject{db1.travelrecord}),AS(ColumnObject{db1.travelrecord.id},id),AS(ColumnObject{db1.travelrecord.id},id2))", Objects.toString(rexBuilder.getStack()));
    }

    private RexBuilder getRexBuilder(Describer describer, SchemaMatcher schemaMatcher) {
        Node primary = describer.expression();
        Map<String, Node> variables = describer.getVariables();

        variables.entrySet().forEach(stringNodeEntry -> stringNodeEntry.setValue(processDotCall(stringNodeEntry.getValue())));
        primary = processDotCall(primary);

        variables.entrySet().forEach(c -> {
            RexBuilder rexBuilder = new RexBuilder(schemaMatcher, variables);
            c.getValue().accept(rexBuilder);
            c.setValue(rexBuilder.getStack());
        });
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher, variables);
        primary.accept(rexBuilder);
        return rexBuilder;
    }

    private Node processDotCall(Node primary) {
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        return primary;
    }

    @Test
    public void apply() throws IOException {
        Describer describer = new Describer("(let t = db1.travelrecord).all((let a =  db1.address).project(a.id).all(a.id=t.id)).project(t.id)");
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        schemaMatcher.addSchema("db1", "address", "id");
        RexBuilder rexBuilder = getRexBuilder(describer, schemaMatcher);
        Assert.assertEquals("project(ALL((TableObject{db1.travelrecord}),ALL(project((TableObject{db1.address}),ColumnObject{db1.address.id}),EQ(ColumnObject{db1.address.id},ColumnObject{db1.travelrecord.id}))),ColumnObject{db1.travelrecord.id})", Objects.toString(rexBuilder.getStack()));
    }
    @Test
    public void dotCallResolver3() throws IOException {
        Describer describer = new Describer("(db1.travelrecord as t).filter(true).map(2) ");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        primary = processDotCall(primary);
        System.out.println(primary);
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Object stack = rexBuilder.getStack();
        System.out.println(stack);
    }
}