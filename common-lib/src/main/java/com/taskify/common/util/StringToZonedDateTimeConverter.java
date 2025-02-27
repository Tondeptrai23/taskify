package com.taskify.common.util;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_DATE_TIME,          // 2024-01-01T15:30:00Z
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,   // 2024-01-01T15:30:00+01:00

            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),  // 2024-01-01 15:30:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd")           // 2024-01-01
    );

    @Override
    public ZonedDateTime convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        // Try each formatter
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                // For date-only formats
                if (source.length() <= 10) {
                    LocalDate date = LocalDate.parse(source, formatter);
                    return date.atStartOfDay(ZoneId.systemDefault());
                }

                // For date-time formats without zone
                if (!source.contains("Z") && !source.contains("+")) {
                    LocalDateTime dateTime = LocalDateTime.parse(source, formatter);
                    return dateTime.atZone(ZoneId.systemDefault());
                }

                // For complete date-time formats with zone
                return ZonedDateTime.parse(source, formatter);
            } catch (DateTimeParseException e) {
                // Continue to next formatter
                continue;
            }
        }

        throw new IllegalArgumentException("Unable to parse date time: " + source);
    }
}