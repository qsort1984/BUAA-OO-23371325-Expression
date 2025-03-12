import java.util.HashMap;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

//* 不可变类，仿照BigInteger构造
public final class Polynomial {
    private final HashSet<Monomial> monoSet;
    private boolean isHash = false;
    private int cashedHashCode;

    public Polynomial() {
        this(new HashSet<>());
    }

    public Polynomial(HashSet<Monomial> monoSet) {
        this.monoSet = new HashSet<>(monoSet); // 深拷贝
    }

    public HashSet<Monomial> getMonoSet() {
        return new HashSet<>(monoSet); // 返回深拷贝
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Polynomial that = (Polynomial) o;

        return monoSet.equals(that.monoSet);
    }

    @Override
    public int hashCode() {
        if (!isHash) {
            isHash = true;
            cashedHashCode = Objects.hash(monoSet);
        }
        return cashedHashCode;
    }

    public Polynomial negate() {
        HashSet<Monomial> newMonoSet = new HashSet<>();
        for (Monomial monomial : this.monoSet) {
            newMonoSet.add(monomial.negate());
        }
        return new Polynomial(newMonoSet);
    }

    public Polynomial addMono(Monomial monomial) {
        HashSet<Monomial> newMonoSet = new HashSet<>(this.monoSet);
        boolean sign = false;

        for (Monomial mono : monoSet) {
            if (mono.isSimilar(monomial)) {
                newMonoSet.remove(mono);
                newMonoSet.add(mono.merge(monomial));
                sign = true;
                break;
            }
        }

        if (!sign) {
            newMonoSet.add(monomial);
        }

        return new Polynomial(newMonoSet);
    }

    public Polynomial add(Polynomial poly) {
        if (monoSet.isEmpty()) {
            return poly;
        }

        ConcurrentHashMap<Monomial,Monomial> newMonoSet = new ConcurrentHashMap<>();

        for (Monomial mono : this.monoSet) {
            newMonoSet.put(mono, mono);
        }

        poly.getMonoSet().parallelStream().forEach(mono1 -> {
            Monomial similarMono = newMonoSet.keySet().parallelStream()
                .filter(mono1::isSimilar)
                .findAny()
                .orElse(null);

            if (similarMono != null) {
                Monomial mergedMono = mono1.merge(similarMono);
                newMonoSet.remove(similarMono);
                newMonoSet.put(mergedMono, mergedMono);
            } else {
                newMonoSet.put(mono1, mono1);
            }
        });

        return new Polynomial(new HashSet<>(newMonoSet.keySet()));
    }

    public Polynomial sub(Polynomial poly) {
        return this.add(poly.negate());
    }

    public Polynomial mul(int num) {
        HashSet<Monomial> newMonoSet = new HashSet<>();

        BigInteger toMul = BigInteger.valueOf(2).pow(num);

        for (Monomial monomial : monoSet) {
            newMonoSet.add(monomial.mul(toMul));
        }

        return new Polynomial(newMonoSet);
    }

    public Polynomial mul(Polynomial poly) {
        //* 并行化
        return this.monoSet.parallelStream()
                .map(mono1 -> mono1.mulPoly(poly))
                .reduce(new Polynomial(), Polynomial::add)
                .simplify();
    }

    public Polynomial pow(int power) {
        if (power < 0) {
            throw new IllegalArgumentException("Power must be non-negative.");
        }
        if (power == 1) {
            return this;
        }

        //* 结果poly
        HashSet<Monomial> newMonoSet = new HashSet<>();
        newMonoSet.add(new Monomial());
        Polynomial result = new Polynomial(newMonoSet);
        //* 基数poly
        Polynomial base = this;

        if (power == 0) {
            return new Polynomial(newMonoSet);
        }

        int powerCopy = power;

        while (powerCopy != 0) {
            if ((powerCopy & 1) == 1) {
                result = result.mul(base);
            }
            base = base.mul(base);
            powerCopy >>>= 1;
        }
        return result;
    }

    public Polynomial merge() {
        HashSet<Monomial> newMonoSet;
        Polynomial result = new Polynomial();

        for (Monomial mono : this.monoSet) {
            boolean merged = false;

            newMonoSet = result.getMonoSet();
            Iterator<Monomial> iterator = newMonoSet.iterator();
            Monomial mergedMono = null;
            while (iterator.hasNext()) {
                Monomial existingMono = iterator.next();
                Polynomial poly = mono.canMerge(existingMono);
                if (poly != null) {
                    mergedMono = mergeMonomials(mono,existingMono,poly);
                    iterator.remove();
                    merged = true;
                    break;
                }
            }

            result = new Polynomial(newMonoSet);

            if (!merged) {
                result = result.addMono(mono);
            } else {
                result = result.addMono(mergedMono);
            }
        }

        return result;
    }

    public  Monomial mergeMonomials(Monomial mono1, Monomial mono2, Polynomial poly) {
        HashMap<Polynomial, BigInteger> newSin = new HashMap<>(mono1.getSin());
        HashMap<Polynomial, BigInteger> newCos = new HashMap<>(mono1.getCos());

        //* sin(x)^3*cos(x)+sin(x)*cos(x)^3
        //* mono2 -> sin(x)^3*cos(x) mono1 -> sin(x)*cos(x)^3
        //* mono1 -> cos(x)^2 mono2 -> sin(x)^2
        if (newSin.containsKey(poly)) {
            if (mono2.getSin().containsKey(poly)) {
                if (newSin.get(poly).compareTo(mono2.getSin().get(poly)) > 0) {
                    BigInteger tem = newSin.get(poly).subtract(BigInteger.valueOf(2));
                    if (tem.signum() == 0) {
                        newSin.remove(poly);
                    } else {
                        newSin.put(poly, tem);
                    }
                } else {
                    BigInteger tem = mono2.getSin().get(poly).subtract(BigInteger.valueOf(2));
                    if (tem.signum() == 0) {
                        newCos.remove(poly);
                    } else {
                        newCos.put(poly, tem);
                    }
                }
            } else {
                newSin.remove(poly);
            }
        } else {
            newCos.remove(poly);
        }

        return new Monomial(mono1.getCoefficient(), mono1.getExponent(), newSin, newCos);
    }

    public Polynomial simplify() {
        //* cos 的二倍角公式化简
        //* TODO

        HashSet<Monomial> monoSet = new HashSet<>();

        for (Monomial mono : this.monoSet) {
            monoSet.add(mono.simplify());
        }

        Polynomial poly = new Polynomial();

        for (Monomial mono : monoSet) {
            poly = poly.addMono(mono);
        }

        poly = poly.merge();

        return poly;
    }

    public Boolean isZero() {
        for (Monomial mono : this.monoSet) {
            if (!mono.isZero()) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        //! 若后续迭代引入变量y等需修改toString方法以保证正确的输出
        StringBuilder sb = new StringBuilder();

        Monomial special = null; //* 正数项

        for (Monomial mono : monoSet) {
            if (mono.isPositive()) {
                special = mono;
                sb.append(mono);
                break;
            }
        }

        for (Monomial mono : monoSet) {
            if (mono == special) {
                continue;
            } else if (mono.isPositive()) {
                sb.append("+");
            } else if (mono.isZero()) {
                continue;
            }

            sb.append(mono);
        }

        if (sb.length() == 0) {
            sb.append("0");
        }

        return sb.toString();
    }
}
