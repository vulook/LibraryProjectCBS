package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    // This query counts the books read by a user after the return date.
    // It uses the following criteria to filter cards:
    // - The card has been approved by a librarian.
    // - The card has not been canceled by the user.
    // - The card belongs to a specific user (user).
    // - The book return date (f.bookReturned) is greater than or equal to the specified date (afterDate).
    @Query("SELECT COUNT(c) " +
            "FROM Card c JOIN Form f ON c = f.card " +
            "WHERE c.approved = true " +
            "AND c.canceled = false " +
            "AND c.user = :user " +
            "AND f.bookReturned >= :afterDate")
    Long countByApprovedAndCanceledUserCard(@Param("user") User user, @Param("afterDate") LocalDate afterDate);


    // This query selects all cards that are pending approval.
    // A card is considered pending approval if it meets the following criteria:
    // - The card is not approved.
    // - The card is not canceled.
    // - The card does not have a librarian ID assigned to it
    @Query("SELECT c.id, " +
            "c.user, c.book, " +
            "c.approved, c.canceled " +
            "FROM Card c " +
            "WHERE c.approved = false AND c.canceled = false AND c.librarianId IS NULL")
    List<Object[]> findPendingApprovalCards();


    // This query selects all forms associated with a specific user, along with additional information such as librarian details.
    // It retrieves the following information for each form:
    // - Form ID
    // - User details (id, email, first name, last name)
    // - Book details (id, title)
    // - Approval status (true if approved, false otherwise)
    // - Cancellation status (true if canceled, false otherwise)
    // - Cancellation reason (reason for cancellation, if applicable)
    // - Librarian ID (ID of the librarian who approved the form)
    // - Librarian details (first name, last name) if a librarian has approved the form
    // - Start date of the form
    // - Return date specified in the form
    // - Actual return date of the book (if returned)
    // - Flag indicating if the book has been returned
    @Query("SELECT c.id, c.user, c.book, c.approved, c.canceled, c.cancellationReason, c.librarianId, " +
            "COALESCE(librarian.firstName, '') AS firstName, " +
            "COALESCE(librarian.lastName, '') AS lastName, " +
            "COALESCE(form.id, NULL) AS formId, " +
            "COALESCE(form.startDate, NULL) AS startDate, " +
            "COALESCE(form.returnDate, NULL) AS returnDate, " +
            "COALESCE(form.bookReturned, NULL) AS bookReturned, " +
            "COALESCE(form.isReturned, false) AS isReturned " +
            "FROM Card c " +
            "LEFT JOIN Form form ON c.id = form.card.id " +
            "LEFT JOIN User librarian ON c.librarianId = librarian.id " +
            "WHERE c.user = :user")
    List<Object[]> getFormsForUserWithLibrarian(@Param("user") User user);


    // This query selects detailed information about a form based on the form ID.
    // It retrieves the following information:
    // - Form ID
    // - User details (id, email, first name, last name)
    // - Book details (id, title)
    // - Librarian ID (ID of the librarian who approved the form)
    // - Librarian details (first name, last name)
    // - Start date of the form
    // - Return date specified in the form
    // - Actual return date of the book (if returned)
    // - Flag indicating if the book has been returned
    @Query("SELECT c.id AS cardId, c.user, c.book, c.librarianId, librarian.firstName AS firstName, librarian.lastName AS lastName, " +
            "f.id AS formId, f.startDate, f.returnDate, f.bookReturned, f.isReturned " +
            "FROM Card c " +
            "LEFT JOIN Form f ON c.id = f.card.id " +
            "LEFT JOIN User librarian ON c.librarianId = librarian.id " +
            "WHERE f.bookReturned IS NOT NULL AND f.isReturned = false")
    List<Object[]> findCardInfoByFormId();


    // This query counts the number of cards with approved forms that have not been returned yet.
    // It counts the number of cards where the form is approved (c.approved = true)
    // and the book has not been returned (f.isReturned = false).
    @Query("SELECT COUNT(c.id) " +
            "FROM Card c " +
            "JOIN Form f ON c.id = f.card.id " +
            "WHERE c.approved = true AND f.isReturned = false")
    Long countReadingBooks();


    // This query selects detailed information about all cards and their associated forms.
    // It retrieves the following information:
    // - Card ID
    // - User details (id, email, first name, last name)
    // - Book details (id, title)
    // - Approval status of the card
    // - Cancellation status of the card
    // - Cancellation reason (if the card is canceled)
    // - Librarian ID (ID of the librarian who approved the form)
    // - Librarian details (first name, last name) for both the card and the form
    // - Form ID
    // - Start date of the form
    // - Return date specified in the form
    // - Actual return date of the book (if returned)
    // - Flag indicating if the book has been returned
    @Query("SELECT c.id AS cardId, c.user, c.book, c.approved, c.canceled, c.cancellationReason, " +
            "c.librarianId, " +
            "CASE " +
            "   WHEN c.librarianId IS NULL AND c.approved = FALSE THEN 'Awaiting approval' " +
            "   ELSE CONCAT(librarian1.firstName, ' ', librarian1.lastName) " +
            "END AS fullName1, " +
            "f.id AS formId, f.startDate, f.returnDate, f.bookReturned, f.isReturned AS returned, " +
            "f.librarianId, " +
            "CASE " +
            "   WHEN f.librarianId IS NULL AND c.canceled = TRUE THEN 'Canceled' " +
            "   ELSE CONCAT(librarian2.firstName, ' ', librarian2.lastName) " +
            "END AS fullName2 " +
            "FROM Card c " +
            "LEFT JOIN Form f ON c.id = f.card.id " +
            "LEFT JOIN User librarian1 ON c.librarianId = librarian1.id " +
            "LEFT JOIN User librarian2 ON f.librarianId = librarian2.id " +
            "ORDER BY c.id DESC")
    List<Object[]> findAllCardInfo();



}
