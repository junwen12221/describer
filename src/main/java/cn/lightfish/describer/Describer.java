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

    public Describer(String text) {
        this.lexer = new Lexer(text);
        this.lexer.nextToken();
        this.operators = new HashMap<>();
        addOperator(".", "DOT", 16, true);
        addOperator("DOT", 16, true);
        addOperator("JOIN", 1, true);
        addOperator("ON", 1, true);
        addOperator("AS", 1, true);
        addOperator("=", "EQ", 15, true);
        addOperator("EQ", 15, true);
        addOperator("WHERE", 1, true);
        addOperator("PROJECT", 1, true);
        addOperator("OR", 1, true);
        addOperator("AND", 1, true);
        addOperator("FILTER", 1, true);
        addOperator("MAP", 1, true);
        addOperator("+", 1, true);
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


    private Node statement() {
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

    public Node expression() {
        Node right = primary();
        Precedence next = null;
        while ((next = operators.get(lexer.tokenString())) != null) {
            right = doShift(right, next.value);
        }
        return right;
    }

    private Node doShift(Node left, int prec) {
        String op = getOp();
        lexer.nextToken();
        Node right = primary();
        Precedence next = null;
        while ((next = operators.get(lexer.tokenString())) != null && rightIsExpr(prec, next)) {
            right = doShift(right, next.value);
        }
        return new CallExpr(op, new ParenthesesExpr(left, right));
    }

    public Node primary() {
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

    public List<Node> statementList() {
        List<Node> list = new ArrayList<>();
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
        List<Node> exprs = new ArrayList<>(3);
        Token token1 = lexer.token();
        if (token1 == Token.RPAREN) {
            lexer.nextToken();
            return new ParenthesesExpr(Collections.emptyList());
        }
        exprs.add(expression());
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