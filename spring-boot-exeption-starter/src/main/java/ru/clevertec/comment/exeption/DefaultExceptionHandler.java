package ru.clevertec.comment.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.clevertec.comment.model.ErrorResponse;


@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler {
    /**
     *
     * @param e
     * @return ErrorResponse containing an {@link ErrorResponse} object and a 400 status code.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse handelValidatorException(MethodArgumentNotValidException e) {
        var error = e.getBindingResult().getAllErrors();
        log.error(error.get(0).getDefaultMessage(), e);
        var er = error.get(0);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(er.getDefaultMessage());
        errorResponse.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return errorResponse;
    }


    /**
     * Handles exceptions of type EntityNotFoundException.
     *
     * @param e the exception to be handled
     * @return ErrorResponse containing the error message and HTTP status code
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseBody
    @Override
    public ErrorResponse handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getLocalizedMessage());
        errorResponse.setCode(String.valueOf(HttpStatus.NOT_FOUND));
        log.error("Exception", e);
        return errorResponse;
    }
}
