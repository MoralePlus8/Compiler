package backend;

import frontend.Lexer;
import frontend.SemanticAnalyse;
import global.Enums;
import global.MidCode;
import global.SymbolAttribute;
import global.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import static frontend.SemanticAnalyse.getIdent;
import static global.StaticVariable.*;

public class MidCodeGeneration {

    //转义字符转int
    static int espChar2int(char c){
        return switch (c){
            case 'a' -> 7;
            case 'b' -> 8;
            case 't' -> 9;
            case 'n' -> 10;
            case 'v' -> 11;
            case 'f' -> 12;
            case '"' -> 34;
            case '\'' -> 39;
            case '\\' -> 92;
            case '0' -> 0;
            default -> -1;
        };
    }

    //char转int
    public static int charCon2int(String charConst){
        char c=charConst.charAt(1);
        if(c!='\\') return c;
        c=charConst.charAt(2);
        return espChar2int(c);
    }

    //string转int数组
    public static ArrayList<Integer> strCon2intArr(String stringConst){
        ArrayList<Integer> arr=new ArrayList<>();
        for(int i=1;i<stringConst.length()-1;i++){
            char c=stringConst.charAt(i);
            if(c!='\\') arr.add((int)c);
            else{
                c=stringConst.charAt(++i);
                arr.add(espChar2int(c));
            }
        }
        return arr;
    }

    private static void add(Enums.MidCodeOp op, String r, String a, String b, int scope){
        midCodes.add(new MidCode(op, r, a, b, scope));
    }

    static String result(int T){
        return "$t"+String.valueOf(T);
    }

    static String jump(int J){
        return String.valueOf(J);
    }

    static String label(int L){
        return "LABEL"+String.valueOf(L);
    }





