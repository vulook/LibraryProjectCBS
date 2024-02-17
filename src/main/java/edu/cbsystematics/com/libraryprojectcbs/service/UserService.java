package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // Saves a new user to the database after initialization
    void createUserDatabaseInit(User user);

    // Creates a new user based on a registration DTO
    void createUserRegistration(UserRegistrationDTO registration);

    // Updates a user's information
    @Transactional
    void updateUser(Long id, User updatedUser);

    // Updates a reader's information
    @Transactional
    void updateReader(Long id, String firstName, String lastName, LocalDate birthDate, String phone, String email, String password);

    // Deletes a user with the given ID
    void deleteUser(Long id);

    // Gets a user by their ID
    Optional<User> getUserById(Long id);

    // Gets a list of all users
    List<User> getAllUsers();

    // Searches users by their full name (first or last)
    List<User> searchUsersByFullName(String query);

    // Counts the total number of users with a specific role ID
    int getTotalUsersByRoleId(Long roleId);

    // Gets a list of users with a specific role ID
    List<User> getListUsersByRoleId(Long roleId);

    // Gets a list of users without any role ID
    List<User> getUsersWithoutRoleId();

    // Finds a user by their email address
    User findByEmail(String email);

    // Returns a page of users with pagination and sorting options
    Page<User> pagination(int pageNumber, int pageSize, String sortField, String sortDirection);
}