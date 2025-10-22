package com.example.telemed.common;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex){
    var e = new ApiError(); e.status=400; e.message=ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex){
    var e = new ApiError(); e.status=422; e.message="Validation failed";
    e.details = ex.getBindingResult().getFieldErrors()
      .stream().map(f -> f.getField()+": "+f.getDefaultMessage()).toList();
    return ResponseEntity.unprocessableEntity().body(e);
  }
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> conflict(IllegalStateException ex){
    var e = new ApiError(); e.status=409; e.message=ex.getMessage();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> generic(Exception ex){
    var e = new ApiError(); e.status=500; e.message="Internal error";
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
  }
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiError> forbidden(ForbiddenException ex) {
    var e = new ApiError(); e.status = 403; e.message = ex.getMessage();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
  }
}
