package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.dto.login.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserService {


    // Initialize user database if user does not exist
    void createUserDatabaseInit(User user);

    // Register a new user
    User createUserRegistration(UserRegistrationDTO registrationDTO);

    // Create a new user
    User createUser(User createdUser);

    // Update an existing user
    @Transactional
    void updateUser(Long id, User updatedUser);

    // Partially update an existing user
    @Transactional
    void partialUpdateUser(Long id, String firstName, String lastName, LocalDate birthDate, String phone, String email, String password);

    // Delete a user
    void deleteUser(Long id);

    // Delete an admin user
    void deleteAdmin(Long id);

    // Get a user by ID
    Optional<User> getUserById(Long userId);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Get all users
    List<User> getAllUsers();

    // Search users by full name
    List<User> searchUsersByFullName(String query);

    // Get total users to count by role ID
    int getTotalUsersByRoleId(Long roleId);

    // Get a list of users by role ID
    List<User> getListUsersByRoleId(Long roleId);

    // Get users without role ID
    List<User> getUsersWithoutRoleId();

    // Get user registrations by role
    List<CountTimePeriod> getUserRegistrationsByRole(UserRole role);

    // Paginate users with sorting
    Page<User> paginationUsers(Integer pageNumber, Integer pageSize, String sortField, String sortDirection);

    // Find a User by their verificationCode
    User findByVerificationCode(String verificationCode);

    // Generates a verification code for resetting the password
    void createPasswordResetToken(User user);

    // Updates the password of a user with the specified ID
    void updatePassword(Long id, String password);
}