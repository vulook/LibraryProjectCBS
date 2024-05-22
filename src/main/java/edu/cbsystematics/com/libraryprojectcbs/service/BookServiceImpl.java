package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.BookNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.AuthorRepository;
import edu.cbsystematics.com.libraryprojectcbs.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }


    @Override
    public void createBook(Book book) {
        bookRepository.save(book);
    }


    @Transactional
    @Override
    public void addAuthorAndBook(Author author, Book book) {
        // Check if the author already exists in the database
        Optional<Author> existingAuthorOptional = authorRepository.findByFirstNameAndLastName(author.getFirstName(), author.getLastName());
        Author existingAuthor = existingAuthorOptional.orElseGet(() -> {
            System.out.println("\033[1;34m\033[1mAuthor '" + author.getFirstName() + " " + author.getLastName() + "' has been saved" + "'\033[0m");
            return authorRepository.save(author);
        });

        // Check if the book already exists in the database
        Optional<Book> existingBookOptional = bookRepository.findByISBN(book.getISBN());
        Book existingBook = existingBookOptional.orElseGet(() -> {
            System.out.println("\033[1;32m\033[1mBook with ISBN '" + book.getISBN() + "' has been saved" + "'\033[0m");
            return bookRepository.save(book);
        });

        // Associate the author with the book
        if (!existingBook.getAuthors().contains(existingAuthor)) {
            existingBook.getAuthors().add(existingAuthor);
            updateBook(existingBook.getId(), existingBook);
            System.out.println("\033[1;33m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' has been associated with the book '" + existingBook.getBookName() + "'\033[0m");
        } else {
            System.out.println("\033[1;33m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' is already associated with the book '" + existingBook.getBookName() + "'\033[0m");
        }
    }


    @Transactional
    @Override
    public Book addAuthorAndBook(Book book) {
        // Check if the book already exists in the database
        Optional<Book> existingBookOptional = bookRepository.findByISBN(book.getISBN());
        if (existingBookOptional.isPresent()) {
            System.out.println("\033[1;31m\033[1mBook with ISBN '" + book.getISBN() + "' already exists in the database\033[0m");
            return book;
        }

        // Iterate over the authors in the book
        book.getAuthors().forEach(author -> {
            // Check if the author already exists in the database
            Optional<Author> existingAuthorOptional = authorRepository.findByFirstNameAndLastName(author.getFirstName(), author.getLastName());
            Author existingAuthor = existingAuthorOptional.orElseGet(() -> {
                System.out.println("\033[1;34m\033[1mAuthor '" + author.getFirstName() + " " + author.getLastName() + "' has been saved\033[0m");
                return authorRepository.save(author);
            });

            // Associate the author with the book
            if (!book.getAuthors().contains(existingAuthor)) {
                book.addAuthor(existingAuthor);
                System.out.println("\033[1;32m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' has been associated with the book '" + book.getBookName() + "'\033[0m");
            } else {
                System.out.println("\033[1;33m\033[1mAuthor '" + existingAuthor.getFirstName() + " " + existingAuthor.getLastName() + "' is already associated with the book '" + book.getBookName() + "'\033[0m");
            }
        });

        // Save the book
        return bookRepository.save(book);
    }


    @Transactional
    @Override
    public void updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found" + id));

        String newISBN = updatedBook.getISBN();
        if (!existingBook.getISBN().equals(newISBN) && bookRepository.existsByISBN(newISBN)) {
            System.out.println("\033[1;31m\033[1mBook with ISBN '" + newISBN + "' already exists.\033[0m");
            return;
        }

        bookRepository.updateBook(
                id,
                updatedBook.getISBN(),
                updatedBook.getBookName(),
                updatedBook.getGenreType(),
                updatedBook.getPageCount(),
                updatedBook.getBookAmount()
        );
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


    @Transactional
    @Override
    public void updateIsSelectedStatus(Long id, boolean isSelected) {
        bookRepository.updateIsSelectedStatus(id, isSelected);
    }


    @Override
    public List<Book> searchBooksByBookName(String query) {
        return bookRepository.searchBooksByBookName(query);
    }


    @Override
    public Optional<Book> getBookByTitle(String bookName) {
        return bookRepository.findBookByTitle(bookName);
    }


    @Override
    public Optional<Book> findBookByISBN(String isbn) {
        return bookRepository.findByISBN(isbn);
    }


    @Override
    public boolean checkIfBookExistsByISBN(String isbn) {
        return bookRepository.existsByISBN(isbn);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<BookInfoDTO> findPaginated(User userRead, int pageNo, int pageSize, String sortField, String sortDirection) {
        // Create a sort object based on sortField and sortDirection
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        // Create a pageable object for pagination
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        // Retrieve results
        Page<Object[]> results = bookRepository.findBooksWithAuthorsAndReadStatus(userRead, pageable);

        // Check for null or empty results
        if (results == null || !results.hasContent()) {
            return Page.empty(pageable);
        }

        // Convert to BookDTO
        List<BookInfoDTO> list = results.stream()
                .map(row -> new BookInfoDTO(
                        (Book) row[0],                         // Book
                        (String) row[1],                       // readStatus
                        (Long) row[2]                          // countRead
                ))
                .toList();

        // Return a new Page object with the converted list and pageable information
        return new PageImpl<>(list, pageable, results.getTotalElements());
    }


}