import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int index = 0;
    private int pos = 0;
    private int sign = 0; //* 连续加减号标记 1 -> + 2 -> -
    private int num = 0; //* tokens的数量

    public Lexer(String input) {
        while (pos < input.length()) {
            if (input.charAt(pos) == '+') {
                plusLexer();
            } else if (input.charAt(pos) == '-') {
                minusLexer();
            } else {
                if (input.charAt(pos) == '(') {
                    tokens.add(new Token(Token.Type.LPAREN, "("));
                    pos++;
                } else if (input.charAt(pos) == ')') {
                    tokens.add(new Token(Token.Type.RPAREN, ")"));
                    pos++;
                } else if (input.charAt(pos) == '*') {
                    tokens.add(new Token(Token.Type.MUL, "*"));
                    pos++;
                } else if (input.charAt(pos) == 'x') {
                    tokens.add(new Token(Token.Type.VAR, "x"));
                    pos++;
                } else if (input.charAt(pos) == '^') {
                    tokens.add(new Token(Token.Type.POWER, "^"));
                    pos++;
                } else if (input.charAt(pos) == '{') {
                    tokens.add(new Token(Token.Type.LBRACE, "{"));
                    pos++;
                } else if (input.charAt(pos) == '}') {
                    tokens.add(new Token(Token.Type.RBRACE, "}"));
                    pos++;
                } else if (input.charAt(pos) == ',') {
                    tokens.add(new Token(Token.Type.COMMA, ","));
                    pos++;
                } else if (input.charAt(pos) == 'f') {
                    tokens.add(new Token(Token.Type.F, "f"));
                    pos++;
                } else if (input.charAt(pos) == 'g') {
                    tokens.add(new Token(Token.Type.G, "g"));
                    pos++;
                } else if (input.charAt(pos) == 'h') {
                    tokens.add(new Token(Token.Type.H, "h"));
                    pos++;
                } else if (input.charAt(pos) == 'd') {
                    tokens.add(new Token(Token.Type.DX, "dx"));
                    pos = pos + 2;
                } else if (input.charAt(pos) == 's') {
                    tokens.add(new Token(Token.Type.SIN, "sin"));
                    pos = pos + 3;
                } else if (input.charAt(pos) == 'c') {
                    tokens.add(new Token(Token.Type.COS, "cos"));
                    pos = pos + 3;
                } else if (input.charAt(pos) == 'n') {
                    tokens.add(new Token(Token.Type.N, "n"));
                    pos = pos + 3;
                } else {
                    numLexer(input);
                }
                sign = 0;
            }
            num++;
        }
    }

    private void plusLexer() {
        if (sign == 0) {
            tokens.add(new Token(Token.Type.PLUS, "+"));
            sign = 1;
        } else {
            num--;
        }
        pos++;
    }

    private void minusLexer() {
        if (sign == 1) {
            num--;
            tokens.set(num, new Token(Token.Type.MINUS, "-"));
            sign = 2;
        } else if (sign == 2) {
            num--;
            tokens.set(num, new Token(Token.Type.PLUS, "+"));
            sign = 1;
        } else {
            tokens.add(new Token(Token.Type.MINUS, "-"));
            sign = 2;
        }
        pos++;
    }

    private void numLexer(String input) {
        char now = input.charAt(pos);
        StringBuilder sb = new StringBuilder();
        while (now >= '0' && now <= '9') {
            sb.append(now);
            pos++;
            if (pos >= input.length()) {
                break;
            }
            now = input.charAt(pos);
        }
        tokens.add(new Token(Token.Type.NUM, sb.toString()));
    }

    public Token getCurToken() {
        return tokens.get(index);
    }

    public void nextToken() {
        index++;
    }

    public void addIndex(int offset) {
        index += offset;
    }

    public boolean isEnd() {
        return index >= tokens.size();
    }

}
