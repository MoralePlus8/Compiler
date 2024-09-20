package global;

import java.util.ArrayList;

public class StaticConst {
    public static ArrayList<StaticClass.SymbolCode> keywordsTable = new ArrayList<>(){{
        add(StaticClass.SymbolCode.MAINTK);
        add(StaticClass.SymbolCode.CONSTTK);
        add(StaticClass.SymbolCode.INTTK);
        add(StaticClass.SymbolCode.CHARTK);
        add(StaticClass.SymbolCode.BREAKTK);
        add(StaticClass.SymbolCode.CONTINUETK);
        add(StaticClass.SymbolCode.IFTK);
        add(StaticClass.SymbolCode.ELSETK);
        add(StaticClass.SymbolCode.FORTK);
        add(StaticClass.SymbolCode.GETINTTK);
        add(StaticClass.SymbolCode.GETCHARTK);
        add(StaticClass.SymbolCode.PRINTFTK);
        add(StaticClass.SymbolCode.RETURNTK);
        add(StaticClass.SymbolCode.VOIDTK);
    }};
}
