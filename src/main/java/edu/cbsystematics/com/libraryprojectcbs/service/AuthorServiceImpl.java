package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.exception.AuthorAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void createAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    public void saveAll(List<Author> authors) {
        authorRepository.saveAll(authors);
    }

    @Override
    @Transactional
    public void updateAuthor(Long id, Author updatedAuthor) {

        // Check for details updatedUser
        if (authorRepository.existsByFirstName(updatedAuthor.getFirstName())
                && authorRepository.existsByLastName(updatedAuthor.getLastName())) {
            throw new AuthorAlreadyExistsException(
                    "Author with First Name: '" + updatedAuthor.getFirstName() + "' and Last Name '" + updatedAuthor.getLastName() + "' already exists");
        }

        authorRepository.updateAuthor(id, updatedAuthor.getFirstName(), updatedAuthor.getLastName());
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    @Transactional
    public List<Author> searchAuthorsByFullName(String query) {
        return authorRepository.searchByAuthorNameOrLastName(query);
    }

    @Override
    public Long getAuthorIdByFullName(String firstName, String lastName) {
        return authorRepository.getAuthorIdByFullName(firstName, lastName);
    }

}