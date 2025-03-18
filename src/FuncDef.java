import java.util.ArrayList;

public class FuncDef {
    private static final Func[] func = new Func[10]; //* 各个序号递推函数定义
    private static final NFunc nfunc = new NFunc();
    private static int pos;
    private static int sign;
    private static int end;
    private static String def;

    public static void setFuncDef(String input) {
        //* f{0}(x[,y])=……
        def = input.replaceAll("[ \t]", "");
        ArrayList<Character> vars = new ArrayList<>();

        pos = 2;
        sign = 0;

        if (def.charAt(pos) != 'n') {
            final int order = def.charAt(pos) - '0'; //* 加入序数
            pos = pos + 3;
            vars.add(def.charAt(pos)); //* 加入第一个变量
            pos++; //* 到达右括号或逗号
            if (def.charAt(pos) == ',') {
                pos++;
                vars.add(def.charAt(pos));
                pos++; //* 跳到右括号
            }
            pos = pos + 2; //* 跳过等号

            func[order] = new Func(vars,def.substring(pos));
        } else {
            //* f{n}(y,x)=-3*f{n-1}(,)+-6*f(n-2)(,)+1983*x*y
            pos = 5;
            nfunc.addVar(def.charAt(pos));
            pos++; //* 到达右括号或逗号
            if (def.charAt(pos) == ',') {
                pos++; //* 跳过逗号
                nfunc.addVar(def.charAt(pos));
                pos = pos + 1; //* 跳过变量到达右括号
                sign = 1;
            }

            //* 到第一个系数
            pos += 2;
            setPartDef();
            //* 到第二个系数
            pos = end + 1;
            setPartDef();
            pos = end + 1;
            nfunc.setExpression(def.substring(pos));
        }
    }

    public static void setPartDef() {
        def = def.substring(pos);
        end = def.indexOf('*');
        nfunc.addCoefficient(def.substring(0, end));

        pos = def.indexOf('(') + 1;
        def = def.substring(pos);
        if (sign == 1) {
            end = findSymbol(def,',');
            ArrayList<String> strings = new ArrayList<>();
            strings.add(def.substring(0, end));

            pos = end + 1;
            def = def.substring(pos);
            end = findSymbol(def,')');
            strings.add(def.substring(0, end));

            nfunc.addFactor(strings);
        } else {
            end = findSymbol(def,')');
            ArrayList<String> strings = new ArrayList<>();
            strings.add(def.substring(0, end));

            nfunc.addFactor(strings);
        }
    }

    public static int findSymbol(String s, char symbol) {
        int tem = 0;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (ch == '(') {
                tem++;
            } else if (ch == ')') {
                if (tem == 0 && ch == symbol) {
                    return i;
                }
                tem--;
            } else if (ch == symbol && tem == 0) {
                return i;
            }
        }

        return -1;
    }

    public static void deduceFuncDef() {
        for (int i = 2; i <= 5; i++) {
            String sb = nfunc.getCoefficients().get(0) +
                "*(" +
                func[i - 1].construct(nfunc.getFactors().get(0)) +
                ")" +
                nfunc.getCoefficients().get(1) +
                "*(" +
                func[i - 2].construct(nfunc.getFactors().get(1)) +
                ")" +
                nfunc.getExpression();

            func[i] = new Func(nfunc.getVars(), sb);
        }
    }

    public static Func getFunc(int order) {
        return func[order];
    }
}
