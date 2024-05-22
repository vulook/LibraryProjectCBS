package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.GenreType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Update Book Information
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.ISBN = :ISBN, b.bookName = :bookName, b.genreType = :genreType, b.pageCount = :pageCount, b.bookAmount = :bookAmount WHERE b.id = :id")
    void updateBook(
            Long id,
            @Param("ISBN") String ISBN,
            @Param("bookName") String bookName,
            @Param("genreType") GenreType genreType,
            @Param("pageCount") int pageCount,
            @Param("bookAmount") int bookAmount
    );

    // Find a book by its ISBN
    Optional<Book> findByISBN(String isbn);

    // Check if a book with a given ISBN exists
    boolean existsByISBN(String isbn);

    // Custom query to update the isSelected status of a book by its ID
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.isSelected = :isSelected WHERE b.id = :id")
    void updateIsSelectedStatus(Long id, @Param("isSelected") boolean isSelected);


    // Search Book Name
    @Query("SELECT w FROM Book w " +
            "WHERE lower(w.bookName) LIKE lower(concat('%', :query, '%'))")
    List<Book> searchBooksByBookName(@Param("query") String query);


    // Search Book by title
    @Query("SELECT b FROM Book b WHERE b.bookName = :bookName")
    Optional<Book> findBookByTitle(@Param("bookName") String bookName);


    // Get the count of books associated with a specific author
    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a " +
            "WHERE a.firstName = :firstName AND a.lastName = :lastName")
    int countBooksInAuthor(@Param("firstName") String firstName, @Param("lastName") String lastName);


    // Custom query to fetch books with authors and read status for a given user with pagination
    @Query("SELECT b AS book, " +
            "CASE WHEN (SELECT COUNT(c.id) FROM Card c WHERE c.book.id = b.id AND c.user = :userRead AND c.approved = true) > 0 THEN 'read' ELSE '' END AS readStatus, " +
            "(SELECT COUNT(c.id) FROM Card c WHERE c.book.id = b.id AND c.approved = true) AS countRead " +
            "FROM Book b LEFT JOIN b.authors a " +
            "GROUP BY b.id")
    Page<Object[]> findBooksWithAuthorsAndReadStatus(@Param("userRead") User userRead, Pageable pageable);

}
