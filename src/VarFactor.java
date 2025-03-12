import java.math.BigInteger;
import java.util.HashSet;

public class VarFactor implements Factor {
    private final int power;
    private final String name;

    public VarFactor(String name, int power) {
        this.power = power;
        this.name = name;
    }

    public Polynomial getPoly() {
        Monomial mono = new Monomial(BigInteger.ONE,BigInteger.valueOf(power));
        HashSet<Monomial> monoSet = new HashSet<>();
        monoSet.add(mono);

        return new Polynomial(monoSet);
    }
}
