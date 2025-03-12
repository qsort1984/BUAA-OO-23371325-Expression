public class DeriveFactor implements Factor {
    private final Expression expr;

    public DeriveFactor(Expression expr) {
        this.expr = expr;
    }

    @Override
    public Polynomial getPoly() {
        return Derive.polyDerive(expr.getPoly());
    }
}
