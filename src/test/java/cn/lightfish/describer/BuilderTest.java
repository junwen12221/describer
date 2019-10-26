package cn.lightfish.describer;

import cn.lightfish.rsqlBuilder.DotCallResolver;
import cn.lightfish.rsqlBuilder.RexBuilder;
import cn.lightfish.rsqlBuilder.SchemaMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

public class BuilderTest {

    @Test
    public void id() throws IOException {
        Describer describer = new Describer("treavelrecord");
        Node primary = describer.primary();
        RexBuilder rexBuilder = new RexBuilder();
        primary.accept(rexBuilder);
        Assert.assertEquals("treavelrecord", rexBuilder.getStack());
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
    public void dataSourceAlias() throws IOException {
        Describer describer = new Describer("db1.travelrecord as t");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("DataSourceAlias{TableObject{db1.travelrecord} as t}", Objects.toString(rexBuilder.getStack()));
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
        Describer describer = new Describer("(db1.travelrecord as t).projectIndex(1 as id1,2 as id2))");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Assert.assertEquals("DataSourceAlias{TableObject{db1.travelrecord} as t}", Objects.toString(rexBuilder.getStack()));
    }

    @Test
    public void dotCallResolver3() throws IOException {
        Describer describer = new Describer("(db1.travelrecord as t).filter(true).map(2) ");
        Node primary = describer.expression();
        SchemaMatcher schemaMatcher = new SchemaMatcher();
        schemaMatcher.addSchema("db1", "travelrecord", "id");
        DotCallResolver callResolver = new DotCallResolver();
        primary.accept(callResolver);
        primary = callResolver.getStack();
        System.out.println(primary);
        RexBuilder rexBuilder = new RexBuilder(schemaMatcher);
        primary.accept(rexBuilder);
        Object stack = rexBuilder.getStack();
        System.out.println(stack);
    }
}