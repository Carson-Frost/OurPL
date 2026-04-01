package cpsc326;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cpsc326.TokenType.*;

class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    Lexer(String source) {
        this.source = source;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("struct", STRUCT);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {
        return c == '_' || isAlpha(c) || isDigit(c);
    }

    private void addToken(TokenType type) {
        addToken(type,null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void string() {
        while (true) {
            if (isAtEnd()) {
                OurPL.error(line, "Unterminated string.");
                return;
            }
            if (peek() == '"') break;
            if (peek() == '\n') line++;
            advance();
        }
        advance(); // skip past the closing quote
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // skip past the dot into decimal
            while (isDigit(peek())) advance();
        }
        double n = Double.valueOf(source.substring(start, current));
        addToken(NUMBER, n);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type != null) {
            addToken(type);
        } else {
            addToken(IDENTIFIER);
        }
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '"': string(); break;

            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;

            case '+': addToken(PLUS); break;
            case '-': addToken(MINUS); break;
            case '*': addToken(STAR); break;
            case '/': addToken(SLASH); break;
            case '.': addToken(DOT); break;
            case ',': addToken(COMMA); break;
            case ';': addToken(SEMICOLON); break;

            case '!':
                if (match('=')) { addToken(BANG_EQUAL); }
                else { addToken(BANG); }
                break;
            case '=':
                if (match('=')) { addToken(EQUAL_EQUAL); }
                else { addToken(EQUAL); }
                break;
            case '<':
                if (match('=')) { addToken(LESS_EQUAL); }
                else { addToken(LESS); }
                break;
            case '>':
                if (match('=')) { addToken(GREATER_EQUAL); }
                else { addToken(GREATER); }
                break;

            case '#':
                while (!isAtEnd() && peek() != '\n') { advance(); }
                break;

            case ' ': break;
            case '\t': break;
            case '\r': break;
            case '\n': line++; break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    OurPL.error(line, "Unexpected character.");
                }
                break;
        }
    }
}