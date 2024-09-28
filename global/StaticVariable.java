package global;

import java.util.ArrayList;

public class StaticVariable {
    public static char character;
    public static boolean isNote;
    public static int intNumber;
    public static int characterCounter;
    public static int lineCounter;
    public static String code;
    public static String token;
    public static Enums.SymbolCode symbol;
    public static Enums.ErrorCode errorCode;
    public static int errorLine;
    public static ArrayList<ErrorPair> errors = new ArrayList<>();
    public static ArrayList<SymbolPair> symbolPairs = new ArrayList<>();
    public static TreeNode root=new TreeNode("CompUnit");
    public static StringBuilder grammarOutput=new StringBuilder();
}
