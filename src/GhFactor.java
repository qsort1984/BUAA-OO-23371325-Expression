public class GhFactor implements Factor {
    private final int order; //* 0 -> g() 1 -> h()
    private final Factor factor1;
    private Factor factor2 = null;

    public GhFactor(int order, Factor factor1) {
        this.order = order;
        this.factor1 = factor1;
    }

    public GhFactor(int order, Factor factor1, Factor factor2) {
        this.order = order;
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    @Override
    public Polynomial getPoly() {
        String expr;
        Func func = GhDef.getGhFunc(order);
        String string1 = "(" + factor1.getPoly().toString() + ")";

        if (factor2 != null) {
            String string2 = "(" + factor2.getPoly().toString() + ")";
            expr = func.getDef().replace(String.valueOf(func.getVars(0)), "#")
                    .replace(String.valueOf(func.getVars(1)), string2)
                    .replace("#", string1);
        } else {
            expr = func.getDef().replace(String.valueOf(func.getVars(0)), string1);
        }
        Lexer lexer = new Lexer(expr);
        Parser parser = new Parser(lexer);

        return parser.parseExpr().getPoly();
    }
}
