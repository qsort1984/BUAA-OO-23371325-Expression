import java.util.ArrayList;

public class GhDef {
    private static final Func[] ghFunc = new Func[2];

    public static void setFuncDef(String input) {
        String def = input.replaceAll("[ \t]", "");
        ArrayList<Character> vars = new ArrayList<>();
        int order;

        int pos = 0;
        //* 0-> g() 1 -> h()
        if (def.charAt(pos) == 'g') {
            order = 0;
        } else {
            order = 1;
        }

        //* g(x,y) = x*sin(y) h(y) = cos(y)^2
        pos += 2;
        vars.add(def.charAt(pos));

        pos++; //* 跳到,或)
        if (def.charAt(pos) == ',') {
            pos++;
            vars.add(def.charAt(pos));
            pos++; //* 跳到)
        }

        pos += 2; //* 跳到=
        ghFunc[order] = new Func(vars, def.substring(pos));
    }

    public static Func getGhFunc(int order) {
        return ghFunc[order];
    }
}
