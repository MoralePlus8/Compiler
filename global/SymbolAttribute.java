package global;

import java.util.ArrayList;

public class SymbolAttribute implements Comparable<SymbolAttribute> {
    public String name;
    public int order;
    public String type;
    public int scope;
    public String value;
    public int address;
    public ArrayList<String> params=new ArrayList<>();

    public SymbolAttribute(){}

    public SymbolAttribute(String type, int scope) {
        this.type = type;
        this.scope = scope;
    }

    public SymbolAttribute(int scope, int order, String name, String type) {
        this.scope = scope;
        this.order = order;
        this.name = name;
        this.type = type;
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

    @Override
    public int compareTo(SymbolAttribute o) {
        if(this.scope == o.scope) return this.order - o.order;
        return this.scope - o.scope;
    }
}
