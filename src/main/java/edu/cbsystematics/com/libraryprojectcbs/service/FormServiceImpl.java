package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    @Autowired
    public FormServiceImpl(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @Override
    public void createForm(Form form) {
        // Calculate returnDate based on startDate
        LocalDate calculatedReturnDate = form.calculateReturnDate();
        form.setReturnDate(calculatedReturnDate);

        // Update userBooksTakenCount
        List<Form> userForms = formRepository.findByUser(form.getUser());
        form.calculateUserBooksTakenCount(userForms);

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
        return formRepository.findByUser(currentReader);
    }

}