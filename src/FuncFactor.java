public class FuncFactor implements Factor {
    private final int order; //* 函数序号
    private final Factor factor1;
    private Factor factor2 = null;

    public FuncFactor(int order, Factor factor1) {
        this.order = order;
        this.factor1 = factor1;
    }

    public FuncFactor(int order, Factor factor1, Factor factor2) {
        this.order = order;
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    @Override
    public Polynomial getPoly() {
        String expr;
        Func func = FuncDef.getFunc(order);
        String string1 = "(" + factor1.getPoly().toString() + ")";

        if (factor2 == null) {
            expr = func.getDef().replace(String.valueOf(func.getVars(0)), string1);
        } else {
            String string2 = "(" + factor2.getPoly().toString() + ")";
            expr = func.getDef().replace(String.valueOf(func.getVars(0)), "#")
                    .replace(String.valueOf(func.getVars(1)), string2)
                    .replace("#", string1);
        }
        Lexer lexer = new Lexer(expr);
        Parser parser = new Parser(lexer);

        return parser.parseExpr().getPoly();
    }
}
