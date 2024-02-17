package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    // Update Cart Information
    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.user = :user_id, c.book = :book_id WHERE c.id = :id")
    void updateCard(
            @Param("id") Long id,
            @Param("user_id") User user,
            @Param("book_id") Book book
    );

}