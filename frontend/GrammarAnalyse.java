package frontend;

import global.Enums;
import global.SymbolPair;
import global.TreeNode;

import static global.StaticConst.*;
import static global.StaticVariable.*;

public class GrammarAnalyse {

    private static int index=1;

    public static void getNextSymbol(){
        System.out.println(symbol+" "+token);
        SymbolPair symbolPair;
        if(index>=symbolPairs.size()){
            return;
        }
        symbolPair =symbolPairs.get(index);
        token= symbolPair.token;
        symbol= symbolPair.symbolCode;
        index++;
    }

    public static Enums.SymbolCode secondSymbol(){
        if(index>=symbolPairs.size()) return null;
        return symbolPairs.get(index).symbolCode;
    }

    public static Enums.SymbolCode thirdSymbol(){
        if(index+1>=symbolPairs.size()) return null;
        return symbolPairs.get(index+1).symbolCode;
    }

    public static void error(Enums.ErrorCode errorCode){

    }



    public static void CompUnit(TreeNode node){

        token=symbolPairs.getFirst().token;
        symbol=symbolPairs.getFirst().symbolCode;

        while(DeclFirst.contains(symbol) && DeclSecond.contains(secondSymbol())
                && DeclThird.contains(thirdSymbol())){
            TreeNode DeclNode=new TreeNode("Decl");
            node.children.add(DeclNode);
            Decl(DeclNode);
        }

        while(FuncDefFirst.contains(symbol) && FuncDefSecond.contains(secondSymbol())
                && FuncDefThird.contains(thirdSymbol())){
            TreeNode FuncDefNode=new TreeNode("FuncDef");
            node.children.add(FuncDefNode);
            FuncDef(FuncDefNode);
        }

        TreeNode MainFuncDefNode=new TreeNode("MainFuncDef");
        node.children.add(MainFuncDefNode);
        MainFuncDef(MainFuncDefNode);
        System.out.println("<CompUnit>");
    }

    public static void Decl(TreeNode node){
        if(symbol.equals(Enums.SymbolCode.CONSTTK)){
            TreeNode ConstDeclNode=new TreeNode("ConstDecl");
            node.children.add(ConstDeclNode);
            ConstDecl(ConstDeclNode);
        }

        else if(symbol.equals(Enums.SymbolCode.INTTK)||symbol.equals(Enums.SymbolCode.CHARTK)){
            TreeNode VarDeclNode=new TreeNode("VarDecl");
            node.children.add(VarDeclNode);
            VarDecl(VarDeclNode);
        }
    }

