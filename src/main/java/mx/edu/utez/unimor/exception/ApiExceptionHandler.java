package mx.edu.utez.unimor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = { Throwable.class })
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiError handleConflict(Exception ex) {
        return error(ex);
    }

    private ApiError error(Exception rawException) {
        Exception ex;
        if (rawException.getCause() != null) {
            ex = (Exception) rawException.getCause();
        } else {
            ex = rawException;
        }
        return new ApiError(ex.getMessage(),
                ex.getClass().getSimpleName());
    }
}