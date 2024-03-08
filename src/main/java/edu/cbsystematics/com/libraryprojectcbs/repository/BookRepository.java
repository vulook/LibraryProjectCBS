package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.GenreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Update Book Information
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.bookName = :bookName, b.genreType = :genreType, b.pageCount = :pageCount, b.bookAmount = :bookAmount WHERE b.id = :id")
    void updateBook(
            Long id,
            @Param("bookName") String bookName,
            @Param("genreType") GenreType genreType,
            @Param("pageCount") int pageCount,
            @Param("bookAmount") int bookAmount
    );

    // Search Book Name
    @Query("SELECT w FROM Book w WHERE lower(w.bookName) LIKE lower(concat('%', :query, '%'))")
    List<Book> searchBooksByBookName(@Param("query") String query);

    // Search Book's ID by title
    @Query("SELECT b.id FROM Book b WHERE b.bookName = :bookName")
    Long getBookIdByTitle(@Param("bookName") String bookName);

    // Get the count of books associated with a specific author
    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a " +
            "WHERE a.firstName = :firstName AND a.lastName = :lastName")
    int countBooksInAuthor(@Param("firstName") String firstName, @Param("lastName") String lastName);

}
