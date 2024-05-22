package edu.cbsystematics.com.libraryprojectcbs.dto.form;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardFormDTO {

    private Long id;

    private User user;

    private Book book;

    private boolean approved;

    private boolean canceled;

    private String cancellationReason;

    private Long librarianId;

    private String librarianFirstName;

    private String librarianLastName;

    private Long formId;

    private LocalDate startDate;

    private LocalDate returnDate;

    private LocalDate bookReturned;

    private boolean isReturned;

}