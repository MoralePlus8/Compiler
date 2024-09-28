package global;

public class Enums {

    public static enum SymbolCode{
        IDENFR,
        INTCON,
        STRCON,
        CHRCON,
        MAINTK,
        CONSTTK,
        INTTK,
        CHARTK,
        BREAKTK,
        CONTINUETK,
        IFTK,
        ELSETK,
        NOT,
        AND,
        OR,
        FORTK,
        GETINTTK,
        GETCHARTK,
        PRINTFTK,
        RETURNTK,
        PLUS,
        MINU,
        VOIDTK,
        MULT,
        DIV,
        MOD,
        LSS,
        LEQ,
        GRE,
        GEQ,
        EQL,
        NEQ,
        ASSIGN,
        SEMICN,
        COMMA,
        LPARENT,
        RPARENT,
        LBRACK,
        RBRACK,
        LBRACE,
        RBRACE;
    }

    public static enum ErrorCode{
        a,b,c,d,e,f,g,h,i,j,k,l,m;
    }

    public static enum Vn{
        CompUnit, Decl, ConstDecl, BType, ConstDef, ConstInitVal,
        VarDecl, VarDef, InitVal, FuncDef, MainFuncDef, FuncType,
        FuncFParams, FuncFParam, Block, BlockItem, Stmt, ForStmt,
        Exp, Cond, LVal, PrimaryExp, Number, Character, UnaryExp,
        UnaryOp, FuncRParams, MulExp, AddExp, RelExp, EqExp, LAndExp,
        ConstExp,
    }
}
