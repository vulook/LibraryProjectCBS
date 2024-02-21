package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.config.PaginationConstants;
import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ROLE_READER;

@Controller
@RequestMapping("/library/users")
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    @Autowired
    public UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/list")
    public String displayUsersList(Model model) {
        // Retrieve a list of all users
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/user-list";
    }

    @GetMapping("/list/{id}")
    public String showUserDetails(@PathVariable Long id, Model model) {
        // Retrieve the user details by ID
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            return "users/user-details";
        } else {
            throw new UserNotFoundException("User not found for ID: " + id);
        }
    }

    // Display the user creation form
    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createdUser", new User());
        return "users/user-create";
    }

    // Process the user form data
    @PostMapping("/create")
    public String processUserForm(@Valid @ModelAttribute("createdUser") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "users/user-create"; // Return user to the creation page with a new attempt
        }

        // Process and save the user data
        return saveUser(user, redirectAttributes);
    }

    private String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Save the user using the userService
            userService.createUserDatabaseInit(user);
            redirectAttributes.addAttribute("successMessage", "User '" + user.getFirstName() + ' ' + user.getLastName() + "' successfully created.");
            return "redirect:/library/users/success";
        } catch (UserRoleNotFoundException e) {
            // Handle exception when the user role is not found
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/library/users/error";
        } catch (UserAlreadyExistsException ex) {
            // Handle exception when the user already exists
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/users/error";
        }
    }

    @GetMapping("/list/{id}/edit")
    public String showEditReaderForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        User user = userService.getUserById(id).orElse(null);
        model.addAttribute("updatedReader", user);
        return "users/reader-edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editReader(@PathVariable Long id, @Valid @ModelAttribute("updatedReader") User updatedReader, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "users/reader-edit";
        }

        if (id != null) {
            try {
                // Update user details.
                userService.partialEdit(id, updatedReader.getFirstName(), updatedReader.getLastName(), updatedReader.getBirthDate(), updatedReader.getPhone(), updatedReader.getEmail(), updatedReader.getPassword());
                redirectAttributes.addAttribute("successMessage", "Reader '" + updatedReader.getFirstName() + ' ' + updatedReader.getLastName() + "' successfully updated.");
                return "redirect:/library/users/success";
            } catch (UserAlreadyExistsException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/users/error";
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }


    @GetMapping("/list/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the user by ID.
            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String firstNameD = user.getFirstName();
                String LastNameD = user.getLastName();
                userService.deleteUser(id);

                redirectAttributes.addAttribute("successMessage", "User '" + firstNameD + ' ' + LastNameD + "' successfully deleted.");
                return "redirect:/library/users/success";
            } else {
                throw new UserNotFoundException("User not found for ID: " + id);
            }
        } catch (AdminDeletionException ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/users/error";
        }
    }

    // display list of users
    @GetMapping("/")
    public String viewHomePage(Model model) {

        return pagination(PaginationConstants.DEFAULT_PAGE_Number, PaginationConstants.DEFAULT_FIELD, PaginationConstants.DEFAULT_SORT_DIRECTION, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String pagination(@PathVariable (value = "pageNumber", required = false) int pageNumber,
                             @RequestParam(value = "sortField", required = false) String sortField,
                             @RequestParam(value = "sortDirection", required = false) String sortDirection,
                             Model model) {

        int pageSize = PaginationConstants.DEFAULT_PAGE_SIZE;

        Page<User> page = userService.pagination(pageNumber, pageSize, sortField, sortDirection);
        List<User> listUsers = page.getContent();

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("ASC") ? "DESC" : "ASC");

        model.addAttribute("listUsers", listUsers);
        return "users/index";
    }

    @GetMapping("/search-user")
    public String showSearchFormUser() {
        // Displays the search form page.
        return "users/search-form";
    }

    @GetMapping("/search-results")
    public String searchWUsers(@RequestParam(name = "searchQuery") String query, Model model) {
        // Searches for users based on the provided query.
        List<User> searchResults = userService.searchUsersByFullName(query);
        if (searchResults.isEmpty()) {
            model.addAttribute("message", "No users found with the given criteria.");
        } else {
            model.addAttribute("users", searchResults);
        }

        // Displays the search results page.
        return "users/search-results";
    }

    @GetMapping("/success")
    public String successPage(@ModelAttribute("successMessage") String successMessage, Model model) {
        // Find the role associated with readers
        Optional<UserRole> readerRole = userRoleService.findRoleByName(ROLE_READER);
        if (readerRole.isPresent()) {
            UserRole userRole = readerRole.get();
            // Retrieve total records associated with the reader role
            int totalRecords = userService.getTotalUsersByRoleId(userRole.getId());
            // Construct a message about the total number of readers
            String totalRecordsMessage = "SMART-library has " + totalRecords + " registered readers.";

            model.addAttribute("totalRecordsMessage", totalRecordsMessage);
            model.addAttribute("successMessage", successMessage);
            return "users/success-page";
        } else {
            throw new UserRoleNotFoundException("UserRole not found");
        }
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/library/users/error";
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public String handleUserRoleNotFoundException(UserRoleNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/library/users/error";
    }

    @GetMapping("/error")
    public String errorPage(@ModelAttribute("errorMessage") String errorMessage, Model model) {
        if (errorMessage.isEmpty()) {
            errorMessage = "Oops! Something went wrong.";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "users/error-page";
    }

}

