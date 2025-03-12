import java.util.ArrayList;

public class NFunc {
    private final ArrayList<Character> vars = new ArrayList<>();
    private final ArrayList<String> coefficients = new ArrayList<>();
    private final ArrayList<ArrayList<String>> factors = new ArrayList<>();
    private String expression;

    public void addVar(char c) {
        vars.add(c);
    }

    public void addCoefficient(String s) {
        coefficients.add(s);
    }

    public void addFactor(ArrayList<String> factor) {
        factors.add(factor);
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ArrayList<Character> getVars() {
        return vars;
    }

    public ArrayList<String> getCoefficients() {
        return coefficients;
    }

    public ArrayList<ArrayList<String>> getFactors() {
        return factors;
    }

    public String getExpression() {
        return expression;
    }

}
