package mx.edu.utez.unimor.exception;

public class RequestException extends Exception {

    private static final long serialVersionUID = 1L;

    public RequestException(Exception message) {
        super(message);
    }
}
