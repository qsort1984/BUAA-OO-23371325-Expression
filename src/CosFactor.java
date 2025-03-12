import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

public class CosFactor implements Factor {
    private final int power;
    private final Factor factor;

    public CosFactor(int power, Factor factor) {
        this.power = power;
        this.factor = factor;
    }

    @Override
    public Polynomial getPoly() {
        HashMap<Polynomial, BigInteger> cos = new HashMap<>();
        cos.put(factor.getPoly(), BigInteger.valueOf(power));

        HashSet<Monomial> monoSet = new HashSet<>();
        monoSet.add(new Monomial(new HashMap<>(),cos));

        return new Polynomial(monoSet);
    }
}
