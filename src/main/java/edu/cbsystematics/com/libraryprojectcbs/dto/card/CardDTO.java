package edu.cbsystematics.com.libraryprojectcbs.dto.card;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;

    private User user;

    private Book book;

    private boolean approved;

    private boolean canceled;

}