package de.caritas.cob.agencyservice.api;

import de.caritas.cob.agencyservice.api.exception.BadRequestException;
import de.caritas.cob.agencyservice.api.exception.KeycloakException;
import de.caritas.cob.agencyservice.api.exception.ServiceException;
import de.caritas.cob.agencyservice.api.service.LogService;
import java.net.UnknownHostException;
import javax.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Customizes API error/exception handling to hide information and/or possible security
 * vulnerabilities.
 *
 */
@NoArgsConstructor
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * 
   * Handle all common "Bad Request" errors (400)
   * 
   */

  /**
   * Custom BadRequest exception
   */
  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleCustomBadRequest(final BadRequestException ex,
      final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Constraint violations
   * 
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex,
      final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Incoming request body could not be deserialized
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status,
      final WebRequest request) {
    LogService.logWarning(status, ex);

    return handleExceptionInternal(ex, null, headers, status, request);
  }

  /**
   * @Valid on object fails validation
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
      final WebRequest request) {
    LogService.logWarning(status, ex);

    return handleExceptionInternal(ex, null, headers, status, request);
  }

  /**
   * 409 - Conflict
   * 
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({InvalidDataAccessApiUsageException.class, DataAccessException.class})
  protected ResponseEntity<Object> handleConflict(final RuntimeException ex,
      final WebRequest request) {
    LogService.logWarning(HttpStatus.CONFLICT, ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  /**
   * 500 - Internal Server Error
   * 
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class,
      IllegalStateException.class, ServiceException.class, KeycloakException.class,
      UnknownHostException.class})
  public ResponseEntity<Object> handleInternal(final RuntimeException ex,
      final WebRequest request) {
    LogService.logInternalServerError(ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

}