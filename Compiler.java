import frontend.GrammarAnalyse;
import frontend.Lexer;

import static global.StaticVariable.root;

public class Compiler {
    public static void main(String[] args) {
        Lexer.lexer();
        GrammarAnalyse.CompUnit(root);
    }
}
