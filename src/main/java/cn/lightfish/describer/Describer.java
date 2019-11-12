package cn.lightfish.describer;

import cn.lightfish.describer.literal.*;
import com.alibaba.fastsql.sql.parser.ParserException;
import com.alibaba.fastsql.sql.parser.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Describer {

    private final Lexer lexer;
    protected Map<String, Precedence> operators;
    private final Map<String, ParseNode> variables = new LinkedHashMap<>();


    public Describer(String text) {
        this.lexer = new Lexer(text);
        this.lexer.nextToken();
        this.operators = new HashMap<>();

        ///////////////////////set/////////////////////////////////
        addOperator("union", 1, true);
        addOperator("union_all", 1, true);
        addOperator("except", 1, true);
        addOperator("except_all", 1, true);
        addOperator("intersect", 1, true);
        addOperator("intersect_all", 1, true);

        //MULTISET nothing


        ///////////////////////////logic/////////////////////////
        addOperator("or", 1, true);
        addOperator("and", 1, true);


        ////////////////////////////////as///////////////////////////
        addOperator("as", 1, true);
        addOperator("asTableAlias", 1, true);
        addOperator("asFieldAlias", 1, true);


        ///////////////////////////////object/////////////////////////.
        addOperator(".", "dot", 16, true);
        addOperator("dot", 16, true);

        addOperator("/", "divide", 1, true);
        addOperator("divide", 1, true);
        addOperator("divide_int", 1, true);

        addOperator("is_distinct_from", 1, true);
        addOperator("is_not_distinct_from", 1, true);
        addOperator("is_different_from", 1, true);

        ///////////////////////////some///////////////////////////
        addOperator("some_less_than", 1, true);
        addOperator("some_less_than_or_equal", 1, true);

        addOperator("some_great_than", 1, true);
        addOperator("some_great_than_or_equal", 1, true);

        addOperator("some_equals", 1, true);
        addOperator("some_not_equals", 1, true);
        ////////////////////////all//////////////////////////////////
        addOperator("some_equals", 1, true);
        addOperator("some_not_equals", 1, true);

        addOperator("%", "mod", 1, true);
        addOperator("mod", 1, true);

        addOperator("+", "plus", 1, true);
        addOperator("plus", 1, true);

        addOperator("-", "minus", 1, true);
        addOperator("minus", 1, true);

        addOperator("!", "not", 1, true);
        addOperator("not", "not", 1, true);

        addOperator("in", 1, true);

        addOperator("not_in", 1, true);

        addOperator("<", "less_than", 1, true);
        addOperator("less_than", 1, true);

        addOperator(">=", "greater_than", 1, true);
        addOperator("greater_than", 1, true);

        addOperator("<=", "less_than_or_equal", 1, true);
        addOperator("less_than_or_equal", 1, true);

        addOperator(">=", "greater_than_or_equal", 1, true);
        addOperator("greater_than_or_equal", 1, true);

        addOperator("=", "eq", 15, true);
        addOperator("eq", 15, true);

        addOperator("=", "equals", 15, true);
        addOperator("equals", 15, true);

        addOperator("<>", "not_equals", 15, true);
        addOperator("not_equals", 15, true);

        addOperator("JOIN", 1, true);
        addOperator("ON", 1, true);
        addOperator("AS_COLUMNNAME", 1, true);

        addOperator("WHERE", 1, true);
        addOperator("PROJECT", 1, true);
        addOperator("OR", 1, true);
        addOperator("AND", 1, true);
        addOperator("FILTER", 1, true);
        addOperator("MAP", 1, true);
        addOperator("+", "ADD", 1, true);
        addOperator("/", "SUB", 1, true);
        addOperator("AS", 1, true);
        addOperator("asTable", 1, true);
    }

    /**
     * 定义变量要求有序
     *
     * @return
     */
    public Map<String, ParseNode> getVariables() {
        return variables;
    }

    public Describer(String text, Map<String, Precedence> operators) {
        this.lexer = new Lexer(text);
        this.lexer.nextToken();
        this.operators = operators;
    }

    public void addOperator(String op, int value, boolean leftAssoc) {
        addOperator(op, op, value, leftAssoc);
    }

    public void addOperator(String op, String opText, int value, boolean leftAssoc) {
        operators.put(op, new Precedence(opText, value, leftAssoc));
        operators.put(opText, new Precedence(opText, value, leftAssoc));
    }

    private String getOp() {
        String op = lexer.tokenString();
        Precedence precedence = operators.get(op);
        if (precedence != null) {
            op = precedence.opText;
        }
        return op;
    }


    private ParseNode statement() {
        if (lexer.identifierEquals("LET")) {
            lexer.nextToken();
            String varName = lexer.stringVal();
            lexer.nextToken();
            if (Token.EQ == lexer.token()) {
                lexer.nextToken();
                try {
                    return new Bind(varName, expression());
                } finally {
                    if (lexer.token() == Token.SEMI) {
                        lexer.nextToken();
                    }
                }
            } else {
                throw new RuntimeException("");
            }
        }
        throw new RuntimeException("unknown token:" + lexer.info());
    }

    public ParseNode expression() {
        ParseNode right = primary();
        Precedence next = null;
        while ((next = operators.get(lexer.tokenString())) != null) {
            right = doShift(right, next.value);
        }
        return right;
    }

    private ParseNode doShift(ParseNode left, int prec) {
        String op = getOp();
        lexer.nextToken();
        ParseNode right = primary();
        Precedence next = null;
        while ((next = operators.get(lexer.tokenString())) != null && rightIsExpr(prec, next)) {
            right = doShift(right, next.value);
        }
        return new CallExpr(op, new ParenthesesExpr(left, right));
    }

    public ParseNode primary() {
        Token token = lexer.token();
        switch (token) {
            default:
            case IDENTIFIER: {
                String id = lexer.tokenString();
                lexer.nextToken();
                if (lexer.token() == Token.LPAREN) {
                    return new CallExpr(id, parentheresExpr());
                }
                return new IdLiteral(id);
            }
            case LITERAL_FLOAT: {
                Literal literal = new DecimalLiteral((BigDecimal) lexer.decimalValue());
                lexer.nextToken();
                return literal;
            }
            case LITERAL_INT: {
                Literal literal = new IntegerLiteral(BigInteger.valueOf(lexer.integerValue().longValue()));
                lexer.nextToken();
                return literal;
            }

            case LITERAL_HEX: {
                Literal literal = new StringLiteral(lexer.hexString());
                lexer.nextToken();
                return literal;
            }
            case LITERAL_NCHARS:
            case LITERAL_CHARS: {
                Literal literal = new StringLiteral(lexer.stringVal());
                lexer.nextToken();
                return literal;
            }
            case LPAREN: {
                return parentheresExpr();
            }
            case EOF:
                throw new ParserException(lexer.info());
        }

    }

    private boolean rightIsExpr(int prec, Precedence next) {
        if (next.leftAssoc) {
            return prec < next.value;
        }
        return prec <= next.value;
    }

    public List<ParseNode> statementList() {
        List<ParseNode> list = new ArrayList<>();
        while (!lexer.isEOF()) {
            list.add(statement());
        }
        return list;
    }

    public static class Precedence {
        private String opText;
        int value;
        boolean leftAssoc; // left associative

        public Precedence(String opText, int v, boolean a) {
            this.opText = opText;
            value = v;
            leftAssoc = a;
        }
    }

    private ParenthesesExpr parentheresExpr() {
        lexer.nextToken();
        List<ParseNode> exprs = new ArrayList<>(3);
        Token token1 = lexer.token();
        String pre = lexer.tokenString();
        if (token1 == Token.RPAREN) {
            lexer.nextToken();
            return new ParenthesesExpr(Collections.emptyList());
        }
        ParseNode expression = expression();
        if ("LET".equalsIgnoreCase(pre)) {
            String name = lexer.tokenString();
            lexer.nextToken();
            ParseNode o;
            if ("=".equalsIgnoreCase(lexer.tokenString())) {
                lexer.nextToken();
                o = expression();
                variables.put(name, o);
                if (lexer.token() == Token.RPAREN) {
                    lexer.nextToken();
                    return new ParenthesesExpr(o);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            throw new UnsupportedOperationException();
        } else {
            exprs.add(expression);
            while (true) {
                if (lexer.token() == Token.RPAREN) {
                    lexer.nextToken();
                    return new ParenthesesExpr(exprs);
                } else if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    exprs.add(expression());
                } else {
                    throw new ParserException(lexer.info());
                }
            }
        }
    }
}