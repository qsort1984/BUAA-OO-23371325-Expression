import java.util.HashMap;
import java.util.HashSet;

public class Expression {
    private final HashMap<Term, Integer> terms = new HashMap<>();

    public void addTerm(Term term, int sign) {
        terms.put(term, sign);
    }

    public Polynomial getPoly() {
        HashSet<Monomial> monoSet = new HashSet<>();
        Polynomial res = new Polynomial(monoSet);

        for (Term term : terms.keySet()) {
            if (terms.get(term) == 1) {
                res = res.add(term.getPoly());
            } else {
                res = res.sub(term.getPoly());
            }
        }

        return res;
    }
}
