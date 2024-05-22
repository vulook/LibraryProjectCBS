package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.AuthorAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.exception.AuthorNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public void updateAuthor(Long id, Author updatedAuthor) {

        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found" + id));


        // Check for details Author
        if (authorRepository.existsByFirstNameAndLastNameAndIdNot(updatedAuthor.getFirstName(), updatedAuthor.getLastName(), id)) {
            throw new AuthorAlreadyExistsException(
                    "Author with First Name: '" + updatedAuthor.getFirstName() + "' and Last Name '" + updatedAuthor.getLastName() + "' already exists");
        }

        authorRepository.updateAuthor(
                id,
                updatedAuthor.getFirstName(),
                updatedAuthor.getLastName()
        );
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }


    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }


    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }


    @Override
    public Optional<Author> findAuthorByFirstNameAndLastName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }


    @Override
    public List<Author> searchAuthorsByFullName(String query) {
        return authorRepository.searchByAuthorNameOrLastName(query);
    }


    @Override
    public List<AuthorDTO> searchAuthorsWithBookCount(String query) {
        List<Object[]> results = authorRepository.searchByAuthorNameOrLastNameWithBookCount(query);

        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        return results.stream()
                .map(result -> {
                    Author author = (Author) result[0];
                    Long bookCount = (Long) result[1];
                    AuthorDTO authorDTO = new AuthorDTO();
                    authorDTO.setId(author.getId());
                    authorDTO.setFirstName(author.getFirstName());
                    authorDTO.setLastName(author.getLastName());
                    authorDTO.setBookCount(bookCount);
                    return authorDTO;
                })
                .collect(Collectors.toList());
    }



}