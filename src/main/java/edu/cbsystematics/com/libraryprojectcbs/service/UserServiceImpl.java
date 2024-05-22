package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.dto.login.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.utils.comparator.ComparatorAge;
import edu.cbsystematics.com.libraryprojectcbs.utils.comparator.ComparatorTerm;
import edu.cbsystematics.com.libraryprojectcbs.exception.AdminDeletionException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRepository;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        // Check if user already exists in the database
        if (checkCreatedUserDetails(user)) {
            return;
        }

        // Encrypt the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the new user
        userRepository.save(user);
    }


    @Loggable(value = ActionType.CREATE)
    @Override
    public User createUserRegistration(UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBirthDate(registrationDTO.getBirthDate());
        user.setPhone(registrationDTO.getPhone());
        user.setEmail(registrationDTO.getEmail());

        // Check if user already exists in the database
        if (checkCreatedUserDetails(user)) {
            throw new UserAlreadyExistsException("User with full name '" + user.getFirstName() + " " + user.getLastName() + "' and birthdate '" + user.getBirthDate() + "' already exists.");

        }

        // Encrypt the user's password
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRegDate(LocalDateTime.now());

        // Create a verification code
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        // Disable the account until it is verified
        user.setEnabled(false);

        // Check if the role "ROLE_READER" already exists
        UserRole roleReader = userRoleService.findRoleByName(ROLE_READER);
        // Role doesn't exist, create and save it or Role exists, use it
        user.setUserRole(Objects.requireNonNullElseGet(roleReader, () -> new UserRole(ROLE_READER, "Reader role with access to browse and borrow library resources")));
        // Save the user
        userRepository.save(user);
        return user;
    }


    @Loggable(value = ActionType.CREATE)
    @Override
    public User createUser(User createdUser) {
        // Check if user already exists in the database
        if (checkCreatedUserDetails(createdUser)) {
            throw new UserAlreadyExistsException("User with full name '" + createdUser.getFirstName() + " " + createdUser.getLastName() + "' and birthdate '" + createdUser.getBirthDate() + "' already exists.");
        }

        createdUser.setPassword(passwordEncoder.encode(createdUser.getPassword()));
        createdUser.setRegDate(LocalDateTime.now());

        // Create a verification code
        String verificationCode = UUID.randomUUID().toString();
        createdUser.setVerificationCode(verificationCode);

        // Disable the account until it is verified
        createdUser.setEnabled(false);

        // Save the user
        userRepository.save(createdUser);
        return createdUser;
    }


    // Method for checking the data of the user
    private boolean checkCreatedUserDetails(User user) {
        return userRepository.existsByFirstName(user.getFirstName())
                && userRepository.existsByLastName(user.getLastName())
                && userRepository.existsByBirthDate(user.getBirthDate());
    }


    @Loggable(value = ActionType.UPDATE)
    @Transactional
    @Override
    public void updateUser(Long id, User updatedUser) {
        // Retrieve the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found" + id));

        // Check if user already exists in the database
        if (checkUpdatedUserDetails(existingUser, updatedUser, id)) {
            throw new UserAlreadyExistsException("User with full name '" + updatedUser.getFirstName() + " " + updatedUser.getLastName() + "' and birthdate '" + updatedUser.getBirthDate() + "' already exists.");
        }

        // Check if the password has changed
        boolean isPasswordChanged = !updatedUser.getPassword().equals(existingUser.getPassword());
        String encodedPassword = isPasswordChanged ? passwordEncoder.encode(updatedUser.getPassword()) : updatedUser.getPassword();
        // Output to console
        System.out.println("isPasswordChanged: " + (isPasswordChanged ? "yes" : "no"));

        // Update fields
        userRepository.updateUser(
                id,
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthDate(),
                updatedUser.getPhone(),
                updatedUser.getEmail(),
                encodedPassword,
                updatedUser.getRegDate(),
                updatedUser.isEnabled(),
                updatedUser.getUserRole()
        );
    }


    // Method for checking the data of the user
    private boolean checkUpdatedUserDetails(User existingUser, User updatedUser, Long id) {
        return (existingUser.getFirstName().equals(updatedUser.getFirstName()) || !userRepository.existsByFirstName(updatedUser.getFirstName())) &&
                (existingUser.getLastName().equals(updatedUser.getLastName()) || !userRepository.existsByLastName(updatedUser.getLastName())) &&
                (existingUser.getBirthDate().equals(updatedUser.getBirthDate()) || !userRepository.existsByBirthDate(updatedUser.getBirthDate())) &&
                !Objects.equals(existingUser.getId(), id);
    }


    @Loggable(value = ActionType.UPDATE)
    @Transactional
    @Override
    public void partialUpdateUser(Long id, String firstName, String lastName, LocalDate birthDate, String phone, String email, String password) {
        // Retrieve the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found" + id));

        // Check updated user details
        checkUpdatedUserDetails(existingUser, firstName, lastName, birthDate);

        // Check if the password has changed
        boolean isPasswordChanged = !password.equals(existingUser.getPassword());
        String encodedPassword = isPasswordChanged ? passwordEncoder.encode(password) : password;
        // Output to console
        System.out.println("isPasswordChanged: " + (isPasswordChanged ? "yes" : "no"));

        // Update the user
        userRepository.partialUpdateUser(
                id,
                firstName,
                lastName,
                birthDate,
                phone,
                email,
                encodedPassword
        );
    }


    // Method for checking the data of the user
    private void checkUpdatedUserDetails(User existingUser, String firstName, String lastName, LocalDate birthDate) {
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


    @Loggable(value = ActionType.DELETE)
    @Override
    public void deleteAdmin(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {

            // Remove the role association if it exists
            user.setUserRole(null);
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("Admin not found");
        }
    }


    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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
    public List<CountTimePeriod> getUserRegistrationsByRole(UserRole role) {
        LocalDateTime currentDate = DateUtils.getCurrentDate().atStartOfDay();
        LocalDateTime previousDay = DateUtils.getPreviousDay().atStartOfDay();
        LocalDateTime previousWeek = DateUtils.getPreviousWeek().atStartOfDay();
        LocalDateTime previousMonth = DateUtils.getPreviousMonth().atStartOfDay();
        LocalDateTime previousSixMonths = DateUtils.getPreviousSixMonths().atStartOfDay();
        LocalDateTime previousYear = DateUtils.getPreviousYear().atStartOfDay();
        LocalDateTime forAllTime = DateUtils.getAllTime().atStartOfDay();

        return Collections.singletonList(
                CountTimePeriod.builder()
                        .countUsersFromCurrentDate(userRepository.countUsersByRoleAddedAfterDate(role, currentDate))
                        .countUsersFromPreviousDay(userRepository.countUsersByRoleAddedAfterDate(role, previousDay))
                        .countUsersFromPreviousWeek(userRepository.countUsersByRoleAddedAfterDate(role, previousWeek))
                        .countUsersFromPreviousMonth(userRepository.countUsersByRoleAddedAfterDate(role, previousMonth))
                        .countUsersFromPreviousSixMonths(userRepository.countUsersByRoleAddedAfterDate(role, previousSixMonths))
                        .countUsersFromPreviousYear(userRepository.countUsersByRoleAddedAfterDate(role, previousYear))
                        .countUsersFromAllTime(userRepository.countUsersByRoleAddedAfterDate(role, forAllTime))
                        .build()
        );
    }


    @Override
    public Page<User> paginationUsers(Integer pageNumber, Integer pageSize, String sortField, String sortDirection) {

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

    @Override
    public User findByVerificationCode(String verificationCode) {
        return userRepository.findByVerificationCode(verificationCode);
    }

    @Override
    public void createPasswordResetToken(User user) {
        // Create a verification code
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        // Create a password reset Date
        user.setPasswordResetDate(LocalDateTime.now());

        // Save the new user
        userRepository.save(user);
    }


    @Override
    public void updatePassword(Long id, String password) {
        userRepository.updatePassword(id, password);
    }

}