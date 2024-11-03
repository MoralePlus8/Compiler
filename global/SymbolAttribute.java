package global;

import java.util.ArrayList;

public class SymbolAttribute implements Comparable<SymbolAttribute> {
    public String name;
    public int order;
    public String type;
    public int scope;
    public int value;
    public ArrayList<String> params=new ArrayList<>();
    public int stackFrameLength=0;
    public int offset=0;
    public boolean isParam=false;

    public SymbolAttribute(String type, int scope, boolean isParam) {
        this.isParam=isParam;
        this.type = type;
        this.scope = scope;
    }

    public SymbolAttribute(String type, int scope) {
        this.type = type;
        this.scope = scope;
    }

    public SymbolAttribute(String type, int val, int scope) {
        this.type = type;
        this.value = val;
        this.scope = scope;
    }

    public SymbolAttribute(int scope, int order, String name, String type) {
        this.scope = scope;
        this.order = order;
        this.name = name;
        this.type = type;
    }


    @Override
    public int compareTo(SymbolAttribute o) {
        if(this.scope == o.scope) return this.order - o.order;
        return this.scope - o.scope;
    }
}
