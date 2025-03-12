import java.util.ArrayList;

public class Func {
    private final ArrayList<Character> vars;
    private final String def;

    public Func(ArrayList<Character> vars, String def) {
        this.vars = vars;
        this.def = def;
    }

    public Character getVars(int index) {
        return vars.get(index);
    }

    public String getDef() {
        return def;
    }

    public String construct(ArrayList<String> factors) {
        String ret;

        if (factors.size() == 2) {
            ret = def.replace(String.valueOf(vars.get(0)),"#")
                    .replace(String.valueOf(vars.get(1)),"(" + factors.get(1) + ")")
                    .replace("#","(" + factors.get(0) + ")");
        } else {
            ret = def.replace(String.valueOf(vars.get(0)),"(" + factors.get(0) + ")");
        }

        return ret;
    }
}
