import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;

//* 不可变类，仿照BigInteger构造
public final class Monomial {
    private final BigInteger coefficient;
    private final BigInteger exponent;
    private final HashMap<Polynomial, BigInteger> sin;
    private final HashMap<Polynomial, BigInteger> cos;
    private boolean isHash = false;
    private int cachedHashCode; // 缓存哈希码

    public Monomial() {
        this.coefficient = new BigInteger("1");
        this.exponent = new BigInteger("0");
        this.sin = new HashMap<>();
        this.cos = new HashMap<>();
    }

    public Monomial(BigInteger coefficient, BigInteger exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.sin = new HashMap<>();
        this.cos = new HashMap<>();
    }

    public Monomial(HashMap<Polynomial, BigInteger> sin, HashMap<Polynomial, BigInteger> cos) {
        this.coefficient = new BigInteger("1");
        this.exponent = new BigInteger("0");
        this.sin = sin;
        this.cos = cos;
    }

    public Monomial(BigInteger coefficient, BigInteger exponent,
        HashMap<Polynomial, BigInteger> sin, HashMap<Polynomial, BigInteger> cos) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.sin = new HashMap<>(sin); // 深拷贝
        this.cos = new HashMap<>(cos); // 深拷贝
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public HashMap<Polynomial, BigInteger> getSin() {
        return new HashMap<>(sin);
    }

    public HashMap<Polynomial, BigInteger> getCos() {
        return new HashMap<>(cos);
    }

    //* 除系数都相同
    public boolean isSimilar(Monomial mono) {
        return  mono.exponent.equals(this.exponent)
                && sin.equals(mono.sin)
                && cos.equals(mono.cos);
    }

    public Monomial merge(Monomial mono) {
        return new Monomial(
            this.coefficient.add(mono.getCoefficient()),
            this.exponent,
            new HashMap<>(this.sin),
            new HashMap<>(this.cos)
        );
    }

    public Monomial negate() {
        return new Monomial(
            this.coefficient.negate(),
            this.exponent,
            new HashMap<>(this.sin),
            new HashMap<>(this.cos)
        );
    }

    public Monomial mul(BigInteger num) {
        return new Monomial(
            this.coefficient.multiply(num),
            this.exponent,
            new HashMap<>(this.sin),
            new HashMap<>(this.cos)
        );
    }

    private boolean isEven(BigInteger number) {
        return number.mod(BigInteger.valueOf(2)).signum() == 0;
    }

    private BigInteger processSin(Polynomial p, BigInteger num,
        HashMap<Polynomial, BigInteger> newSin) {

        Iterator<Map.Entry<Polynomial, BigInteger>> sinIterator = newSin.entrySet().iterator();
        while (sinIterator.hasNext()) {
            Map.Entry<Polynomial, BigInteger> sinEntry = sinIterator.next();
            Polynomial q = sinEntry.getKey();
            BigInteger sinValue = sinEntry.getValue();
            Polynomial negP = p.negate();

            if ((p.equals(q) || negP.equals(q)) && !sinValue.equals(BigInteger.ZERO)) {
                if (num.equals(sinValue)
                    && num.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0) {
                    if (coefficient.getLowestSetBit() >= num.intValue()) {
                        sinIterator.remove();
                        newSin.merge(p.mul(1), num, BigInteger::add);
                        return num;
                    }
                }
            }
        }
        return BigInteger.ZERO;
    }

    //* sin(-x)和cos(-x)化简
    public int assistSimplify(HashMap<Polynomial, BigInteger> newSin,
        HashMap<Polynomial, BigInteger> newCos) {
        int reverse = 1;

        //* sin
        Iterator<Map.Entry<Polynomial, BigInteger>> sinIterator = newSin.entrySet().iterator();
        while (sinIterator.hasNext()) {
            Map.Entry<Polynomial, BigInteger> sinEntry = sinIterator.next();
            Polynomial p = sinEntry.getKey();
            BigInteger sinValue = sinEntry.getValue();

            Polynomial negP = p.negate();
            if (newSin.containsKey(negP)) {
                BigInteger negSinValue = newSin.get(negP);

                reverse *= sinValue.testBit(0) ? -1 : 1;
                newSin.put(negP, sinValue.add(negSinValue));
                sinIterator.remove();
            }
        }

        //* cos
        Iterator<Map.Entry<Polynomial, BigInteger>> cosIterator = newCos.entrySet().iterator();
        while (cosIterator.hasNext()) {
            Map.Entry<Polynomial, BigInteger> cosEntry = cosIterator.next();
            Polynomial p = cosEntry.getKey();
            BigInteger cosValue = cosEntry.getValue();

            Polynomial negP = p.negate();
            if (newCos.containsKey(negP)) {

                newCos.compute(negP, (k, negCosValue) -> cosValue.add(negCosValue));
                cosIterator.remove();
            }
        }

        return reverse;
    }

