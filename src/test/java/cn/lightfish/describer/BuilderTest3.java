package cn.lightfish.describer;

import cn.lightfish.DesRelNodeHandler;
import cn.lightfish.rsqlBuilder.Db1;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.junit.Assert;
import org.junit.Test;


public class BuilderTest3 {
    private final FrameworkConfig config;
    private final DesRelNodeHandler desRelNodeHandler;

    public BuilderTest3() {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema = rootSchema.add("db1", new ReflectiveSchema(new Db1()));
        this.config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema).build();
        this.desRelNodeHandler = new DesRelNodeHandler(this.config);
    }

    @Test
    public void select() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).map(id)");
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),id(\"id\"))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(1)\n(2)\n", dump(relNode));
    }

    @Test
    public void filter() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).filter(id = 1).map(id)");
        Assert.assertEquals("map(filter(from(id(\"db1\"),id(\"travelrecord\")),eq(id(\"id\"),literal(1))),id(\"id\"))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalFilter(condition=[=($0, 1)])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(1)\n", dump(relNode));
    }

    @Test
    public void or() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).filter(id = 1 or id = 2).map(id)");
        Assert.assertEquals("map(filter(from(id(\"db1\"),id(\"travelrecord\")),or(eq(id(\"id\"),literal(1)),eq(id(\"id\"),literal(2)))),id(\"id\"))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalFilter(condition=[OR(=($0, 1), =($0, 2))])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(1)\n(2)\n", dump(relNode));
    }

    @Test
    public void and() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).filter(id = 1 or (id = 2 and user_id = 10)).map(id)");
        Assert.assertEquals("map(filter(from(id(\"db1\"),id(\"travelrecord\")),or(eq(id(\"id\"),literal(1)),(and(eq(id(\"id\"),literal(2)),eq(id(\"user_id\"),literal(10)))))),id(\"id\"))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalFilter(condition=[OR(=($0, 1), AND(=($0, 2), =($1, 10)))])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(1)\n", dump(relNode));
    }

    @Test
    public void add() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).filter(id = 1 or (id = 2 and user_id = 10)).map(id+1)");
        Assert.assertEquals("map(filter(from(id(\"db1\"),id(\"travelrecord\")),or(eq(id(\"id\"),literal(1)),(and(eq(id(\"id\"),literal(2)),eq(id(\"user_id\"),literal(10)))))),+(id(\"id\"),literal(1)))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalFilter(condition=[OR(=($0, 1), AND(=($0, 2), =($1, 10)))])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(1)\n", dump(relNode));
    }
    String toflatSyntaxAstText(String text) {
        return DesRelNodeHandler.toflatSyntaxAstText(text);
    }

    RelNode toRelNode(String text) {
        return this.desRelNodeHandler.toRelNode(this.desRelNodeHandler.complieFlatSyntaxAstText(text));
    }

    String dump(RelNode relNode) {
        return DesRelNodeHandler.dump(relNode);
    }
}