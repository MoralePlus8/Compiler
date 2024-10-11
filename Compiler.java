import frontend.GrammarAnalyse;
import frontend.Lexer;
import frontend.SemanticAnalyse;
import global.Enums;

import java.util.ArrayList;

import static global.StaticVariable.grammarOutput;
import static global.StaticVariable.root;

public class Compiler {

    public static void main(String[] args) {
        Lexer.lexer();
        GrammarAnalyse.parser();
        SemanticAnalyse.treeParser();
    }
}
