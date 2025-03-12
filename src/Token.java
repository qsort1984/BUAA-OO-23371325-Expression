public class Token {
    public enum Type {
        PLUS, MINUS, MUL,
        LPAREN, RPAREN, LBRACE, RBRACE,
        NUM, VAR, N,
        POWER, COMMA,
        SIN, COS,
        F, G, H,
        DX
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
