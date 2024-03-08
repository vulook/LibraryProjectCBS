package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserMapper;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.FormService;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForStatus;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.MembershipDuration;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping(LIBRARIAN_HOME_URL)
public class LibrarianController {

    private final UserService userService;

    private final LogsService logsService;

    private final FormService formService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    private final UserRoleService userRoleService;

    @Autowired
    public LibrarianController(UserService userService, LogsService logsService, FormService formService, UserAuthenticationUtils userAuthenticationUtils, UserRoleService userRoleService) {
        this.userService = userService;
        this.logsService = logsService;
        this.formService = formService;
        this.userAuthenticationUtils = userAuthenticationUtils;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/")
    public String showLibrarianDashboard(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Extract user information
        User librarian = userService.findByEmail(username);
        String fullName = (librarian != null) ? librarian.getFirstName() + " " + librarian.getLastName() : "ANONYMOUS";
        String status = RoleUtilsForStatus.getRoleLabel(roles);
        System.out.println("Show LibrarianDashboard: " + fullName);

        Long countLogin = logsService.countUserActions(ActionType.LOGIN, librarian);
        String membershipDuration = MembershipDuration.calculateTotalDuration(librarian);

        Integer countBook = formService.getFormsByUser(librarian).size();

        model.addAttribute("LIBRARIAN_HOME_URL", LIBRARIAN_HOME_URL);
        model.addAttribute("fullName", fullName);
        model.addAttribute("status", status);
        model.addAttribute("countLogin", countLogin);
        model.addAttribute("membershipDuration", membershipDuration);
        model.addAttribute("countBook", countBook);
        return "librarian/librarian-dashboard";
    }

    @GetMapping("/about-librarian")
    public String showAboutLibrarian(Authentication authentication, Model model) {
        String emailLibrarian = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User librarian = userService.findByEmail(emailLibrarian);
        // Get user Logs
        List<Logs> librarianLogs = logsService.getLogsByUserCreator(librarian);

        model.addAttribute("LIBRARIAN_HOME_URL", LIBRARIAN_HOME_URL);
        model.addAttribute("librarian", librarian);
        model.addAttribute("librarianLogs", librarianLogs);
        return "librarian/librarian-about-user";
    }

    @GetMapping("/{id}/edit")
    public String showEditLibrarianForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            UserDTO librarianDTO = UserMapper.toDTO(userOptional.get());

            model.addAttribute("LIBRARIAN_HOME_URL", LIBRARIAN_HOME_URL);
            model.addAttribute("librarian", librarianDTO);
            return "librarian/librarian-edit";
        } else {
            model.addAttribute("update_error", "Error updating user!");
            return "redirect:/librarian-about-user";
        }
    }

    @PostMapping("/{id}/edit")
    public String editLibrarian(@PathVariable Long id, @Valid @ModelAttribute("librarian") UserDTO librarianDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {

        // Check if email is already in use
        User emailExisting = userService.findByEmail(librarianDTO.getEmail());
        if (emailExisting != null && !Objects.equals(emailExisting.getId(), id)) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            librarianDTO.setId(id);
            return "librarian/librarian-edit";
        }

        User librarianUpdate = UserMapper.toEntity(librarianDTO);

        try {
            // Update user details.
            userService.partialUpdateUser(id, librarianUpdate.getFirstName(), librarianUpdate.getLastName(), librarianUpdate.getBirthDate(), librarianUpdate.getPhone(), librarianUpdate.getEmail(), librarianUpdate.getPassword());
            redirectAttributes.addAttribute("successMessage", "User '" + librarianDTO.getFirstName() + ' ' + librarianDTO.getLastName() + "'  has been updated successfully.");
            return "redirect:" + LIBRARIAN_HOME_URL + "about-librarian?update_success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + LIBRARIAN_HOME_URL + "about-librarian?update_error";
        }

    }









}

