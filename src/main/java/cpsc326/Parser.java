package cpsc326;

import java.util.List;
import static cpsc326.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException{ }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        Expr expr = equality(); // figured I'd do it like this because this rule won't be so simple at some point I think??
        return expr;
    }

    private Expr equality() {
        // parse left side first
        Expr left = comparison();

        // loop while we see == or !=
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            // we already moved past the operator because match advances
            Token op = previous();
            Expr right = comparison();
            // wrap everything back into left for 1. either this or original left is returned and 2. we can keep parsing if there is another == or !=
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    private Expr comparison() {
        Expr left = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token op = previous();
            Expr right = term();
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    private Expr term() {
        Expr left = factor();

        while (match(PLUS, MINUS)) {
            Token op = previous();
            Expr right = factor();
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    private Expr factor() {
        Expr left = unary();

        while (match(SLASH, STAR)) {
            Token op = previous();
            Expr right = unary();
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token op = previous();
            Expr right = unary();
            return new Expr.Unary(op, right);
        } else {
            return primary();
        }
    }

    private Expr primary() {
        if (match(TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(NIL)) {
            return new Expr.Literal(null);
        }

        // can do these with one check because both store value in 'literal field'
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        // grouping, if we see '(', make a new expression then consume ')'
        if (match(LEFT_PAREN)) {
            Expr inner = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(inner);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match (TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        OurPL.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while(!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch(peek().type) {
                case STRUCT:
                case FOR:
                case FUN:
                case IF:
                case PRINT:
                case RETURN:
                case VAR:
                case WHILE:
                    return;
            }
            
            advance();
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
