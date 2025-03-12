public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expression parseExpr() {
        Expression expr = new Expression();

        Token token = lexer.getCurToken();
        if (token.getType() == Token.Type.PLUS) {
            lexer.nextToken();
            expr.addTerm(parseTerm(),1);
        } else if (token.getType() == Token.Type.MINUS) {
            lexer.nextToken();
            expr.addTerm(parseTerm(),0);
        } else {
            expr.addTerm(parseTerm(),1);
        }

        if (!lexer.isEnd()) {
            Token.Type type = lexer.getCurToken().getType();
            while (!lexer.isEnd() && (type == Token.Type.PLUS || type == Token.Type.MINUS)) {
                if (type == Token.Type.PLUS) {
                    lexer.nextToken();
                    expr.addTerm(parseTerm(),1);
                } else {
                    lexer.nextToken();
                    expr.addTerm(parseTerm(),0);
                }
                if (!lexer.isEnd()) {
                    type = lexer.getCurToken().getType();
                }
            }
        }

        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();

        Token token = lexer.getCurToken();
        if (token.getType() == Token.Type.PLUS) {
            term.setSign(1);
            lexer.nextToken();
        } else if (token.getType() == Token.Type.MINUS) {
            term.setSign(0);
            lexer.nextToken();
        } else {
            term.setSign(1);
        }

        term.addFactor(parseFactor());
        while (!lexer.isEnd() && lexer.getCurToken().getType() == Token.Type.MUL) {
            lexer.nextToken();
            term.addFactor(parseFactor());
        }

        return term;
    }

    public Factor parseFactor() {
        Token token = lexer.getCurToken();
        if (token.getType() == Token.Type.PLUS) {
            lexer.nextToken();
            return parseNumFactor(1);
        } else if (token.getType() == Token.Type.MINUS) {
            lexer.nextToken();
            return parseNumFactor(0);
        } else if (token.getType() == Token.Type.NUM) {
            return parseNumFactor(1);
        } else if (token.getType() == Token.Type.VAR) {
            return parseVarFactor();
        } else if (token.getType() == Token.Type.SIN) {
            return parseSinFactor();
        } else if (token.getType() == Token.Type.COS) {
            return parseCosFactor();
        } else if (token.getType() == Token.Type.F) {
            return parseFuncFactor();
        } else if (token.getType() == Token.Type.G) {
            return parseGhFactor(true);
        } else if (token.getType() == Token.Type.H) {
            return parseGhFactor(false);
        } else if (token.getType() == Token.Type.DX) {
            return parseDeriveFactor();
        } else {
            return parseExprFactor();
        }
    }

    public DeriveFactor parseDeriveFactor() {
        lexer.addIndex(2); //* skip dx(

        Expression expr = parseExpr();
        DeriveFactor deriveFactor = new DeriveFactor(expr);
        lexer.nextToken(); //* skip )

        return deriveFactor;
    }

    public GhFactor parseGhFactor(boolean sign) {
        //* TODO
        //* true -> g() false -> h()
        return null;
    }

    public ExprFactor parseExprFactor() {
        lexer.nextToken(); //* skip (
        ExprFactor exprFactor;
        Expression expr = parseExpr();
        lexer.nextToken(); //* skip )

        if (!lexer.isEnd() && lexer.getCurToken().getType() == Token.Type.POWER) {
            lexer.nextToken(); //* skip ^
            Token token = lexer.getCurToken();
            if (token.getType() == Token.Type.PLUS) {
                lexer.nextToken(); //* skip +
                token = lexer.getCurToken();
            }

            exprFactor = new ExprFactor(Integer.parseInt(token.getContent()), expr);
            lexer.nextToken(); //* skip num
        } else {
            exprFactor = new ExprFactor(1, expr);
        }

        return exprFactor;

    }

    public NumFactor parseNumFactor(int sign) {
        Token token = lexer.getCurToken();
        NumFactor numFactor;
        if (sign == 1) {
            numFactor = new NumFactor(token.getContent());
        } else {
            numFactor = new NumFactor("-" + token.getContent());
        }

        lexer.nextToken(); //* skip num

        return numFactor;
    }

    public VarFactor parseVarFactor() {
        Token token1 = lexer.getCurToken();
        lexer.nextToken(); //* skip x
        VarFactor varFactor;
        if (lexer.isEnd()) {
            varFactor = new VarFactor(token1.getContent(), 1);
        } else {
            Token token2 = lexer.getCurToken();
            if (token2.getType() == Token.Type.POWER) {
                lexer.nextToken(); //* skip ^
                Token token3 = lexer.getCurToken();
                if (token3.getType() == Token.Type.PLUS) {
                    lexer.nextToken(); //* skip +
                    token3 = lexer.getCurToken();
                }

                int tem = Integer.parseInt(token3.getContent());
                varFactor = new VarFactor(token1.getContent(), tem);
                lexer.nextToken(); //* skip num
            } else {
                varFactor = new VarFactor(token1.getContent(), 1);
            }
        }

        return varFactor;
    }

    public SinFactor parseSinFactor() {
        lexer.nextToken(); //* skip sin
        lexer.nextToken(); //* skip (
        SinFactor sinFactor;
        Factor factor = parseFactor();
        lexer.nextToken(); //* skip )

        if (!lexer.isEnd() && lexer.getCurToken().getType() == Token.Type.POWER) {
            lexer.nextToken(); //* skip ^
            Token token = lexer.getCurToken();
            if (token.getType() == Token.Type.PLUS) {
                lexer.nextToken(); //* skip +
                token = lexer.getCurToken();
            }

            lexer.nextToken();
            sinFactor = new SinFactor(Integer.parseInt(token.getContent()), factor);
        } else {
            sinFactor = new SinFactor(1, factor);
        }

        return sinFactor;
    }

    public CosFactor parseCosFactor() {
        lexer.nextToken();
        lexer.nextToken(); //* 处理左括号
        CosFactor cosFactor;
        Factor factor = parseFactor();
        lexer.nextToken(); //* 处理右括号

        if (!lexer.isEnd() && lexer.getCurToken().getType() == Token.Type.POWER) {
            lexer.nextToken();
            Token token = lexer.getCurToken();
            if (token.getType() == Token.Type.PLUS) {
                lexer.nextToken();
                token = lexer.getCurToken();
            }

            lexer.nextToken();
            cosFactor = new CosFactor(Integer.parseInt(token.getContent()), factor);
        } else {
            cosFactor = new CosFactor(1, factor);
        }

        return cosFactor;
    }

    public FuncFactor parseFuncFactor() {
        final FuncFactor funcFactor;

        lexer.addIndex(2); //* 跳过f{
        final int order = Integer.parseInt(lexer.getCurToken().getContent());
        lexer.addIndex(3); //* 跳过i}(
        Factor factor1 = parseFactor();

        if (lexer.getCurToken().getType() == Token.Type.COMMA) {
            lexer.nextToken(); //* 跳过逗号
            Factor factor2 = parseFactor();
            funcFactor = new FuncFactor(order, factor1, factor2);
        } else {
            funcFactor = new FuncFactor(order, factor1);
        }

        lexer.nextToken(); //* 跳过)

        return funcFactor;
    }
}
