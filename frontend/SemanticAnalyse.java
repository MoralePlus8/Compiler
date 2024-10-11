package frontend;

import global.*;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static global.StaticVariable.*;

public class SemanticAnalyse {

    private static void error(int line, Enums.ErrorCode errorCode){
        errors.add(new ErrorPair(line, errorCode));
    }

    public static SymbolAttribute getIndent(String name, int currScope){
        int temp=currScope;
        while(temp!=0){
            if(symbolTable.get(temp).containsKey(name)){
                return symbolTable.get(temp).get(name);
            }
            temp=outerScope.get(temp);
        }
        return null;
    }

    public static void getAllVt(TreeNode node, ArrayList<TreeNode> sentence){
        if(node.children.isEmpty()){
            sentence.add(node);
            return;
        }
        for(TreeNode n: node.children){
            getAllVt(n, sentence);
        }
    }

    public static boolean hasReturn(TreeNode node){
        if(node.children.isEmpty()){
            return node.symbolName.equals("return");
        }
        boolean ret=false;
        for(TreeNode n: node.children){
            if(n.nodeType!= Enums.V.Block && hasReturn(n)) {
                ret=true;
            }
        }
        return ret;
    }

    public static String getTypeOfExp(TreeNode ExpNode, int scope){
        ArrayList<TreeNode> sentence=new ArrayList<>();
        getAllVt(ExpNode,sentence);
        String s="";
        for(TreeNode n: sentence){
            if(n.nodeType==Enums.V.IDENFR){
                SymbolAttribute symbol=getIndent(n.symbolName, scope);
                if(symbol!=null){
                    String type=symbol.type;
                    if(!s.equals("CharArray")&&!s.equals("IntArray")){
                        s = switch (type) {
                            case "IntArray", "ConstIntArray" -> "IntArray";
                            case "CharArray", "ConstCharArray" -> "CharArray";
                            case "Int", "ConstIntA", "IntFunc" -> "Int";
                            case "Char", "ConstChar", "CharFunc" -> "Char";
                            default -> s;
                        };
                    }
                }
            }
        }
        return s;
    }

