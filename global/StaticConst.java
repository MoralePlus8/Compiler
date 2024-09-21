package global;


import java.util.Arrays;
import java.util.HashMap;

public class StaticConst {
    public static HashMap<String, Enums.SymbolCode> keywordsTable = new HashMap<>();
    static {
        keywordsTable.put("const", Enums.SymbolCode.CONSTTK);
        keywordsTable.put("if", Enums.SymbolCode.IFTK);
        keywordsTable.put("main", Enums.SymbolCode.MAINTK);
        keywordsTable.put("int", Enums.SymbolCode.INTTK);
        keywordsTable.put("char", Enums.SymbolCode.CHARTK);
        keywordsTable.put("break", Enums.SymbolCode.BREAKTK);
        keywordsTable.put("return", Enums.SymbolCode.RETURNTK);
        keywordsTable.put("else", Enums.SymbolCode.ELSETK);
        keywordsTable.put("for", Enums.SymbolCode.FORTK);
        keywordsTable.put("continue", Enums.SymbolCode.CONTINUETK);
        keywordsTable.put("getint", Enums.SymbolCode.GETINTTK);
        keywordsTable.put("getchar", Enums.SymbolCode.GETCHARTK);
        keywordsTable.put("printf", Enums.SymbolCode.PRINTFTK);
        keywordsTable.put("void", Enums.SymbolCode.VOIDTK);
    }
}
