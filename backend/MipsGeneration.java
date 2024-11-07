package backend;

import frontend.Lexer;
import global.Enums;
import global.MidCode;
import global.MipsCode;
import global.SymbolAttribute;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import static frontend.SemanticAnalyse.getIdent;
import static global.Enums.mipsCodeOp.*;
import static global.StaticVariable.*;

public class MipsGeneration {

    private static void add(Enums.mipsCodeOp op, String t1, String t2, String t3){
        mipsCodes.add(new MipsCode(op, t1, t2, t3));
    }
    private static void add(Enums.mipsCodeOp op, String t1, String t2){
        mipsCodes.add(new MipsCode(op, t1, t2));
    }
    private static void add(Enums.mipsCodeOp op, String t1){
        mipsCodes.add(new MipsCode(op, t1));
    }
    private static void add(Enums.mipsCodeOp op){
        mipsCodes.add(new MipsCode(op));
    }

    //将所有函数的栈帧长度存储在符号表中，返回主函数栈帧长度
    static int getStackLengthAndOffset(){
        int cnt=0;
        String funcName=null;
        for(MidCode midCode:midCodes){

            if(midCode.op.equals(Enums.MidCodeOp.ARRAY)){
                cnt+=4*Integer.parseInt(midCode.b);
            }
            else if(midCode.op.equals(Enums.MidCodeOp.FUNC)||midCode.op.equals(Enums.MidCodeOp.MAIN)){
                if(funcName!=null){
                    SymbolAttribute s= getIdent(funcName, 1);
                    if(s!=null){
                        s.stackFrameLength=cnt+8;//多余8B存储$ra和$fp
                    }
                }
                cnt=0;
                funcName=midCode.r;
            }
            else{
                cnt+=4;
            }
        }
        return cnt+8;//多余8B存储$ra和$fp
    }

    private static SymbolAttribute getSymbol(String name, int currScope){
        int temp=currScope;
        while(temp!=0){
            if(symbolTable.get(temp).containsKey(name)&&mipsTable.get(temp).containsKey(name)){
                return symbolTable.get(temp).get(name);
            }
            temp=outerScope.get(temp);
        }
        return null;
    }

    //将普通变量ident的值加载到dst寄存器中
    static void loadVar(String ident, int scope, String dst){
        char c=ident.charAt(0);
        if(Character.isDigit(c)||c=='-'){
            add(li, dst, ident);
        }
        else{
            SymbolAttribute s= getSymbol(ident, scope);
            if(s!=null){
                //全局变量
                if(s.scope==1) {
                    add(lw, dst, String.valueOf(s.offset), "$gp");
                }
                //局部变量
                else{
                    add(lw, dst, String.valueOf(-s.offset), "$fp");
                }
            }
        }
    }

    //将ident数组的index下标处的值加载到dst寄存器
    static void loadArray(String ident, String index, int scope, String dst){
        SymbolAttribute s= getSymbol(ident, scope);
        if(s!=null){

            char c=index.charAt(0);
            if(Character.isDigit(c)||c=='-'){
                add(li, "$t0", index);
            }
            else{
                loadVar(index,scope,"$t0");
            }

            if(s.isParam){
                add(sll, "$t0", "$t0", "2");
                add(lw, "$t1",  String.valueOf(-s.offset), "$fp");
                add(add, "$t1", "$t1", "$t0");
                add(lw, dst, "0", "$t1");
            }

            else {
                if(s.scope==1){
                    add(sll, "$t0", "$t0", "2");
                    add(add, "$t1", "$t0", "$gp");
                    add(lw, dst, String.valueOf(s.offset), "$t1");
                }
                else{
                    add(sll, "$t0", "$t0", "2");
                    add(add, "$t1", "$t0", "$fp");
                    add(lw, dst, String.valueOf(-s.offset), "$t1");
                }
            }


        }
    }

    //将src寄存器中的值存储到ident数组的index下标处
    static void storeArray(String ident, String index, int scope, String src){
        SymbolAttribute s= getSymbol(ident, scope);
        if(s!=null) {

            char c=index.charAt(0);
            if(Character.isDigit(c)||c=='-'){
                add(li, "$t1", index);
            }
            else{
                loadVar(index,scope,"$t1");
            }

            if(s.isParam){
                add(sll, "$t1", "$t1", "2");
                add(lw, "$t2", String.valueOf(-s.offset), "$fp");
                add(add, "$t2", "$t2", "$t1");
                if(s.type.equals("CharArray")||s.type.equals("ConstCharArray")) add(and, src, src, "0xFF");
                add(sw, src, "0", "$t2");

            }

            else{
                if(s.scope==1) {
                    add(sll, "$t1", "$t1", "2");
                    add(add, "$t1", "$t1", "$gp");
                    if(s.type.equals("CharArray")||s.type.equals("ConstCharArray")) add(and, src, src, "0xFF");
                    add(sw, src, String.valueOf(s.offset), "$t1");
                }
                else{
                    add(sll, "$t1", "$t1", "2");
                    add(add, "$t1", "$t1", "$fp");
                    if(s.type.equals("CharArray")||s.type.equals("ConstCharArray")) add(and, src, src, "0xFF");
                    add(sw, src, String.valueOf(-s.offset), "$t1");
                }
            }

        }
    }

