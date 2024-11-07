package global;

public class Enums {

    public enum SymbolCode {
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

    public enum ErrorCode {
        a, b, c, d, e, f, g, h, i, j, k, l, m;
    }

    public enum V {
        CompUnit, Decl, ConstDecl, BType, ConstDef, ConstInitVal,
        VarDecl, VarDef, InitVal, FuncDef, MainFuncDef, FuncType,
        FuncFParams, FuncFParam, Block, BlockItem, Stmt, ForStmt,
        Exp, Cond, LVal, PrimaryExp, Number, Character, UnaryExp,
        UnaryOp, FuncRParams, MulExp, AddExp, RelExp, EqExp, LAndExp,
        ConstExp, IDENFR, INTCON, LOrExp,
        STRCON, CHRCON, MAINTK, CONSTTK, INTTK, CHARTK, BREAKTK,
        CONTINUETK, IFTK, ELSETK, NOT, AND, OR, FORTK, GETINTTK,
        GETCHARTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT,
        DIV, MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN,
        COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE;;
    }

    public enum MidCodeOp{

        PLUS, //+
        MINU, //-
        MULT, //*
        DIV,  // /
        MOD,  // %
        LSS,  //<
        LEQ,  //<=
        GRE,  //>
        GEQ,  //>=
        EQL,  //==
        NEQ,  //!=
        ASSIGN,  //=
        MAIN,   //主函数
        GOTO,  //无条件跳转
        JUMP,  //跳转标记
        BZ,    //不满足条件跳转
        PUSH,  //函数调用时参数传递
        CALL,  //函数调用
        RET,   //函数返回语句
        RETVALUE, //有返回值函数返回的结果
        READINT,  //读int
        READCHAR, //读char
        PRINT, //写
        LABEL, //标号,区分不同作用域和标号
        ARRAY, //数组
        VAR,   //变量
        FUNC,  //函数定义
        PARAM, //函数参数
        GETARRAY,  //取数组的值  t = a[]
        PUTARRAY,  //给数组赋值  a[] = t
        EXIT,  //退出 main最后
    }

    public enum mipsCodeOp{
        add,// +
        sub,// -
        mul,// *
        div,// /
        and,//
        slt,// <
        sle,// <=
        sgt,// >
        sge,// >=
        seq,// ==
        sne,// !=
        sll,//
        li,//
        la,//
        move,//
        beqz,//
        lw,//
        sw,//
        mflo,// 取LO寄存器
        mfhi,//取HI寄存器
        j,//
        jal,//
        jr,//
        label,//
        block,
        data,
        syscall,//
    }
}
