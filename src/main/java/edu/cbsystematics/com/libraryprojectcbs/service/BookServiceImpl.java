package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    @Override
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void saveAll(List<Book> books) {
        bookRepository.saveAll(books);
    }

    @Override
    @Transactional
    public void updateBook(Long id, Book updatedBook) {
        bookRepository.updateBook(id, updatedBook.getBookName(), updatedBook.getGenreType(), updatedBook.getPageCount(), updatedBook.getBookAmount());
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> searchBooksByBookName(String query) {
        return bookRepository.searchBooksByBookName(query);
    }

    @Override
    public Long getBookIdByTitle(String bookName) {
        return bookRepository.getBookIdByTitle(bookName);
    }

    // Add an author and a book to the database
    @Override
    @Transactional
    public void addAuthorAndBook(Author author, Book book) {
        // Check if the author already exists in the database
        Long authorId = authorService.getAuthorIdByFullName(author.getFirstName(), author.getLastName());

        // Check if the book already exists in the database
        Long bookId = getBookIdByTitle(book.getBookName());

        // If the author doesn't exist, create them
        if (authorId == null) {
            authorService.createAuthor(author);
            System.out.println("Author '" + author.getFirstName() + " " + author.getLastName() + "' has been saved");
        }

        // If the book doesn't exist, create it
        if (bookId == null) {
            createBook(book);
            System.out.println("Book '" + book.getBookName() + "' has been saved");
        }

        // Fetch the updated author and book IDs after creation
        authorId = authorService.getAuthorIdByFullName(author.getFirstName(), author.getLastName());
        bookId = getBookIdByTitle(book.getBookName());

        // Associate the author with the book
        Optional<Author> existingAuthorOptional = authorService.getAuthorById(authorId);
        Optional<Book> existingBookOptional = getBookById(bookId);
        if (existingAuthorOptional.isPresent() && existingBookOptional.isPresent()) {
            Author existingAuthor = existingAuthorOptional.get();
            Book existingBook = existingBookOptional.get();

            // Check if the author is not already associated with the book
            if (!existingBook.getAuthors().contains(existingAuthor)) {
                existingBook.getAuthors().add(existingAuthor);
                updateBook(bookId, existingBook);
                System.out.println("\033[1;34m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' has been associated with the book '" + existingBook.getBookName() + "'\033[0m");
            } else {
                System.out.println("\033[1;34m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' is already associated with the book '" + existingBook.getBookName() + "'\033[0m");
            }
        }
    }

    public int countBooksInAuthor(String firstName, String lastName) {
        return bookRepository.countBooksInAuthor(firstName, lastName);
    }


}