    public Monomial simplify() {
        if (this.isZero()) {
            return new Monomial(BigInteger.ZERO, BigInteger.ZERO, new HashMap<>(), new HashMap<>());
        }

        final HashMap<Polynomial, BigInteger> newSin = new HashMap<>(this.sin);
        final HashMap<Polynomial, BigInteger> newCos = new HashMap<>(this.cos);
        BigInteger newCoefficient = this.coefficient;

        this.sin.keySet().removeIf(p -> p.isZero() && sin.get(p).signum() == 0);
        this.cos.keySet().removeIf(Polynomial::isZero);

        if (assistSimplify(newSin,newCos) == -1) {
            newCoefficient = newCoefficient.negate();
        }

        //* sin 的二倍角公式化简
        Iterator<Map.Entry<Polynomial, BigInteger>> cosIterator = newCos.entrySet().iterator();
        while (cosIterator.hasNext()) {
            Map.Entry<Polynomial, BigInteger> cosEntry = cosIterator.next();
            Polynomial p = cosEntry.getKey();
            BigInteger cosValue = cosEntry.getValue();

            if (cosValue.equals(BigInteger.ZERO)) {
                continue;
            }

            if (!isEven(newCoefficient)) {
                break;
            }

            BigInteger min = processSin(p, cosEntry.getValue(), newSin);
            if (min.signum() != 0) {
                cosIterator.remove();
                newCoefficient = newCoefficient.shiftRight(min.intValue());
            }
        }

        //* 去除sin和cos的零次项
        newSin.keySet().removeIf(p -> newSin.get(p).equals(BigInteger.ZERO));
        newCos.keySet().removeIf(p -> newCos.get(p).equals(BigInteger.ZERO));

        return new Monomial(newCoefficient, this.exponent, newSin, newCos);
    }

    public boolean isPositive() {
        return coefficient.signum() == 1;
    }

