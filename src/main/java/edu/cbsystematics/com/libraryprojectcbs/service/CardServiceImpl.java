package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.CardFormDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.FormDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.BookNotAvailableException;
import edu.cbsystematics.com.libraryprojectcbs.exception.BookNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.exception.CardNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.models.*;
import edu.cbsystematics.com.libraryprojectcbs.repository.BookRepository;
import edu.cbsystematics.com.libraryprojectcbs.repository.CardRepository;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final FormService formService;

    private final BookRepository bookRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, FormService formService, BookRepository bookRepository) {
        this.cardRepository = cardRepository;
        this.formService = formService;
        this.bookRepository = bookRepository;
    }


    @Override
    @Transactional
    public void approveBookDatabaseInit(boolean approved, boolean canceled, String cancellationReason, Long librarianId, User user, Book book, LocalDate startDate, LocalDate bookReturned, Long libId) {
        Card card = new Card(approved, canceled, cancellationReason, librarianId, user, book);
        if (card.getBook().isAvailable()) {
            // Decrease the available amount of the book
            card.getBook().setBookAvailable(card.getBook().getBookAvailable() - 1);
            bookRepository.save(card.getBook());

        } else {
            throw new BookNotAvailableException("The selected book is not available.");
        }

        card.setApproved(true);
        // Assign the librarian
        card.setLibrarianId(librarianId);
        // Save card
        cardRepository.save(card);

        // Create a new Form object
        Form form = new Form(card, startDate, bookReturned, libId);
        formService.createForm(form);
    }


    @Override
    @Transactional
    public void selectBook(Card card) {

        int bookAvailable = card.getBook().getBookAvailable();
        int bookAmount = card.getBook().getBookAmount();

        if (bookAvailable <= 0 || bookAvailable > bookAmount) {
            throw new IllegalArgumentException("Invalid value for availableAmount: " + bookAvailable);
        }

        // Check if the book is available
        if (card.getBook().isAvailable()) {
            // Decrease the available amount of the book
            card.getBook().setBookAvailable(card.getBook().getBookAvailable() - 1);
            card.getBook().setSelected(false);
            bookRepository.save(card.getBook());

            // Create a new card
            cardRepository.save(card);

        } else {
            throw new BookNotAvailableException("The selected book is not available.");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> getPendingApprovalCards() {
        List<Object[]> results = cardRepository.findPendingApprovalCards();
        if (results.isEmpty()) {
            results = new ArrayList<>();
        }
        return results.stream()
                .map(row -> new CardDTO(
                        (Long) row[0],          // cardId
                        (User) row[1],          // user
                        (Book) row[2],          // book
                        (boolean) row[3],       // approved
                        (boolean) row[4]        // canceled
                ))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CardFormDTO> getFormsForUser(User user) {
        List<Object[]> results = cardRepository.getFormsForUserWithLibrarian(user);
        if (results.isEmpty()) {
            results = new ArrayList<>();
        }

        return results.stream()
                .map(row -> new CardFormDTO(
                        (Long) row[0],            // id
                        (User) row[1],            // user
                        (Book) row[2],            // book
                        (boolean) row[3],         // approved
                        (boolean) row[4],         // canceled
                        (String) row[5],          // cancellationReason
                        (Long) row[6],            // librarianId
                        (String) row[7],          // librarianFirstName
                        (String) row[8],          // librarianLastName
                        (Long) row[9],            // formId
                        (LocalDate) row[10],      // LocalDate startDate
                        (LocalDate) row[11],      // LocalDate returnDate
                        (LocalDate) row[12],      // LocalDate bookReturned
                        (boolean) row[13]         // isReturned
                ))
                .toList();
    }


    @Loggable(value = ActionType.APPROVE)
    @Override
    @Transactional
    public void approveBooks(Long cardId, Long librarianId) {
        // Retrieve the card from the database
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        // Set the approval status of the card to true
        card.setApproved(true);
        // Assign the librarian
        card.setLibrarianId(librarianId);

        // Set the selected status of the book to false
        card.getBook().setSelected(false);

        // Save card
        cardRepository.save(card);

        // Create a new Form object
        Form form = new Form(card);
        formService.createForm(form);
    }


    @Loggable(value = ActionType.CANCEL)
    @Override
    @Transactional
    public void cancelApproveBooks(Long cardId, Long librarianId) {
        // Retrieve the card from the database
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        // Retrieve the book from the database
        Book existingBook = bookRepository.findById(card.getBook().getId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        // Set the approval status of the card to true and assign the librarian
        card.setApproved(false);
        card.setLibrarianId(librarianId);
        card.setCanceled(true);
        card.setCancellationReason("Reason for cancellation");

        // Save card
        cardRepository.save(card);

        // Update the book's available amount
        existingBook.setBookAvailable(existingBook.getBookAvailable() + 1); // Increase available amount
        existingBook.setSelected(false);
        bookRepository.save(existingBook);
    }


    @Override
    @Transactional(readOnly = true)
    public List<FormDTO> getFormDetails() {
        List<Object[]> results =  cardRepository.findCardInfoByFormId();
        if (results.isEmpty()) {
            results = new ArrayList<>();
        }

        return results.stream()
                .map(row -> new FormDTO(
                        (Long) row[0],            // cardId
                        (User) row[1],            // user
                        (Book) row[2],            // book
                        (Long) row[3],            // librarianId
                        (String) row[4],          // librarianFirstName
                        (String) row[5],          // librarianLastName
                        (Long) row[6],            // formId
                        (LocalDate) row[7],       // LocalDate startDate
                        (LocalDate) row[8],       // LocalDate returnDate
                        (LocalDate) row[9],       // LocalDate bookReturned
                        (boolean) row[10]         // isReturned
                ))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public Long countReadingBooks() {
        return cardRepository.countReadingBooks();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardInfoDTO> findAllCardInfo() {
        return cardRepository.findAllCardInfo().stream()
                .map(objects -> {
                    CardInfoDTO cardInfoDTO = new CardInfoDTO();
                    cardInfoDTO.setCardId((Long) objects[0]);
                    cardInfoDTO.setUser(objects[1] != null ? (User) objects[1] : null);
                    cardInfoDTO.setBook(objects[2] != null ? (Book) objects[2] : null);
                    cardInfoDTO.setApproved(objects[3] != null && (boolean) objects[3]);
                    cardInfoDTO.setCanceled(objects[4] != null && (boolean) objects[4]);
                    cardInfoDTO.setCancellationReason(objects[5] != null ? (String) objects[5] : null);
                    cardInfoDTO.setLibrarian1Id((Long) objects[6]);
                    cardInfoDTO.setFullName1(objects[7] != null ? (String) objects[7] : "");
                    cardInfoDTO.setFormId((Long) objects[8]);
                    cardInfoDTO.setStartDate(objects[9] != null ? (LocalDate) objects[9] : null);
                    cardInfoDTO.setReturnDate(objects[10] != null ? (LocalDate) objects[10] : null);
                    cardInfoDTO.setBookReturned(objects[11] != null ? (LocalDate) objects[11] : null);
                    cardInfoDTO.setReturned(objects[12] != null && Boolean.TRUE.equals(objects[12]));
                    cardInfoDTO.setLibrarian2Id((Long) objects[13]);
                    cardInfoDTO.setFullName2(objects[14] != null ? (String) objects[14] : "");
                    cardInfoDTO.setDaysLeft(cardInfoDTO.getDaysLeft());
                    cardInfoDTO.setViolation(cardInfoDTO.getViolation());
                    return cardInfoDTO;
                })
                .toList();
    }

    @Loggable(value = ActionType.DELETE)
    @Override
    public void deleteCard(Long id) {
        // Retrieve the card from the database
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        // Retrieve the book from the database
        Book existingBook = bookRepository.findById(card.getBook().getId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if(!card.isCanceled()) {
            // Increase the number of available books
            existingBook.setBookAvailable(existingBook.getBookAvailable() + 1);
            bookRepository.save(existingBook);
        }

        cardRepository.deleteById(id);
    }

    @Override
    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public List<CountTimePeriod> countUserBooksReadAfterDate(User user) {
        LocalDate currentDate = DateUtils.getCurrentDate();
        LocalDate previousDay = DateUtils.getPreviousDay();
        LocalDate previousWeek = DateUtils.getPreviousWeek();
        LocalDate previousMonth = DateUtils.getPreviousMonth();
        LocalDate previousSixMonths = DateUtils.getPreviousSixMonths();
        LocalDate previousYear = DateUtils.getPreviousYear();
        LocalDate forAllTime = DateUtils.getAllTime();

        return Collections.singletonList(
                CountTimePeriod.builder()
                        .countUsersFromCurrentDate(cardRepository.countByApprovedAndCanceledUserCard(user, currentDate))
                        .countUsersFromPreviousDay(cardRepository.countByApprovedAndCanceledUserCard(user, previousDay))
                        .countUsersFromPreviousWeek(cardRepository.countByApprovedAndCanceledUserCard(user, previousWeek))
                        .countUsersFromPreviousMonth(cardRepository.countByApprovedAndCanceledUserCard(user, previousMonth))
                        .countUsersFromPreviousSixMonths(cardRepository.countByApprovedAndCanceledUserCard(user, previousSixMonths))
                        .countUsersFromPreviousYear(cardRepository.countByApprovedAndCanceledUserCard(user, previousYear))
                        .countUsersFromAllTime(cardRepository.countByApprovedAndCanceledUserCard(user, forAllTime))
                        .build()
        );
    }




}





