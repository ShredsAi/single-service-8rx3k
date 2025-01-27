package ai.shreds.adapter.primary;

import ai.shreds.shared.exceptions.SharedValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdapterControllerException {

    @ExceptionHandler(SharedValidationException.class)
    public ResponseEntity<String> handleValidationError(SharedValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleSystemError(Exception ex) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
}
