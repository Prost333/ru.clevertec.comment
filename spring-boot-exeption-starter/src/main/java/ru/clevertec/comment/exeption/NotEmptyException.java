package ru.clevertec.comment.exeption;

public class NotEmptyException extends RuntimeException {

    public NotEmptyException(String message) {
        super(message);
    }
}