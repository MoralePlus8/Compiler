package global;

public class SymbolPair {
    public String token;
    public Enums.SymbolCode symbolCode;
    public SymbolPair(String token, Enums.SymbolCode symbolCode) {
        this.token = token;
        this.symbolCode= symbolCode;
    }
    public SymbolPair() {}
}
