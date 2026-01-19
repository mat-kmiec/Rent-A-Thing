package pl.rentathing.Rental.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.rentathing.Rental.Controller.RentalApiController;
import pl.rentathing.item.exception.ItemNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = RentalApiController.class)
public class GlobalRentalExceptionHandler {

    @ExceptionHandler({
            PastDateException.class,
            StartAfterEndDateException.class,
            DateNotAvailableException.class
    })
    public ResponseEntity<Object> handleRentalLogicExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }


    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ItemNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Przedmiot o podanym identyfikatorze nie istnieje.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Błędny format parametru: '%s'. Oczekiwano typu %s.",
                ex.getName(), ex.getRequiredType().getSimpleName());

        if (ex.getName().contains("Date")) {
            message += " Prawidłowy format to YYYY-MM-DD.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Wystąpił nieoczekiwany błąd serwera.",
                request
        );
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }
}