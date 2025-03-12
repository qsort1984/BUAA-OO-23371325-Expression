public class Token {
    public enum Type {
        PLUS, MINUS, MUL, LPAREN, RPAREN, NUM, VAR, POWER, SIN, COS, F, LBRACE, RBRACE, COMMA, N
    }

    private final Type type;
    private final String content;

    public Token(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
