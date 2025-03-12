public class ExprFactor implements Factor {
    private final int power;
    private final Expression expr;

    public ExprFactor(int power, Expression expr) {
        this.power = power;
        this.expr = expr;
    }

    @Override
    public Polynomial getPoly() {
        return expr.getPoly().pow(power);
    }
}
