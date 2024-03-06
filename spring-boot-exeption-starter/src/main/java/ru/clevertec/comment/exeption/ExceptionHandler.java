package ru.clevertec.comment.exeption;


import ru.clevertec.comment.model.ErrorResponse;

public interface ExceptionHandler {
    ErrorResponse handleException(Exception e);
}
