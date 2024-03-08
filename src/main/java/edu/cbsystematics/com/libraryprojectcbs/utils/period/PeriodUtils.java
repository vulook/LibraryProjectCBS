package edu.cbsystematics.com.libraryprojectcbs.utils.period;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodUtils {

    // Got the start of the nth month ago
    public static LocalDateTime getStartOfMonthsAgo(int monthsAgo) {
        return LocalDateTime.now().minusMonths(monthsAgo).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    // Got the end of the nth month ago
    public static LocalDateTime getEndOfMonthsAgo(int monthsAgo) {
        return LocalDateTime.now().minusMonths(monthsAgo).with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }


    // Extract the month name
    public static String getStartMonthNameOfMonthsAgo(int monthsAgo) {
        return LocalDate.now().minusMonths(monthsAgo).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /*
    // Got the start of the nth month ago
    public static LocalDateTime getStartOfMonthsAgo(int monthsAgo) {
        return LocalDate.now().minusMonths(monthsAgo).atStartOfDay();
    }

    // Got the end of the nth month ago
    public static LocalDateTime getEndOfMonthsAgo(int monthsAgo) {
        LocalDate startOfMonth = getStartOfMonthsAgo(monthsAgo).toLocalDate();
        YearMonth yearMonth = YearMonth.of(startOfMonth.getYear(), startOfMonth.getMonth());
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        return startOfMonth.atTime(23, 59, 59).withDayOfMonth(lastDayOfMonth);
    }
    */

}