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
    public void test1() throws Exception {
        String text = toflatSyntaxAstText("from(db1,travelrecord).select(t.id)");
        Assert.assertEquals("select(from(\"db1\",\"travelrecord\"),dot(\"t\",\"id\"))", text);
        RelNode relNode = toRelNode(text);
        Assert.assertEquals("LogicalProject(id=[$0])\r\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\r\n", RelOptUtil.toString(relNode));
        Assert.assertEquals("(100)\r\n(150)\r\n", dump(relNode));
    }

    String toflatSyntaxAstText(String text) {
        return DesRelNodeHandler.toflatSyntaxAstText(text);
    }

    RelNode toRelNode(String text) {
        return this.desRelNodeHandler.toRelNode(this.desRelNodeHandler.complieFlatSyntaxAstText(text));
    }

    String dump(RelNode relNode) {
        return this.desRelNodeHandler.dump(relNode);
    }
}