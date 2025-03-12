import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

public class SinFactor implements Factor {
    private final int power;
    private final Factor factor;

    public SinFactor(int power, Factor factor) {
        this.power = power;
        this.factor = factor;
    }

    @Override
    public Polynomial getPoly() {
        HashMap<Polynomial, BigInteger> sin = new HashMap<>();
        sin.put(factor.getPoly(), BigInteger.valueOf(power));

        HashSet<Monomial> monoSet = new HashSet<>();
        monoSet.add(new Monomial(sin, new HashMap<>()));

        return new Polynomial(monoSet);
    }
}
