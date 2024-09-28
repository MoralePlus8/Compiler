package global;

public class ErrorPair implements Comparable<ErrorPair> {
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

    @Override
    public int compareTo(ErrorPair o) {
        return this.errorLine - o.errorLine;
    }
}
