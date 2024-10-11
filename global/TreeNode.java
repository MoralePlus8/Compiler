package global;

import java.util.ArrayList;

public class TreeNode {
    public String symbolName;
    public Enums.V nodeType;
    public int line;
    public TreeNode father;
    public ArrayList<TreeNode> children=new ArrayList<>();
    public TreeNode(){}
    public TreeNode(String symbolName, Enums.V type, int line, TreeNode father) {
        this.symbolName = symbolName;this.nodeType=type;this.line=line;this.father=father;
    }
}
