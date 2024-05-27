package lt.jb.aero.flight.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.logging.Logger

@ControllerAdvice
class ExceptionHandler {

    companion object {
        val logger = Logger.getLogger(ExceptionHandler::class.java.name)
    }

    @ExceptionHandler
    fun handleFlightNotFoundException(ex: FlightNotFoundException): ResponseEntity<ErrorMessage> {
        logger.info("Caught FlightNotFoundException. Message=${ex.message}")

        val errorMessage = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message!!)
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleFlightNotUniqueException(ex: FlightNotUniqueException): ResponseEntity<ErrorMessage> {
        logger.info("Caught FlightNotUniqueException. Message=${ex.message}")

        val errorMessage = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message!!)
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorMessage> {
        logger.info("Caught IllegalArgumentException. Message=${ex.message}")

        val errorMessage = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message!!)
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}

data class ErrorMessage(val statusCode: Int, val message: String)