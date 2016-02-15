package eu.thog92.lwjall.util;

@SuppressWarnings("serial")
public class LWJALLException extends Exception {

    public LWJALLException(String message) {
        super(message);
    }

    public LWJALLException(String message, Throwable cause) {
        super(message, cause);
    }
}
