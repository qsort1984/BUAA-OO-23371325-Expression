import java.util.ArrayList;
import java.util.HashSet;

public class Term {
    private final ArrayList<Factor> factors = new ArrayList<>();
    private int sign; //* 项正负标记

    public void addFactor(Factor factor) {
        factors.add(factor);
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public Polynomial getPoly() {
        HashSet<Monomial> monoSet = new HashSet<>();
        monoSet.add(new Monomial());
        Polynomial res = new Polynomial(monoSet);

        for (Factor factor : factors) {
            res = res.mul(factor.getPoly());
        }

        if (sign == 0) {
            res.negate();
        }

        return res;
    }
}
