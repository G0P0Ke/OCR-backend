package com.andreev.ocrbackend.input.rest.advice

import com.andreev.ocrbackend.DocumentNotFoundException
import com.andreev.ocrbackend.LabelingPermissionException
import com.andreev.ocrbackend.ModelNotFoundException
import com.andreev.ocrbackend.ProjectAlreadyExistsException
import com.andreev.ocrbackend.ProjectNotFoundException
import com.andreev.ocrbackend.UserAlreadyExistsException
import com.andreev.ocrbackend.UserNotFoundException
import com.andreev.ocrbackend.dto.ErrorDescription
import com.andreev.ocrbackend.dto.ErrorResponse
import mu.KLogging
import mu.KotlinLogging
import org.hibernate.validator.internal.engine.path.PathImpl
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.Locale
import javax.validation.ConstraintViolationException

@ControllerAdvice
@ResponseBody
class RestErrorHandler(val exceptionMessageSource: MessageSource) : ResponseEntityExceptionHandler() {
    private companion object : KLogging() {
        val log = KotlinLogging.logger {} //to avoid name clash with `logger` from ResponseEntityExceptionHandler
        val DEFAULT_HEADERS = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult
            .fieldErrors.map {
                ErrorDescription("Field ${it.field} ${it.defaultMessage}")
            }
        return response(errors, HttpStatus.BAD_REQUEST)
    }


    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun handle(e: ConstraintViolationException): ResponseEntity<Any> {
        val errors = e.constraintViolations?.map {
            val propertyPath: PathImpl? = it.propertyPath as? PathImpl
            buildErrorDto("invalid.field.value", propertyPath?.leafNode?.asString(), it.message)
        }.orEmpty()
        return response(HttpStatus.BAD_REQUEST, *errors.toTypedArray())
    }

    @ExceptionHandler(Throwable::class)
    fun handle(e: Throwable): ResponseEntity<Any> {
        log.error(e.message, e)
        return response(
            listOf(ErrorDescription(e.message ?: "No exception message was provided")),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handle(e: UserNotFoundException): ErrorResponse {
        log.info(e) { e.message }
        return ErrorResponse(listOf(ErrorDescription(e.message)))
    }

    @ExceptionHandler(value = [ModelNotFoundException::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handle(e: ModelNotFoundException): ErrorResponse {
        log.info(e) { e.message }
        return ErrorResponse(listOf(ErrorDescription(e.message)))
    }

    @ExceptionHandler(value = [ProjectNotFoundException::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handle(e: ProjectNotFoundException): ErrorResponse {
        log.info(e) { e.message }
        return ErrorResponse(listOf(ErrorDescription(e.message)))
    }

    @ExceptionHandler(value = [DocumentNotFoundException::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handle(e: DocumentNotFoundException): ErrorResponse {
        log.info(e) { e.message }
        return ErrorResponse(listOf(ErrorDescription(e.message)))
    }

    @ExceptionHandler(value = [UserAlreadyExistsException::class])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handle(e: UserAlreadyExistsException): ErrorResponse {
        log.error(e.message, e)
        return ErrorResponse(listOf(ErrorDescription(e.message ?: "No exception message was provided")))
    }

    @ExceptionHandler(value = [ProjectAlreadyExistsException::class])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handle(e: ProjectAlreadyExistsException): ErrorResponse {
        log.error(e.message, e)
        return ErrorResponse(listOf(ErrorDescription(e.message ?: "No exception message was provided")))
    }

    @ExceptionHandler(value = [LabelingPermissionException::class])
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    fun handle(e: LabelingPermissionException): ErrorResponse {
        log.error(e.message, e)
        return ErrorResponse(listOf(ErrorDescription(e.message ?: "No exception message was provided")))
    }

    private fun buildErrorDto(errorMessage: String, vararg args: String?): ErrorDescription {
        return ErrorDescription(
            exceptionMessageSource.getMessage(errorMessage, args, Locale.getDefault())
        )
    }

    private fun response(status: HttpStatus, vararg errors: ErrorDescription): ResponseEntity<Any> {
        val errorsList = errors.toList()
        log.info { errorsList }
        return ResponseEntity(ErrorResponse(errorsList), DEFAULT_HEADERS, status)
    }

    private fun response(
        errors: List<ErrorDescription>,
        status: HttpStatus,
    ): ResponseEntity<Any> {
        return ResponseEntity(ErrorResponse(errors), DEFAULT_HEADERS, status)
    }

}