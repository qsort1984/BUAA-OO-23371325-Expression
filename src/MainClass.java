import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            String def = scanner.nextLine();
            GhDef.setFuncDef(def);
        }

        n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < 3 * n; i++) {
            String def = scanner.nextLine();
            FuncDef.setFuncDef(def);
        }
        if (n == 1) {
            FuncDef.deduceFuncDef();
        }

        String input = scanner.nextLine();
        Lexer lexer = new Lexer(input.replaceAll("[ \t]",""));
        Parser parser = new Parser(lexer);
        Expression expr = parser.parseExpr();

        System.out.println(expr.getPoly().simplify());
    }
}
