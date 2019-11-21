package cn.lightfish.describer;

import cn.lightfish.DesRelNodeHandler;
import cn.lightfish.rsqlBuilder.Db1;
import cn.lightfish.rsqlBuilder.DesBuilder;
import cn.lightfish.rsqlBuilder.Rel2Des;
import cn.lightfish.wu.BaseQuery;
import cn.lightfish.wu.QueryOp;
import cn.lightfish.wu.ast.base.ExplainVisitor;
import cn.lightfish.wu.ast.base.Expr;
import cn.lightfish.wu.ast.base.Literal;
import cn.lightfish.wu.ast.base.Schema;
import cn.lightfish.wu.ast.query.FieldType;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.CorrelationId;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.util.NlsString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.lightfish.DesRelNodeHandler.dump;
import static cn.lightfish.DesRelNodeHandler.parse2SyntaxAst;
import static org.apache.calcite.sql.SqlExplainLevel.DIGEST_ATTRIBUTES;

public class RelSpec extends BaseQuery {

    private FrameworkConfig config;

    @Before
    public void setUp() {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema = rootSchema.add("db1", new ReflectiveSchema(new Db1()));
        this.config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema).build();
    }

    public RelNode toRelNode(Schema node) {
        return new QueryOp(DesBuilder.create(config)).complie(node);
    }

    public RexNode toRexNode(Expr node) {
        return new QueryOp(DesBuilder.create(config)).toRex(node);
    }

    private ParseNode getParseNode(String text) {
        Describer describer = new Describer(text);
        return describer.expression();
    }

    @Test
    public void l() throws IOException {


    }

    @Test
    public void selectWithoutFrom() throws IOException {
        Schema select;
        RelNode relNode;
        select = valuesSchema(fields(fieldType("1", "int")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int)),values())", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "int")), values(1, 2, 3, 4, 5));
        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER 1)], tuples=[[{ 1 }, { 2 }, { 3 }, { 4 }, { 5 }]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int)),values(literal(1),literal(2),literal(3),literal(4),literal(5)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "int"), fieldType("2", "int")), values(1, 2, 3, 4));
        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER 1, INTEGER 2)], tuples=[[{ 1, 2 }, { 3, 4 }]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "string"), fieldType("2", "string")), values("1", "2", "3", "4"));
        Assert.assertEquals("LogicalValues(type=[RecordType(VARCHAR 1, VARCHAR 2)], tuples=[[{ '1', '2' }, { '3', '4' }]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,string),fieldType(2,string)),values(literal('1'),literal('2'),literal('3'),literal('4')))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "float")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(FLOAT 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "long")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(BIGINT 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "string")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(VARCHAR 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "binary")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(VARBINARY 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "binary")), values(new byte[]{'a'}));
        Assert.assertEquals("LogicalValues(type=[RecordType(VARBINARY 1)], tuples=[[{ X'61' }]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "date")), values());
        Assert.assertEquals("LogicalValues(type=[RecordType(DATE 1)], tuples=[[]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "date")), values(date("2019-11-17")));
        Assert.assertEquals("LogicalValues(type=[RecordType(DATE 1)], tuples=[[{ 2019-11-17 }]])\n", toString(relNode = toRelNode(select)));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "time")), values(time("00:09:00")));
        Assert.assertEquals("LogicalValues(type=[RecordType(TIME(0) 1)], tuples=[[{ 00:09:00 }]])\n", toString(relNode = toRelNode(select)));
        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        select = valuesSchema(fields(fieldType("1", "timestamp")), values(dateTime(now.toString())));
        Assert.assertEquals("valuesSchema(fields(fieldType(1,int),fieldType(2,int)),values(literal(1),literal(2),literal(3),literal(4)))", toDSL(relNode));

        Assert.assertTrue(toString(toRelNode(select)).contains("TIMESTAMP"));
    }


    private String toString(RelNode relNode) {
        return RelOptUtil.toString(relNode, DIGEST_ATTRIBUTES).replaceAll("\r", "");
    }

    private String toString(RexNode relNode) {
        return relNode.toString();
    }


    @Test
    public void selectAllWithoutFrom() throws IOException {
        Schema select = all(valuesSchema(fields(fieldType("1", "int")), values()));
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)])", select.toString());

        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER 1)], tuples=[[]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectAllWithoutFrom2() throws IOException {
        String text = "all(valuesSchema(fields(fieldType(id,int)),values()))";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = getS(expression);
        Assert.assertEquals("all(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values()))", s);


        Schema select = all(valuesSchema(fields(fieldType("id", "int")), values()));
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int)])", select.toString());

        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER id)], tuples=[[]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectDistinctWithoutFrom() throws IOException {
        Schema select = distinct(valuesSchema(fields(fieldType("1", "int")), values(2, 2)));
        Assert.assertEquals("DistinctSchema(schema=ValuesSchema(values=[2, 2], fieldNames=[FieldSchema(id=1, type=int)]))", select.toString());
        RelNode relNode = toRelNode(select);

        Assert.assertEquals("LogicalAggregate(group=[{0}])\n" +
                "  LogicalValues(type=[RecordType(INTEGER 1)], tuples=[[{ 2 }, { 2 }]])\n", toString(relNode));
        Assert.assertEquals("(2)\n", dump(relNode));
    }

    @Test
    public void selectDistinctWithoutFrom2() throws IOException {
        String text = "distinct(valuesSchema(fields(fieldType(id,int)),values()))";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = getS(expression);
        Assert.assertEquals("distinct(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values()))", s);

        Schema select = distinct(valuesSchema(fields(fieldType("id", "int")), values()));
        Assert.assertEquals("DistinctSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int)]))", select.toString());
    }

    @Test
    public void selectProjectItemWithoutFrom() throws IOException {
        Schema select = project(valuesSchema(fields(fieldType("1", "int"), fieldType("2", "string")), values()), "2", "1");
        Assert.assertEquals("ProjectSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)]), columnNames=[2, 1], fieldSchemaList=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)])", select.toString());

        Assert.assertEquals("LogicalProject(2=[$0], 1=[$1])\n" +
                "  LogicalValues(type=[RecordType(INTEGER 1, VARCHAR 2)], tuples=[[]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectProjectItemWithoutFrom2() throws IOException {
        String text = "project(valuesSchema(fields(fieldType(id,int),fieldType(id2,int)),values()),id3,id4)";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = getS(expression);
        Assert.assertEquals("project(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\")),fieldType(id(\"id2\"),id(\"int\"))),values()),id(\"id3\"),id(\"id4\"))", s);

        Schema select = project(valuesSchema(fields(fieldType("id", "int"), fieldType("id2", "string")), values()), "id3", "id4");
        Assert.assertEquals("ProjectSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int), FieldSchema(id=id2, type=string)]), columnNames=[id3, id4], fieldSchemaList=[FieldSchema(id=id, type=int), FieldSchema(id=id2, type=string)])", select.toString());
    }

    @Test
    public void from() throws IOException {
        String text = "from(db1,travelrecord)";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = getS(expression);
        Assert.assertEquals("from(id(\"db1\"),id(\"travelrecord\"))", s);

        Assert.assertEquals("FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])", from(id("db1"), id("travelrecord")).toString());

        Schema select = from("db1", "travelrecord");
        Assert.assertEquals("FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])", select.toString());

        Assert.assertEquals("LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectProjectFrom() throws IOException {
        String text = "from(db1,travelrecord).project(id)";
        String s = getS(parse2SyntaxAst(text));
        Assert.assertEquals("project(from(id(\"db1\"),id(\"travelrecord\")),id(\"id\"))", s);

        Schema select = project(from("db1", "travelrecord"), "1");
        Assert.assertEquals("ProjectSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), columnNames=[1], fieldSchemaList=[])", select.toString());

    }

    @Test
    public void selectUnionAll() throws IOException {
        Schema select = unionAll(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=UNION_ALL,list=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])])", select.toString());

        String text = "from(db1,travelrecord) unionAll  from(\"db1\", \"travelrecord\")";
        String s = getS(parse2SyntaxAst(text));
        Assert.assertEquals("unionAll(from(id(\"db1\"),id(\"travelrecord\")),from(id(\"\"db1\"\"),id(\"\"travelrecord\"\")))", s);

        Assert.assertEquals("LogicalUnion(all=[true])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectUnionDistinct() throws IOException {
        Schema select = unionDistinct(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=UNION_DISTINCT,list=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])])", select.toString());

        String text = "from(db1,travelrecord) unionDistinct  from(\"db1\", \"travelrecord\")";
        Assert.assertEquals("unionDistinct(from(id(\"db1\"),id(\"travelrecord\")),from(id(\"\"db1\"\"),id(\"\"travelrecord\"\")))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalUnion(all=[false])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(select)));
    }

    private String getS(ParseNode parseNode) {
        return DesRelNodeHandler.syntaxAstToFlatSyntaxAstText(parseNode);
    }

    @Test
    public void selectExceptDistinct() throws IOException {
        Schema select = exceptDistinct(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=EXCEPT_DISTINCT,list=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])])", select.toString());

        String text = "from(db1,travelrecord) exceptDistinct  from(db1,travelrecord)";
        Assert.assertEquals("exceptDistinct(from(id(\"db1\"),id(\"travelrecord\")),from(id(\"db1\"),id(\"travelrecord\")))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalMinus(all=[false])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(select)));
    }

    @Test
    public void selectExceptAll() throws IOException {
        Schema select = exceptAll(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=EXCEPT_ALL,list=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])])", select.toString());

        String text = "from(db1,travelrecord) exceptAll  from(\"db1\", \"travelrecord\")";
        Assert.assertEquals("exceptAll(from(id(\"db1\"),id(\"travelrecord\")),from(id(\"\"db1\"\"),id(\"\"travelrecord\"\")))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalMinus(all=[true])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(select)));
    }


    @Test
    public void selectFromOrder() throws IOException {
        Schema schema = orderBy(from("db1", "travelrecord"), order("id", "ASC"), order("user_id", "DESC"));
        Assert.assertEquals("OrderSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), orders=[OrderItem(columnName=Identifier(value=id), direction=ASC), OrderItem(columnName=Identifier(value=user_id), direction=DESC)])", schema.toString());

        String text = "orderBy(from(db1,travelrecord),order(id,ASC), order(user_id,DESC))";
        Assert.assertEquals("orderBy(from(id(\"db1\"),id(\"travelrecord\")),order(id(\"id\"),id(\"asc\")),order(id(\"user_id\"),id(\"desc\")))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromLimit() throws IOException {
        Schema schema = limit(from("db1", "travelrecord"), 1, 1000);
        Assert.assertEquals("LimitSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), offset=Literal(value=1), limit=Literal(value=1000))", schema.toString());

        String text = "limit(from(db1,travelrecord),order(id,ASC), order(user_id,DESC),1,1000)";
        Assert.assertEquals("limit(from(id(\"db1\"),id(\"travelrecord\")),order(id(\"id\"),id(\"asc\")),order(id(\"user_id\"),id(\"desc\")),literal(1),literal(1000))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalSort(offset=[1], fetch=[1000])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKey() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKeyAvg() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(avg("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='avg', alias='avg(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(avg(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(avg(id(\"id\"))))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], avg(id)=[AVG($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKeyCount() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(count("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='count', alias='count(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(count(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(count(id(\"id\"))))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKeyCountStar() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(count()));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='count', alias='count()', operands=[]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(countStar()))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(countStar()))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count()=[COUNT()])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }


    @Test
    public void selectFromGroupByKeyCountDistinct() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(distinct(count("id"))));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='count', distinct=true, alias='count(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(count(id).distinct()))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(distinct(count(id(\"id\")))))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT(DISTINCT $0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(all(distinct(count("id"))))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], asName=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((as(count("id"), "asName")))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((approximate(count("id"))))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((exact(count("id"))))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0) FILTER $2])\n" +
                "  LogicalProject(id=[$0], user_id=[$1], $f2=[=($0, 1)])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((filter(count("id"), eq(id("id"), literal(1)))))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((ignoreNulls(count("id"))))))));

        Assert.assertEquals("LogicalAggregate(group=[{0}], count(id)=[COUNT($0) WITHIN GROUP ([0 DESC])])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(group(from("db1", "travelrecord"), keys(regular(id("id"))),
                aggregating((sort(count("id"), order("id", "DESC"))))))));

    }

    @Test
    public void selectFromGroupByKeyFirst() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(first("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='first', alias='first(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(first(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(first(id(\"id\"))))", getS(parse2SyntaxAst(text)));


        Assert.assertEquals("LogicalAggregate(group=[{0}], first(id)=[FIRST_VALUE($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));

    }

    @Test
    public void selectFromGroupByKeyLast() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(last("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='last', alias='last(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(last(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(last(id(\"id\"))))", getS(parse2SyntaxAst(text)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], last(id)=[LAST_VALUE($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKeyMax() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(max("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='max', alias='max(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text = "group(from(db1,travelrecord),keys(regular(id)), aggregating(max(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(max(id(\"id\"))))", getS(parse2SyntaxAst(text)));

        String text2 = "from(db1,travelrecord).group(keys(regular(id)),aggregating(max(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(max(id(\"id\"))))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalAggregate(group=[{0}], max(id)=[MAX($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectFromGroupByKeyMin() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(min("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='min', alias='min(id)', operands=[Identifier(value=id)]])", schema.toString());

        String text2 = "from(db1,travelrecord).group(keys(regular(id)),aggregating(min(id)))";
        Assert.assertEquals("group(from(id(\"db1\"),id(\"travelrecord\")),keys(regular(id(\"id\"))),aggregating(min(id(\"id\"))))", getS(parse2SyntaxAst(text2)));


        Assert.assertEquals("LogicalAggregate(group=[{0}], min(id)=[MIN($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectUcaseFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), ucase("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[ucase(Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(ucase(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),ucase(id(\"id\")))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject($f0=[UPPER($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectUpperFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), upper("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[upper(Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(upper(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),upper(id(\"id\")))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject($f0=[UPPER($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectLcaseFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), lcase("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[lcase(Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(lcase(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),lcase(id(\"id\")))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject($f0=[LOWER($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectLowerFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), lower("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[lower(Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(lower(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),lower(id(\"id\")))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject($f0=[LOWER($0)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectMidFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), mid("id", 1));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[mid(Identifier(value=id),Literal(value=1))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(mid(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),mid(id(\"id\")))", getS(parse2SyntaxAst(text2)));
    }

    @Test
    public void selectLenFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), len("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[len(Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(len(id))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),len(id(\"id\")))", getS(parse2SyntaxAst(text2)));
    }

    @Test
    public void selectRoundFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), round("id", 2));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[round(Identifier(value=id),Literal(value=2))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(round(id,2))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),round(id(\"id\"),literal(2)))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject($f0=[ROUND($0, 2)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void selectNowFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), now());
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[now()])", schema.toString());

        String text2 = "from(db1,travelrecord).map(now())";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),now())", getS(parse2SyntaxAst(text2)));


    }

    @Test
    public void selectFormatFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), format(now(), "YYYY-MM-DD"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), expr=[format(now(),Literal(value=YYYY-MM-DD))])", schema.toString());

        String text2 = "from(db1,travelrecord).map(format(now(),'YYYY-MM-DD'))";
        Assert.assertEquals("map(from(id(\"db1\"),id(\"travelrecord\")),format(now(),literal(\"YYYY-MM-DD\")))", getS(parse2SyntaxAst(text2)));

    }

    @Test
    public void filterIn() throws IOException {
        Schema schema = filter(from("db1", "travelrecord"), in("id", 1, 2));
        Assert.assertEquals("FilterSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), exprs=[or(eq(Identifier(value=id),Literal(value=1)),eq(Identifier(value=id),Literal(value=2)))])", schema.toString());

        String text2 = "from(db1,travelrecord).filter(in(id,1,2))";
        Assert.assertEquals("filter(from(id(\"db1\"),id(\"travelrecord\")),in(id(\"id\"),literal(1),literal(2)))", getS(parse2SyntaxAst(text2)));


        Assert.assertEquals("LogicalFilter(condition=[OR(=($0, 1), =($0, 2))])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }


    @Test
    public void filterBetween() throws IOException {
        Schema schema = filter(from("db1", "travelrecord"), between("id", 1, 2));
        Assert.assertEquals("FilterSchema(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), exprs=[and(lte(Literal(value=1),Identifier(value=id)),gte(Identifier(value=id),Literal(value=2)))])", schema.toString());


        String text2 = "from(db1,travelrecord).filter(between(id,1,2))";
        Assert.assertEquals("filter(from(id(\"db1\"),id(\"travelrecord\")),between(id(\"id\"),literal(1),literal(2)))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalFilter(condition=[>=($0, 2)])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void testIsnull() throws IOException {

        Assert.assertEquals("isnull(Identifier(value=id))", isnull(id("id")).toString());

        String text2 = "isnull(id)";
        Assert.assertEquals("isnull(id(\"id\"))", getS(parse2SyntaxAst(text2)));

        Expr expr = isnull(literal(1));
        Assert.assertEquals("IS NULL(1)", toString(toRexNode(expr)));
    }

    @Test
    public void testIfnull() throws IOException {
        Assert.assertEquals("ifnull(Identifier(value=id),Literal(value=default))", ifnull("id", "default").toString());

        String text2 = "ifnull(id)";
        Assert.assertEquals("ifnull(id(\"id\"))", getS(parse2SyntaxAst(text2)));

    }

    @Test
    public void testNullif() throws IOException {
        Expr expr = nullif("id", "default");
        Assert.assertEquals("nullif(Identifier(value=id),Literal(value=default))", expr.toString());

        String text2 = "nullif(id)";
        Assert.assertEquals("nullif(id(\"id\"))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("NULLIF(null:NULL, null:NULL)", toString(toRexNode(nullif(literal(null), literal(null)))));
    }

    @Test
    public void testIsNotNull() throws IOException {
        Expr expr = isnotnull("id");
        Assert.assertEquals("isnotnull(Identifier(value=id))", expr.toString());

        String text2 = "isnotnull(id)";
        Assert.assertEquals("isnotnull(id(\"id\"))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("IS NOT NULL(null:NULL)", toString(toRexNode(isnotnull(literal(null)))));

    }

    @Test
    public void testInteger() throws IOException {
        Expr expr = literal(1);
        RexNode rexNode;
        Assert.assertEquals("Literal(value=1)", expr.toString());

        String text2 = "1";
        Assert.assertEquals("literal(1)", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("1", toString(rexNode = toRexNode(literal(Integer.valueOf(1)))));

        Expr expr1 = geExpr(rexNode);
        Assert.assertEquals("Literal(value=1)", expr1.toString());
    }

    @Test
    public void testLong() throws IOException {
        Expr expr = literal(1L);
        RexNode rexNode;

        Assert.assertEquals("Literal(value=1)", expr.toString());
        String text2 = "1";
        Assert.assertEquals("literal(1)", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("1", toString(rexNode = toRexNode(literal(Long.valueOf(1)))));

        Expr expr1 = geExpr(rexNode);
        Assert.assertEquals("Literal(value=1)", expr1.toString());
    }

    @Test
    public void testFloat() throws IOException {
        Expr expr = literal(Float.MAX_VALUE);
        RexNode rexNode;

        Assert.assertEquals("Literal(value=3.4028234663852886E+38)", expr.toString());
        String text2 = String.valueOf(Float.MAX_VALUE);
        Assert.assertEquals("literal(3.4028235E+38)", getS(parse2SyntaxAst(text2)));
        Assert.assertEquals("1.0:DECIMAL(2, 1)", toString(rexNode = toRexNode(literal(Float.valueOf(1)))));

        Expr expr1 = geExpr(rexNode);
        Assert.assertEquals("Literal(value=1.0)", expr1.toString());
    }

    @Test
    public void testId() throws IOException {
        Expr expr = id("id");
        Assert.assertEquals("Identifier(value=id)", expr.toString());

        String text2 = "id";
        Assert.assertEquals("id(\"id\")", getS(parse2SyntaxAst(text2)));
    }

    @Test
    public void testString() throws IOException {
        Expr expr = literal("str");
        Assert.assertEquals("Literal(value=str)", expr.toString());

        String text2 = "'str'";
        Assert.assertEquals("literal(\"str\")", getS(parse2SyntaxAst(text2)));

        RexNode rexNode = toRexNode(literal("str"));
        Assert.assertEquals("'str'", toString(rexNode));

        Expr expr1 = geExpr(rexNode);
        Assert.assertEquals("Literal(value=str)", expr1.toString());
    }

    @Test
    public void testTime() throws IOException {
        Expr expr = literal(time("00:09:00"));
        Assert.assertEquals("Literal(value=00:09)", expr.toString());

        String text2 = "time('00:09:00')";
        Assert.assertEquals("time(literal(\"00:09:00\"))", getS(parse2SyntaxAst(text2)));

        RexNode rexNode = toRexNode(literal(time("00:09:00")));
        Assert.assertEquals("00:09:00", toString(rexNode));

        Expr expr1 = geExpr(rexNode);
        Assert.assertEquals("Literal(value=00:09)", expr1.toString());
    }

    @Test
    public void testMinus() throws IOException {
        Expr expr = minus(id("id"), literal(1));
        Assert.assertEquals("minus(Identifier(value=id),Literal(value=1))", expr.toString());

        String text2 = "id-1";
        Assert.assertEquals("minus(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("-(1, 1)", toString(toRexNode(minus(literal(1), literal(1)))));
    }

    @Test
    public void testEqual() throws IOException {
        Expr expr = eq(id("id"), literal(1));
        Assert.assertEquals("eq(Identifier(value=id),Literal(value=1))", expr.toString());

        String text2 = "id=1";
        Assert.assertEquals("eq(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("=(1, 1)", toString(toRexNode(eq(literal(1), literal(1)))));
    }

    @Test
    public void testAnd() throws IOException {
        Expr expr = and(literal(1), literal(1));
        Assert.assertEquals("and(Literal(value=1),Literal(value=1))", expr.toString());

        String text2 = "1 and 1";
        Assert.assertEquals("and(literal(1),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("AND(1, 1)", toString(toRexNode(expr)));
    }

    @Test
    public void testor() throws IOException {
        Expr expr = or(literal(1), literal(1));
        Assert.assertEquals("or(Literal(value=1),Literal(value=1))", expr.toString());

        String text2 = "1 or 1";
        Assert.assertEquals("or(literal(1),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("OR(1, 1)", toString(toRexNode(expr)));
    }

    @Test
    public void testEqualOr() throws IOException {
        String text2 = "1 = 2 or 1 = 1";
        Assert.assertEquals("or(eq(literal(1),literal(2)),eq(literal(1),literal(1)))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("OR(=(1, 2), =(1, 1))", toString(toRexNode(or(eq(literal(1), literal(2)), eq(literal(1), literal(1))))));
    }

    @Test
    public void testNot() throws IOException {
        Expr expr = not(literal(1));
        Assert.assertEquals("not(Literal(value=1))", expr.toString());

        String text2 = "not(1 = 1)";
        Assert.assertEquals("not(eq(literal(1),literal(1)))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("NOT(=(1, 1))", toString(toRexNode(not(eq(literal(1), literal(1))))));

    }

    @Test
    public void testNotEqual() throws IOException {
        Expr expr = ne(id("id"), literal(1));
        Assert.assertEquals("ne(Identifier(value=id),Literal(value=1))", expr.toString());

        String text2 = "id <> 1";
        Assert.assertEquals("ne(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("NOT(<>(1, 1))", toString(toRexNode(not(ne(literal(1), literal(1))))));
    }

    @Test
    public void testGreaterThan() throws IOException {
        Expr expr = gt(id("id"), literal(1));
        Assert.assertEquals("gt(Identifier(value=id),Literal(value=1))", expr.toString());

        String text2 = "id > 1";
        Assert.assertEquals("gt(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals(">(1, 1)", toString(toRexNode((gt(literal(1), literal(1))))));
    }

    @Test
    public void testGreaterThanEqual() throws IOException {
        Expr expr = gte(id("id"), literal(true));
        Assert.assertEquals("gte(Identifier(value=id),Literal(value=true))", expr.toString());

        String text2 = "id >= 1";
        Assert.assertEquals("gte(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals(">=(1, 1)", toString(toRexNode((gte(literal(1), literal(1))))));
    }

    @Test
    public void testLessThan() throws IOException {
        Expr expr = lt(id("id"), literal(true));
        Assert.assertEquals("lt(Identifier(value=id),Literal(value=true))", expr.toString());

        String text2 = "id < 1";
        Assert.assertEquals("lt(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("<(1, 1)", toString(toRexNode((lt(literal(1), literal(1))))));
    }

    @Test
    public void testLessThanEqual() throws IOException {
        Expr expr = lte(id("id"), literal(true));
        Assert.assertEquals("lte(Identifier(value=id),Literal(value=true))", expr.toString());

        String text2 = "id <= 1";
        Assert.assertEquals("lte(id(\"id\"),literal(1))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("<=(1, 1)", toString(toRexNode((lte(literal(1), literal(1))))));
    }

    @Test
    public void testDot() throws IOException {
        Expr expr = dot(id("table"), id("column"));
        Assert.assertEquals("dot(Identifier(value=table),Identifier(value=column))", expr.toString());

        String text2 = "table.column";
        Assert.assertEquals("dot(id(\"table\"),id(\"column\"))", getS(parse2SyntaxAst(text2)));

        Schema map = map(from("db1", "travelrecord"), dot(id("travelrecord"), id("id")));
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(map)));

        map = map(as(from("db1", "travelrecord"), "a"), dot(id("a"), id("id")));
        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(map)));
    }

    @Test
    public void testAsColumnName() throws IOException {
        Expr expr = as(literal(1), id("column"));
        Assert.assertEquals("asColumnName(Literal(value=1),Identifier(value=column))", expr.toString());

        String text2 = "1 as column";
        Assert.assertEquals("as(literal(1),id(\"column\"))", getS(parse2SyntaxAst(text2)));

        Schema map = map(as(from("db1", "travelrecord"), "a"), dot(id("a"), id("id")), as(literal(1), id("column")));
        Assert.assertEquals("LogicalProject(id=[$0], column=[1])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(map)));
    }

    @Test
    public void testAsTableName() throws IOException {
        Schema schema = map(as(from("db1", "travelrecord"), id("table2")), dot(id("table2"), id("id")));
        Assert.assertEquals("MapSchema(schema=AsTable(schema=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), alias=table2), expr=[dot(Identifier(value=table2),Identifier(value=id))])", schema.toString());

        String text2 = "from(db1,table) as table2";
        Assert.assertEquals("as(from(id(\"db1\"),id(\"table\")),id(\"table2\"))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("LogicalProject(id=[$0])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n", toString(toRelNode(schema)));
    }

    @Test
    public void testCast() throws IOException {
        Expr expr = cast(literal(1), id("float"));
        Assert.assertEquals("cast(Literal(value=1),Identifier(value=float))", expr.toString());

        String text2 = "cast(1,float)";
        Assert.assertEquals("cast(literal(1),id(\"float\"))", getS(parse2SyntaxAst(text2)));

        Assert.assertEquals("1:FLOAT", toString(toRexNode(expr)));
    }

    @Test
    public void testInnerJoin() throws IOException, SQLException {
        Schema schema = innerJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=INNER_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        String text2 = "innerJoin(table.id = table2.id , from(db1,travelrecord),from(db1,travelrecord2))";
        Assert.assertEquals("innerJoin(eq(dot(id(\"table\"),id(\"id\")),dot(id(\"table2\"),id(\"id\"))),from(id(\"db1\"),id(\"travelrecord\")),from(id(\"db1\"),id(\"travelrecord2\")))", getS(parse2SyntaxAst(text2)));
        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[inner])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testLeftJoin() throws IOException {
        Schema schema = leftJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=LEFT_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[left])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testRightJoin() throws IOException {
        Schema schema = rightJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=RIGHT_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[right])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testFullJoin() throws IOException {
        Schema schema = fullJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=FULL_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());


        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[full])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testSemiJoin() throws IOException {
        Schema schema = semiJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=SEMI_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());


        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[semi])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testAntiJoin() throws IOException {
        Schema schema = antiJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), from("db1", "travelrecord"), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=ANTI_JOIN, schemas=[FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)]), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalJoin(condition=[=($0, $2)], joinType=[anti])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }

    @Test
    public void testCorrelateInnerJoin() throws IOException {
        Schema schema = correlateInnerJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), correlate(from("db1", "travelrecord")), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=CORRELATE_INNER_JOIN, schemas=[CorrelateSchema(from=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalFilter(condition=[=($0, $2)])\n" +
                "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{}])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);
    }


    @Test
    public void testCorrelateLeftJoin() throws IOException {
        Schema schema = correlateLeftJoin(eq(id("travelrecord", "id"), id("travelrecord2", "id")), correlate(from("db1", "travelrecord")), from("db1", "travelrecord2"));
        Assert.assertEquals("JoinSchema(type=CORRELATE_LEFT_JOIN, schemas=[CorrelateSchema(from=FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord)])), FromSchema(names=[Identifier(value=db1), Identifier(value=travelrecord2)])], condition=eq(dot(Identifier(value=travelrecord),Identifier(value=id)),dot(Identifier(value=travelrecord2),Identifier(value=id))))", schema.toString());

        RelNode relNode = toRelNode(schema);
        Assert.assertEquals("LogicalCorrelate(correlation=[$cor0], joinType=[left], requiredColumns=[{}])\n" +
                "  LogicalTableScan(table=[[db1, travelrecord]])\n" +
                "  LogicalFilter(condition=[=($cor0.id, $0)])\n" +
                "    LogicalTableScan(table=[[db1, travelrecord2]])\n", toString(relNode));
        dump(relNode);

    }

    @Test
    public void test() {
        Schema select;
        select = valuesSchema(fields(fieldType("1", "int")), values((1)));

        RelNode relNode = toRelNode(select);
//
//        Assert.assertEquals("LogicalValues(type=[RecordType(INTEGER 1)], tuples=[[]])\n", toString(relNode));

        Rel2Des rel2Des = new Rel2Des();


        Schema schema = getSchema(relNode);
        String sb = toString(schema);
        System.out.println(sb);
        System.out.println(schema.toString());
    }

    private String toString(Schema schema) {
        ExplainVisitor explainVisitor = new ExplainVisitor();
        schema.accept(explainVisitor);
        return explainVisitor.getSb();
    }

    private String toDSL(RelNode relNode) {
        return toString(getSchema(relNode));
    }

    private String toDSL(RexNode rexNode) {
        return geExpr(rexNode).toString();
    }

    private Expr geExpr(RexNode rexNode) {
        SqlKind kind = rexNode.getKind();
        if (kind == SqlKind.LITERAL) {
            RexLiteral rexNode1 = (RexLiteral) rexNode;
            return literal(unWrapper(rexNode1));
        }
        return null;
    }

    private Schema getSchema(RelNode relNode) {
        List<RelNode> inputs = relNode.getInputs();
        String relTypeName = relNode.getRelTypeName();
        String correlVariable = relNode.getCorrelVariable();
        RelOptTable table = relNode.getTable();
        Set<CorrelationId> variablesSet = relNode.getVariablesSet();
        switch (relTypeName) {
            case "LogicalValues": {
                return logicValues(relNode);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Schema logicValues(RelNode relNode) {
        LogicalValues logicalValues = (LogicalValues) relNode;
        return valuesSchema(getFieldSchema(relNode), getValues(logicalValues));
    }

    private List<Literal> getValues(LogicalValues relNode1) {
        ImmutableList<ImmutableList<RexLiteral>> tuples = relNode1.getTuples();
        if (tuples == null) {
            return Collections.emptyList();
        }
        return tuples.stream().flatMap(Collection::stream).map(rexLiteral -> literal(unWrapper(rexLiteral))).collect(Collectors.toCollection(ArrayList::new));
    }

    private Object unWrapper(RexLiteral rexLiteral) {
        if (rexLiteral.isNull()) {
            return null;
        }
        RelDataType type = rexLiteral.getType();
        SqlTypeName sqlTypeName = type.getSqlTypeName();
        switch (sqlTypeName) {
            case BOOLEAN:
            case SMALLINT:
            case TINYINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case FLOAT:
            case REAL:
            case DOUBLE:
                return rexLiteral.getValue();
            case DATE: {
                Integer valueAs = (Integer) rexLiteral.getValue4();
                return LocalDate.ofEpochDay(valueAs);
            }
            case TIME: {
                Integer value = (Integer) rexLiteral.getValue4();
                return LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(value));
            }
            case TIMESTAMP:
                Long value = (Long) rexLiteral.getValue4();
                Instant instant = Instant.ofEpochMilli(value);
                return LocalDateTime.from(instant);
            case CHAR:
            case VARCHAR:
                return ((NlsString) rexLiteral.getValue()).getValue();
            case BINARY:
                return ((ByteString) rexLiteral.getValue()).toByteArray();
            case VARBINARY:
                return ((ByteString) rexLiteral.getValue()).toByteArray();
            case NULL:
                return null;
        }
        throw new UnsupportedOperationException();
    }

    private List<FieldType> getFieldSchema(RelNode relNode) {
        RelDataType rowType = relNode.getRowType();
        List<RelDataTypeField> fieldList = rowType.getFieldList();
        ArrayList<FieldType> fieldSchemas = new ArrayList<>(fieldList.size());
        for (RelDataTypeField relDataTypeField : fieldList) {
            String name = relDataTypeField.getName();
            SqlTypeName sqlTypeName = relDataTypeField.getType().getSqlTypeName();
            fieldSchemas.add(fieldType(name, type(sqlTypeName)));
        }
        return fieldSchemas;
    }

    private String type(SqlTypeName type) {
        return QueryOp.typeMap.inverse().get(type);
    }

    //////////////////////////////////////////
    void visitFieldSchema(String id, String type) {

    }

}