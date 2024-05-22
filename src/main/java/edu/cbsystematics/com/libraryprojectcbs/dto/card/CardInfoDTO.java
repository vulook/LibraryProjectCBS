package edu.cbsystematics.com.libraryprojectcbs.dto.card;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoDTO {

    private Long cardId;
	
    private User user;

    private Book book;

    private boolean approved;

    private boolean canceled;

    private String cancellationReason;

    private Long librarian1Id;

    private String fullName1;

    private Long formId;

    private LocalDate startDate;

    private LocalDate returnDate;

    private LocalDate bookReturned;

    private boolean returned;

    private Long librarian2Id;

    private String fullName2;

    private Long daysLeft;

    private boolean violation;


    public Long getDaysLeft() {
        if (returnDate != null) {
            return ChronoUnit.DAYS.between(Objects.requireNonNullElseGet(bookReturned, LocalDate::now), returnDate);
        }
        return 0L;
    }

    public boolean getViolation() {
        if (returnDate != null) {
            LocalDate currentDate = LocalDate.now();
            if (bookReturned != null) {
                return bookReturned.isAfter(returnDate);
            } else {
                long daysOverdue = ChronoUnit.DAYS.between(returnDate, currentDate);
                return daysOverdue >= 0;
            }
        }
        return false;
    }

}