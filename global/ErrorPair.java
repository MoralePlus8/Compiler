package global;

public class ErrorPair {
    public int errorLine;
    public Enums.ErrorCode errorCode;
    public ErrorPair(int errorLine, Enums.ErrorCode errorCode) {
        this.errorLine = errorLine;
        this.errorCode = errorCode;
    }
    public ErrorPair(){}
    @Override
    public String toString() {
        return Integer.toString(errorLine) + " " + errorCode.name();
    }
}
