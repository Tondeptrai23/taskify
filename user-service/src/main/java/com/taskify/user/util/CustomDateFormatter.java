package com.taskify.user.util;


import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Custom date format to parse multiple date formats.
 */
@Component
public class CustomDateFormatter implements Formatter<ZonedDateTime> {
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    @Override
    public ZonedDateTime parse(String text, Locale locale) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                if (formatter.equals(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
                    // For simple date format, add default time and zone
                    LocalDate date = LocalDate.parse(text, formatter);
                    return date.atStartOfDay(ZoneId.systemDefault());
                }
                // For ISO formats
                return ZonedDateTime.parse(text, formatter);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Unable to parse date: " + text);
    }

    @Override
    public String print(ZonedDateTime object, Locale locale) {
        return object.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}