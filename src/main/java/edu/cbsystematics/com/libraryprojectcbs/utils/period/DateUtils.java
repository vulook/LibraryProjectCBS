package edu.cbsystematics.com.libraryprojectcbs.utils.period;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    // Current date
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    // For the last day
    public static LocalDate getPreviousDay() {
        return getCurrentDate().minusDays(1);
    }

    // For the last week
    public static LocalDate getPreviousWeek() {
        return getCurrentDate().minusWeeks(1);
    }

    // For the last month
    public static LocalDate getPreviousMonth() {
        return getCurrentDate().minusMonths(1);
    }

    // For the last 6 months
    public static LocalDate getPreviousSixMonths() {
        return getCurrentDate().minusMonths(6);
    }

    // For the last year
    public static LocalDate getPreviousYear() {
        return getCurrentDate().minusYears(1);
    }

    // for all time
    public static LocalDate getAllTime() {
        return getCurrentDate().minusYears(10);
    }


}