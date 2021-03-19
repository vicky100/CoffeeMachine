package main.java.exceptions;

public class InvalidQuantityException extends Exception {

    public InvalidQuantityException(final String msg) {
        super(msg);
    }

    public InvalidQuantityException(final String msg, final Throwable err) {
        super(msg, err);
    }
}