    public static void analyseNode(TreeNode currNode, int scope, int outer){
        outerScope.put(scope, outer);

        //如果当前节点是语句块, 更改作用域
        if(currNode.nodeType== Enums.V.Block){
            scopeCounter++;
            symbolTable.put(scopeCounter+1, new HashMap<>());
            int temp=scopeCounter;
            for (TreeNode n : currNode.children) {
                analyseNode(n, temp, scope);
            }
        }

        //其他情况直接遍历子节点
        else {
            //如果当前节点是常量声明
            if (currNode.nodeType == Enums.V.ConstDecl) {
                ArrayList<TreeNode> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);
                if (sentence.get(1).symbolName.equals("int")) {
                    for(TreeNode n: currNode.children){
                        if(n.nodeType == Enums.V.ConstDef){
                            ArrayList<TreeNode> s = new ArrayList<>();
                            getAllVt(n, s);

                            if(symbolTable.get(scope).containsKey(s.get(0).symbolName)){
                                error(n.children.get(0).line, Enums.ErrorCode.b);
                            }

                            if(s.size()>1 && s.get(1).symbolName.equals("[")){
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("ConstIntArray", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "ConstIntArray"));
                            }
                            else{
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("ConstInt", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "ConstInt"));
                            }
                            symbolCounter++;
                        }
                    }
                }
                else {
                    for(TreeNode n: currNode.children){
                        if(n.nodeType == Enums.V.ConstDef){
                            ArrayList<TreeNode> s = new ArrayList<>();
                            getAllVt(n, s);

                            if(symbolTable.get(scope).containsKey(s.get(0).symbolName)){
                                error(n.children.get(0).line, Enums.ErrorCode.b);
                            }

                            if(s.size()>1 && s.get(1).symbolName.equals("[")){
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("ConstCharArray", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "ConstCharArray"));
                            }
                            else{
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("ConstChar", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "ConstChar"));
                            }
                            symbolCounter++;
                        }
                    }
                }
            }

            //如果当前节点是变量声明
            else if(currNode.nodeType == Enums.V.VarDecl){
                ArrayList<TreeNode> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);
                if (sentence.get(0).symbolName.equals("int")) {
                    for(TreeNode n: currNode.children){
                        if(n.nodeType == Enums.V.VarDef){
                            ArrayList<TreeNode> s = new ArrayList<>();
                            getAllVt(n, s);

                            if(symbolTable.get(scope).containsKey(s.get(0).symbolName)){
                                error(n.children.get(0).line, Enums.ErrorCode.b);
                            }

                            if(s.size()>1 && s.get(1).symbolName.equals("[")){
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("IntArray", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "IntArray"));
                            }
                            else{
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("Int", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "Int"));
                            }
                            symbolCounter++;
                        }
                    }
                }
                else {
                    for(TreeNode n: currNode.children){
                        if(n.nodeType == Enums.V.VarDef){
                            ArrayList<TreeNode> s = new ArrayList<>();
                            getAllVt(n, s);

                            if(symbolTable.get(scope).containsKey(s.get(0).symbolName)){
                                error(n.children.get(0).line, Enums.ErrorCode.b);
                            }

                            if(s.size()>1 && s.get(1).symbolName.equals("[")){
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("CharArray", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "CharArray"));
                            }
                            else{
                                symbolTable.get(scope).put(s.get(0).symbolName, new SymbolAttribute("Char", scope));
                                symbolEntries.add(new SymbolAttribute(scope, symbolCounter, s.get(0).symbolName, "Char"));
                            }
                            symbolCounter++;
                        }
                    }
                }
            }

            //如果当前节点是FuncDef
            else if (currNode.nodeType == Enums.V.FuncDef) {
                ArrayList<TreeNode> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);

                if(symbolTable.get(scope).containsKey(currNode.children.get(1).symbolName)){
                    error(currNode.children.get(1).line, Enums.ErrorCode.b);
                }



                boolean flag=hasReturn(currNode);

                if (sentence.get(0).symbolName.equals("void")) {

                    for(int i=0;i<sentence.size();i++){
                        if(sentence.get(i).symbolName.equals("return") && !sentence.get(i+1).symbolName.equals(";")){
                            error(sentence.get(i).line, Enums.ErrorCode.f);
                        }
                    }
                    symbolTable.get(scope).put(sentence.get(1).symbolName, new SymbolAttribute("VoidFunc", scope));
                    symbolEntries.add(new SymbolAttribute(scope, symbolCounter, sentence.get(1).symbolName, "VoidFunc"));
                }

                if (sentence.get(0).symbolName.equals("int")) {

                    if(!flag){
                        error(sentence.get(sentence.size()-1).line, Enums.ErrorCode.g);
                    }

                    symbolTable.get(scope).put(sentence.get(1).symbolName, new SymbolAttribute("IntFunc", scope));
                    symbolEntries.add(new SymbolAttribute(scope, symbolCounter, sentence.get(1).symbolName, "IntFunc"));
                }

                if (sentence.get(0).symbolName.equals("char")) {

                    if(!flag){
                        error(sentence.get(sentence.size()-1).line, Enums.ErrorCode.g);
                    }

                    symbolTable.get(scope).put(sentence.get(1).symbolName, new SymbolAttribute("CharFunc", scope));
                    symbolEntries.add(new SymbolAttribute(scope, symbolCounter, sentence.get(1).symbolName, "CharFunc"));
                }

                symbolCounter++;

            }

            //如果当前节点是主函数定义
            else if(currNode.nodeType == Enums.V.MainFuncDef){
                ArrayList<TreeNode> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);
                boolean flag=false;
                for (TreeNode node : sentence) {
                    if (node.symbolName.equals("return")) {
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    error(sentence.get(sentence.size()-1).line, Enums.ErrorCode.g);
                }

            }


            //如果当前节点是函数形参
            else if (currNode.nodeType == Enums.V.FuncFParam) {
                ArrayList<TreeNode> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);

                TreeNode FuncDefNode=currNode.father.father;
                String funName=FuncDefNode.children.get(2).symbolName;
                SymbolAttribute symbol=getIndent(funName, scope);

                if(symbolTable.get(scopeCounter+1).containsKey(sentence.get(1).symbolName)){
                    error(currNode.children.get(1).line, Enums.ErrorCode.b);
                }

                if(sentence.get(0).symbolName.equals("int")){
                    if(sentence.size()>2 && sentence.get(2).symbolName.equals("[")){
                        if(symbol!=null)symbol.params.add("IntArray");

                        symbolTable.get(scopeCounter+1).put(sentence.get(1).symbolName, new SymbolAttribute("IntArray", scopeCounter+1));
                        symbolEntries.add(new SymbolAttribute(scopeCounter+1, symbolCounter, sentence.get(1).symbolName, "IntArray"));
                    }
                    else{
                        if(symbol!=null)symbol.params.add("Int");

                        symbolTable.get(scopeCounter+1).put(sentence.get(1).symbolName, new SymbolAttribute("Int", scopeCounter+1));
                        symbolEntries.add(new SymbolAttribute(scopeCounter+1, symbolCounter, sentence.get(1).symbolName, "Int"));
                    }
                }
                else{
                    if(sentence.size()>2 && sentence.get(2).symbolName.equals("[")){
                        if(symbol!=null)symbol.params.add("CharArray");

                        symbolTable.get(scopeCounter+1).put(sentence.get(1).symbolName, new SymbolAttribute("CharArray", scopeCounter+1));
                        symbolEntries.add(new SymbolAttribute(scopeCounter+1, symbolCounter, sentence.get(1).symbolName, "CharArray"));
                    }
                    else{
                        if(symbol!=null)symbol.params.add("Char");

                        symbolTable.get(scopeCounter+1).put(sentence.get(1).symbolName, new SymbolAttribute("Char", scopeCounter+1));
                        symbolEntries.add(new SymbolAttribute(scopeCounter+1, symbolCounter, sentence.get(1).symbolName, "Char"));
                    }
                }
                symbolCounter++;
            }

            //如果当前节点是语句
            else if(currNode.nodeType == Enums.V.Stmt){
                TreeNode firstChild=currNode.children.get(0);
                if(firstChild.nodeType==Enums.V.LVal&&currNode.children.get(1).nodeType==Enums.V.ASSIGN){
                    String name=firstChild.children.get(0).symbolName;
                    SymbolAttribute symbol=getIndent(name, scope);
                    if(symbol!=null){
                        String type=symbol.type;
                        if(type.equals("ConstChar") || type.equals("ConstCharArray") || type.equals("ConstInt") || type.equals("ConstIntArray")){
                            error(firstChild.children.get(0).line, Enums.ErrorCode.h);
                        }
                    }
                }

                else if(firstChild.symbolName.equals("break")||firstChild.symbolName.equals("continue")){
                    boolean flag=false;
                    TreeNode node=currNode;
                    while(node.father!=null){
                        if(node.nodeType==Enums.V.Stmt&&node.father.nodeType== Enums.V.Stmt){
                            flag=true;
                            break;
                        }
                        node=node.father;
                    }
                    if(!flag){
                        error(firstChild.line, Enums.ErrorCode.m);
                    }
                }

                else if(firstChild.nodeType==Enums.V.PRINTFTK){
                    String format=currNode.children.get(2).symbolName;
                    int cnt=0;
                    for(int i=0;i<format.length()-1;i++){
                        if(format.charAt(i)=='%'&&(format.charAt(i+1)=='d'||format.charAt(i+1)=='c')) cnt++;
                    }
                    if(cnt!=(currNode.children.size()-5)/2){
                        error(firstChild.line, Enums.ErrorCode.l);
                    }
                }
            }

            //如果当前节点是ForStmt
            else if(currNode.nodeType==Enums.V.ForStmt){
                TreeNode n=currNode.children.get(0).children.get(0);
                SymbolAttribute symbol=getIndent(n.symbolName, scope);
                if(symbol!=null){
                    String type=symbol.type;
                    if(type.equals("ConstChar") || type.equals("ConstCharArray") || type.equals("ConstInt") || type.equals("ConstIntArray")){
                        error(n.line, Enums.ErrorCode.h);
                    }
                }
            }


            //如果当前节点是左值表达式
            else if(currNode.nodeType==Enums.V.LVal){
                String name = currNode.children.get(0).symbolName;
                SymbolAttribute symbol=getIndent(name, scope);
                if(symbol==null) {
                    error(currNode.children.get(0).line, Enums.ErrorCode.c);
                }
            }

            //如果当前节点是一元表达式
            if(currNode.nodeType==Enums.V.UnaryExp){
                TreeNode firstChild=currNode.children.get(0);
                if(firstChild.nodeType==Enums.V.IDENFR){
                    SymbolAttribute fParams=getIndent(firstChild.symbolName, scope);
                    if(fParams==null){
                        error(firstChild.line, Enums.ErrorCode.c);
                    }

                    else if(firstChild.nodeType==Enums.V.IDENFR&&currNode.children.get(2).nodeType== Enums.V.FuncRParams){
                        TreeNode rParams=currNode.children.get(2);
                        int rpCount=(rParams.children.size()+1)/2;
                        if(rpCount!=fParams.params.size()){
                            error(firstChild.line, Enums.ErrorCode.d);
                        }
                        else {
                            for(int i=0;i<rpCount;i++){
                                String type1=fParams.params.get(i);
                                String type2=getTypeOfExp(rParams.children.get(2*i+1), scope);
                                if(!type1.equals(type2)){
                                    error(firstChild.line, Enums.ErrorCode.e);
                                }
                            }
                        }
                    }
                }
            }

            for (TreeNode n : currNode.children) {
                analyseNode(n, scope, outer);
            }
        }
    }

    public static void treeParser(){
        try {

            File output = new File("./symbol.txt");
            File error = new File("./error.txt");
            if(!output.createNewFile()) Lexer.clearFile("./symbol.txt");
            if(!error.createNewFile()) Lexer.clearFile("./error.txt");
            Lexer.clearFile("./parser.txt");
            FileWriter writer = new FileWriter(output, true);
            FileWriter errorWriter = new FileWriter(error, true);

            symbolCounter=0;
            scopeCounter = 1;
            symbolTable.put(1, new HashMap<>());
            symbolTable.put(2, new HashMap<>());
            analyseNode(root, 1, 0);
            Collections.sort(symbolEntries);



            if(!errors.isEmpty()){
                Collections.sort(errors);

                for(int i=0;i<errors.size();i++){
                    ErrorPair errorPair=errors.get(i);
                    if(i==0){
                        ArrayList<TreeNode> se=new ArrayList<>();
                        getAllVt(root, se);
                        for(int j=0;j<60&&j<se.size();j++) errorWriter.write(se.get(j).symbolName+" ");
                    }
                    errorWriter.write(errorPair.toString()+'\n');
                    errorWriter.flush();
                }
            }



            for(SymbolAttribute s:symbolEntries){
                writer.write(s.scope+" "+s.name+" "+s.type+"\n");
            }
            writer.flush();

            writer.close();
            errorWriter.close();


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
