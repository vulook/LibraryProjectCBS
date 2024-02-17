package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.config.aspect.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.config.comporator.ComparatorAge;
import edu.cbsystematics.com.libraryprojectcbs.config.comporator.ComparatorTerm;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.AdminDeletionException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ROLE_ADMIN;
import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ROLE_READER;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserRoleService userRoleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void createUserDatabaseInit(User user) {
        // Check created user details
        validateCreatedUserDetails(user);

        // Encrypt the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the new user
        User newUser = userRepository.save(user);

        // Print the new user
        System.out.println(newUser);
    }

    @Override
    public void createUserRegistration(UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBirthDate(registrationDTO.getBirthDate());
        user.setPhone(registrationDTO.getPhone());
        user.setEmail(registrationDTO.getEmail());
        // Encrypt the user's password
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRegDate(LocalDate.now());

        // Check if the role "ROLE_READER" already exists
        Optional<UserRole> roleReader = userRoleService.findRoleByName(ROLE_READER);
        if (roleReader.isEmpty()) {
            // Role doesn't exist, create and save it
            user.setUserRole(new UserRole(ROLE_READER, "Reader role with access to browse and borrow library resources"));
        } else {
            // Role exists, use it
            user.setUserRole(roleReader.get());
        }

        // Check created user details
        validateCreatedUserDetails(user);

        // Save the user
        User savedUser = userRepository.save(user);

        print (savedUser);

    }


    // Method for checking the data of the user
    private void validateCreatedUserDetails(User user) {
        if (userRepository.existsByFirstName(user.getFirstName())
                && userRepository.existsByLastName(user.getLastName())
                && userRepository.existsByBirthDate(user.getBirthDate())) {
            throw new UserAlreadyExistsException(
                    "User with First Name: '" + user.getFirstName() + "', Last Name '" + user.getLastName() +
                            "', Date of birth '" + user.getBirthDate() + "' already exists");
        }
    }

    @Override
    @Transactional
    public void updateUser(Long id, User updatedUser) {
        // Retrieve the existing user
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            // Check updated user details
            validateUpdatedUserDetails(existingUser, updatedUser);
        }
        // Update fields
        userRepository.updateUser(
                id,
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthDate(),
                updatedUser.getPhone(),
                updatedUser.getEmail(),
                updatedUser.getPassword(),
                updatedUser.getRegDate(),
                updatedUser.getUserRole()
        );
    }

    private void validateUpdatedUserDetails(User existingUser, User updatedUser) {
        if ((!existingUser.getFirstName().equals(updatedUser.getFirstName()) && userRepository.existsByFirstName(updatedUser.getFirstName())) ||
                (!existingUser.getLastName().equals(updatedUser.getLastName()) && userRepository.existsByLastName(updatedUser.getLastName())) ||
                (!existingUser.getBirthDate().equals(updatedUser.getBirthDate()) && userRepository.existsByBirthDate(updatedUser.getBirthDate()))) {
            throw new UserAlreadyExistsException(
                    "User with First Name: '" + updatedUser.getFirstName() + "', Last Name '" + updatedUser.getLastName() +
                            "', Date of birth '" + updatedUser.getBirthDate() + "' already exists");
        }
    }

    @Override
    @Transactional
    public void updateReader(Long id, String firstName, String lastName, LocalDate birthDate, String phone, String email, String password) {
        // Retrieve the existing user
        Optional<User> existingReaderOptional = userRepository.findById(id);
        // Check for details updatedUser
        if (existingReaderOptional.isPresent()) {
            User existingUser = existingReaderOptional.get();
            // Validate the updated details
            validateUpdatedReaderDetails(existingUser, firstName, lastName, birthDate);
        }
        // Update the reader
        userRepository.updateReader(id, firstName, lastName, birthDate, phone, email, password);
    }

    private void validateUpdatedReaderDetails(User existingUser, String firstName, String lastName, LocalDate birthDate) {
        if ((!existingUser.getFirstName().equals(firstName) && userRepository.existsByFirstName(firstName)) ||
                (!existingUser.getLastName().equals(lastName) && userRepository.existsByLastName(lastName)) ||
                (!existingUser.getBirthDate().equals(birthDate) && userRepository.existsByBirthDate(birthDate))) {
            throw new UserAlreadyExistsException(
                    "Reader with First Name: '" + firstName + "', Last Name '" + lastName +
                            "', Date of birth '" + birthDate + "' already exists");
        }
    }

    @Loggable(value = ActionType.DELETE)
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.getUserRole().getRoleName().equals(ROLE_ADMIN)) {
                throw new AdminDeletionException("Cannot delete administrator user");
            }

            // Remove the role association if it exists
            user.setUserRole(null);
            userRepository.deleteById(id);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsersByFullName(String query) {
        return userRepository.searchUserByNameOrLastName(query);
    }

    @Override
    public int getTotalUsersByRoleId(Long roleId) {
        // Validate the role ID
        if (roleId == null) {
            return 0;
        }

        // Return the total number of users
        return userRepository.countAllByUserRoleId(roleId);
    }

    @Override
    public List<User> getListUsersByRoleId(Long roleId) {
        return userRepository.findAllByUserRoleId(roleId);
    }

    @Override
    public List<User> getUsersWithoutRoleId() {
        return userRepository.findUsersWithoutRole();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Page<User> pagination(int pageNumber, int pageSize, String sortField, String sortDirection) {

        // Check if sortField is "age" or "term"
        if ("age".equals(sortField) || "term".equals(sortField)) {
            // Retrieve all users
            List<User> users = userRepository.findAll();

            Comparator<User> comparator = switch (sortField) {
                case "age" -> new ComparatorAge();
                case "term" -> new ComparatorTerm();
                default -> throw new IllegalArgumentException("Invalid sortField: " + sortField);
            };

            // Comparator
            if (sortDirection != null && sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())) {
                comparator = comparator.reversed();
            }

            // Sort users using the comparator
            users.sort(comparator);

            // Calculate for pagination
            int start = (pageNumber - 1) * pageSize;
            int end = Math.min(start + pageSize, users.size());
            List<User> sublist = users.subList(start, end);

            // Return a Page containing the sublist of users
            return new PageImpl<>(sublist, PageRequest.of(pageNumber - 1, pageSize), users.size());

        } else {
            // Create a sort object
            Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
            // Create a pageable object
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

            // Return a Page of users using findAll with pagination
            return userRepository.findAll(pageable);
        }

    }

    private void print (User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        String email = user.getEmail();
        String role = user.getUserRole().getRoleName();
        String message = String.format("\033[1;31mUser created successfully: %s, Email: %s, Role: %s\033[0m", fullName, email, role);
        System.out.println(message);
    }


}