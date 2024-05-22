package edu.cbsystematics.com.libraryprojectcbs.dto.book;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoDTO {

    private Book book;

    private String readStatus;

    private Long countRead;

    public BookInfoDTO(Book book) {
        this.book = book;
    }

}
