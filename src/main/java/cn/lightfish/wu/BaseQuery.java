package cn.lightfish.wu;

import cn.lightfish.wu.ast.AggregateCall;
import cn.lightfish.wu.ast.Direction;
import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.base.*;
import cn.lightfish.wu.ast.query.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public static Schema map(Schema table, Expr... id) {
        return new MapSchema(table, Arrays.asList(id));
    }

    public static Schema map(Schema table, List<Expr> ids) {
        return new MapSchema(table, ids);
    }

    public static Schema map(Schema table, String... id) {
        return new MapSchema(table, Arrays.asList(id).stream().map(i -> new Identifier(i)).collect(Collectors.toList()));
    }

    public static List<Literal> values(Object... values) {
        return Arrays.stream(values).map(i -> literal(i)).collect(Collectors.toList());
    }

    public static List<Literal> values(Literal... values) {
        return Arrays.asList(values);
    }

    public static LocalDateTime timeStamp(String s) {
        return LocalDateTime.parse(s);
    }

    public static Literal timeStamp(Literal s) {
        return literal(timeStamp((String) s.getValue()));
    }

    public static Literal timeLiteral(String s) {
        return literal(time(s));
    }

    public static LocalTime time(String s) {
        return LocalTime.parse(s);
    }

    public static Literal time(Literal s) {
        return literal(LocalTime.parse((String) s.getValue()));
    }

    public static Literal timeStampLiteral(String s) {
        return literal(timeStamp(s));
    }

    public static Literal dateLiteral(String s) {
        return literal(date(s));
    }

    public static LocalDate date(String s) {
        return LocalDate.parse(s);
    }

    public static Literal date(Literal s) {
        return literal(date((String) s.getValue()));
    }

    public static List<FieldType> fields(FieldType... fields) {
        return Arrays.asList(fields);
    }

    public static ValuesSchema valuesSchema(List<FieldType> fields, List<Literal> values) {
        return new ValuesSchema(fields, values);
    }

    public static List<Node> schemaValues(Node... values) {
        return Arrays.asList(values);
    }

    public Schema limit(Schema from, long offset, long limit) {
        return new LimitSchema(from, new Literal(offset), new Literal(limit));
    }

