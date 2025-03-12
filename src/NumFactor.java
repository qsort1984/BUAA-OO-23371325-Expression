import java.math.BigInteger;
import java.util.HashSet;

public class NumFactor implements Factor {
    private final BigInteger value;

    public NumFactor(String string) {
        this.value = new BigInteger(string);
    }

    public Polynomial getPoly() {
        Monomial mono = new Monomial(value,BigInteger.ZERO);
        HashSet<Monomial> monoSet = new HashSet<>();
        monoSet.add(mono);

        return new Polynomial(monoSet);
    }
}
