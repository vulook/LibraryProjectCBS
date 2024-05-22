package edu.cbsystematics.com.libraryprojectcbs.utils.period;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CountTimePeriod {

    private Long countUsersFromCurrentDate;

    private Long countUsersFromPreviousDay;

    private Long countUsersFromPreviousWeek;

    private Long countUsersFromPreviousMonth;

    private Long countUsersFromPreviousSixMonths;

    private Long countUsersFromPreviousYear;

    private Long countUsersFromAllTime;

}