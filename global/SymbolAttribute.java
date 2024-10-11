package global;

public class SymbolAttribute {
    public String type;
    public int scope;
    public String value;
    public int address;

    public SymbolAttribute(){}

    public SymbolAttribute(String type, int scope) {
        this.type = type;
        this.scope = scope;
    }

    public SymbolAttribute(String type, int scope, String value) {
        this.type = type;
        this.scope = scope;
        this.value = value;
    }

    public SymbolAttribute(String type, int scope, String value, int address) {
        this.type = type;
        this.scope = scope;
        this.value = value;
        this.address = address;
    }

}
