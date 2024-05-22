package edu.cbsystematics.com.libraryprojectcbs.dto.book;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorBookDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookMapper {

    // Convert Book Entity into BookDTO
    public static BookDTO toDTO(Book book) {
        Set<AuthorBookDTO> authorBookDTOs = book.getAuthors().stream()
                .map(author -> new AuthorBookDTO(author.getFirstName(), author.getLastName()))
                .collect(Collectors.toSet());

        return new BookDTO(
                book.getId(),
                book.getISBN(),
                book.getBookName(),
                book.getGenreType(),
                book.getPageCount(),
                book.getBookAmount(),
                book.getBookAvailable(),
                authorBookDTOs
        );
    }


    // Convert BookDTO into Book JPA Entity
    public static Book toEntity(BookDTO bookDTO) {
        Set<Author> authors = bookDTO.getAuthors().stream()
                .map(authorBookDTO -> new Author(authorBookDTO.getFirstName(), authorBookDTO.getLastName()))
                .collect(Collectors.toSet());

        return new Book(
                bookDTO.getISBN(),
                bookDTO.getBookName(),
                bookDTO.getGenreType(),
                bookDTO.getPageCount(),
                bookDTO.getBookAmount(),
                bookDTO.getBookAvailable(),
                authors
        );
    }

}