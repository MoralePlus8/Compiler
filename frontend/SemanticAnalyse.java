package frontend;

import global.Enums;
import global.SymbolAttribute;
import global.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static global.StaticVariable.*;

public class SemanticAnalyse {

    public static void getAllVt(TreeNode node, ArrayList<String> sentence){
        for(TreeNode n: node.children){
            if(n.children.isEmpty()){
                sentence.add(n.symbolName);
                return;
            }
            getAllVt(n, sentence);
        }
    }

    public static void analyseNode(TreeNode currNode, int scope, int outer){
        outerScope.put(scopeCounter, outer);

        //如果当前节点是语句块, 更改作用域
        if(currNode.nodeType== Enums.V.Block){
            scopeCounter++;
            for (TreeNode n : currNode.children) {
                analyseNode(n, scopeCounter, scope);
            }
        }

        //其他情况直接遍历子节点
        else {
            //如果当前节点是Decl
            if (currNode.nodeType == Enums.V.Decl) {
                ArrayList<String> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);
                if (sentence.get(0).equals("const")) {
                    if (sentence.get(1).equals("int")) {
                        if (sentence.contains("[") && sentence.contains("]")) {
                            symbolTable.put(sentence.get(2), new SymbolAttribute("ConstIntArray", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(2)).append(" ConstIntArray\n");
                        } else {
                            symbolTable.put(sentence.get(2), new SymbolAttribute("ConstInt", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(2)).append(" ConstInt\n");
                        }
                    } else {
                        if (sentence.contains("[") && sentence.contains("]")) {
                            symbolTable.put(sentence.get(2), new SymbolAttribute("ConstCharArray", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(2)).append(" ConstCharArray\n");
                        } else {
                            symbolTable.put(sentence.get(2), new SymbolAttribute("ConstChar", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(2)).append(" ConstChar\n");
                        }
                    }
                } else {
                    if (sentence.get(0).equals("int")) {
                        if (sentence.contains("[") && sentence.contains("]")) {
                            symbolTable.put(sentence.get(1), new SymbolAttribute("IntArray", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" IntArray\n");
                        } else {
                            symbolTable.put(sentence.get(1), new SymbolAttribute("Int", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" Int\n");
                        }
                    } else {
                        if (sentence.contains("[") && sentence.contains("]")) {
                            symbolTable.put(sentence.get(1), new SymbolAttribute("CharArray", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" CharArray\n");
                        } else {
                            symbolTable.put(sentence.get(1), new SymbolAttribute("Char", scope));
                            semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" Char\n");
                        }
                    }
                }
            }

            //如果当前节点是FuncDef
            else if (currNode.nodeType == Enums.V.FuncDef) {
                ArrayList<String> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);

                if (sentence.get(0).equals("void")) {
                    symbolTable.put(sentence.get(1), new SymbolAttribute("VoidFunc", scope));
                    semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" VoidFunc\n");
                }

                if (sentence.get(0).equals("int")) {
                    symbolTable.put(sentence.get(1), new SymbolAttribute("IntFunc", scope));
                    semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" IntFunc\n");
                }

                if (sentence.get(0).equals("char")) {
                    symbolTable.put(sentence.get(1), new SymbolAttribute("CharFunc", scope));
                    semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" CharFunc\n");
                }

            }

            //如果当前节点是函数形参
            else if (currNode.nodeType == Enums.V.FuncFParam) {
                ArrayList<String> sentence = new ArrayList<>();
                getAllVt(currNode, sentence);
                if(sentence.get(0).equals("int")){
                    if(sentence.contains("[") && sentence.contains("]")){
                        symbolTable.put(sentence.get(1), new SymbolAttribute("IntArray", scope));
                        semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" IntArray\n");
                    }
                    else{
                        symbolTable.put(sentence.get(1), new SymbolAttribute("Int", scope));
                        semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" Int\n");
                    }
                }
                else{
                    if(sentence.contains("[") && sentence.contains("]")){
                        symbolTable.put(sentence.get(1), new SymbolAttribute("CharArray", scope));
                        semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" CharArray\n");
                    }
                    else{
                        symbolTable.put(sentence.get(1), new SymbolAttribute("Char", scope));
                        semanticOutput.append(scope).append(" ").append(sentence.get(1)).append(" Char\n");
                    }
                }
            }


            for (TreeNode n : currNode.children) {
                analyseNode(n, scope, outer);
            }
        }
    }

    public static void treeParser(){
        scopeCounter=1;
        analyseNode(root, 1, 0);
    }

}
