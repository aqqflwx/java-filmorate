package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение валидации входных данных.
 */
public class ValidationException extends RuntimeException {
    /**
     * Создаёт исключение с сообщением.
     * @param message текст ошибки
     */
    public ValidationException(final String message) {
        super(message);
    }
}
