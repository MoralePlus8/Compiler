package global;

import java.util.ArrayList;

public class TreeNode {
    public String symbolName;
    public ArrayList<TreeNode> children=new ArrayList<>();
    public TreeNode(){}
    public TreeNode(String symbolName) {this.symbolName = symbolName;}
}
