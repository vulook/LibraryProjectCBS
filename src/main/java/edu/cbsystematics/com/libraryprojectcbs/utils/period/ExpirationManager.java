package edu.cbsystematics.com.libraryprojectcbs.utils.period;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.MINUTES_TO_EXPIRE_CONST;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpirationManager {

    private static final int MINUTES_TO_EXPIRE = MINUTES_TO_EXPIRE_CONST;

    // Sets the expiry time for Verification Code.
    public static LocalDateTime expiryDateTimeVerificationCode(LocalDateTime regDate) {
        return regDate.plusMinutes(MINUTES_TO_EXPIRE);
    }

    // Returns a formatted representation of the expiry time.
    public static String getExpiryDateTimeVerificationCode(LocalDateTime expiryDateTimeVerificationCode) {
        // Creates a DateTimeFormatter object with the specified pattern.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        return formatter.format(expiryDateTimeVerificationCode);
    }

    // Checks if token has expired.
    public static boolean isExpiredDateTimeVerificationCode(LocalDateTime regDate) {
        return LocalDateTime.now().isAfter(regDate.plusMinutes(MINUTES_TO_EXPIRE));
    }

}