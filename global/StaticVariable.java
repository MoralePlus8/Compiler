package global;

import java.util.ArrayList;
import java.util.HashMap;

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
    public static TreeNode root=new TreeNode("CompUnit", Enums.V.CompUnit, 1, null);
    public static StringBuilder grammarOutput=new StringBuilder();
    public static int scopeCounter=0;
    public static HashMap<Integer, Integer> outerScope = new HashMap<>();
    public static HashMap<Integer, HashMap<String, SymbolAttribute>> symbolTable = new HashMap<>();
    public static ArrayList<SymbolAttribute> symbolEntries = new ArrayList<>();
    public static int symbolCounter=0;
    public static ArrayList<MidCode> midCodes = new ArrayList<>();

}
