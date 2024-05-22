package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Update Author Information
    @Modifying
    @Transactional
    @Query("UPDATE Author a SET a.firstName = :firstName, a.lastName = :lastName WHERE a.id = :id")
    void updateAuthor(
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );

    // Search by FirstName Or/And LastName
    @Query("SELECT w FROM Author w WHERE lower(w.firstName) LIKE lower(concat('%', :query, '%')) OR lower(w.lastName) LIKE lower(concat('%', :query, '%'))")
    List<Author> searchByAuthorNameOrLastName(@Param("query") String query);


    @Query("SELECT w, COUNT(b) AS bookCount FROM Author w LEFT JOIN w.books b WHERE lower(w.firstName) LIKE lower(concat('%', :query, '%')) OR lower(w.lastName) LIKE lower(concat('%', :query, '%')) GROUP BY w")
    List<Object[]> searchByAuthorNameOrLastNameWithBookCount(@Param("query") String query);


    @Query("SELECT a FROM Author a WHERE a.firstName = :firstName AND a.lastName = :lastName")
    Optional<Author> findByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);


    boolean existsByFirstNameAndLastNameAndIdNot(String firstName, String lastName, Long id);

}