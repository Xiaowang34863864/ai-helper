package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public UserService.PageResponse<User> getUsers(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") int page,
      @RequestParam(defaultValue = "5") @Min(value = 1, message = "size最小为1")
          @Max(value = 100, message = "size最大为100")
          int size,
      @RequestParam(required = false) String name) {
    return userService.getUsers(page, size, name);
  }

  @PostMapping
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    User created = userService.createUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}

@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "参数校验失败", errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations()
        .forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
    ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "请求参数不合法", errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(PersistenceException.class)
  public ResponseEntity<ApiError> handlePersistenceException(PersistenceException ex) {
    ApiError body =
        new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "数据库操作失败，可能存在重复数据或字段约束不满足",
            null);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleException(Exception ex) {
    ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误", null);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}

class ApiError {
  private final Instant timestamp;
  private final int status;
  private final String message;
  private final Map<String, String> errors;

  ApiError(int status, String message, Map<String, String> errors) {
    this.timestamp = Instant.now();
    this.status = status;
    this.message = message;
    this.errors = errors;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