    //将src寄存器中的值存储到ident变量中
    static void storeVar(String ident, int scope, String src){
        SymbolAttribute s= getSymbol(ident, scope);
        if(s==null){
            s=new SymbolAttribute("Int", scope);
            s.offset=varOffset;
            symbolTable.get(scope).put(ident, s);
            mipsTable.get(scope).put(ident, s);
            if(scope==1) add(sw, src, String.valueOf(varOffset), "$gp");
            else add(sw, src, String.valueOf(-varOffset), "$fp");
            varOffset+=4;
        }
        else{
            if(s.type.equals("Char")) add(and, src, src, "0xFF");
            if(s.scope==1) add(sw, src, String.valueOf(s.offset), "$gp");
            else add(sw, src, String.valueOf(-s.offset), "$fp");
        }
    }

    static int varOffset=0;
    static int paramOffset=0;
    static boolean isFirstFunc=true;
    static boolean isInMain=false;
    static int paramCounter=0;

    static void getMipsCode(){


        int mainLength=getStackLengthAndOffset();
        int funcLength=0;

        for(int i=0;i<midCodes.size();i++){
            MidCode midCode=midCodes.get(i);

            if(midCode.op.equals(Enums.MidCodeOp.PLUS)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(add, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.MINU)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(sub, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.MULT)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(mul, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.DIV)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(div, "$t0", "$t1");
                add(mflo, "$t2");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.MOD)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(div, "$t0", "$t1");
                add(mfhi, "$t2");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.LSS)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(slt, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.LEQ)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(sle, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.GRE)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(sgt, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.GEQ)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(sge, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.EQL)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(seq, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.NEQ)){
                loadVar(midCode.a, midCode.scope, "$t0");
                loadVar(midCode.b, midCode.scope,"$t1");
                add(sne, "$t2", "$t0", "$t1");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.ASSIGN)){
                loadVar(midCode.a, midCode.scope, "$t0");
                storeVar(midCode.r, midCode.scope,"$t0");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.GOTO)){
                if(midCode.a==null){
                    add(j, "JUMP"+midCode.r);
                }
                else{
                    add(j, "LOOP"+midCode.r+midCode.a);
                }
            }

            else if(midCode.op.equals(Enums.MidCodeOp.BZ)){
                loadVar(midCode.a, midCode.scope, "$t0");
                add(beqz, "$t0", "JUMP"+midCode.r);
            }

            else if(midCode.op.equals(Enums.MidCodeOp.JUMP)){
                if(midCode.a==null){
                    add(label, "JUMP"+midCode.r);
                }
                else{
                    add(label, "LOOP"+midCode.r+midCode.a);
                }
            }

            else if(midCode.op.equals(Enums.MidCodeOp.PUSH)){
                char c=midCode.r.charAt(0);

                //如果参数是立即数
                if(Character.isDigit(c)||c=='-'){
                    add(li, "$t0", midCode.r);
                    add(sw, "$t0", String.valueOf(-paramOffset), "$sp");
                }

                //参数是标识符
                else {
                    SymbolAttribute s= getSymbol(midCode.r, midCode.scope);
                    if(s!=null){

                        //参数是普通变量
                        if(s.type.equals("Int")||s.type.equals("Char")||s.type.equals("ConstInt")||s.type.equals("ConstChar")){
                            loadVar(midCode.r, midCode.scope, "$t0");
                            add(sw, "$t0", String.valueOf(-paramOffset), "$sp");
                        }

                        //参数是数组
                        else{
                            if(s.isParam){
                                add(lw, "$t0", String.valueOf(-s.offset), "$fp");
                            }
                            else {
                                if(s.scope==1){
                                    add(li, "$t0", String.valueOf(s.offset));
                                    add(add, "$t0", "$t0", "$gp");//是全局变量，根据$gp寄存器求地址
                                }
                                else{
                                    add(li, "$t0", String.valueOf(-s.offset));
                                    add(add, "$t0", "$t0", "$fp");//是局部变量。根据$fp寄存器求地址
                                }
                            }

                            add(sw, "$t0", String.valueOf(-paramOffset), "$sp");
                        }
                    }
                }
                paramOffset+=4;
            }

            else if(midCode.op.equals(Enums.MidCodeOp.CALL)){
                paramOffset=0;
                SymbolAttribute func= getSymbol(midCode.r, midCode.scope);
                if(func!=null){
                    funcLength=func.stackFrameLength;
                }
                add(add, "$sp", "$sp", String.valueOf(-funcLength));
                add(sw, "$ra", "4", "$sp");
                add(sw, "$fp", "8", "$sp");
                add(add, "$fp", "$sp", String.valueOf(funcLength));
                add(jal, midCode.r);
                add(lw, "$fp", "8", "$sp");
                add(lw, "$ra", "4", "$sp");
                add(add, "$sp", "$sp", String.valueOf(funcLength));
            }

            else if(midCode.op.equals(Enums.MidCodeOp.RET)){
                if(isInMain){
                    add(li, "$v0", "10");
                    add(syscall);
                }

                else{
                    if(midCode.r!=null){
                        loadVar(midCode.r, midCode.scope, "$v0");
                    }
                    add(jr, "$ra");
                }

            }

            else if(midCode.op.equals(Enums.MidCodeOp.RETVALUE)){
                if(midCode.a.equals("char")) add(and, "$v0", "$v0", "0xFF");
                storeVar(midCode.r, midCode.scope, "$v0");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.READINT)){
                add(li, "$v0", "5");
                add(syscall);
                storeVar(midCode.r, midCode.scope, "$v0");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.READCHAR)){
                add(li, "$v0", "12");
                add(syscall);
                storeVar(midCode.r, midCode.scope, "$v0");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.PRINT)){
                if(midCode.a==null||midCode.a.equals("escape")){
                    mipsData.add((new MipsCode(data, "s_"+dataCounter+":", ".asciiz", midCode.r)));
                    add(la, "$a0", "s_"+dataCounter);
                    add(li, "$v0", "4");
                    add(syscall);
                    dataCounter++;
                }
                else {
                    if(midCode.a.equals("int")){
                        loadVar(midCode.r, midCode.scope, "$a0");
                        add(li, "$v0", "1");
                        add(syscall);
                    }

                    else{
                        loadVar(midCode.r, midCode.scope, "$a0");
                        add(li, "$v0", "11");
                        add(syscall);
                    }
                }
            }

            else if(midCode.op.equals(Enums.MidCodeOp.LABEL)){
                add(block);
            }

            else if(midCode.op.equals(Enums.MidCodeOp.ARRAY)){
                mipsTable.get(midCode.scope).put(midCode.r, null);
                SymbolAttribute s= getSymbol(midCode.r, midCode.scope);
                if(s!=null){

                    if(midCode.scope==1){
                        s.offset=varOffset;
                        varOffset+=Integer.parseInt(midCode.b)*4;
                    }

                    else{
                        varOffset+=Integer.parseInt(midCode.b)*4;
                        s.offset=varOffset-4;
                    }
                }

            }

            else if(midCode.op.equals(Enums.MidCodeOp.VAR)){
                mipsTable.get(midCode.scope).put(midCode.r, null);
                SymbolAttribute s= getSymbol(midCode.r, midCode.scope);
                if(s!=null) {

                    s.offset = varOffset;
                    varOffset += 4;

                    if(midCode.b!=null){
                        loadVar(midCode.b, midCode.scope, "$t0");
                        storeVar(midCode.r, midCode.scope, "$t0");
                    }
                }
            }

            else if(midCode.op.equals(Enums.MidCodeOp.FUNC)){
                paramCounter=0;

                if(isFirstFunc){
                    isFirstFunc=false;
                    add(j, "main");
                }

                mipsTable.get(midCode.scope).put(midCode.r, null);
                SymbolAttribute func= getSymbol(midCode.r, midCode.scope);
                if(func!=null){
                    varOffset=func.params.size()*4;
                    add(label, midCode.r);
                }

            }

            else if(midCode.op.equals(Enums.MidCodeOp.PARAM)){
                mipsTable.get(midCode.scope).put(midCode.r, null);
                SymbolAttribute s= getSymbol(midCode.r, midCode.scope);
                if(s!=null){
                    s.isParam=true;
                    s.offset = paramCounter*4;
                    paramCounter++;
                    //if(paramCounter==1&&midCode.b.equals("arr")&&midCode.a.equals("char")) exit();
                }
            }

            else if(midCode.op.equals(Enums.MidCodeOp.MAIN)){
                isInMain=true;
                varOffset=0;
                if(isFirstFunc){
                    isFirstFunc=false;
                    add(j, "main");
                }

                add(label, "main");
                add(move, "$fp", "$sp");
                add(add,"$sp", "$sp", String.valueOf(-mainLength));
            }

            else if(midCode.op.equals(Enums.MidCodeOp.GETARRAY)){
                loadArray(midCode.a, midCode.b, midCode.scope, "$t2");
                storeVar(midCode.r, midCode.scope, "$t2");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.PUTARRAY)){
                loadVar(midCode.b, midCode.scope, "$t0");
                storeArray(midCode.r, midCode.a, midCode.scope, "$t0");
            }

            else if(midCode.op.equals(Enums.MidCodeOp.EXIT)){
                return;
            }
        }
    }

    public static void generateMipsCode(){
        try{
            File output = new File("./mips.txt");
            if(!output.createNewFile()) Lexer.clearFile("./mips.txt");
            FileWriter writer = new FileWriter(output, true);

            getMipsCode();

            writer.write(".data\n");
            for(MipsCode mipsCode:mipsData){
                writer.write(mipsCode.toString()+"\n");
            }

            writer.write(".text\n");
            for(MipsCode mipsCode:mipsCodes){
                writer.write(mipsCode.toString()+"\n");
            }

            writer.flush();

            writer.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
