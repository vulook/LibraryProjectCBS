package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface FormService {

    // Create a new Form
    void createForm(Form form);

    // Return a book
    void returnBook(Long formId);

    @Transactional
    void approveReturn(Long formId, Long librarianId);

    // Update a form by its ID with new form data
    void updateForm(Long id, Form updatedForm);

    // Delete a form by its ID
    void deleteForm(Long id);

    // Retrieve a form by its ID if it exists
    Optional<Form> getFormById(Long id);

    // Retrieve a list of all forms
    List<Form> getAllForms();

    // Retrieve a list of forms for a specific user
    List<Form> getFormsByUser(User currentReader);

    List<CountTimePeriod> getFormsRegistrationByLibrarian(Long librarianId);

}
