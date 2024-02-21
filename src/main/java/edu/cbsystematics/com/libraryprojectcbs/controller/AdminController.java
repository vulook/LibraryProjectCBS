package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.UserAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ADMIN_HOME_URL;


@Controller
@RequestMapping(ADMIN_HOME_URL)
public class AdminController {

    private final UserService userService;

    private final LogsService logsService;

    private final UserRoleService userRoleService;

    @Autowired
    public AdminController(UserService userService, LogsService logsService, UserRoleService userRoleService) {
        this.userService = userService;
        this.logsService = logsService;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/")
    public String showAdminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // @AuthenticationPrincipal annotation injects the currently authenticated user’s UserDetails into the method
        String email = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.collectingAndThen(
                        Collectors.joining(""),
                        result -> result.isEmpty() ? "ROLE_ANONYMOUS" : result));

        // Extract user information
        User admin = userService.findByEmail(email);
        String fullName = (admin != null) ? admin.getFirstName() + " " + admin.getLastName() : "ANONYMOUS";
        System.out.println("Show AdminDashboard: " + fullName);

        // Get user Logs
        List<Logs> adminLogs = logsService.getLogsByUserCreator(admin);

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("admin", admin);
        model.addAttribute("fullName", fullName);
        model.addAttribute("role", roles);
        model.addAttribute("adminLogs", adminLogs);

        return "admin/admin-dashboard";
    }

    @GetMapping("/{id}/edit")
    public String showEditAdminForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        User adminPartialEdit = userService.getUserById(id).orElse(null);

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("adminPartialEdit", adminPartialEdit);
        return "admin/admin-edit";
    }

    @PostMapping("/{id}/edit")
    public String editAdmin(@PathVariable Long id, @Valid @ModelAttribute("adminPartialEdit") User userPartialEdit,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "admin/admin-edit";
        }

        if (id != null) {
            try {
                // Update user details.
                userService.partialEdit(id, userPartialEdit.getFirstName(), userPartialEdit.getLastName(), userPartialEdit.getBirthDate(), userPartialEdit.getPhone(), userPartialEdit.getEmail(), userPartialEdit.getPassword());
                redirectAttributes.addAttribute("successMessage", "User '" + userPartialEdit.getFirstName() + ' ' + userPartialEdit.getLastName() + "' successfully updated.");
                return "redirect:" + ADMIN_HOME_URL;
            } catch (UserAlreadyExistsException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:" + ADMIN_HOME_URL;
            }
        } else {
            throw new UserNotFoundException("User not found");
        }

    }








}

