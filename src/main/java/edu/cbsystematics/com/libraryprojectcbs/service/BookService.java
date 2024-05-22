package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface BookService {

    // Create a new book
    void createBook(Book book);

    // Add an author to a book
    void addAuthorAndBook(Author author, Book book);

    // Add multiple authors to a book
    Book addAuthorAndBook(Book book);

    // Update an existing book
    @Transactional
    void updateBook(Long id, Book updatedBook);

    // Delete a book by its ID
    void deleteBook(Long id);

    // Get a book by its ID
    Optional<Book> getBookById(Long id);

    // Get all books
    List<Book> getAllBooks();

    // Update the isSelected status of a book
    @Transactional
    void updateIsSelectedStatus(Long id, boolean isSelected);

    // Search for books by their name
    List<Book> searchBooksByBookName(String query);

    // Get a book by its title
    Optional<Book> getBookByTitle(String bookName);

    // Find a book by its ISBN
    Optional<Book> findBookByISBN(String isbn);

    // Check if a book exists by its ISBN
    boolean checkIfBookExistsByISBN(String isbn);

    // Find a paginated list of books for a given user
    @Transactional(readOnly = true)
    Page<BookInfoDTO> findPaginated(User userRead, int pageNo, int pageSize, String sortField, String sortDirection);

}