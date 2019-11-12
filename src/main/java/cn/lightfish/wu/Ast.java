package cn.lightfish.wu;

import cn.lightfish.wu.ast.as.AsTable;
import cn.lightfish.wu.ast.base.*;
import cn.lightfish.wu.ast.query.*;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Ast {
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

    public static Schema select(Schema table, Node... id) {
        return new MapSchema(table, Arrays.asList(id));
    }

    public static Schema select(Schema table, String... id) {
        return new MapSchema(table, Arrays.asList(id).stream().map(i -> new Identifier(i)).collect(Collectors.toList()));
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

    public static ValuesSchema values(List<FieldSchema> fieldNames, Node... values) {
        return new ValuesSchema(fieldNames, values);
    }

    public static Schema as(ValuesSchema values, FieldSchema... fieldType) {
        return new ValueSchema(values, Arrays.asList(fieldType));
    }

    public static List<Node> tuple(Node... values) {
        return Arrays.asList(values);
    }

    public static Property property(String table, String column) {
        return new Property(Arrays.asList(table, column));
    }

    public void run() {

//        Schema table = from("db1", "table");
//        table = as(table, fieldType("id", "string"));
//        describe(table);
//        table = filter(table, eq(id("id"), literal(1)));
//        table = select(table, as(plus(id("id"), literal(1)), "id"));
//        Values values = values(plus(literal(1), literal(1)), literal(1),
//                literal(1), literal(2), literal(3));
//        Schema as = as(values, fieldType("id1", "string"), fieldType("id2", "string"), fieldType("id3", "string"));

    }


}