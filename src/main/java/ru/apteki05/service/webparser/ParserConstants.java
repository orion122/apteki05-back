package ru.apteki05.service.webparser;

public class ParserConstants {

    private ParserConstants() {
    }

    /**
     * Точка нужна для дробных чисел.
     * Не работает для строки: "3 шт." => "3."
     */
    public static final String NOT_DIGITS_AND_DOT = "[^0-9.]";
    public static final String NOT_DIGITS = "[^0-9]";

}