//    public Schema minusAll(Schema from, Schema... from1) {
//        return new SetOpSchema(Op.MINUS_ALL, list(from, from1));
//    }
//
//    public Schema minusDistinct(Schema from, Schema... from1) {
//        return new SetOpSchema(Op.MINUS_DISTINCT, list(from, from1));
//    }

    public static List<Expr> groupKeys(Expr... columnNames) {
        return list(columnNames);
    }

    public Schema exceptAll(Schema from, Schema... from1) {
        return exceptAll(from, list(from1));
    }

    public Schema exceptAll(Schema from, List<Schema> from1) {
        return new SetOpSchema(Op.EXCEPT_ALL, list(from, from1));
    }

    public Schema exceptDistinct(Schema schema, Schema... froms) {
        return exceptDistinct(schema, list(froms));
    }

    public Schema orderBy(Schema from, OrderItem... orderColumns) {
        return new OrderSchema(from, Arrays.asList(orderColumns));
    }

    public Schema orderBy(Schema from, List<OrderItem> orderColumns) {
        return new OrderSchema(from, orderColumns);
    }

    public OrderItem order(String columnName, String direction) {
        return new OrderItem(new Identifier(columnName), Direction.parse(direction));
    }

    public List<OrderItem> orderKeys(OrderItem... items) {
        return Arrays.asList(items);
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

    public static Literal literal(Object value) {
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

    public static Expr dot(String t, String id) {
        return dot(new Identifier(t), new Identifier(id));
    }


    public static Expr or(Expr left, Expr right) {
        return funWithSimpleAlias("or", left, right);
    }

    public static Identifier id(String value) {
        return new Identifier(value);
    }

    public static Expr id(String schema, String table) {
        return dot(new Identifier(schema), new Identifier(table));
    }

    public static FieldType fieldType(Identifier fieldName, Identifier type) {
        return new FieldType(fieldName.getValue(), type.getValue());
    }

    public static FieldType fieldType(String fieldName, String type) {
        return new FieldType(fieldName, type);
    }


    public static FromSchema from(Identifier... names) {
        return from(list(names));
    }

    public static FromSchema from(List<Identifier> names) {
        return new FromSchema(names);
    }

    public static FromSchema from(String... names) {
        return from(Arrays.stream(names).map(i -> id(i)).collect(Collectors.toList()));
    }

    public static <T> List<T> list(T... schema) {
        return Arrays.asList(schema);
    }

    public Schema unionDistinct(Schema schema, Schema... froms) {
        return new SetOpSchema(Op.UNION_DISTINCT, list(schema, froms));
    }

    public static AggregateCall callWithAlias(String function, String alias, String... columnNames) {
        return call(function, alias, Arrays.stream(columnNames).map(i -> id(i)).collect(Collectors.toList()));
    }
    public static Expr eq(Expr left, Expr right) {
        return funWithSimpleAlias("eq", left, right);
    }

    public static Expr dot(Expr left, Expr right) {
        return new Expr(Op.DOT, left, right);
    }

    public static Expr ne(Expr left, Expr right) {
        return funWithSimpleAlias("ne", left, right);
    }

    public static Expr gt(Expr left, Expr right) {
        return funWithSimpleAlias("gt", left, right);
    }

    public static Expr gte(Expr left, Expr right) {
        return funWithSimpleAlias("gte", left, right);
    }

    public static Expr lt(Expr left, Expr right) {
        return funWithSimpleAlias("lt", left, right);
    }

    public static Expr lte(Expr left, Expr right) {
        return funWithSimpleAlias("lte", left, right);
    }

    public static Expr and(Expr left, Expr right) {
        return funWithSimpleAlias("and", left, right);
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

    public static Expr not(Expr value) {
        return funWithSimpleAlias("not", value);
    }

    public <T> List<T> list(T schema, List<T> froms) {
        ArrayList<T> objects = new ArrayList<>(froms.size() + 1);
        objects.add(schema);
        objects.addAll(froms);
        return objects;
    }

    public <T> List<T> list(List<T> schema, List<T> froms) {
        ArrayList<T> objects = new ArrayList<>(froms.size() + 1);
        objects.addAll(schema);
        objects.addAll(froms);
        return objects;
    }
    public static Expr plus(Expr left, Expr right) {
        return funWithSimpleAlias("plus", left, right);
    }

    public static Expr minus(Expr left, Expr right) {
        return funWithSimpleAlias("minus", left, right);
    }


    public static void describe(Schema table) {
        describe(table, System.out);
    }

    public static void describe(Schema table, PrintStream out) {
        out.print('[');
        for (FieldType field : table.fields()) {
            out.print(field.getId());
            out.print(':');
            out.print(field.getType());
        }
        out.print(']');
    }

    public static Schema filter(Schema asSchema, Expr expr) {
        return new FilterSchema(asSchema, expr);
    }


    public Schema projectNamed(Schema schema, List<Identifier> alias) {
        return new ProjectSchema(schema, alias.stream().map(i -> i.getValue()).collect(Collectors.toList()));
    }

    public Schema projectNamed(Schema schema, String... alias) {
        return projectNamed(schema, Arrays.stream(alias).map(i -> id(i)).collect(Collectors.toList()));
    }

    public static AggregateCall call(String function, String... columnNames) {
        return call(function, Arrays.stream(columnNames).map(i -> literal(i)).collect(Collectors.toList()));
    }

    public static AggregateCall call(String function, Expr... columnNames) {
        return call(function, list(columnNames));
    }

    public static List<Node> tuple(Node... values) {
        return Arrays.asList(values);
    }


    public AggregateCall avg(String columnName) {
        return callWithSimpleAlias("avg", columnName);
    }

    public AggregateCall count() {
        return callWithSimpleAlias("count");
    }

    public AggregateCall count(String columnName) {
        return callWithSimpleAlias("count", columnName);
    }

    public static AggregateCall call(String function, List<Expr> columnNames) {
        return call(function, null, columnNames);
    }

    public static AggregateCall call(String function, String alias, List<Expr> nodes, boolean distinct, boolean approximate, boolean ignoreNulls, Expr filter, List<OrderItem> orderKeys) {
        return new AggregateCall(function, alias, nodes, distinct, approximate, ignoreNulls, filter, orderKeys);
    }

    public Schema unionDistinct(Schema schema, List<Schema> froms) {
        return new SetOpSchema(Op.UNION_DISTINCT, list(schema, froms));
    }

    public Schema unionAll(Schema... froms) {
        return unionAll(froms[0], Arrays.asList(froms).subList(1, froms.length));
    }

    public Schema unionAll(Schema from, List<Schema> schemas) {
        return new SetOpSchema(Op.UNION_ALL, list(from, schemas));
    }

    public static AggregateCall call(String function, String alias, Expr... columnNames) {
        return call(function, alias, list(columnNames));
    }

    public Schema exceptDistinct(Schema schema, List<Schema> froms) {
        return new SetOpSchema(Op.EXCEPT_DISTINCT, list(schema, froms));
    }

    public static AggregateCall call(String function, String alias, List<Expr> nodes) {
        return new AggregateCall(function, alias, nodes, null, null, null, null, null);
    }

    public AggregateCall callWithSimpleAlias(String function, String... columnNames) {
        return callWithAlias(function, function + "(" + String.join(",", Arrays.asList(columnNames)) + ")", columnNames);
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

    public static Expr funWithSimpleAlias(String fun, Expr... nodes) {
        return funWithSimpleAlias(fun, list(nodes));
    }

    public List<GroupItem> keys(GroupItem... keys) {
        return list(keys);
    }

    public List<AggregateCall> aggregating(AggregateCall... keys) {
        return list(keys);
    }

    public static Expr funWithSimpleAlias(String fun, List<Expr> nodes) {
        return new Fun(fun, fun + "(" + nodes.stream().map(i -> i.toString()).collect(Collectors.joining(",")) + ")", nodes);
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

    public GroupItem regular(Expr... nodes) {
        return regular(list(nodes));
    }

    public Expr in(String column, Object... values) {
        return in(column, literal(values[0]), Arrays.stream(values).map(i -> literal(i)).collect(Collectors.toList()));
    }

    public GroupItem regular(List<Expr> nodes) {
        return new GroupItem(Op.REGULAR, nodes);
    }

    public Expr between(Expr column, Expr start, Expr end) {
        return and(gte(column, start), lte(column, end));
    }

    public Expr in(Identifier column, Expr... values) {
        return in(column, values[0], Arrays.asList(values).subList(1, values.length));
    }

    public Expr in(String column, Expr... values) {
        return in(column, values[0], Arrays.asList(values).subList(1, values.length));
    }

    public Expr now() {
        return funWithSimpleAlias("now", Collections.emptyList());
    }

    public Expr format(String... columnNames) {
        return format(new Identifier(columnNames[0]), new Identifier(columnNames[1]));
    }

    public Expr in(String column, Expr value, List<Expr> values) {
        return in(new Identifier(column), value, values);
    }

    public Expr in(Expr column, Expr value, List<Expr> values) {
        if (values.isEmpty()) {
            return eq(column, value);
        } else {
            return or(eq(column, value), values.stream().map(i -> eq(column, i)).collect(Collectors.toList()));
        }
    }

    public Expr format(Expr... nodes) {
        return format(list(nodes));
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

    public Expr format(Expr node, String format) {
        return format(node, new Literal(format));
    }

    public Expr len(String columnName) {
        return len(new Identifier(columnName));
    }

    public Expr format(List<Expr> nodes) {
        return funWithSimpleAlias("format", nodes);
    }

    public Expr round(String column, int decimals) {
        return round(new Identifier(column), new Literal(decimals));
    }

    public Expr mid(Expr... start) {
        return funWithSimpleAlias("mid", start);
    }

    public Expr funWithSimpleAlias(String fun, String... columnNames) {
        return fun(fun, fun + "(" + String.join(",", Arrays.asList(columnNames)) + ")", columnNames);
    }

    public Expr fun(String fun, String alias, String... nodes) {
        return fun(fun, alias, Arrays.stream(nodes).map(i -> id(i)).collect(Collectors.toList()));
    }

    public Expr len(Expr... column) {
        return funWithSimpleAlias("len", column);
    }

    public Expr round(Expr... column) {
        return funWithSimpleAlias("round", column);
    }

    public Expr fun(String fun, String alias, List<Expr> nodes) {
        return new Fun(fun, alias, nodes);
    }

    public AggregateCall countDistinct(String columnName) {
        return callWithAlias("countDistinct", "count(distinct " + columnName + ")", columnName);
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

    public Schema correlate(Schema from) {
        return new CorrelateSchema(from);
    }

    public Schema correlateInnerJoin(Expr expr, List<Schema> froms) {
        return join(Op.CORRELATE_INNER_JOIN, expr, froms);
    }

    public Schema correlateInnerJoin(Schema... froms) {
        return correlateInnerJoin(list(froms));
    }

    public Schema correlateInnerJoin(List<Schema> froms) {
        return join(Op.CORRELATE_INNER_JOIN, null, froms);
    }

    public Schema correlateLeftJoin(Expr expr, Schema... froms) {
        return correlateLeftJoin(expr, list(froms));
    }

    public Schema correlateLeftJoin(Expr expr, List<Schema> froms) {
        return join(Op.CORRELATE_LEFT_JOIN, expr, froms);
    }

    public Schema innerJoin(Expr expr, FromSchema... from) {
        return innerJoin(expr, list(from));
    }


    public List<AsTable> as(List<FromSchema> froms) {
        return froms.stream().map(i -> as(i, i.getNames().get(i.getNames().size() - 1))).collect(Collectors.toList());
    }

    public Schema innerJoin(Expr expr, Schema... from) {
        return innerJoin(expr, list(from));
    }

    public Schema innerJoin(Expr expr, List<Schema> from) {
        return join(Op.INNER_JOIN, expr, from);
    }

    @NotNull
    public Schema join(Op type, Expr expr, List<Schema> froms) {
        for (Schema from : froms) {
            if (from.getAlias() == null) {
                throw new UnsupportedOperationException();
            }
        }

        return new JoinSchema(type, froms, expr);
    }


    public Expr cast(Expr literal, Identifier type) {
        return new Expr(Op.CAST, literal, type);
    }

    public Expr as(Expr literal, String column) {
        return as(literal, id(column));
    }
    public Expr as(Expr literal, Identifier column) {
        return new Expr(Op.AS_COLUMNNAME, literal, column);
    }

    public Expr isnull(String columnName) {
        return isnull(new Identifier(columnName));
    }

    public Expr isnull(Expr columnName) {
        return funWithSimpleAlias("isnull", columnName);
    }

    public Expr ifnull(String columnName, Object value) {
        return ifnull(new Identifier(columnName), literal(value));
    }

    public Expr ifnull(Expr columnName, Expr value) {
        return funWithSimpleAlias("ifnull", columnName, value);
    }

    public Expr isnotnull(String columnName) {
        return isnotnull(id(columnName));
    }

    public Expr isnotnull(Expr columnName) {
        return funWithSimpleAlias("isnotnull", columnName);
    }

    public Expr nullif(String columnName, Object value) {
        return nullif(id(columnName), literal(value));
    }

    public Expr nullif(Expr columnName, Expr value) {
        return funWithSimpleAlias("nullif", columnName, value);
    }

    public AggregateCall distinct(AggregateCall aggregateCall) {
        return aggregateCall.distinct();
    }

    public AggregateCall all(AggregateCall aggregateCall) {
        return aggregateCall.all();
    }

    public AggregateCall approximate(AggregateCall aggregateCall) {
        return aggregateCall.approximate(true);
    }

    public AggregateCall exact(AggregateCall aggregateCall) {
        return aggregateCall.approximate(false);
    }

    public AggregateCall as(AggregateCall aggregateCall, String alias) {
        return aggregateCall.as(alias);
    }

    public AggregateCall as(AggregateCall aggregateCall, Identifier alias) {
        return as(aggregateCall, alias.getValue());
    }

    public AggregateCall filter(AggregateCall aggregateCall, Expr node) {
        return aggregateCall.filter(node);
    }

    public AggregateCall sort(AggregateCall aggregateCall, OrderItem... orderKeys) {
        return sort(aggregateCall, list(orderKeys));
    }

    public AggregateCall sort(AggregateCall aggregateCall, List<OrderItem> orderKeys) {
        return aggregateCall.sort(orderKeys);
    }

    public AggregateCall checkNulls(AggregateCall aggregateCall) {
        return aggregateCall.ignoreNulls(false);
    }

    public AggregateCall ignoreNulls(AggregateCall aggregateCall) {
        return aggregateCall.ignoreNulls(true);
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