    public static void ConstDecl(TreeNode node){
        if(symbol.equals(Enums.SymbolCode.CONSTTK)){
            TreeNode ConstNode=new TreeNode("const");
            node.children.add(ConstNode);
            getNextSymbol();

            TreeNode BTypeNode=new TreeNode("BType");
            node.children.add(BTypeNode);
            BType(BTypeNode);

            TreeNode ConstDefNode=new TreeNode("ConstDef");
            node.children.add(ConstDefNode);
            ConstDef(ConstDefNode);

            getNextSymbol();
            while(symbol.equals(Enums.SymbolCode.COMMA)){
                TreeNode CommaNode=new TreeNode(",");
                node.children.add(CommaNode);
                getNextSymbol();

                TreeNode ConstDefNode2=new TreeNode("ConstDef");
                node.children.add(ConstDefNode2);
                ConstDef(ConstDefNode2);
            }

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);
        }
        System.out.println("<ConstDecl>");
    }

    public static void BType(TreeNode node){
        node.children.add(new TreeNode(token));
        getNextSymbol();
    }

    public static void  ConstDef(TreeNode node){
        TreeNode IdentNode=new TreeNode(token);
        node.children.add(IdentNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.LBRACK)){
            TreeNode LBracketNode=new TreeNode("[");
            node.children.add(LBracketNode);
            getNextSymbol();

            TreeNode ConstExpNode=new TreeNode("ConstExp");
            node.children.add(ConstExpNode);
            ConstExp(ConstExpNode);

            if(!symbol.equals(Enums.SymbolCode.RBRACK)) error(Enums.ErrorCode.k);
            else{
                TreeNode RBracketNode=new TreeNode("]");
                node.children.add(RBracketNode);
                getNextSymbol();
            }
        }

        TreeNode EQNode=new TreeNode("=");
        node.children.add(EQNode);
        getNextSymbol();

        TreeNode ConstInitValNode=new TreeNode("ConstInitVal");
        node.children.add(ConstInitValNode);
        ConstInitVal(ConstInitValNode);

        System.out.println("<ConstDef>");
    }

    public static void ConstInitVal (TreeNode node){
        if(symbol.equals(Enums.SymbolCode.STRCON)){
            TreeNode StrConNode=new TreeNode(token);
            node.children.add(StrConNode);
            getNextSymbol();
        }
        else if(symbol.equals(Enums.SymbolCode.LBRACE)){
            TreeNode LBraceNode=new TreeNode("{");
            node.children.add(LBraceNode);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.RBRACE)){
                TreeNode RBraceNode=new TreeNode("}");
                node.children.add(RBraceNode);
                getNextSymbol();
            }
            else{
                TreeNode ConstExpNode=new TreeNode("ConstExp");
                node.children.add(ConstExpNode);
                ConstExp(ConstExpNode);

                while(symbol.equals(Enums.SymbolCode.COMMA)){
                    TreeNode CommaNode=new TreeNode(",");
                    node.children.add(CommaNode);
                    getNextSymbol();

                    TreeNode ConstExpNode2=new TreeNode("ConstExp");
                    node.children.add(ConstExpNode2);
                    ConstExp(ConstExpNode2);
                }

                TreeNode RBraceNode=new TreeNode("}");
                node.children.add(RBraceNode);
                getNextSymbol();
            }
        }
        else if(UnaryExpFirst.contains(symbol)){
            TreeNode ConstExpNode=new TreeNode("ConstExp");
            node.children.add(ConstExpNode);
            ConstExp(ConstExpNode);
        }

        System.out.println("<ConstInitVal>");
    }

    public static void VarDecl(TreeNode node){
        TreeNode BTypeNode=new TreeNode("BType");
        node.children.add(BTypeNode);
        BType(BTypeNode);

        TreeNode VarDefNode=new TreeNode("VarDef");
        node.children.add(VarDefNode);
        VarDef(VarDefNode);

        while(symbol.equals(Enums.SymbolCode.COMMA)){
            TreeNode CommaNode=new TreeNode(",");
            node.children.add(CommaNode);
            getNextSymbol();

            TreeNode VarDefNode2=new TreeNode("VarDef");
            node.children.add(VarDefNode2);
            VarDef(VarDefNode2);
        }

        if(symbol.equals(Enums.SymbolCode.SEMICN)){
            TreeNode SemicNode=new TreeNode(";");
            node.children.add(SemicNode);
            getNextSymbol();
        }

        else error(Enums.ErrorCode.i);

        System.out.println("<VarDecl>");
    }

    public static void VarDef(TreeNode node){
        TreeNode IdentNode=new TreeNode(token);
        node.children.add(IdentNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.LBRACK)) {
            TreeNode LBracketNode = new TreeNode("[");
            node.children.add(LBracketNode);
            getNextSymbol();

            TreeNode ConstExpNode = new TreeNode("ConstExp");
            node.children.add(ConstExpNode);
            ConstExp(ConstExpNode);

            if (!symbol.equals(Enums.SymbolCode.RBRACE)) error(Enums.ErrorCode.k);
            else {
                TreeNode RBracketNode = new TreeNode("]");
                node.children.add(RBracketNode);
                getNextSymbol();
            }
        }

        if(symbol.equals(Enums.SymbolCode.ASSIGN)){
            TreeNode EQNode=new TreeNode("=");
            node.children.add(EQNode);
            getNextSymbol();

            TreeNode InitValNode=new TreeNode("InitVal");
            node.children.add(InitValNode);
            InitVal(InitValNode);
        }


        System.out.println("<VarDef>");
    }

    public static void InitVal(TreeNode node){
        if(symbol.equals(Enums.SymbolCode.LBRACE)){
            TreeNode LBraceNode=new TreeNode("{");
            node.children.add(LBraceNode);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.RBRACE)){
                TreeNode RBraceNode=new TreeNode("}");
                node.children.add(RBraceNode);
                getNextSymbol();
            }

            else{
                TreeNode ExpNode;
                ExpNode = new TreeNode("Exp");
                node.children.add(ExpNode);
                Exp(ExpNode);

                while(symbol.equals(Enums.SymbolCode.COMMA)){
                    TreeNode CommaNode=new TreeNode(",");
                    node.children.add(CommaNode);
                    getNextSymbol();

                    TreeNode ExpNode2=new TreeNode("Exp");
                    node.children.add(ExpNode2);
                    Exp(ExpNode2);
                }

                TreeNode RBraceNode=new TreeNode("}");
                node.children.add(RBraceNode);
                getNextSymbol();
            }
        }

        else if(symbol.equals(Enums.SymbolCode.STRCON)){
            TreeNode StrConNode=new TreeNode(token);
            node.children.add(StrConNode);
            getNextSymbol();
        }

        else if(UnaryExpFirst.contains(symbol)){
            TreeNode ExpNode=new TreeNode("Exp");
            node.children.add(ExpNode);
            Exp(ExpNode);
        }

        System.out.println("<InitVal>");
    }

    public static void FuncDef(TreeNode node){
        TreeNode FuncTypeNode=new TreeNode("FuncType");
        node.children.add(FuncTypeNode);
        FuncType(FuncTypeNode);

        TreeNode IdentNode=new TreeNode(token);
        node.children.add(IdentNode);
        getNextSymbol();

        TreeNode LParenNode=new TreeNode("(");
        node.children.add(LParenNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.INTTK)||symbol.equals(Enums.SymbolCode.CHARTK)){
            TreeNode FuncFParamsNode=new TreeNode("FuncFParams");
            node.children.add(FuncFParamsNode);
            FuncFParams(FuncFParamsNode);
        }

        if(symbol.equals(Enums.SymbolCode.RPARENT)){
            TreeNode RParenNode=new TreeNode(")");
            node.children.add(RParenNode);
            getNextSymbol();
        }
        else error(Enums.ErrorCode.j);

        TreeNode BlockNode=new TreeNode("Block");
        node.children.add(BlockNode);
        Block(BlockNode);

        System.out.println("<FuncDef>");
    }

    public static void MainFuncDef(TreeNode node){
        TreeNode IntNode=new TreeNode("int");
        node.children.add(IntNode);
        getNextSymbol();

        TreeNode MainNode=new TreeNode("main");
        node.children.add(MainNode);
        getNextSymbol();

        TreeNode LParenNode=new TreeNode("(");
        node.children.add(LParenNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.RPARENT)){
            TreeNode RParenNode=new TreeNode(")");
            node.children.add(RParenNode);
            getNextSymbol();
        }
        else error(Enums.ErrorCode.j);

        TreeNode BlockNode=new TreeNode("Block");
        node.children.add(BlockNode);
        Block(BlockNode);

        System.out.println("<MainFuncDef>");
    }

    public static void FuncType(TreeNode node){
        node.children.add(new TreeNode(token));
        getNextSymbol();
    }

    public static void FuncFParams(TreeNode node){
        TreeNode FuncFParamNode=new TreeNode("FuncFParam");
        node.children.add(FuncFParamNode);
        FuncFParam(FuncFParamNode);

        while(symbol.equals(Enums.SymbolCode.COMMA)){
            TreeNode CommaNode=new TreeNode(",");
            node.children.add(CommaNode);
            getNextSymbol();

            TreeNode FuncFParamNode2=new TreeNode("FuncFParam");
            node.children.add(FuncFParamNode2);
            FuncFParam(FuncFParamNode2);
        }

        System.out.println("<FuncFParams>");
    }

    public static void FuncFParam(TreeNode node){
        TreeNode BTypeNode=new TreeNode("BType");
        node.children.add(BTypeNode);
        BType(BTypeNode);

        TreeNode IdentNode=new TreeNode(token);
        node.children.add(IdentNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.LBRACK)){
            TreeNode LBracketNode=new TreeNode("[");
            node.children.add(LBracketNode);
            getNextSymbol();
        }

        if(!symbol.equals(Enums.SymbolCode.RBRACK)){
            TreeNode RBracketNode=new TreeNode("]");
            node.children.add(RBracketNode);
            getNextSymbol();
        }
        else error(Enums.ErrorCode.k);

        System.out.println("<FuncFParam>");

    }

    public static void Block(TreeNode node){
        TreeNode LBraceNode=new TreeNode("{");
        node.children.add(LBraceNode);
        getNextSymbol();

        while(!symbol.equals(Enums.SymbolCode.RBRACE)){
            TreeNode BlockItemNode=new TreeNode("BlockItem");
            node.children.add(BlockItemNode);
            BlockItem(BlockItemNode);
        }

        TreeNode RBraceNode=new TreeNode("}");
        node.children.add(RBraceNode);
        getNextSymbol();

        System.out.println("<Block>");
    }

    public static void BlockItem(TreeNode node){
        if(DeclFirst.contains(symbol)){
            TreeNode DeclNode=new TreeNode("Decl");
            node.children.add(DeclNode);
            Decl(DeclNode);
        }

        else if(StmtFirst.contains(symbol)){
            TreeNode StmtNode=new TreeNode("Stmt");
            node.children.add(StmtNode);
            Stmt(StmtNode);
        }

    }

    public static void Stmt(TreeNode node){

        Enums.SymbolCode s=secondSymbol();

        if(symbol.equals(Enums.SymbolCode.SEMICN)){
            TreeNode SemicNode=new TreeNode(";");
            node.children.add(SemicNode);
            getNextSymbol();
        }

        else if(UnaryExpFirst.contains(symbol) && (s==null || (!s.equals(Enums.SymbolCode.ASSIGN)
                && !s.equals(Enums.SymbolCode.LBRACK)))){
            TreeNode ExpNode=new TreeNode("Exp");
            node.children.add(ExpNode);
            Exp(ExpNode);

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);
        }

        else if(symbol.equals(Enums.SymbolCode.IDENFR)){
            TreeNode LValNode=new TreeNode("LVal");
            node.children.add(LValNode);
            LVal(LValNode);

            TreeNode EQNode=new TreeNode("=");
            node.children.add(EQNode);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.GETINTTK) || symbol.equals(Enums.SymbolCode.GETCHARTK)){

                TreeNode GetNode=new TreeNode(token);
                node.children.add(GetNode);
                getNextSymbol();

                TreeNode LParenNode=new TreeNode("(");
                node.children.add(LParenNode);
                getNextSymbol();

                if(symbol.equals(Enums.SymbolCode.RPARENT)){
                    TreeNode RParenNode=new TreeNode(")");
                    node.children.add(RParenNode);
                    getNextSymbol();
                }
                else error(Enums.ErrorCode.j);
            }

            else{
                TreeNode ExpNode=new TreeNode("Exp");
                node.children.add(ExpNode);
                getNextSymbol();
            }

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);

        }

        else if(symbol.equals(Enums.SymbolCode.LBRACE)){
            TreeNode BlockNode=new TreeNode("Block");
            node.children.add(BlockNode);
            Block(BlockNode);
        }

        else if(symbol.equals(Enums.SymbolCode.IFTK)){
            TreeNode IfNode=new TreeNode("If");
            node.children.add(IfNode);
            getNextSymbol();

            TreeNode LParenNode=new TreeNode("(");
            node.children.add(LParenNode);
            getNextSymbol();

            TreeNode CondNode=new TreeNode("Cond");
            node.children.add(CondNode);
            Cond(CondNode);

            if(symbol.equals(Enums.SymbolCode.RPARENT)){
                TreeNode RParenNode=new TreeNode(")");
                node.children.add(RParenNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.j);

            TreeNode StmtNode=new TreeNode("Stmt");
            node.children.add(StmtNode);
            Stmt(StmtNode);

            if(symbol.equals(Enums.SymbolCode.ELSETK)){
                TreeNode ElseNode=new TreeNode("Else");
                node.children.add(ElseNode);
                getNextSymbol();

                TreeNode StmtNode2=new TreeNode("Stmt");
                node.children.add(StmtNode2);
                Stmt(StmtNode2);
            }
        }

        else if(symbol.equals(Enums.SymbolCode.FORTK)){
            TreeNode ForNode=new TreeNode("for");
            node.children.add(ForNode);
            getNextSymbol();

            TreeNode LParenNode=new TreeNode("(");
            node.children.add(LParenNode);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.IDENFR)){
                TreeNode ForStmtNode=new TreeNode("ForStmt");
                node.children.add(ForStmtNode);
                ForStmt(ForStmtNode);
            }

            TreeNode SemicNode=new TreeNode(";");
            node.children.add(SemicNode);
            getNextSymbol();

            if(UnaryExpFirst.contains(symbol)){
                TreeNode CondNode=new TreeNode("Cond");
                node.children.add(CondNode);
                Cond(CondNode);
            }

            TreeNode SemicNode2=new TreeNode(";");
            node.children.add(SemicNode2);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.IDENFR)){
                TreeNode ForStmtNode=new TreeNode("ForStmt");
                node.children.add(ForStmtNode);
                ForStmt(ForStmtNode);
            }

            TreeNode RParenNode=new TreeNode(")");
            node.children.add(RParenNode);
            getNextSymbol();

            TreeNode StmtNode=new TreeNode("Stmt");
            node.children.add(StmtNode);
            Stmt(StmtNode);
        }

        else if(symbol.equals(Enums.SymbolCode.BREAKTK) || symbol.equals(Enums.SymbolCode.CONTINUETK)){
            TreeNode BCNode=new TreeNode(token);
            node.children.add(BCNode);
            getNextSymbol();

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);
        }

        else if(symbol.equals(Enums.SymbolCode.RETURNTK)){
            TreeNode ReturnNode=new TreeNode("return");
            node.children.add(ReturnNode);
            getNextSymbol();

            if(UnaryExpFirst.contains(symbol)){
                TreeNode ExpNode=new TreeNode("Exp");
                node.children.add(ExpNode);
                Exp(ExpNode);
            }

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);
        }

        else if(symbol.equals(Enums.SymbolCode.PRINTFTK)){
            TreeNode PrintNode=new TreeNode("print");
            node.children.add(PrintNode);
            getNextSymbol();

            TreeNode LParenNode=new TreeNode("(");
            node.children.add(LParenNode);
            getNextSymbol();

            TreeNode StrConNode=new TreeNode(token);
            node.children.add(StrConNode);
            getNextSymbol();

            while(symbol.equals(Enums.SymbolCode.COMMA)){
                TreeNode CommaNode=new TreeNode(",");
                node.children.add(CommaNode);
                getNextSymbol();

                TreeNode ExpNode=new TreeNode("Exp");
                node.children.add(ExpNode);
                Exp(ExpNode);
            }

            if(symbol.equals(Enums.SymbolCode.RPARENT)){
                TreeNode RParenNode=new TreeNode(")");
                node.children.add(RParenNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.j);

            if(symbol.equals(Enums.SymbolCode.SEMICN)){
                TreeNode SemicNode=new TreeNode(";");
                node.children.add(SemicNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.i);
        }

        System.out.println("<Stmt>");
    }

    public static void ForStmt(TreeNode node){
        TreeNode LValNode = new TreeNode("LVal");
        node.children.add(LValNode);
        LVal(LValNode);

        TreeNode EQNode = new TreeNode("=");
        node.children.add(EQNode);
        getNextSymbol();

        TreeNode ExpNode = new TreeNode("Exp");
        node.children.add(ExpNode);
        Exp(ExpNode);

        System.out.println("<ForStmt>");
    }

    public static void Exp(TreeNode node){
        TreeNode AddExpNode;
        AddExpNode = new TreeNode("AddExp");
        node.children.add(AddExpNode);
        AddExp(AddExpNode);

        System.out.println("<Exp>");
    }

    public static void Cond(TreeNode node){
        TreeNode LOrExpNode=new TreeNode("LOrExp");
        node.children.add(LOrExpNode);
        LOrExp(LOrExpNode);

        System.out.println("<Cond>");
    }

    public static void LVal(TreeNode node){
        TreeNode IdentNode=new TreeNode(token);
        node.children.add(IdentNode);
        getNextSymbol();

        if(symbol.equals(Enums.SymbolCode.LBRACK)){
            TreeNode LBracketNode=new TreeNode("[");
            node.children.add(LBracketNode);
            getNextSymbol();

            TreeNode ExpNode=new TreeNode("Exp");
            node.children.add(ExpNode);
            Exp(ExpNode);

            if(symbol.equals(Enums.SymbolCode.RBRACK)){
                TreeNode RBracketNode=new TreeNode("]");
                node.children.add(RBracketNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.k);
        }

        System.out.println("<LVal>");
    }

    public static void PrimaryExp(TreeNode node){
        if(symbol.equals(Enums.SymbolCode.LPARENT)){
            TreeNode LParenNode=new TreeNode("(");
            node.children.add(LParenNode);
            getNextSymbol();

            TreeNode ExpNode=new TreeNode("Exp");
            node.children.add(ExpNode);
            Exp(ExpNode);

            if(symbol.equals(Enums.SymbolCode.RPARENT)){
                TreeNode RParenNode=new TreeNode(")");
                node.children.add(RParenNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.j);
        }

        else if(symbol.equals(Enums.SymbolCode.IDENFR)){
            TreeNode LValNode=new TreeNode("LVal");
            node.children.add(LValNode);
            LVal(LValNode);
        }

        else if(symbol.equals(Enums.SymbolCode.INTCON)){
            TreeNode NumberNode=new TreeNode("Number");
            node.children.add(NumberNode);
            Number(NumberNode);
        }

        else if(symbol.equals(Enums.SymbolCode.CHRCON)){
            TreeNode CharNode=new TreeNode("Character");
            node.children.add(CharNode);
            Character(CharNode);
        }

        System.out.println("<PrimaryExp>");
    }

    public static void Number(TreeNode node){
        node.children.add(new TreeNode(token));
        getNextSymbol();
        System.out.println("<Number>");
    }

    public static void Character(TreeNode node){
        node.children.add(new TreeNode(token));
        getNextSymbol();
        System.out.println("<Character>");
    }

    public static void UnaryExp(TreeNode node){
        Enums.SymbolCode s=secondSymbol();
        if(symbol.equals(Enums.SymbolCode.IDENFR) && s!=null && s.equals(Enums.SymbolCode.LPARENT)){
            TreeNode IdentNode=new TreeNode(token);
            node.children.add(IdentNode);
            getNextSymbol();

            TreeNode LParenNode=new TreeNode("(");
            node.children.add(LParenNode);
            getNextSymbol();

            if(UnaryExpFirst.contains(symbol)){
                TreeNode FuncRParamsNode=new TreeNode("FuncRParams");
                node.children.add(FuncRParamsNode);
                FuncRParams(FuncRParamsNode);
            }

            if(symbol.equals(Enums.SymbolCode.RPARENT)){
                TreeNode RParenNode=new TreeNode(")");
                node.children.add(RParenNode);
                getNextSymbol();
            }
            else error(Enums.ErrorCode.j);
        }

        else if(token.equals("+") || token.equals("-") || token.equals("!")){
            TreeNode UnaryOpNode=new TreeNode("UnaryOp");
            node.children.add(UnaryOpNode);
            UnaryOp(UnaryOpNode);

            TreeNode UnaryExpNode;
            UnaryExpNode = new TreeNode("UnaryExp");
            node.children.add(UnaryExpNode);
            UnaryExp(UnaryExpNode);
        }

        else{
            TreeNode PrimaryExpNode=new TreeNode("PrimaryExp");
            node.children.add(PrimaryExpNode);
            PrimaryExp(PrimaryExpNode);
        }

        System.out.println("<UnaryExp>");
    }

    public static void UnaryOp(TreeNode node){
        node.children.add(new TreeNode(token));
        getNextSymbol();
    }

    public static void FuncRParams(TreeNode node){
        TreeNode ExpNode=new TreeNode("Exp");
        node.children.add(ExpNode);
        Exp(ExpNode);

        while(symbol.equals(Enums.SymbolCode.COMMA)){
            TreeNode CommaNode=new TreeNode(",");
            node.children.add(CommaNode);
            getNextSymbol();

            TreeNode ExpNode2=new TreeNode("Exp");
            node.children.add(ExpNode2);
            Exp(ExpNode2);
        }

        System.out.println("<FuncRParams>");
    }

    public static void MulExp(TreeNode node){
        TreeNode UnaryExpNode=new TreeNode("UnaryExp");
        node.children.add(UnaryExpNode);
        UnaryExp(UnaryExpNode);

        while(token.equals("*") || token.equals("/") || token.equals("%")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode UnaryExpNode2=new TreeNode("UnaryExp");
            node.children.add(UnaryExpNode2);
            UnaryExp(UnaryExpNode2);
        }

        System.out.println("<MulExp>");
    }

    public static void AddExp(TreeNode node){
        TreeNode MulExpNode=new TreeNode("MulExp");
        node.children.add(MulExpNode);
        MulExp(MulExpNode);

        while(token.equals("+") || token.equals("-")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode MulExpNode2=new TreeNode("MulExp");
            node.children.add(MulExpNode2);
            MulExp(MulExpNode2);
        }

        System.out.println("<AddExp>");
    }

    public static void RelExp(TreeNode node){
        TreeNode AddExpNode;
        AddExpNode = new TreeNode("AddExp");
        node.children.add(AddExpNode);
        AddExp(AddExpNode);

        while(token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode AddExpNode2;
            AddExpNode2 = new TreeNode("AddExp");
            node.children.add(AddExpNode2);
            AddExp(AddExpNode2);
        }

        System.out.println("<RelExp>");
    }

    public static void EqExp(TreeNode node){
        TreeNode RelExpNode=new TreeNode("RelExp");
        node.children.add(RelExpNode);
        RelExp(RelExpNode);

        while(token.equals("==") || token.equals("!=")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode RelExpNode2=new TreeNode("RelExp");
            node.children.add(RelExpNode2);
            RelExp(RelExpNode2);
        }

        System.out.println("<EqExp>");
    }

    public static void LAndExp(TreeNode node){
        TreeNode EqExpNode=new TreeNode("EqExp");
        node.children.add(EqExpNode);
        EqExp(EqExpNode);

        while(token.equals("&&")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode EqExpNode2=new TreeNode("EqExp");
            node.children.add(EqExpNode2);
            EqExp(EqExpNode2);
        }

        System.out.println("<LAndExp>");
    }

    public static void LOrExp(TreeNode node){
        TreeNode LAndExpNode=new TreeNode("LAndExp");
        node.children.add(LAndExpNode);
        LAndExp(LAndExpNode);

        while(token.equals("||")){
            TreeNode opNode=new TreeNode(token);
            node.children.add(opNode);
            getNextSymbol();

            TreeNode LAndExpNode2=new TreeNode("LAndExp");
            node.children.add(LAndExpNode2);
            LAndExp(LAndExpNode2);
        }

        System.out.println("<LOrExp>");
    }

    public static void ConstExp(TreeNode node){
        TreeNode AddExpNode=new TreeNode("AddExp");
        node.children.add(AddExpNode);
        AddExp(AddExpNode);
    }
}