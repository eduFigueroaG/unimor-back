package mx.edu.utez.unimor.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiError {

    private  String message;
    private	 String exception;
    private final boolean status = false;

    public ApiError(String message, String exception) {
        super();
        this.message = message;
        this.exception = exception;
    }
}
