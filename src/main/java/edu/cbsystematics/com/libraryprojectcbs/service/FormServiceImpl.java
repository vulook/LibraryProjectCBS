package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.exception.FormNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.BookRepository;
import edu.cbsystematics.com.libraryprojectcbs.repository.FormRepository;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    private final BookRepository bookRepository;

    @Autowired
    public FormServiceImpl(FormRepository formRepository, BookRepository bookRepository) {
        this.formRepository = formRepository;
        this.bookRepository = bookRepository;
    }


    @Override
    public void createForm(Form form) {
        // Update userBooksTakenCount
        Card card = form.getCard();
        User user = card.getUser();
        List<Form> userForms = formRepository.findByCard_User(user);
        form.calculateUserBooksTakenCount(userForms);

        formRepository.save(form);
    }


    @Override
    public void returnBook(Long formId) {
        // Retrieve the form from the database
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found"));

        // Update the bookReturned date
        form.setBookReturned(LocalDate.now());

        // Save the updated form
        formRepository.save(form);
    }


    @Override
    @Transactional
    public void approveReturn(Long formId, Long librarianId) {
        // Retrieve the form from the database
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found"));

        // Set the isReturned flag to true
        form.setReturned(true);

        // Set the librarianId
        form.setLibrarianId(librarianId);

        // Increment the book's available count
        Book book = form.getCard().getBook();
        book.setBookAvailable(book.getBookAvailable() + 1);
        bookRepository.save(book);

        // Save the updated form
        formRepository.save(form);
    }


    @Override
    @Transactional
    public void updateForm(Long id, Form updatedForm) {
        formRepository.updateForm(id, updatedForm.getStartDate());
    }

    @Override
    public void deleteForm(Long id) {
        formRepository.deleteById(id);
    }

    @Override
    public Optional<Form> getFormById(Long id) {
        return formRepository.findById(id);
    }

    @Override
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Override
    public List<Form> getFormsByUser(User currentReader) {
        return formRepository.findByCard_User(currentReader);
    }


    @Override
    public List<CountTimePeriod> getFormsRegistrationByLibrarian(Long librarianId) {
        var currentDate = DateUtils.getCurrentDate();
        var previousDay = DateUtils.getPreviousDay();
        var previousWeek = DateUtils.getPreviousWeek();
        var previousMonth = DateUtils.getPreviousMonth();
        var previousSixMonths = DateUtils.getPreviousSixMonths();
        var previousYear = DateUtils.getPreviousYear();
        var forAllTime = DateUtils.getAllTime();

        return Collections.singletonList(
                CountTimePeriod.builder()
                        .countUsersFromCurrentDate(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, currentDate))
                        .countUsersFromPreviousDay(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, previousDay))
                        .countUsersFromPreviousWeek(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, previousWeek))
                        .countUsersFromPreviousMonth(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, previousMonth))
                        .countUsersFromPreviousSixMonths(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, previousSixMonths))
                        .countUsersFromPreviousYear(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, previousYear))
                        .countUsersFromAllTime(formRepository.countFormsApprovedByLibrarianAfterDate(librarianId, forAllTime))
                        .build()
        );
    }



}