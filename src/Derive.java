import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

public class Derive {
    public static Polynomial polyDerive(Polynomial poly) {
        HashSet<Monomial> monoSet = poly.getMonoSet();
        Polynomial result = new Polynomial();
        for (Monomial monomial : monoSet) {
            result = result.add(monoDerive(monomial));
        }

        return result;
    }

    public static Polynomial monoDerive(Monomial mono) {
        Polynomial result  = new Polynomial();
        BigInteger coefficient = mono.getCoefficient();
        BigInteger exponent = mono.getExponent();
        HashMap<Polynomial,BigInteger> sin = mono.getSin();
        HashMap<Polynomial,BigInteger> cos = mono.getCos();

        Monomial dxMono = new Monomial(
            coefficient.multiply(exponent),
            exponent.subtract(BigInteger.ONE).max(BigInteger.ZERO),
            sin, cos);
        HashSet<Monomial> dxMonoSet = new HashSet<>();
        dxMonoSet.add(dxMono);
        Polynomial dxPoly = new Polynomial(dxMonoSet);

        result = result.add(dxPoly);

        result = result.add(sinDerive(mono));
        result = result.add(cosDerive(mono));

        return result;
    }

    public static Polynomial sinDerive(Monomial mono) {
        Polynomial result  = new Polynomial();
        Monomial dxMono;
        HashSet<Monomial> dxMonoSet = new HashSet<>();
        Polynomial dxPoly;

        BigInteger coefficient = mono.getCoefficient();
        BigInteger exponent = mono.getExponent();
        HashMap<Polynomial,BigInteger> sin = mono.getSin();
        HashMap<Polynomial,BigInteger> cos = mono.getCos();

        HashMap<Polynomial,BigInteger> newSin;
        HashMap<Polynomial,BigInteger> newCos;
        for (Polynomial poly : sin.keySet()) {
            newSin = new HashMap<>(sin);
            newCos = new HashMap<>(cos);

            //* 处理当前的sin(poly)^t
            final BigInteger t = sin.get(poly);
            if (t.equals(BigInteger.ZERO)) {
                dxPoly = new Polynomial();
            } else {
                //* t != 0 sin(poly)^t -> t*sin(poly)^t-1*cos(poly)*(poly')
                newSin.put(poly, t.subtract(BigInteger.ONE));
                if (newCos.containsKey(poly)) {
                    newCos.put(poly, newCos.get(poly).add(BigInteger.ONE));
                } else {
                    newCos.put(poly, BigInteger.ONE);
                }
                dxMono = new Monomial(coefficient.multiply(t),exponent,newSin,newCos);
                dxMonoSet.clear();
                dxMonoSet.add(dxMono);
                dxPoly = new Polynomial(dxMonoSet);
                dxPoly = dxPoly.mul(polyDerive(poly));
            }

            result = result.add(dxPoly);
        }

        return result;
    }

    public static Polynomial cosDerive(Monomial mono) {
        Polynomial result  = new Polynomial();
        Polynomial dxPoly;
        Monomial dxMono;
        HashSet<Monomial> dxMonoSet = new HashSet<>();

        HashMap<Polynomial,BigInteger> sin = mono.getSin();
        HashMap<Polynomial,BigInteger> cos = mono.getCos();
        BigInteger coefficient = mono.getCoefficient();
        BigInteger exponent = mono.getExponent();

        HashMap<Polynomial,BigInteger> newSin;
        HashMap<Polynomial,BigInteger> newCos;
        for (Polynomial poly : sin.keySet()) {
            newSin = new HashMap<>(sin);
            newCos = new HashMap<>(cos);

            //* 处理当前的cos(poly)^t
            final BigInteger t = sin.get(poly);
            if (t.equals(BigInteger.ZERO)) {
                dxPoly = new Polynomial();
            } else {
                //* t != 0 cos(poly)^t -> -t*cos(poly)^t-1*sin(poly)*(poly')
                newCos.put(poly, t.subtract(BigInteger.ONE));
                if (newSin.containsKey(poly)) {
                    newSin.put(poly, newSin.get(poly).add(BigInteger.ONE));
                } else {
                    newSin.put(poly, BigInteger.ONE);
                }
                dxMono = new Monomial(coefficient.multiply(t.negate()),exponent,newSin,newCos);
                dxMonoSet.clear();
                dxMonoSet.add(dxMono);
                dxPoly = new Polynomial(dxMonoSet);
                dxPoly = dxPoly.mul(polyDerive(poly));
            }

            result = result.add(dxPoly);
        }

        return result;
    }
}
