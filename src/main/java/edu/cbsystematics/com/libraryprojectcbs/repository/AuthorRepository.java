package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    // Search ID by FullName
    @Query("SELECT a.id FROM Author a WHERE a.firstName = :firstName AND a.lastName = :lastName")
    Long getAuthorIdByFullName (@Param("firstName") String firstName, @Param("lastName") String lastName);

    // Check if a firstName with the given firstName exists. Returns true if a match is found
    boolean existsByFirstName(String firstName);

    // Check if a lastName with the given lastName exists. Returns true if a match is found
    boolean existsByLastName(String lastName);

}