    static void CompUnitHandler(TreeNode node){
        scopeCounter=1;
        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.Decl)){
                DeclHandler(child,1);
            }
            else if(child.nodeType.equals(Enums.V.FuncDef)){
                FuncDefHandler(child);
            }
            else if(child.nodeType.equals(Enums.V.MainFuncDef)){
                MainFuncDefHandler(child);
            }
        }
    }

    static void DeclHandler(TreeNode node, int scope){
        if(node.children.get(0).nodeType.equals(Enums.V.ConstDecl)){
            ConstDeclHandler(node.children.get(0), scope);
        }
        else if(node.children.get(0).nodeType.equals(Enums.V.VarDecl)){
            VarDeclHandler(node.children.get(0), scope);
        }
    }

    static void ConstDeclHandler(TreeNode node, int scope){
        String type=BTypeHandler(node.children.get(1));
        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.ConstDef)){
                ConstDefHandler(child, type, scope);
            }
        }
    }

    static String BTypeHandler(TreeNode node){
        return node.children.get(0).symbolName;
    }

    static void ConstDefHandler(TreeNode node, String type, int scope){
        String r=node.children.get(0).symbolName;

        if(node.children.get(1).symbolName.equals("[")){//常量数组定义
            String t1=ConstExpHandler(node.children.get(2));
            add(Enums.MidCodeOp.ARRAY, r, type, t1, scope);
            ConstInitValHandler(node.children.get(5), r, type, scope, Integer.parseInt(t1));
        }

        else{//普通常量定义
            ConstInitValHandler(node.children.get(2), r, type, scope, 1);
        }
    }

    static void ConstInitValHandler(TreeNode node, String ident, String type, int scope, int length){

        if(node.children.get(0).symbolName.equals("{")){//数组初值
            int cnt=0;
            for(TreeNode child : node.children){
                if(child.nodeType.equals(Enums.V.ConstExp)){
                    String t1=ConstExpHandler(child);
                    add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(cnt), t1, scope);
                    cnt++;
                }
            }
        }

        else if(node.children.get(0).nodeType.equals(Enums.V.STRCON)){
            ArrayList<Integer> arr=strCon2intArr(node.children.get(0).symbolName);
            for(int i=0;i<arr.size();i++){
                add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(i), arr.get(i).toString(), scope);
            }
            for(int i=arr.size();i<length;i++) add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(i), "0", scope);
        }

        else{//非数组初值
            String t1=ConstExpHandler(node.children.get(0));
            add(Enums.MidCodeOp.VAR, ident, type, t1, scope);
        }
    }

    static void VarDeclHandler(TreeNode node, int scope){
        String type=BTypeHandler(node.children.get(0));
        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.VarDef)){
                VarDefHandler(child, type, scope);
            }
        }
    }

    static void VarDefHandler(TreeNode node, String type, int scope){
        String ident=node.children.get(0).symbolName;

        int initIndex=0;
        for(int i=0; i<node.children.size(); i++){
            if(node.children.get(i).nodeType.equals(Enums.V.InitVal)){
                initIndex=i;
                break;
            }
        }

        if(initIndex==0){       //不分配初值
            if(node.children.size()==1){ //普通变量
                add(Enums.MidCodeOp.VAR, ident, type, null, scope);
            }

            else{                       //数组变量
                String t1=ConstExpHandler(node.children.get(2));
                add(Enums.MidCodeOp.ARRAY, ident, type, t1, scope);
            }
        }

        else{                   //分配初值
            String t1="1";
            if(node.children.get(1).symbolName.equals("[")){
                t1=ConstExpHandler(node.children.get(2));
                add(Enums.MidCodeOp.ARRAY, ident, type, t1, scope);
            }

            InitValHandler(node.children.get(initIndex), ident, type, scope, Integer.parseInt(t1));
        }
    }

    static void InitValHandler(TreeNode node, String ident, String type, int scope, int length){
        TreeNode child=node.children.get(0);
        if(child.nodeType.equals(Enums.V.Exp)){         //普通变量初值
            String t1=ExpHandler(child, scope);
            add(Enums.MidCodeOp.VAR, ident, type, t1, scope);
        }

        else if(child.nodeType.equals(Enums.V.STRCON)){ //字符串初值

            ArrayList<Integer> arr=strCon2intArr(child.symbolName);
            for(int i=0;i<arr.size();i++){
                add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(i), arr.get(i).toString(), scope);
            }
            for(int i=arr.size();i<length;i++) add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(i), "0", scope);
        }

        else{                                           //数组形式初值
            int cnt=0;
            for(TreeNode c: node.children){
                if(c.nodeType.equals(Enums.V.Exp)){
                    String t1=ExpHandler(c, scope);
                    add(Enums.MidCodeOp.PUTARRAY, ident, String.valueOf(cnt), t1, scope);
                    cnt++;
                }
            }
        }
    }

    static void FuncDefHandler(TreeNode node){
        int fpIndex=0;
        int temp=++labelCounter;
        String type=FuncTypeHandler(node.children.get(0));
        String ident=node.children.get(1).symbolName;

        for(int i=0;i<node.children.size();i++){
            if(node.children.get(i).nodeType.equals(Enums.V.FuncFParams)){
                fpIndex=i;
                break;
            }
        }

        add(Enums.MidCodeOp.LABEL, label(temp), "begin", null, 1);
        add(Enums.MidCodeOp.FUNC, ident, type, null, 1);

        if(fpIndex!=0){                               //有形参
            FuncFParamsHandler(node.children.get(fpIndex), scopeCounter+1);
        }

        BlockHandler(node.children.get(node.children.size()-1), 0, 1);

        add(Enums.MidCodeOp.RET, null, null, null, 1);
        add(Enums.MidCodeOp.LABEL, label(temp), "end", null, 1);
    }

    static void MainFuncDefHandler(TreeNode node){

        int temp=++labelCounter;
        add(Enums.MidCodeOp.LABEL, label(temp), "begin", null, 1);
        add(Enums.MidCodeOp.MAIN,null,null,null, 1);
        BlockHandler(node.children.get(node.children.size()-1), 0, 1);

        add(Enums.MidCodeOp.RET, null, null, null, 1);
        add(Enums.MidCodeOp.LABEL, label(temp), "end", null, 1);
    }

    static String FuncTypeHandler(TreeNode node){
        return node.children.get(0).symbolName;
    }

    static void FuncFParamsHandler(TreeNode node, int scope){
        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.FuncFParam)){
                FuncFParamHandler(child, scope);
            }
        }
    }

    static void FuncFParamHandler(TreeNode node, int scope){
        String type=BTypeHandler(node.children.get(0));
        String ident=node.children.get(1).symbolName;

        if(node.children.size()==2){            //普通变量参数
            add(Enums.MidCodeOp.PARAM, ident, type, "var", scope);
        }

        else{                                   //数组参数
            add(Enums.MidCodeOp.PARAM, ident, type, "arr", scope);
        }
    }

    static void BlockHandler(TreeNode node, int loop, int scope){

        int temp=++labelCounter;
        int ts=++scopeCounter;
        add(Enums.MidCodeOp.LABEL, label(temp), "begin", null, scope);

        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.BlockItem)){
                BlockItemHandler(child, loop, ts);
            }
        }

        add(Enums.MidCodeOp.LABEL, label(temp), "end", null, scope);
    }

    static void BlockItemHandler(TreeNode node, int loop, int scope){
        if(node.children.get(0).nodeType.equals(Enums.V.Decl)){
            DeclHandler(node.children.get(0), scope);
        }

        else{
            StmtHandler(node.children.get(0), loop, scope);
        }
    }

    static void StmtHandler(TreeNode node, int loop, int scope){
        TreeNode fc=node.children.get(0);

        if(fc.nodeType.equals(Enums.V.LVal)){
            if(node.children.get(2).nodeType.equals(Enums.V.GETINTTK)){
                add(Enums.MidCodeOp.READINT, result(++TCount),null,null, scope);
                LValHandler(node.children.get(0), false, result(TCount), scope);
            }
            else if(node.children.get(2).nodeType.equals(Enums.V.GETCHARTK)){
                add(Enums.MidCodeOp.READCHAR, result(++TCount),null,null, scope);
                LValHandler(node.children.get(0), false, result(TCount), scope);
            }
            else{
                String t1;
                t1 = ExpHandler(node.children.get(2), scope);
                LValHandler(node.children.get(0), false, t1, scope);
            }
        }

        else if(fc.nodeType.equals(Enums.V.Exp)){
            ExpHandler(fc, scope);
        }

        else if(fc.nodeType.equals(Enums.V.Block)){
            BlockHandler(fc, loop, scope);
        }

        else if(fc.nodeType.equals(Enums.V.IFTK)){
            if(node.children.size()==5){            //无else语句
                jumpCounter+=2;
                int temp=jumpCounter;

                CondHandler(node.children.get(2), temp, scope);
                add(Enums.MidCodeOp.GOTO, jump(temp-1), null, null, scope);

                add(Enums.MidCodeOp.JUMP, jump(temp), null, null, scope);
                StmtHandler(node.children.get(4), loop, scope);
                add(Enums.MidCodeOp.JUMP, jump(temp-1), null, null, scope);

            }

            else{                                   //有else语句
                jumpCounter+=3;
                int temp=jumpCounter;

                CondHandler(node.children.get(2), temp, scope);
                add(Enums.MidCodeOp.GOTO, jump(temp-2),null,null, scope);

                add(Enums.MidCodeOp.JUMP, jump(temp),null,null, scope);
                StmtHandler(node.children.get(4), loop, scope);
                add(Enums.MidCodeOp.GOTO, jump(temp-1),null,null, scope);

                add(Enums.MidCodeOp.JUMP, jump(temp-2),null,null, scope);
                StmtHandler(node.children.get(6), loop, scope);
                add(Enums.MidCodeOp.JUMP, jump(temp-1),null,null, scope);
            }
        }

        else if(fc.nodeType.equals(Enums.V.FORTK)){

            jumpCounter+=2;
            int temp=jumpCounter;
            TreeNode fs1=null, fs2=null, cond=null;
            if(node.children.get(2).nodeType.equals(Enums.V.ForStmt)){
                fs1=node.children.get(2);
            }
            if(node.children.get(node.children.size()-3).nodeType.equals(Enums.V.ForStmt)){
                fs2=node.children.get(node.children.size()-3);
            }
            for(TreeNode child : node.children){
                if(child.nodeType.equals(Enums.V.Cond)){
                    cond=child;
                    break;
                }
            }


            if(fs1!=null){
                ForStmtHandler(fs1, scope);
            }

            add(Enums.MidCodeOp.JUMP, jump(temp-1), "begin", null, scope);

            if(cond!=null){
                CondHandler(cond, temp, scope);
            }
            else{
                add(Enums.MidCodeOp.GOTO, jump(temp), null, null, scope);
            }

            add(Enums.MidCodeOp.GOTO, jump(temp-1), "end", null, scope);
            add(Enums.MidCodeOp.JUMP, jump(temp), null, null, scope);
            StmtHandler(node.children.get(node.children.size()-1), temp-1, scope);
            add(Enums.MidCodeOp.JUMP, jump(temp-1), "post", null, scope);

            if(fs2!=null){
                ForStmtHandler(fs2, scope);
            }
            add(Enums.MidCodeOp.GOTO, jump(temp-1), "begin", null, scope);
            add(Enums.MidCodeOp.JUMP, jump(temp-1), "end", null, scope);

        }

        else if(fc.nodeType.equals(Enums.V.BREAKTK)){
            add(Enums.MidCodeOp.GOTO, jump(loop), "end", null, scope);
        }

        else if(fc.nodeType.equals(Enums.V.CONTINUETK)){
            add(Enums.MidCodeOp.GOTO, jump(loop), "post", null, scope);
        }

        else if(fc.nodeType.equals(Enums.V.RETURNTK)){
            if(node.children.get(1).nodeType.equals(Enums.V.Exp)){
                String t1=ExpHandler(node.children.get(1), scope);
                add(Enums.MidCodeOp.RET, t1, null, null, scope);
            }
            else{
                add(Enums.MidCodeOp.RET, null,null,null, scope);
            }
        }

        else if(fc.nodeType.equals(Enums.V.PRINTFTK)){
            int cnt=0,left=1;
            String format=node.children.get(2).symbolName;

            for(int i=1;i<format.length()-1;i++){
                char c=format.charAt(i);
                if(c=='%'&&i+1<format.length()-1&&(format.charAt(i+1)=='d'||format.charAt(i+1)=='c')){
                    if(left<i){
                        add(Enums.MidCodeOp.PRINT, format.substring(left, i),null,null, scope);
                    }

                    c=format.charAt(++i);
                    left=i+1;
                    String t1=ExpHandler(node.children.get(4+cnt*2), scope);
                    cnt++;

                    if(c=='d'){
                        add(Enums.MidCodeOp.PRINT, t1,"int",null, scope);
                    }
                    else{
                        add(Enums.MidCodeOp.PRINT, t1,"char",null, scope);
                    }
                }

                if(c=='\\'){
                    if(left<i){
                        add(Enums.MidCodeOp.PRINT, format.substring(left, i),null,null, scope);
                    }
                    i++;
                    left=i+1;
                    add(Enums.MidCodeOp.PRINT, "\\n","escape",null, scope);
                }
            }
            if(left!=format.length()-1){
                add(Enums.MidCodeOp.PRINT, format.substring(left),null,null, scope);
            }
        }
    }

    static void ForStmtHandler(TreeNode node, int scope){
        String t1=ExpHandler(node.children.get(2), scope);
        LValHandler(node.children.get(0), false, t1, scope);
    }

    static String ExpHandler(TreeNode node, int scope){
        return AddExpHandler(node.children.get(0), scope);
    }

    static void CondHandler(TreeNode node, int J, int scope){
        LOrExpHandler(node.children.get(0), J, scope);
    }

    static String LValHandler(TreeNode node, boolean isGet, String val, int scope){

        String ident=node.children.get(0).symbolName;

        if(node.children.size()>1){//LVal → Ident '[' Exp ']'
            String t1=ExpHandler(node.children.get(2), scope);

            if(isGet){
                add(Enums.MidCodeOp.GETARRAY, result(++TCount), ident, t1, scope);
                return result(TCount);
            }
            else {
                add(Enums.MidCodeOp.PUTARRAY, ident, t1, val, scope);
                return null;
            }
        }

        else{                      //LVal → Ident
            if(isGet){
                return ident;
            }
           else {
                add(Enums.MidCodeOp.ASSIGN, ident, val,null, scope);
                return null;
            }
        }
    }

    static String PrimaryExpHandler(TreeNode node, int scope){

        if(node.children.get(0).symbolName.equals("(")){//PrimaryExp → '(' Exp ')'
            return ExpHandler(node.children.get(1), scope);
        }

        else if(node.children.get(0).nodeType.equals(Enums.V.Number)){//PrimaryExp → Number
            return NumberHandler(node.children.get(0));
        }

        else if(node.children.get(0).nodeType.equals(Enums.V.Character)){//PrimaryExp → Character
            return CharacterHandler(node.children.get(0));
        }

        else{//PrimaryExp → LVal
            return LValHandler(node.children.get(0), true, null, scope);
        }
    }

    static String NumberHandler(TreeNode node){
        return node.children.get(0).symbolName;
    }

    static String CharacterHandler(TreeNode node){
        return String.valueOf(charCon2int(node.children.get(0).symbolName));
    }

    static String UnaryExpHandler(TreeNode node, int scope){
        TreeNode child=node.children.get(0);

        if(child.nodeType.equals(Enums.V.PrimaryExp)){  //UnaryExp → PrimaryExp
            return PrimaryExpHandler(child, scope);
        }

        else if(child.nodeType.equals(Enums.V.IDENFR)){ //UnaryExp → Ident '(' [FuncRParams] ')'
            String ident=node.children.get(0).symbolName;
            SymbolAttribute sym= getIdent(ident, 1);
            String funcType= sym==null?"":sym.type;

            FuncRParamsHandler(node.children.get(2), scope);
            add(Enums.MidCodeOp.CALL, ident, null, null, scope);
            if(funcType.equals("IntFunc")){
                add(Enums.MidCodeOp.RETVALUE, result(++TCount), "int", null, scope);
            }

            else if(funcType.equals("CharFunc")){
                add(Enums.MidCodeOp.RETVALUE, result(++TCount), "char", null, scope);
            }
            return result(TCount);
        }

        else{                                           //UnaryExp → UnaryOp UnaryExp
            String t1=UnaryExpHandler(node.children.get(1), scope);
            String uop=child.children.get(0).symbolName;

            if(uop.equals("+")){
                return t1;
            }

            else if(uop.equals("-")){
                add(Enums.MidCodeOp.MINU, result(++TCount), "0", t1, scope);
                return result(TCount);
            }

            else{
                add(Enums.MidCodeOp.EQL, result(++TCount), "0", t1, scope);
                return result(TCount);
            }
        }
    }

    static void FuncRParamsHandler(TreeNode node, int scope){

        ArrayList<String> params=new ArrayList<>();
        for(TreeNode child : node.children){
            if(child.nodeType.equals(Enums.V.Exp)){
                params.add(ExpHandler(child, scope));

            }
        }

        for(String t: params){
            add(Enums.MidCodeOp.PUSH, t, null, null, scope);
        }
    }

    static String MulExpHandler(TreeNode node, int scope){
        if(node.children.size() == 1){//MulExp仅由一个UnaryExp组成
            return UnaryExpHandler(node.children.get(0), scope);
        }

        else {//MulExp由MulExp和UnaryExp组成

            String t1=MulExpHandler(node.children.get(0), scope);
            String t2=UnaryExpHandler(node.children.get(2), scope);

            String temp=node.children.get(1).symbolName;
            Enums.MidCodeOp op= Enums.MidCodeOp.MULT;
            if(temp.equals("/")) op=Enums.MidCodeOp.DIV;
            if(temp.equals("%")) op=Enums.MidCodeOp.MOD;

            add(op, result(++TCount), t1, t2, scope);
            return result(TCount);
        }
    }

    static String AddExpHandler(TreeNode node, int scope){
        if(node.children.size() == 1){//AddExp仅由一个MulExp组成
            return MulExpHandler(node.children.get(0), scope);
        }

        else {//AddExp由AddExp和MulExp组成

            String t1=AddExpHandler(node.children.get(0), scope);
            String t2=MulExpHandler(node.children.get(2), scope);
            Enums.MidCodeOp op=node.children.get(1).symbolName.equals("+")? Enums.MidCodeOp.PLUS: Enums.MidCodeOp.MINU;
            add(op, result(++TCount), t1, t2, scope);
            return result(TCount);
        }
    }

    static String RelExpHandler(TreeNode node, int scope){
        if(node.children.get(0).nodeType.equals(Enums.V.AddExp)){
            return AddExpHandler(node.children.get(0), scope);
        }

        else{
            Enums.MidCodeOp op=switch (node.children.get(1).symbolName){
                case "<" -> Enums.MidCodeOp.LSS;
                case ">" -> Enums.MidCodeOp.GRE;
                case "<=" -> Enums.MidCodeOp.LEQ;
                case ">=" -> Enums.MidCodeOp.GEQ;
                default -> null;
            };

            String t1=RelExpHandler(node.children.get(0), scope);
            String t2=AddExpHandler(node.children.get(2), scope);
            add(op, result(++TCount), t1, t2, scope);
            return result(TCount);
        }
    }

    static String EqExpHandler(TreeNode node, int scope){
        if(node.children.get(0).nodeType.equals(Enums.V.RelExp)){
            return RelExpHandler(node.children.get(0), scope);
        }

        else{
            String t1=EqExpHandler(node.children.get(0), scope);
            String t2=RelExpHandler(node.children.get(2), scope);
            Enums.MidCodeOp op=node.children.get(1).symbolName.equals("==")? Enums.MidCodeOp.EQL: Enums.MidCodeOp.NEQ;
            add(op, result(++TCount), t1, t2, scope);
            return result(TCount);
        }
    }

    static void LAndExpHandler(TreeNode node, int J, int scope){
        TreeNode fc=node.children.get(0);
        if(fc.nodeType.equals(Enums.V.LAndExp)){
            LAndExpHandler(fc, J, scope);
            String t1=EqExpHandler(node.children.get(2), scope);
            add(Enums.MidCodeOp.BZ, jump(J), t1, null, scope);
        }

        else{
            String t1=EqExpHandler(fc, scope);
            add(Enums.MidCodeOp.BZ, jump(J), t1, null, scope);
        }
    }

    static void LOrExpHandler(TreeNode node, int J, int scope){
        TreeNode fc=node.children.get(0);
        if(fc.nodeType.equals(Enums.V.LOrExp)){
            LOrExpHandler(fc, J, scope);
            LAndExpHandler(node.children.get(2), ++jumpCounter, scope);
        }
        else{
            LAndExpHandler(fc, ++jumpCounter, scope);
        }
        add(Enums.MidCodeOp.GOTO, jump(J), null, null, scope);
        add(Enums.MidCodeOp.JUMP, jump(jumpCounter), null, null, scope);
    }


    static String ConstExpHandler(TreeNode node){
        return String.valueOf(calConstExp(node, scopeCounter));
    }

    public static int calConstExp(TreeNode node, int scope){
        if(node.nodeType.equals(Enums.V.ConstExp)||node.nodeType==Enums.V.Exp){
            return calConstExp(node.children.get(0),scope);
        }
        if(node.nodeType.equals(Enums.V.AddExp)){
            if(node.children.size()>1){
                int t1=calConstExp(node.children.get(0),scope);
                int t2=calConstExp(node.children.get(2),scope);
                if(node.children.get(1).symbolName.equals("+"))return t1+t2;
                else return t1-t2;
            }
            else return calConstExp(node.children.get(0),scope);
        }
        if(node.nodeType.equals(Enums.V.MulExp)){
            if(node.children.size()>1){
                int t1=calConstExp(node.children.get(0),scope);
                int t2=calConstExp(node.children.get(2),scope);
                if(node.children.get(1).symbolName.equals("*"))return t1*t2;
                else if(node.children.get(1).symbolName.equals("/"))return t1/t2;
                else return t1%t2;
            }
            else return calConstExp(node.children.get(0),scope);
        }
        if(node.nodeType.equals(Enums.V.UnaryExp)){
            TreeNode fc=node.children.get(0);
            if(node.children.size()>1){
                if(fc.children.get(0).symbolName.equals("-")) return -1*calConstExp(node.children.get(1),scope);
                return calConstExp(node.children.get(1),scope);
            }
            else return calConstExp(fc,scope);
        }
        if(node.nodeType.equals(Enums.V.PrimaryExp)){
            TreeNode fc=node.children.get(0);
            if(node.children.size()>1){
                return calConstExp(node.children.get(1),scope);
            }
            if(fc.nodeType.equals(Enums.V.LVal)){
                SymbolAttribute s= getIdent(fc.children.get(0).symbolName, scope);
                return s==null?0:s.value;
            }
            if(fc.nodeType.equals(Enums.V.Number)){
                return Integer.parseInt(fc.children.get(0).symbolName);
            }
            if(fc.nodeType.equals(Enums.V.Character)){
                return MidCodeGeneration.charCon2int(fc.children.get(0).symbolName);
            }
        }
        return 0;
    }


    public static void generateMidcode(){
        try{
            File output = new File("./output.txt");
            if(!output.createNewFile()) Lexer.clearFile("./output.txt");
            FileWriter writer = new FileWriter(output, true);

            scopeCounter=1;
            CompUnitHandler(root);

            for(MidCode midCode:midCodes){
                writer.write(midCode.toString()+"\n");
            }

            writer.flush();

            writer.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
