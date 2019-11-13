package cn.lightfish.wu;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.Direction;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.base.*;
import cn.lightfish.wu.ast.query.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BaseQuery {
    public static Schema empty() {
        return new ValuesSchema(Collections.emptyList(), Collections.emptyList());
    }

    public static Schema all(Schema schema) {
        return schema;
    }

    public static Schema distinct(Schema schema) {
        return new DistinctSchema(schema);
    }

    public static Schema map(Schema table, Node... id) {
        return new MapSchema(table, Arrays.asList(id));
    }

    public static Schema map(Schema table, String... id) {
        return new MapSchema(table, Arrays.asList(id).stream().map(i -> new Identifier(i)).collect(Collectors.toList()));
    }

    public static List<Node> values(Node... values) {
        return Arrays.asList(values);
    }

    public static List<FieldSchema> fields(FieldSchema... fields) {
        return Arrays.asList(fields);
    }

    public static ValuesSchema valuesSchema(List<FieldSchema> fields, List<Node> values) {
        return new ValuesSchema(fields, values);
    }

    public static List<Node> schemaValues(Node... values) {
        return Arrays.asList(values);
    }

    public Schema limit(Schema from, long offset, long limit) {
        return new LimitSchema(from, new Literal(offset), new Literal(limit));
    }

    public Schema minusAll(Schema from, Schema... from1) {
        return new SetOpSchema(Op.MINUS_ALL, list(from, from1));
    }

    public Schema minusDistinct(Schema from, Schema... from1) {
        return new SetOpSchema(Op.MINUS_DISTINCT, list(from, from1));
    }

    public Schema exceptAll(Schema from, Schema... from1) {
        return new SetOpSchema(Op.EXCEPT_ALL, list(from, from1));
    }

    public Schema exceptDistinct(Schema schema, Schema... froms) {
        return new SetOpSchema(Op.EXCEPT_DISTINCT, list(schema, froms));
    }

    public Schema orderBy(Schema from, OrderItem... orderColumns) {
        return new OrderSchema(from, Arrays.asList(orderColumns));
    }

    public Schema orderBy(Schema from, List<OrderItem> orderColumns) {
        return new OrderSchema(from, orderColumns);
    }

    public OrderItem order(String columnName, String direction) {
        return new OrderItem(new Identifier(columnName), Direction.valueOf(direction));
    }

    public org.apache.calcite.util.Pair<String, Direction> order(String columnName, Direction direction) {
        return new org.apache.calcite.util.Pair<String, Direction>(columnName, direction);
    }

    @NotNull
    public <T> List<T> list(T schema, T... froms) {
        ArrayList<T> objects = new ArrayList<>(froms.length + 1);
        objects.add(schema);
        objects.addAll(Arrays.asList(froms));
        return objects;
    }

    public static AsTable set(Schema expr, String alias) {
        return new AsTable(expr, alias);
    }

    public static AsTable as(Schema expr, String alias) {
        return new AsTable(expr, alias);
    }


    public static AsTable as(Schema schema, Identifier as) {
        return as(schema, as.getValue());
    }

    public static Node dot(String t, String id) {
        return dot(new Identifier(t), new Identifier(id));
    }

    public static Node as(Node expr, String alias) {
        return new Expr(Op.AS_COLUMNNAME, expr, new Identifier(alias));
    }

    public static Node literal(Object value) {
        return new Literal(value);
    }

    public static Identifier id(String value) {
        return new Identifier(value);
    }

    public static FieldSchema fieldType(String fieldName, String type) {
        return new FieldSchema(fieldName, type);
    }

    public static Schema from(Identifier... names) {
        return new FromSchema(names[0].getValue(), names[1].getValue());
    }

    public static Schema from(String... names) {
        return new FromSchema(names);
    }

    public <T> List<T> list(T... schema) {
        return Arrays.asList(schema);
    }

    public Schema unionDistinct(Schema schema, Schema... froms) {
        return new SetOpSchema(Op.UNION_DISTINCT, list(schema, froms));
    }

    public static Expr eq(Node left, Node right) {
        return new Expr(Op.EQ, left, right);
    }

    public static Expr dot(Node left, Node right) {
        return new Expr(Op.DOT, left, right);
    }

    public static Expr ne(Node left, Node right) {
        return new Expr(Op.NE, left, right);
    }

    public static Expr gt(Node left, Node right) {
        return new Expr(Op.GT, left, right);
    }

    public static Expr gte(Node left, Node right) {
        return new Expr(Op.GTE, left, right);
    }

    public static Expr lt(Node left, Node right) {
        return new Expr(Op.LT, left, right);
    }

    public static Expr lte(Node left, Node right) {
        return new Expr(Op.LTE, left, right);
    }

    public static Expr and(Node left, Node right) {
        return new Expr(Op.AND, left, right);
    }

    public static Expr or(Node left, Node right) {
        return new Expr(Op.OR, left, right);
    }

    public static Expr not(Node left, Node right) {
        return new Expr(Op.NOT, left, right);
    }

    public static Expr plus(Node left, Node right) {
        return new Expr(Op.PLUS, left, right);
    }

    public static Expr minus(Node left, Node right) {
        return new Expr(Op.MINUS, left, right);
    }


    public static void describe(Schema table) {
        describe(table, System.out);
    }

    public static void describe(Schema table, PrintStream out) {
        out.print('[');
        for (FieldSchema field : table.fields()) {
            out.print(field.getId());
            out.print(':');
            out.print(field.getType());
        }
        out.print(']');
    }

    public static Schema filter(Schema asSchema, Expr expr) {
        return new FilterSchema(asSchema, expr);
    }

    public Schema project(Schema schema, String... alias) {
        return new ProjectSchema(schema, Arrays.asList(alias));
    }

    public Schema project(Schema schema, List<String> alias) {
        return new ProjectSchema(schema, alias);
    }

    public Schema unionAll(Schema... froms) {
        return unionAll(Arrays.asList(froms));
    }

    public Schema unionAll(List<Schema> froms) {
        return new SetOpSchema(Op.UNION_ALL, froms);
    }

    public static List<Node> tuple(Node... values) {
        return Arrays.asList(values);
    }

    public static Property property(String table, String column) {
        return new Property(Arrays.asList(table, column));
    }

    public AggregateCall avg(String columnName) {
        return callWithSimpleAlias("avg", columnName);
    }

    public AggregateCall count(String columnName) {
        return callWithSimpleAlias("count", columnName);
    }

    public AggregateCall callWithSimpleAlias(String function, String... columnNames) {
        return call(function, function + "(" + String.join(",", Arrays.asList(columnNames)) + ")", columnNames);
    }

    public AggregateCall call(String function, String alias, String... columnNames) {
        return call(function, alias, Arrays.stream(columnNames).map(i -> id(i)).collect(Collectors.toList()));
    }

    public AggregateCall call(String function, String alias, Node... columnNames) {
        return call(function, alias, list(columnNames));
    }

    public AggregateCall call(String function, String alias, List<Node> nodes) {
        return new AggregateCall(function, alias, nodes, null, null, null, null, null);
    }

    public Schema group(Schema from, List<GroupItem> groupItems) {
        return group(from, groupItems, Collections.emptyList());
    }

    public Schema group(Schema from, List<GroupItem> groupItems, List<AggregateCall> calls) {
        return new GroupSchema(from, groupItems, calls);
    }

    public GroupItem regular(String... nodes) {
        return regular(Arrays.stream(nodes).map(i -> id(i)).collect(Collectors.toList()));
    }

    public GroupItem regular(Node... nodes) {
        return regular(list(nodes));
    }

    public List<GroupItem> keys(GroupItem... keys) {
        return list(keys);
    }

    public List<AggregateCall> aggregating(AggregateCall... keys) {
        return list(keys);
    }

    public GroupItem regular(List<Node> nodes) {
        return new GroupItem(Op.REGULAR, nodes);
    }

    public AggregateCall first(String columnName) {
        return callWithSimpleAlias("first", columnName);
    }

    public AggregateCall last(String columnName) {
        return callWithSimpleAlias("last", columnName);
    }

    public AggregateCall max(String columnName) {
        return callWithSimpleAlias("max", columnName);
    }

    public AggregateCall min(String columnName) {
        return callWithSimpleAlias("min", columnName);
    }

    public AggregateCall sum(String columnName) {
        return callWithSimpleAlias("sum", columnName);
    }

//    public void run() {
//
////        Schema table = from("db1", "table");
////        table = as(table, fieldType("id", "string"));
////        describe(table);
////        table = filter(table, eq(id("id"), literal(1)));
////        table = map(table, as(plus(id("id"), literal(1)), "id"));
////        Values values = values(plus(literal(1), literal(1)), literal(1),
////                literal(1), literal(2), literal(3));
////        Schema as = as(values, fieldType("id1", "string"), fieldType("id2", "string"), fieldType("id3", "string"));
//
//    }


}