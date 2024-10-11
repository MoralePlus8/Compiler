package global;

import java.util.ArrayList;

public class TreeNode {
    public String symbolName;
    public Enums.V nodeType;
    public ArrayList<TreeNode> children=new ArrayList<>();
    public TreeNode(){}
    public TreeNode(String symbolName, Enums.V type) {this.symbolName = symbolName;this.nodeType=type;}
}
