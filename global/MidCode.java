package global;

public class MidCode {
    public Enums.MidCodeOp op;
    public String a=null;
    public String b=null;
    public String r=null;
    public int scope;

    public MidCode(Enums.MidCodeOp op, String r, String a, String b, int scope){
        this.op = op;
        this.r = r;
        this.a = a;
        this.b = b;
        this.scope = scope;
    }

    @Override
    public String toString(){
        String ret= switch (op){
            case PLUS->r+" = "+a+" + "+b;
            case MINU->r+" = "+a+" - "+b;
            case MULT->r+" = "+a+" * "+b;
            case DIV->r+" = "+a+" / "+b;
            case MOD->r+" = "+a+" % "+b;
            case LSS->r+" = "+a+" < "+b;
            case LEQ->r+" = "+a+" <= "+b;
            case GRE->r+" = "+a+" > "+b;
            case GEQ->r+" = "+a+" >= "+b;
            case EQL->r+" = "+a+" == "+b;
            case NEQ->r+" = "+a+" != "+b;
            case ASSIGN->r+" = "+a;
            case GOTO->a==null?"GOTO JUMP"+r:"GOTO LOOP"+r+a;
            case BZ->"if "+a+" == 0 then goto JUMP"+r;
            case JUMP->a==null?"------------<JUMPDST JUMP"+r+">":"----------------<JUMPDST LOOP"+r+a+">";
            case PUSH->"PUSH "+r;
            case CALL->"CALL "+r;
            case RET->r==null?"RET":"RET "+r;
            case RETVALUE -> "RETVALUE "+r;
            case READINT -> "READINT "+r;
            case READCHAR -> "READCHAR "+r;
            case PRINT -> a==null?"PRINT \""+r+"\"":"PRINT "+a+" "+r;
            case LABEL -> "--------------------<"+r+" "+a+">";
            case ARRAY -> "ARRAY "+a+" "+r+"["+b+"]";
            case VAR -> b==null?"VAR "+a+" "+r:"VAR "+a+" "+r+" = "+b;
            case FUNC -> a+" "+r+"()";
            case PARAM -> !b.equals("arr")?"PARA "+a+" "+r:"PARA "+a+" "+r+"[]";
            case MAIN -> "Main";
            case GETARRAY -> r+" = "+a+"["+b+"]";
            case PUTARRAY -> r+"["+a+"]"+" = "+b;
            case EXIT -> "EXIT";
        };
        return ret+"       "+scope;
    }

}
