import frontend.GrammarAnalyse;
import frontend.Lexer;
import global.Enums;

import static global.StaticVariable.grammarOutput;
import static global.StaticVariable.root;

public class Compiler {
    public static void main(String[] args) {

        Lexer.lexer();
        GrammarAnalyse.parser();
    }
}
