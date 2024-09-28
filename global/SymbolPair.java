package global;

public class SymbolPair {
    public String token;
    public Enums.SymbolCode symbolCode;
    public int lineCount;
    public SymbolPair(String token, Enums.SymbolCode symbolCode, int lineCount) {
        this.token = token;
        this.symbolCode= symbolCode;
        this.lineCount = lineCount;
    }
    public SymbolPair() {}
}