    public boolean isZero() {
        if (coefficient.signum() == 0) {
            return true;
        } else {
            for (Polynomial p : sin.keySet()) {
                if (p.isZero() && sin.get(p).signum() != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSingle() {
        if (sin.isEmpty() && cos.isEmpty()) {
            return exponent.signum() == 0 || coefficient.equals(BigInteger.ONE);
        }
        if (exponent.signum() == 0 && coefficient.equals(BigInteger.ONE)) {
            return (sin.isEmpty() && cos.size() == 1) || (sin.size() == 1 && cos.isEmpty());
        }
        return false;
    }

    public Polynomial mulPoly(Polynomial poly) {
        HashSet<Monomial> newMonoSet = new HashSet<>();
        for (Monomial mono : poly.getMonoSet()) {
            //* 将当前 Monomial 与 Polynomial 中的每个 Monomial 相乘
            BigInteger newCoefficient = this.coefficient.multiply(mono.getCoefficient());
            BigInteger newExponent = this.exponent.add(mono.getExponent());
            HashMap<Polynomial, BigInteger> newSin = new HashMap<>(this.sin);
            HashMap<Polynomial, BigInteger> newCos = new HashMap<>(this.cos);

            //* 合并 sin 和 cos
            for (Map.Entry<Polynomial, BigInteger> entry : mono.getSin().entrySet()) {
                newSin.put(entry.getKey(),
                    newSin.getOrDefault(entry.getKey(), BigInteger.ZERO).add(entry.getValue()));
            }
            for (Map.Entry<Polynomial, BigInteger> entry : mono.getCos().entrySet()) {
                newCos.put(entry.getKey(),
                    newCos.getOrDefault(entry.getKey(), BigInteger.ZERO).add(entry.getValue()));
            }

            newMonoSet.add(new Monomial(newCoefficient, newExponent, newSin, newCos));
        }
        return new Polynomial(newMonoSet);
    }

    private String triConstruct(boolean sign) {
        StringBuilder sb = new StringBuilder();
        boolean signTem = sign; //* false -> 输出* true -> 不输出*

        for (Polynomial poly : sin.keySet()) {
            if (sin.get(poly).compareTo(BigInteger.ZERO) == 0) {
                continue;
            } else {
                if (!signTem) {
                    sb.append("*");
                } else {
                    signTem = false;
                }
                sb.append("sin(");
                sb.append(assistConstruct(poly.simplify()));
            }

            if (sin.get(poly).compareTo(BigInteger.ONE) != 0) {
                sb.append("^");
                sb.append(sin.get(poly));
            }
        }

        for (Polynomial poly : cos.keySet()) {
            if (cos.get(poly).compareTo(BigInteger.ZERO) == 0) {
                continue;
            } else {
                if (!signTem) {
                    sb.append("*");
                } else {
                    signTem = false;
                }
                sb.append("cos(");
                sb.append(assistConstruct(poly.simplify()));
            }

            if (cos.get(poly).compareTo(BigInteger.ONE) != 0) {
                sb.append("^");
                sb.append(cos.get(poly));
            }
        }

        return sb.toString();
    }

    private String assistConstruct(Polynomial poly) {
        StringBuilder sb = new StringBuilder();
        boolean paren = true;

        if (poly.getMonoSet().size() == 1) {
            for (Monomial mono : poly.getMonoSet()) {
                if (mono.isSingle()) {
                    paren = false;
                }
            }
        }
        if (paren) {
            sb.append("(");
        }
        sb.append(poly);
        if (paren) {
            sb.append(")");
        }
        sb.append(")");

        return sb.toString();
    }

    public Polynomial canMerge(Monomial mono) {
        if (!mono.getCoefficient().equals(coefficient) ||
            !mono.getExponent().equals(exponent)) {
            return null;
        }

        HashMap<Polynomial, BigInteger> monoSin = mono.getSin();
        HashMap<Polynomial, BigInteger> monoCos = mono.getCos();

        Polynomial getSinPoly;
        if (monoSin.isEmpty()) {
            getSinPoly = getSquare(monoSin, sin);
        } else {
            getSinPoly = getSquare(sin, monoSin);
        }
        if (getSinPoly == null) {
            return null;
        }

        Polynomial getCosPoly;
        if (monoCos.isEmpty()) {
            getCosPoly = getSquare(monoCos, cos);
        } else {
            getCosPoly = getSquare(cos, monoCos);
        }
        if (!getSinPoly.equals(getCosPoly)) {
            return null;
        }

        return getSinPoly;
    }

    public Polynomial getSquare(HashMap<Polynomial, BigInteger> sinCos,
        HashMap<Polynomial, BigInteger> thisSinCos) {
        boolean get = false; //* 找到sin^2或cos^2项
        Polynomial getSinCosPoly = null;

        for (Polynomial poly : thisSinCos.keySet()) {
            if (sinCos.containsKey(poly)) {
                BigInteger diff = thisSinCos.get(poly).subtract(sinCos.get(poly));
                if (diff.equals(BigInteger.valueOf(2)) || diff.equals(BigInteger.valueOf(-2))) {
                    if (!get) {
                        get = true;
                        getSinCosPoly = new Polynomial(poly.getMonoSet());
                    } else {
                        return null;
                    }
                } else if (diff.signum() != 0) {
                    return null;
                }
            } else {
                if (thisSinCos.get(poly).equals(new BigInteger("2"))) {
                    if (!get) {
                        get = true;
                        getSinCosPoly = new Polynomial(poly.getMonoSet());
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        return getSinCosPoly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        boolean sign = false; //* 是否输出第一项三角函数前的“*”

        if (coefficient.compareTo(BigInteger.ONE) == 0) {
            if (exponent.equals(BigInteger.ONE)) {
                sb.append("x");
            } else if (exponent.compareTo(BigInteger.ZERO) > 0) {
                sb.append("x^");
                sb.append(exponent);
            } else if (sin.isEmpty() && cos.isEmpty()) {
                sb.append("1");
            } else {
                sign = true;
            }
        } else if (coefficient.compareTo(new BigInteger("-1")) == 0) {
            if (exponent.equals(BigInteger.ONE)) {
                sb.append("-x");
            } else if (exponent.compareTo(BigInteger.ZERO) > 0) {
                sb.append("-x^");
                sb.append(exponent);
            } else if (sin.isEmpty() && cos.isEmpty()) {
                sb.append("-1");
            } else {
                sb.append("-");
                sign = true;
            }
        } else {
            sb.append(coefficient);

            if (exponent.equals(BigInteger.ONE)) {
                sb.append("*x");
            } else if (exponent.compareTo(BigInteger.ZERO) > 0) {
                sb.append("*x^");
                sb.append(exponent);
            }
        }

        String tem = triConstruct(sign);
        if (sign && tem.isEmpty()) {
            sb.append("1");
        } else {
            sb.append(tem);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Monomial mono = (Monomial) o;

        return mono.coefficient.compareTo(this.coefficient) == 0
                && mono.exponent.compareTo(this.exponent) == 0
                && sin.equals(mono.sin)
                && cos.equals(mono.cos);
    }

    @Override
    public int hashCode() {
        if (!isHash) {
            cachedHashCode = Objects.hash(coefficient, exponent, sin, cos);
            isHash = true;
        }
        return cachedHashCode;

    }
}
