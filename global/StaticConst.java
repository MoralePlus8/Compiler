package global;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
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

    public static ArrayList<Enums.SymbolCode>  DeclFirst=new ArrayList<>();
    public static ArrayList<Enums.SymbolCode>  DeclSecond=new ArrayList<>();
    public static ArrayList<Enums.SymbolCode>  DeclThird=new ArrayList<>();
    static{
        DeclFirst.add(Enums.SymbolCode.CONSTTK);
        DeclFirst.add(Enums.SymbolCode.INTTK);
        DeclFirst.add(Enums.SymbolCode.CHARTK);

        DeclSecond.add(Enums.SymbolCode.INTTK);
        DeclSecond.add(Enums.SymbolCode.CHARTK);
        DeclSecond.add(Enums.SymbolCode.IDENFR);

        DeclThird.add(Enums.SymbolCode.IDENFR);
        DeclThird.add(Enums.SymbolCode.COMMA);
        DeclThird.add(Enums.SymbolCode.SEMICN);
        DeclThird.add(Enums.SymbolCode.ASSIGN);
    }

    public static ArrayList<Enums.SymbolCode>  FuncDefFirst=new ArrayList<>();
    public static ArrayList<Enums.SymbolCode>  FuncDefSecond=new ArrayList<>();
    public static ArrayList<Enums.SymbolCode>  FuncDefThird=new ArrayList<>();
    static{
        FuncDefFirst.add(Enums.SymbolCode.VOIDTK);
        FuncDefFirst.add(Enums.SymbolCode.INTTK);
        FuncDefFirst.add(Enums.SymbolCode.CHARTK);

        FuncDefSecond.add(Enums.SymbolCode.IDENFR);

        FuncDefThird.add(Enums.SymbolCode.LPARENT);
    }

    public static ArrayList<Enums.SymbolCode>  UnaryExpFirst=new ArrayList<>();
    static{
        UnaryExpFirst.add(Enums.SymbolCode.IDENFR);
        UnaryExpFirst.add(Enums.SymbolCode.LPARENT);
        UnaryExpFirst.add(Enums.SymbolCode.PLUS);
        UnaryExpFirst.add(Enums.SymbolCode.MINU);
        UnaryExpFirst.add(Enums.SymbolCode.NOT);
        UnaryExpFirst.add(Enums.SymbolCode.INTCON);
        UnaryExpFirst.add(Enums.SymbolCode.CHRCON);
    }

    public static ArrayList<Enums.SymbolCode> StmtFirst=new ArrayList<>();
    static{
        StmtFirst.add(Enums.SymbolCode.IFTK);
        StmtFirst.add(Enums.SymbolCode.FORTK);
        StmtFirst.add(Enums.SymbolCode.CONTINUETK);
        StmtFirst.add(Enums.SymbolCode.BREAKTK);
        StmtFirst.add(Enums.SymbolCode.RETURNTK);
        StmtFirst.add(Enums.SymbolCode.PRINTFTK);
        StmtFirst.add(Enums.SymbolCode.IDENFR);
        StmtFirst.add(Enums.SymbolCode.SEMICN);
        StmtFirst.add(Enums.SymbolCode.LBRACE);
        StmtFirst.add(Enums.SymbolCode.LPARENT);
        StmtFirst.add(Enums.SymbolCode.PLUS);
        StmtFirst.add(Enums.SymbolCode.MINU);
        StmtFirst.add(Enums.SymbolCode.NOT);
        StmtFirst.add(Enums.SymbolCode.INTCON);
        StmtFirst.add(Enums.SymbolCode.CHRCON);
    }





}
