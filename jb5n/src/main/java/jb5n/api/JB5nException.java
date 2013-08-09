package jb5n.api;

public class JB5nException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Reason reason;

    public enum Reason {
        InternalError, InvalidArgument, MissingResource, InvalidMethodSignature
    }

    public JB5nException(Reason reason, String msg) {
        super(msg);
        this.reason = reason;
    }

    public JB5nException(Reason reason, String msg, Throwable t) {
        super(msg, t);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

}
