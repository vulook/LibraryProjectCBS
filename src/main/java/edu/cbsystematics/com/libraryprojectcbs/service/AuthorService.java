package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface AuthorService {

    // Create a new Author.
    void createAuthor(Author author);

    // Create a new Authors.
    void saveAll(List<Author> authors);

    // Update an existing Author by ID.
    void updateAuthor(Long id, Author updatedAuthor);

    // Delete an Author by ID.
    void deleteAuthor(Long id);

    // Retrieve all Authors.
    List<Author> getAllAuthors();

    // Retrieve an Author by their ID.
    Optional<Author> getAuthorById(Long id);

    Optional<Author> findAuthorByFirstNameAndLastName(String firstName, String lastName);

    // Search for Authors by first or last name.
    List<Author> searchAuthorsByFullName(String query);

    List<AuthorDTO> searchAuthorsWithBookCount(String query);

}