package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    // Update Form Information
    @Modifying
    @Transactional
    @Query("UPDATE Form f SET f.startDate = :startDate, f.returnDate = :returnDate, f.bookReturned = :bookReturned, f.user = :userByUserId, f.book = :bookByBookId WHERE f.id = :id")
    void updateForm(
            @Param("id") Long id,
            LocalDate startDate,
            LocalDate returnDate,
            LocalDate bookReturned,
            User user,
            Book book
    );

}