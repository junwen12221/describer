package cn.lightfish.describer;

import cn.lightfish.DesRelNodeHandler;
import cn.lightfish.wu.BaseQuery;
import cn.lightfish.wu.ast.base.ExplainVisitor;
import cn.lightfish.wu.ast.base.Expr;
import cn.lightfish.wu.ast.base.Schema;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AstSpec extends BaseQuery {
    private static ExplainVisitor explainVisitor() {
        return new ExplainVisitor();
    }

    private ParseNode getParseNode(String text) {
        Describer describer = new Describer(text);
        return describer.expression();
    }

    @Test
    public void selectWithoutFrom() throws IOException {
        Schema select = valuesSchema(fields(fieldType("1", "int")), values());
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)])", select.toString());
    }

    @Test
    public void selectWithoutFrom2() throws IOException {
        String text = "valuesSchema(fields(fieldType(id,int)),values())";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = DesRelNodeHandler.syntaxAstToFlatSyntaxAstText(expression);
        Assert.assertEquals("valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values())", s);


        Schema select = valuesSchema(fields(fieldType(id("id"), id("int"))), values());
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int)])", select.toString());
    }

    @Test
    public void selectAllWithoutFrom() throws IOException {
        Schema select = all(valuesSchema(fields(fieldType("1", "int")), values()));
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)])", select.toString());
    }

    @Test
    public void selectAllWithoutFrom2() throws IOException {
        String text = "all(valuesSchema(fields(fieldType(id,int)),values()))";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = DesRelNodeHandler.syntaxAstToFlatSyntaxAstText(expression);
        Assert.assertEquals("all(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values()))", s);


        Schema select = all(valuesSchema(fields(fieldType("id", "int")), values()));
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int)])", select.toString());
    }

    @Test
    public void selectDistinctWithoutFrom() throws IOException {
        Schema select = distinct(valuesSchema(fields(fieldType("1", "int")), values()));
        Assert.assertEquals("DistinctSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)]))", select.toString());
    }

    @Test
    public void selectDistinctWithoutFrom2() throws IOException {
        String text = "distinct(valuesSchema(fields(fieldType(id,int)),values()))";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = DesRelNodeHandler.syntaxAstToFlatSyntaxAstText(expression);
        Assert.assertEquals("distinct(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values()))", s);

        Schema select = distinct(valuesSchema(fields(fieldType("id", "int")), values()));
        Assert.assertEquals("DistinctSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=id, type=int)]))", select.toString());
    }

    @Test
    public void selectProjectItemWithoutFrom() throws IOException {
        Schema select = project(valuesSchema(fields(fieldType("1", "int"), fieldType("2", "string")), values()), "2", "1");
        Assert.assertEquals("ProjectSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)]), alias=[2, 1], fieldSchemaList=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)])", select.toString());
    }

    @Test
    public void selectProjectItemWithoutFrom2() throws IOException {
        String text = "distinct(valuesSchema(fields(fieldType(id,int)),values()))";
        ParseNode expression = getParseNode(text);
        Assert.assertEquals(text, expression.toString());
        String s = DesRelNodeHandler.syntaxAstToFlatSyntaxAstText(expression);
        Assert.assertEquals("distinct(valuesSchema(fields(fieldType(id(\"id\"),id(\"int\"))),values()))", s);

        Schema select = project(valuesSchema(fields(fieldType("id", "int"), fieldType("2", "string")), values()), "2", "1");
        Assert.assertEquals("ProjectSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int), FieldSchema(id=id, type=string)]), alias=[2, 1], fieldSchemaList=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)])", select.toString());
    }

    @Test
    public void from() throws IOException {
        Schema select = from("db1", "travelrecord");
        Assert.assertEquals("FromSchema(names=[db1, travelrecord])", select.toString());
    }

    @Test
    public void selectProjectFrom() throws IOException {
        Schema select = project(from("db1", "travelrecord"), "1");
        Assert.assertEquals("ProjectSchema(schema=FromSchema(names=[db1, travelrecord]), alias=[1], fieldSchemaList=[])", select.toString());
    }

    @Test
    public void selectUnionAll() throws IOException {
        Schema select = unionAll(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=UNION_ALL,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectUnionDistinct() throws IOException {
        Schema select = unionDistinct(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=UNION_DISTINCT,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectExceptDistinct() throws IOException {
        Schema select = exceptDistinct(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=EXCEPT_DISTINCT,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectExceptAll() throws IOException {
        Schema select = exceptAll(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=EXCEPT_ALL,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectMinusAll() throws IOException {
        Schema select = minusAll(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=MINUS_ALL,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectMinusDistinct() throws IOException {
        Schema select = minusDistinct(from("db1", "travelrecord"), from("db1", "travelrecord"));
        Assert.assertEquals("SetOpSchema(op=MINUS_DISTINCT,list=[FromSchema(names=[db1, travelrecord]), FromSchema(names=[db1, travelrecord])])", select.toString());
    }

    @Test
    public void selectFromOrder() throws IOException {
        Schema schema = orderBy(from("db1", "travelrecord"), order("id", "ASC"), order("user_id", "DESC"));
        Assert.assertEquals("OrderSchema(schema=FromSchema(names=[db1, travelrecord]), orders=[OrderItem(columnName=Identifier(value=id), direction=ASC), OrderItem(columnName=Identifier(value=user_id), direction=DESC)])", schema.toString());
    }

    @Test
    public void selectFromLimit() throws IOException {
        Schema schema = limit(from("db1", "travelrecord"), 0, 1000);
        Assert.assertEquals("LimitSchema(schema=FromSchema(names=[db1, travelrecord]), offset=Literal(value=0), limit=Literal(value=1000))", schema.toString());
    }

    @Test
    public void selectFromGroupByKey() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyAvg() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(avg("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='avg', alias='avg(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyCount() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(count("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='count', alias='count(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyCountStar() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(count("*")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='count', alias='count(*)', operands=[Identifier(value=*)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyCountDistinct() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(countDistinct("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='countDistinct', alias='count(distinct id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyFirst() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(first("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='first', alias='first(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyLast() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(last("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='last', alias='last(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyMax() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(max("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='max', alias='max(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectFromGroupByKeyMin() throws IOException {
        Schema schema = group(from("db1", "travelrecord"), keys(regular(id("id"))), aggregating(min("id")));
        Assert.assertEquals("GroupSchema(schema=FromSchema(names=[db1, travelrecord]), keys=[GroupItem(exprs=[Identifier(value=id)])], exprs=[AggregateCall(function='min', alias='min(id)', operands=[Identifier(value=id)]])", schema.toString());
    }

    @Test
    public void selectUcaseFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), ucase("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[ucase(Identifier(value=id))])", schema.toString());
    }

    @Test
    public void selectUpperFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), upper("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[upper(Identifier(value=id))])", schema.toString());
    }

    @Test
    public void selectLcaseFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), lcase("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[lcase(Identifier(value=id))])", schema.toString());
    }

    @Test
    public void selectLowerFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), lower("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[lower(Identifier(value=id))])", schema.toString());
    }

    @Test
    public void selectMidFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), mid("id", 1));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[mid(Identifier(value=id),Literal(value=1))])", schema.toString());
    }

    @Test
    public void selectMidFrom2() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), mid("id", 1, 3));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[mid(Identifier(value=id),Literal(value=1),Literal(value=3))])", schema.toString());
    }

    @Test
    public void selectLenFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), len("id"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[len(Identifier(value=id))])", schema.toString());
    }

    @Test
    public void selectRoundFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), round("id", 2));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[round(Identifier(value=id),Literal(value=2))])", schema.toString());
    }

    @Test
    public void selectNowFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), now());
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[now()])", schema.toString());
    }

    @Test
    public void selectFormatFrom() throws IOException {
        Schema schema = map(from("db1", "travelrecord"), format(now(), "YYYY-MM-DD"));
        Assert.assertEquals("MapSchema(schema=FromSchema(names=[db1, travelrecord]), expr=[format(now(),Literal(value=YYYY-MM-DD))])", schema.toString());
    }

    @Test
    public void filterIn() throws IOException {
        Schema schema = filter(from("db1", "travelrecord"), in("id", 1, 2));
        Assert.assertEquals("FilterSchema(schema=FromSchema(names=[db1, travelrecord]), exprs=[OR(EQ(Identifier(value=id),Literal(value=1)),EQ(Identifier(value=id),Literal(value=2)))])", schema.toString());
    }

    @Test
    public void filterBetween() throws IOException {
        Schema schema = filter(from("db1", "travelrecord"), between("id", 1, 2));
        Assert.assertEquals("FilterSchema(schema=FromSchema(names=[db1, travelrecord]), exprs=[AND(LTE(Literal(value=1),Identifier(value=id)),GTE(Identifier(value=id),Literal(value=2)))])", schema.toString());
    }

    @Test
    public void testIsnull() throws IOException {
        Expr expr = isnull("id");
        Assert.assertEquals("isnull(Identifier(value=id))", expr.toString());
    }

    @Test
    public void testIfnull() throws IOException {
        Expr expr = ifnull("id", "default");
        Assert.assertEquals("ifnull(Identifier(value=id),Literal(value=default))", expr.toString());
    }

    @Test
    public void testNullif() throws IOException {
        Expr expr = nullif("id", "default");
        Assert.assertEquals("nullif(Identifier(value=id),Literal(value=default))", expr.toString());
    }

    @Test
    public void testIsNotNull() throws IOException {
        Expr expr = isnotnull("id");
        Assert.assertEquals("isnotnull(Identifier(value=id))", expr.toString());
    }

    @Test
    public void testInteger() throws IOException {
        Expr expr = literal(1);
        Assert.assertEquals("Literal(value=1)", expr.toString());
    }

    @Test
    public void testLong() throws IOException {
        Expr expr = literal(1L);
        Assert.assertEquals("Literal(value=1)", expr.toString());
    }

    @Test
    public void testFloat() throws IOException {
        Expr expr = literal(Float.MAX_VALUE);
        Assert.assertEquals("Literal(value=3.4028234663852886E+38)", expr.toString());
    }

    @Test
    public void testId() throws IOException {
        Expr expr = id("id");
        Assert.assertEquals("Identifier(value=id)", expr.toString());
    }

    @Test
    public void testString() throws IOException {
        Expr expr = literal("str");
        Assert.assertEquals("Literal(value=str)", expr.toString());
    }


    @Test
    public void testAdd() throws IOException {
        Expr expr = plus(id("id"), literal(1));
        Assert.assertEquals("PLUS(Identifier(value=id),Literal(value=1))", expr.toString());
    }

    @Test
    public void testMinus() throws IOException {
        Expr expr = minus(id("id"), literal(1));
        Assert.assertEquals("MINUS(Identifier(value=id),Literal(value=1))", expr.toString());
    }

    @Test
    public void testEqual() throws IOException {
        Expr expr = eq(id("id"), literal(1));
        Assert.assertEquals("EQ(Identifier(value=id),Literal(value=1))", expr.toString());
    }

    @Test
    public void testAnd() throws IOException {
        Expr expr = and(literal(true), literal(true));
        Assert.assertEquals("AND(Literal(value=true),Literal(value=true))", expr.toString());
    }

    @Test
    public void testOr() throws IOException {
        Expr expr = or(literal(true), literal(true));
        Assert.assertEquals("OR(Literal(value=true),Literal(value=true))", expr.toString());
    }

    @Test
    public void testNot() throws IOException {
        Expr expr = not(literal(true));
        Assert.assertEquals("NOT(Literal(value=true))", expr.toString());
    }

    @Test
    public void testNotEqual() throws IOException {
        Expr expr = ne(id("id"), literal(true));
        Assert.assertEquals("NE(Identifier(value=id),Literal(value=true))", expr.toString());
    }

    @Test
    public void testGreaterThan() throws IOException {
        Expr expr = gt(id("id"), literal(true));
        Assert.assertEquals("GT(Identifier(value=id),Literal(value=true))", expr.toString());
    }

    @Test
    public void testGreaterThanEqual() throws IOException {
        Expr expr = gte(id("id"), literal(true));
        Assert.assertEquals("GTE(Identifier(value=id),Literal(value=true))", expr.toString());
    }

    @Test
    public void testLessThan() throws IOException {
        Expr expr = lt(id("id"), literal(true));
        Assert.assertEquals("LT(Identifier(value=id),Literal(value=true))", expr.toString());
    }

    @Test
    public void testLessThanEqual() throws IOException {
        Expr expr = lte(id("id"), literal(true));
        Assert.assertEquals("LTE(Identifier(value=id),Literal(value=true))", expr.toString());
    }

    @Test
    public void testDot() throws IOException {
        Expr expr = dot(id("table"), id("column"));
        Assert.assertEquals("DOT(Identifier(value=table),Identifier(value=column))", expr.toString());
    }

    @Test
    public void testAsColumnName() throws IOException {
        Expr expr = as(literal(1), id("column"));
        Assert.assertEquals("AS_COLUMNNAME(Literal(value=1),Identifier(value=column))", expr.toString());
    }

    @Test
    public void testAsTableName() throws IOException {
        Schema schema = as(from("db1", "table"), id("table2"));
        Assert.assertEquals("AsTable(schema=FromSchema(names=[db1, table]), alias=table2)", schema.toString());
    }

    @Test
    public void testCast() throws IOException {
        Expr expr = cast(literal(1), id("float"));
        Assert.assertEquals("CAST(Literal(value=1),Identifier(value=float))", expr.toString());
    }

    @Test
    public void testInnerJoin() throws IOException {
        Schema schema = innerJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=INNER_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testLeftJoin() throws IOException {
        Schema schema = leftJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=LEFT_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testRightJoin() throws IOException {
        Schema schema = rightJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=RIGHT_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testFullJoin() throws IOException {
        Schema schema = fullJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=FULL_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testSemiJoin() throws IOException {
        Schema schema = semiJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=SEMI_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testAntiJoin() throws IOException {
        Schema schema = antiJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=ANTI_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testCorrelateInnerJoin() throws IOException {
        Schema schema = correlateInnerJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=CORRELATE_INNER_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

    @Test
    public void testCorrelateLeftJoin() throws IOException {
        Schema schema = correlateLeftJoin(eq(id("table", "id"), id("table2", "id")), from("db1", "table"), from("db1", "table2"));
        Assert.assertEquals("JoinSchema(type=CORRELATE_LEFT_JOIN, schemas=[FromSchema(names=[db1, table]), FromSchema(names=[db1, table2])], condition=EQ(Property(value=[table, id]),Property(value=[table2, id])))", schema.toString());
    }

}