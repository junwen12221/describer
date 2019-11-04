package cn.lightfish.describer;

import cn.lightfish.describer.literal.IdLiteral;
import cn.lightfish.rsqlBuilder.DotCallResolver;
import cn.lightfish.rsqlBuilder.NameBuilder;
import cn.lightfish.rsqlBuilder.schema.SchemaMatcher;
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
        NameBuilder rexBuilder = new NameBuilder();
        primary.accept(rexBuilder);
        Assert.assertEquals(new IdLiteral("treavelrecord"), rexBuilder.getStack());
    }

    @Test
    public void schema() throws IOException {
        Describer describer = new Describer("db1");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", null, null);
        NameBuilder rexBuilder = new NameBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("SchemaObject{db1}", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void table() throws IOException {
        Describer describer = new Describer("db1.travelrecord");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", null);
        NameBuilder rexBuilder = new NameBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("TableObject{db1.travelrecord}", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void column() throws IOException {
        Describer describer = new Describer("db1.travelrecord.ID");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "ID");
        NameBuilder rexBuilder = new NameBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("ColumnObject{db1.travelrecord.ID}", Objects.toString(rexBuilder.getStack()));
    }


    @Test
    public void dotCallResolver() throws IOException {
        Describer describer = new Describer("from().select() ");
        Node primary = describer.expression();
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        Assert.assertTrue("select(from())".equalsIgnoreCase(Objects.toString(callResolver.getStack())));
    }

    @Test
    public void dotCallResolver2() throws IOException {
        Describer describer = new Describer("from(1).select(2) ");
        Node primary = describer.expression();
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        Assert.assertTrue("select(from(1),2)".equalsIgnoreCase(Objects.toString(callResolver.getStack())));
    }

    @Test
    public void project() throws IOException {
        Describer describer = new Describer("(let t = db1.travelrecord).project(t.ID as ID,t.ID as id2)");
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "ID");

        //////////////////////
        NameBuilder rexBuilder = getRexBuilder(describer, schemaMatcher);

        Assert.assertEquals("project((TableObject{db1.travelrecord}),AS_COLUMNNAME(ColumnObject{db1.travelrecord.ID},ID),AS_COLUMNNAME(ColumnObject{db1.travelrecord.ID},id2))", Objects.toString(rexBuilder.getStack()));
    }

    public static NameBuilder getRexBuilder(Describer describer, SchemaMatcher schemaMatcher) {
        Node primary = describer.expression();
        Map<String, Node> variables = describer.getVariables();

        variables.entrySet().forEach(stringNodeEntry -> stringNodeEntry.setValue(processDotCall(stringNodeEntry.getValue())));
        primary = processDotCall(primary);

        variables.entrySet().forEach(c -> {
            NameBuilder rexBuilder = new NameBuilder(schemaMatcher, variables);
            c.getValue().accept(rexBuilder);
            c.setValue(rexBuilder.getStack());
        });
        NameBuilder rexBuilder = new NameBuilder(schemaMatcher, variables);
        primary.accept(rexBuilder);
        return rexBuilder;
    }

    private static Node processDotCall(Node primary) {
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        return primary;
    }

    @Test
    public void join() throws IOException {
        Describer describer = new Describer("(let j =  " +
                "join((let t1 = db1.travelrecord),(let t2 = db1.address),(t1.ID = t2.ID)))" +
                ".project(j.ID as ID)");
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "ID");
        schemaMatcher.addSchema("db1", "address", "ID");
        //////////////////////
        NameBuilder rexBuilder = getRexBuilder(describer, schemaMatcher);

        Assert.assertEquals("project((JOIN((TableObject{db1.travelrecord}),(TableObject{db1.address}),(EQ(ColumnObject{db1.travelrecord.ID},ColumnObject{db1.address.ID})))),AS_COLUMNNAME(j.ID,ID))", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void apply() throws IOException {
        Describer describer = new Describer("(let t = db1.travelrecord).all((let a =  db1.address).project(a.ID).all(a.ID=t.ID)).project(t.ID)");
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "ID");
        schemaMatcher.addSchema("db1", "address", "ID");
        NameBuilder rexBuilder = getRexBuilder(describer, schemaMatcher);
        Assert.assertEquals("project(ALL((TableObject{db1.travelrecord}),ALL(project((TableObject{db1.address}),ColumnObject{db1.address.ID}),EQ(ColumnObject{db1.address.ID},ColumnObject{db1.travelrecord.ID}))),ColumnObject{db1.travelrecord.ID})", Objects.toString(rexBuilder.getStack()));
    }
    @Test
    public void dotCallResolver3() throws IOException {
        Describer describer = new Describer("(db1.travelrecord as t).filter(true).select(2) ");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "ID");
        primary = processDotCall(primary);
        System.out.println(primary);
        NameBuilder rexBuilder = new NameBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Object stack = rexBuilder.getStack();
        System.out.println(stack);
    }
}