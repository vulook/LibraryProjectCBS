package edu.cbsystematics.com.libraryprojectcbs.utils.period;

import edu.cbsystematics.com.libraryprojectcbs.exception.UserNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MembershipDuration {

    public static String calculateTotalDuration(User user) {

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Get the registration date
        LocalDate startDate = user.getRegDate();
        // Get the current date
        LocalDate endDate = LocalDate.now();

        // Calculate the total years between the start date and end date
        long years = ChronoUnit.YEARS.between(startDate, endDate);
        startDate = startDate.plusYears(years);

        // Calculate the total months between the start date and the new end date
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        startDate = startDate.plusMonths(months);

        // Calculate the total days between the start date and the new end date
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        return years + " years " + months + " months " + days + " days";
    }


}
