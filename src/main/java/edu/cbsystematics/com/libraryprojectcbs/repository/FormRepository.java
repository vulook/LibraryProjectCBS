package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    // Update the start date of a form by its ID
    @Modifying
    @Transactional
    @Query("UPDATE Form f SET f.startDate = :startDate WHERE f.id = :id")
    void updateForm(
            Long id,
            @Param("startDate") LocalDate startDate
    );

    // Find all overdue forms for a specific user (forms with return date before the current date and book not yet returned)
    @Query("SELECT f FROM Form f INNER JOIN f.card c WHERE c.user = :user AND f.returnDate < :currentDate AND f.bookReturned IS NULL")
    List<Form> findOverdueFormsForUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    // Find forms by the user associated with the card
    List<Form> findByCard_User(User user);


    @Query("SELECT COUNT(f) FROM Form f JOIN Card c ON f.card = c WHERE c.approved = true AND c.librarianId = :librarianId AND f.startDate >= :afterDate")
    Long countFormsApprovedByLibrarianAfterDate(@Param("librarianId") Long librarianId, @Param("afterDate") LocalDate afterDate);

}


