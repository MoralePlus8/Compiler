package global;

public class MidCode {
    public Enums.MidCodeOp op;
    public String a=null;
    public String b=null;
    public String r=null;

    public MidCode(Enums.MidCodeOp op, String r, String a, String b){
        this.op = op;
        this.r = r;
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString(){
        return switch (op){
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
            case GOTO->"GOTO "+r;
            case BZ->"if "+a+" == 0 then goto "+r;
            case BNZ->"if "+b+" == 0 then goto "+r;
            case JUMP->a==null?"JUMPTO JUMP"+r:"JUMPTO LOOP"+r+a;
            case PUSH->"PUSH "+r;
            case CALL->"CALL "+r;
            case RET->"RET";
            case RETVALUE -> "RET "+r;
            case READ -> "READ "+r;
            case PRINT -> "PRINT "+r;
            case LABEL -> "LABEL "+r+" "+a;
            case CONST -> null;
            case ARRAY -> "ARRAY "+a+" "+r+"["+b+"]";
            case VAR -> b==null?"VAR "+a+" "+r:"VAR "+a+" "+r+" = "+b;
            case FUNC -> a+" "+r+"()";
            case PARAM -> "PARA "+a+" "+r;
            case MAIN -> "Main";
            case GETARRAY -> r+" = "+a+" ["+b+"]";
            case PUTARRAY -> a+" ["+b+"]"+" = "+r;
            case EXIT -> "EXIT";
        };
    }

}
