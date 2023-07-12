package com.mgmtp.easyquizy.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    static final String ERROR = "error";

    /**
     * This method handles ConstraintViolationException by returning a map of errors.
     * ConstraintViolationException will be thrown when bean validation fails for request params or path variables.
     * The key of the map is the property path and the value is the error message.
     *
     * @return a ResponseEntity containing a map of errors
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        // entry of field name and error message
                        violation -> violation.getPropertyPath().toString().split("\\.")[1],
                        ConstraintViolation::getMessage
                ));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method overrides the handleTypeMismatch method from ResponseEntityExceptionHandler to provide custom handling for TypeMismatchException.
     * It returns a user-readable error message in the response.
     *
     * @return a ResponseEntity containing an error message
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
        Map<String, String> error = new HashMap<>();
        String fieldName = exception.getName();
        Class<?> requiredType = exception.getRequiredType();
        if (requiredType != null) {
            String type = requiredType.getSimpleName();
            String message = String.format("should be a valid '%s'", type);
            error.put(fieldName, message);
        } else {
            String message = "wrong type";
            error.put(fieldName, message);
        }
        return new ResponseEntity<>(error, headers, status);
    }

    /**
     * This method overrides the handleMissingServletRequestParameter method from ResponseEntityExceptionHandler to provide custom handling for MissingServletRequestParameterException.
     * It returns the error message from the exception in the response.
     *
     * @return a ResponseEntity containing an error message
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        String fieldName = ex.getParameterName();
        String message = ex.getMessage();
        error.put(fieldName, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RecordNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();
        errors.put(ERROR, message);
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentNotValidException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * MethodArgumentNotValidException will be thrown when @RequestBody data validation fails.
     *
     * @return a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * HttpMessageNotReadableException will be thrown when @RequestBody data cannot be parsed from json.
     *
     * @return a ResponseEntity with an error message and HttpStatus.BAD_REQUEST
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            String fieldType = invalidFormatException.getTargetType().getSimpleName();
            String message = String.format("should be a valid '%s'", fieldType);
            error.put(fieldName, message);
        } else {
            String message = "Invalid request body format.";
            error.put(ERROR, message);
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other internal server exceptions
     *
     * @return a ResponseEntity with an error message and a status indicating internal server error
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, "An internal server error occurred. Please try again later.");
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles NoHandlerFoundException by returning a ResponseEntity with an error message and HttpStatus.NOT_FOUND.
     * NoHandlerFoundException is thrown when the server cannot find a handler for a request, typically indicating that the requested resource does not exist.
     *
     * @return a ResponseEntity with an error message and HttpStatus.NOT_FOUND
     */
    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR, "Resource not found.");
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFieldsException.class)
    public ResponseEntity<Object> handleInvalidFieldsException(InvalidFieldsException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpDuplicatedQuestionException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * HttpDuplicatedQuestionException will be thrown when @RequestBody data contain duplication questions from json.
     *
     * @return a ResponseEntity with an error message and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(value = {DuplicatedQuestionException.class})
    @ResponseBody
    public ResponseEntity<Object> handleDuplicatedQuestionException(DuplicatedQuestionException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();
        errors.put(ERROR, message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpNoMatchEventIdException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * HttpNoMatchEventIdException will be thrown when @RequestBody when update a quiz with a different event's id from json.
     *
     * @return a ResponseEntity with an error message and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(value = {NoMatchEventIdException.class})
    @ResponseBody
    public ResponseEntity<Object> handleNoMatchEventIdException(NoMatchEventIdException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();
        errors.put(ERROR, message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

