package org.example.bookingql.utils;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtils {
    public static Timestamp convertDateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return Timestamp.from(date.toInstant());
    }
}
