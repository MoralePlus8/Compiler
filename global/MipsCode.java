package global;

public class MipsCode {

    public Enums.mipsCodeOp op;
    public String t1=null;
    public String t2=null;
    public String t3=null;

    public MipsCode(Enums.mipsCodeOp op, String t1, String t2, String t3){
        this.op = op;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public MipsCode(Enums.mipsCodeOp op, String t1, String t2){
        this.op = op;
        this.t1 = t1;
        this.t2 = t2;
    }

    public MipsCode(Enums.mipsCodeOp op, String t1){
        this.op = op;
        this.t1 = t1;
    }

    public MipsCode(Enums.mipsCodeOp op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return switch(op){
            case add, sub, mul, slt, sle, sgt, sge, seq, sne, sll -> op +" "+t1+","+t2+","+t3;
            case li, la, move, beqz, div -> op +" "+t1+","+t2;
            case mflo, mfhi, j, jal, jr -> op +" "+t1;
            case lw, sw -> op +" "+t1+","+t2+"("+t3+")";
            case label -> t1+":";
            case syscall -> op.toString();
            case data -> t1+" "+t2+",\""+t3+"\"";
            case block -> "";
        };
    }
}
