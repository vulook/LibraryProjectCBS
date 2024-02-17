package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;

import java.util.List;
import java.util.Optional;


public interface BookService {

    // Create a new Book.
    void createBook(Book book);

    // Create a new Books.
    void saveAll(List<Book> books);

    // Update an existing Book by ID.
    void updateBook(Long id, Book updatedBook);

    // Delete a Book by ID.
    void deleteBook(Long id);

    // Retrieve a Book by its ID.
    Optional<Book> getBookById(Long id);

    // Retrieve all Books.
    List<Book> getAllBooks();

    // Search for Books by book name.
    List<Book> searchBooksByBookName(String query);

    // Add an author and a book to the database
    void addAuthorAndBook(Author author, Book book);

    // Retrieve a Book's ID by its title
    Long getBookIdByTitle(String bookName);

    // Count of books associated with author
    int countBooksInAuthor(String firstName, String lastName);

}