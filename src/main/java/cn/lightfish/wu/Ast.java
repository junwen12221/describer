package cn.lightfish.wu;

import cn.lightfish.wu.ast.*;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class Ast {

    public static Node as(Node expr, String alias) {
        return new Expr(Op.AS_COLUMNNAME, expr, new Identifier(alias));
    }

    public static Node literal(Object value) {
        return new Literal(value);
    }

    public static Node id(String value) {
        return new Identifier(value);
    }

    public static FieldSchema fieldType(String fieldName, String type) {
        return new FieldSchema(fieldName, type);
    }

    public static Schema from(String... names) {
        return new FromSchema(names);
    }

    public static Schema select(Schema table, Node... id) {
        return new MapSchema(table, Arrays.asList(id));
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

    public static void main(String[] args) {

    }

    public void run() {

//        Schema table = from("db1", "table");
//        table = as(table, fieldType("ID", "string"));
//        describe(table);
//        table = filter(table, eq(id("ID"), literal(1)));
//        table = select(table, as(plus(id("ID"), literal(1)), "ID"));
//        Values values = values(plus(literal(1), literal(1)), literal(1),
//                literal(1), literal(2), literal(3));
//        Schema as = as(values, fieldType("id1", "string"), fieldType("id2", "string"), fieldType("id3", "string"));

    }

    private Schema as(ValuesSchema values, FieldSchema... fieldType) {
        return new ValueSchema(values, Arrays.asList(fieldType));
    }

    private List<Node> tuple(Node... values) {
        return Arrays.asList(values);
    }







}