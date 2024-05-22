package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.CardFormDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.FormDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface CardService {

    // Initiates the approval process for books in the database
    @Transactional
    void approveBookDatabaseInit(boolean approved, boolean canceled, String cancellationReason, Long librarianId, User user, Book book, LocalDate startDate, LocalDate bookReturned, Long libId);

    // Creates a new cart entry when a user selects a book
    @Transactional
    void selectBook(Card card);

    // Retrieves a list of pending approval cards
    @Transactional(readOnly = true)
    List<CardDTO> getPendingApprovalCards();

    // Retrieves a list of forms for a specific user
    @Transactional(readOnly = true)
    List<CardFormDTO> getFormsForUser(User user);

    // Approves selected books by librarian
    @Transactional
    void approveBooks(Long cardId, Long librarianId);

    // Cancels approval of selected books by librarian
    @Transactional
    void cancelApproveBooks(Long cardId, Long librarianId);

    // Retrieves details of all forms
    @Transactional(readOnly = true)
    List<FormDTO> getFormDetails();

    // Counts the number of reading books
    @Transactional(readOnly = true)
    Long countReadingBooks();

    // Retrieves information about all cart entries
    @Transactional(readOnly = true)
    List<CardInfoDTO> findAllCardInfo();

    // Deletes a cart entry by its ID
    void deleteCard(Long id);

    // Retrieves a cart entry by its ID if it exists
    Optional<Card> getCardById(Long id);

    // Retrieves a list of all cart entries
    List<Card> getAllCards();

    // Counts the number of books read by a user after a specific date
    List<CountTimePeriod> countUserBooksReadAfterDate(User user);

}