package cn.lightfish.wu;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ast {

    public static AsNode as(Node expr, String alias) {
        return new AsNode(expr, alias);
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

    public static Schema select(Schema table, AsNode... id) {
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

    public static Schema as(Schema table, FieldSchema... fieldSchema) {
        return new AsSchema(table, Arrays.asList(fieldSchema));
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

    public static Values values(Node... values) {
        return new Values(values);
    }

    public static void main(String[] args) {

    }

    public void run() {
        Schema table = from("db1", "table");
        table = as(table, fieldType("id", "string"));
        describe(table);
        table = filter(table, eq(id("id"), literal(1)));
        table = select(table, as(plus(id("id"), literal(1)), "id"));
        Values values = values(plus(literal(1), literal(1)), literal(1),
                literal(1), literal(2), literal(3));
        Schema as = as(values, fieldType("id1", "string"), fieldType("id2", "string"), fieldType("id3", "string"));

    }

    private Schema as(Values values, FieldSchema... fieldType) {
        return new ValueSchema(values, Arrays.asList(fieldType));
    }

    private List<Node> tuple(Node... values) {
        return Arrays.asList(values);
    }

    public enum Op {
        PROGRAM,

        //relational operators
        FROM, MAP, FILTER, LIMIT, ORDER, GROUP, VALUES, DISTINCT, JOIN,

        // types
        SCHEMA, SCALAR_TYPE, FIELD_SCHEMA, AS_SCHEMA,

        //atoms
        LITERAL, IDENTIFIER,

        //debug
        DESCRIBE, DUMP,

        // operators
        DOT, EQ, NE, GT, LT, GTE, LTE, PLUS, MINUS, AND, OR, NOT, AS
    }

    @Data
    @AllArgsConstructor
    public static class AsNode {
        Node expr;
        String alias;
    }

    @Data
    public static class FieldSchema extends Node {
        final String id;
        final String type;

        public FieldSchema(String id, String type) {
            super(Op.FIELD_SCHEMA);
            this.id = id;
            this.type = type;
        }
    }

    @Data
    public static class Literal extends Node {
        final Object value;

        public Literal(Object value) {
            super(Op.LITERAL);
            this.value = value;
        }
    }

    @Data
    public static class Identifier extends Node {
        final String value;

        public Identifier(String value) {
            super(Op.IDENTIFIER);
            this.value = value;
        }
    }

    @Data
    public static class Expr extends Node {
        Node left;
        Node right;

        public Expr(Op op, Node left, Node right) {
            super(op);
            this.left = left;
            this.right = right;
        }
    }

    @Data
    public static abstract class Schema extends Node {
        public Schema(Op op) {
            super(op);
        }

        public abstract List<FieldSchema> fields();

    }

    @Data
    public static class FromSchema extends Schema {
        private String[] names;

        public FromSchema(String... names) {
            super(Op.FROM);
            this.names = names;
        }

        @Override
        public List<FieldSchema> fields() {
            throw new UnsupportedOperationException();
        }
    }

    @Data
    public static class Values extends Node {
        private final List<Node> values;

        public Values(List<Node> values) {
            super(Op.VALUES);
            this.values = values;
        }

        public Values(Node... values) {
            this(Arrays.asList(values));
        }
    }

    public static class AsSchema extends Schema {
        private final Schema schema;
        private final List<FieldSchema> fields;

        public AsSchema(Schema schema, List<FieldSchema> fields) {
            super(Op.AS_SCHEMA);
            this.schema = schema;
            this.fields = fields;
        }

        public Schema getSchema() {
            return schema;
        }

        public List<FieldSchema> getFields() {
            return fields;
        }

        @Override
        public List<FieldSchema> fields() {
            return Collections.unmodifiableList(fields);
        }
    }

    public static class FilterSchema extends Schema {
        private final Schema schema;
        private final Node expr;

        public FilterSchema(Schema schema, Node expr) {
            super(Op.FILTER);
            this.schema = schema;
            this.expr = expr;
        }

        @Override
        public List<FieldSchema> fields() {
            return Collections.unmodifiableList(schema.fields());
        }

        public Schema getSchema() {
            return schema;
        }

        public Node getExpr() {
            return expr;
        }
    }

    public static class MapSchema extends Schema {
        private final Schema schema;
        private final List<AsNode> expr;

        public MapSchema(Schema schema, List<AsNode> expr) {
            super(Op.MAP);
            this.schema = schema;
            this.expr = expr;
        }

        @Override
        public List<FieldSchema> fields() {
            return Collections.unmodifiableList(schema.fields());
        }

        public List<AsNode> getExpr() {
            return expr;
        }

        /**
         * Getter for property 'schema'.
         *
         * @return Value for property 'schema'.
         */
        public Schema getSchema() {
            return schema;
        }
    }


    public static abstract class Node {
        public final Op op;

        public Node(Op op) {
            this.op = op;
        }

        public Op getOp() {
            return op;
        }
    }
}