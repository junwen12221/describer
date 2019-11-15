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

    public static Expr literal(Object value) {
        return new Literal(value);
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


    public static Expr or(Expr left, Expr right) {
        return new Expr(Op.OR, left, right);
    }

    public static Identifier id(String value) {
        return new Identifier(value);
    }

    public static Property id(String schema, String table) {
        return new Property(Arrays.asList(schema, table));
    }

    public static FieldSchema fieldType(Identifier fieldName, Identifier type) {
        return new FieldSchema(fieldName.getValue(), type.getValue());
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

    public static Expr or(Expr node, List<Expr> nodes) {
        if (nodes.isEmpty()) {
            return node;
        }
        int size = nodes.size();
        if (size == 1) {
            return or(node, nodes.get(0));
        }
        Expr res = node;
        for (int i = 1; i < size; i++) {
            res = or(res, nodes.get(i));
        }
        return res;
    }

    public static Expr not(Node value) {
        return new Expr(Op.NOT, value);
    }

    public <T> List<T> list(T schema, List<T> froms) {
        ArrayList<T> objects = new ArrayList<>(froms.size() + 1);
        objects.add(schema);
        objects.addAll(froms);
        return objects;
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

    public Expr between(String column, Object start, Object end) {
        return between(new Identifier(column), literal(start), literal(end));
    }

    public Expr between(Node column, Node start, Node end) {
        return and(lte(start, column), gte(column, end));
    }

    public Expr in(String column, Object... values) {
        return in(column, literal(values[0]), Arrays.stream(values).map(i -> literal(i)).collect(Collectors.toList()));
    }

    public Expr in(String column, Node... values) {
        return in(column, values[0], Arrays.asList(values).subList(1, values.length));
    }

    public Expr in(String column, Node value, List<Node> values) {
        return in(new Identifier(column), value, values);
    }

    public Expr in(Node column, Node value, List<Node> values) {
        if (values.isEmpty()) {
            return eq(column, value);
        } else {
            return or(eq(column, value), values.stream().map(i -> eq(column, i)).collect(Collectors.toList()));
        }
    }

    public Expr now() {
        return funWithSimpleAlias("now", Collections.emptyList());
    }

    public Expr format(String... columnNames) {
        return format(new Identifier(columnNames[0]), new Identifier(columnNames[1]));
    }

    public Expr format(Node... nodes) {
        return format(list(nodes));
    }

    public Expr format(Node node, String format) {
        return format(node, new Literal(format));
    }

    public Expr format(List<Node> nodes) {
        return funWithSimpleAlias("format", nodes);
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

    public Expr mid(String columnName, long start, long limit) {
        return mid(new Identifier(columnName), new Literal(start), new Literal(limit));
    }

    public Expr mid(String columnName, long start) {
        return mid(new Identifier(columnName), new Literal(start));
    }

    public Expr mid(Node... start) {
        return funWithSimpleAlias("mid", start);
    }

    public Expr len(String columnName) {
        return len(new Identifier(columnName));
    }

    public Expr len(Node... column) {
        return funWithSimpleAlias("len", column);
    }

    public Expr round(String column, int decimals) {
        return round(new Identifier(column), new Literal(decimals));
    }

    public Expr round(Node... column) {
        return funWithSimpleAlias("round", column);
    }

    public Expr funWithSimpleAlias(String fun, String... columnNames) {
        return fun(fun, fun + "(" + String.join(",", Arrays.asList(columnNames)) + ")", columnNames);
    }

    public Expr fun(String fun, String alias, String... nodes) {
        return fun(fun, alias, Arrays.stream(nodes).map(i -> id(i)).collect(Collectors.toList()));
    }

    public Expr funWithSimpleAlias(String fun, Node... nodes) {
        return funWithSimpleAlias(fun, list(nodes));
    }

    public Expr funWithSimpleAlias(String fun, List<Node> nodes) {
        return new Fun(fun, fun + "(" + nodes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) + ")", nodes);
    }

    public Expr fun(String fun, String alias, List<Node> nodes) {
        return new Fun(fun, alias, nodes);
    }

    public AggregateCall countDistinct(String columnName) {
        return call("countDistinct", "count(distinct " + columnName + ")", columnName);
    }

    public Schema leftJoin(Expr expr, Schema... froms) {
        return leftJoin(expr, list(froms));
    }

    public Schema leftJoin(Expr expr, List<Schema> froms) {
        return join(Op.LEFT_JOIN, expr, froms);
    }


    public Schema rightJoin(Expr expr, Schema... froms) {
        return rightJoin(expr, list(froms));
    }

    public Schema rightJoin(Expr expr, List<Schema> froms) {
        return join(Op.RIGHT_JOIN, expr, froms);
    }

    public Schema fullJoin(Expr expr, Schema... froms) {
        return fullJoin(expr, list(froms));
    }

    public Schema fullJoin(Expr expr, List<Schema> froms) {
        return join(Op.FULL_JOIN, expr, froms);
    }

    public Schema semiJoin(Expr expr, Schema... froms) {
        return semiJoin(expr, list(froms));
    }

    public Schema semiJoin(Expr expr, List<Schema> froms) {
        return join(Op.SEMI_JOIN, expr, froms);
    }

    public Schema antiJoin(Expr expr, Schema... froms) {
        return antiJoin(expr, list(froms));
    }

    public Schema antiJoin(Expr expr, List<Schema> froms) {
        return join(Op.ANTI_JOIN, expr, froms);
    }

    public Schema correlateInnerJoin(Expr expr, Schema... froms) {
        return correlateInnerJoin(expr, list(froms));
    }

    public Schema correlateInnerJoin(Expr expr, List<Schema> froms) {
        return join(Op.CORRELATE_INNER_JOIN, expr, froms);
    }

    public Schema correlateLeftJoin(Expr expr, Schema... froms) {
        return correlateLeftJoin(expr, list(froms));
    }

    public Schema correlateLeftJoin(Expr expr, List<Schema> froms) {
        return join(Op.CORRELATE_LEFT_JOIN, expr, froms);
    }

    public Schema innerJoin(Expr expr, Schema... from) {
        return innerJoin(expr, list(from));
    }

    public Schema innerJoin(Expr expr, List<Schema> from) {
        return join(Op.INNER_JOIN, expr, from);
    }

    @NotNull
    private Schema join(Op type, Expr expr, List<Schema> from) {
        return new JoinSchema(type, from, expr);
    }

    public Expr cast(Expr literal, Identifier type) {
        return new Expr(Op.CAST, literal, type);
    }

    public Expr as(Expr literal, Identifier column) {
        return new Expr(Op.AS_COLUMNNAME, literal, column);
    }

    public Expr isnull(String columnName) {
        return isnull(new Identifier(columnName));
    }

    public Expr isnull(Node columnName) {
        return funWithSimpleAlias("isnull", columnName);
    }

    public Expr ifnull(String columnName, Object value) {
        return ifnull(new Identifier(columnName), literal(value));
    }

    public Expr ifnull(Node columnName, Node value) {
        return funWithSimpleAlias("ifnull", columnName, value);
    }

    public Expr isnotnull(String columnName) {
        return isnotnull(id(columnName));
    }

    public Expr isnotnull(Node columnName) {
        return funWithSimpleAlias("isnotnull", columnName);
    }

    public Expr nullif(String columnName, Object value) {
        return nullif(id(columnName), literal(value));
    }

    public Expr nullif(Node columnName, Node value) {
        return funWithSimpleAlias("nullif", columnName, value);
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