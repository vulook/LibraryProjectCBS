package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Form;

import java.util.List;
import java.util.Optional;

public interface FormService {

    // Create a new form
    void createForm(Form form);

    // Update a form by its ID with new form data
    void updateForm(Long id, Form updatedForm);

    // Delete a form by its ID
    void deleteForm(Long id);

    // Retrieve a form by its ID if it exists
    Optional<Form> getFormById(Long id);

    // Retrieve a list of all forms
    List<Form> getAllForms();

}
