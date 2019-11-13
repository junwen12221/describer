package cn.lightfish.describer;

import cn.lightfish.wu.BaseQuery;
import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.base.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AstSpec extends BaseQuery {
    private static ExplainVisitor explainVisitor() {
        return new ExplainVisitor();
    }

    @Test
    public void selectWithoutFrom() throws IOException {
        Schema select = valuesSchema(fields(fieldType("1", "int")), values());
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)])", select.toString());
    }

    @Test
    public void selectAllWithoutFrom() throws IOException {
        Schema select = all(valuesSchema(fields(fieldType("1", "int")), values()));
        Assert.assertEquals("ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)])", select.toString());
    }

    @Test
    public void selectDistinctWithoutFrom() throws IOException {
        Schema select = distinct(valuesSchema(fields(fieldType("1", "int")), values()));
        Assert.assertEquals("DistinctSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int)]))", select.toString());
    }

    @Test
    public void selectProjectItemWithoutFrom() throws IOException {
        Schema select = project(valuesSchema(fields(fieldType("1", "int"), fieldType("2", "string")), values()), "2", "1");
        Assert.assertEquals("ProjectSchema(schema=ValuesSchema(values=[], fieldNames=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)]), alias=[2, 1], fieldSchemaList=[FieldSchema(id=1, type=int), FieldSchema(id=2, type=string)])", select.toString());
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

    public Expr ucase(String columnName) {
        return funWithSimpleAlias("ucase", columnName);
    }

    public Expr upper(String columnName) {
        return funWithSimpleAlias("upper", columnName);
    }

    public Expr lcase(String columnName) {
        return funWithSimpleAlias("lcase", columnName);
    }

    public Expr lower(String columnName) {
        return funWithSimpleAlias("lower", columnName);
    }

    public Expr funWithSimpleAlias(String fun, String... columnNames) {
        return fun(fun, fun + "(" + String.join(",", Arrays.asList(columnNames)) + ")", columnNames);
    }

    public Expr fun(String fun, String alias, String... nodes) {
        return fun(fun, alias, Arrays.stream(nodes).map(i -> id(i)).collect(Collectors.toList()));
    }

    public Expr fun(String fun, String alias, List<Node> nodes) {
        return new Fun(fun, alias, nodes);
    }

    public AggregateCall countDistinct(String columnName) {
        return call("countDistinct", "count(distinct " + columnName + ")", columnName);
    }


}