package de.caritas.cob.agencyservice.api.service;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import javax.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service for logging
 */
public class LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  private LogService() {
  }

  /**
   * Logs a database error
   */
  public static void logDatabaseError(Exception exception) {
    LOGGER.error("Database error: {}", getStackTrace(exception));
  }

  /**
   * javax Bad Request Exception
   */
  public static void logBadRequestException(BadRequestException exception) {
    LOGGER.warn("Bad Request: {}", getStackTrace(exception));
  }

  /**
   * Logs a {@link NumberFormatException}
   */
  public static void logNumberFormatException(NumberFormatException exception) {
    LOGGER.error("Error while formating number: {}", getStackTrace(exception));
  }

  /**
   * Logs a info message
   *
   * @param message the message
   */
  public static void logInfo(String message) {
    LOGGER.info(message);
  }

  /**
   * Logs a warning.
   *
   * @param exception the exception to be logged
   */
  public static void logWarning(Exception exception) {
    LOGGER.warn("AgencyService API: {}", getStackTrace(exception));
  }

  /**
   * Logs a warning.
   *
   * @param httpStatus status to be logged
   * @param exception the exception to be logged
   */
  public static void logWarning(HttpStatus httpStatus, Exception exception) {
    LOGGER.warn("AgencyService API: {}: {}", httpStatus.getReasonPhrase(),
        getStackTrace(exception));
  }

  /**
   * Logs a internal server error.
   *
   * @param exception the exception to be logged
   */
  public static void logInternalServerError(Exception exception) {
    LOGGER.error("AgencyService API: 500 Internal Server Error: {}", getStackTrace(exception));
  }

}