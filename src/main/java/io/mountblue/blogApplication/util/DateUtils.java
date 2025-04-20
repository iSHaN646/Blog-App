package io.mountblue.blogApplication.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("dateUtils")
public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }
}