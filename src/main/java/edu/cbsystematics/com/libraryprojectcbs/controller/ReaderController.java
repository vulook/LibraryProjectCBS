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
@PreAuthorize("hasRole('READER')")
@RequestMapping(READER_HOME_URL)
public class ReaderController {

    private final UserService userService;

    private final LogsService logsService;

    private final FormService formService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public ReaderController(UserService userService, LogsService logsService, FormService formService, UserAuthenticationUtils userAuthenticationUtils) {
        this.userService = userService;
        this.logsService = logsService;
        this.formService = formService;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    @GetMapping("/")
    public String showReaderDashboard(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Extract user information
        User reader = userService.findByEmail(username);
        String fullName = (reader != null) ? reader.getFirstName() + " " + reader.getLastName() : "ANONYMOUS";
        String status = RoleUtilsForStatus.getRoleLabel(roles);
        System.out.println("Show ReaderDashboard: " + fullName);

        Long countLogin = logsService.countUserActions(ActionType.LOGIN, reader);
        String membershipDuration = MembershipDuration.calculateTotalDuration(reader);

        Integer countBook = formService.getFormsByUser(reader).size();

        model.addAttribute("READER_HOME_URL", READER_HOME_URL);
        model.addAttribute("fullName", fullName);
        model.addAttribute("status", status);
        model.addAttribute("countLogin", countLogin);
        model.addAttribute("membershipDuration", membershipDuration);
        model.addAttribute("countBook", countBook);

        return "reader/reader-dashboard";
    }

    @GetMapping("/about-reader")
    public String showAboutReader(Authentication authentication, Model model) {
        String emailReader = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User reader = userService.findByEmail(emailReader);
        // Get user Logs
        List<Logs> readerLogs = logsService.getLogsByUserCreator(reader);

        model.addAttribute("READER_HOME_URL", READER_HOME_URL);
        model.addAttribute("reader", reader);
        model.addAttribute("readerLogs", readerLogs);
        return "reader/reader-about-user";
    }

    @GetMapping("/{id}/edit")
    public String showEditReaderForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            UserDTO readerDTO = UserMapper.toDTO(userOptional.get());

            model.addAttribute("READER_HOME_URL", READER_HOME_URL);
            model.addAttribute("reader", readerDTO);
            return "reader/reader-edit";
        } else {
            model.addAttribute("update_error", "Error updating user!");
            return "redirect:/reader-about-user";
        }
    }

    @PostMapping("/{id}/edit")
    public String editReader(@PathVariable Long id, @Valid @ModelAttribute("reader") UserDTO readerDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {

        // Check if email is already in use
        User emailExisting = userService.findByEmail(readerDTO.getEmail());
        if (emailExisting != null && !Objects.equals(emailExisting.getId(), id)) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            readerDTO.setId(id);
            return "reader/reader-edit";
        }

        User readerUpdate = UserMapper.toEntity(readerDTO);

        try {
            // Update user details.
            userService.partialUpdateUser(id, readerUpdate.getFirstName(), readerUpdate.getLastName(), readerUpdate.getBirthDate(), readerUpdate.getPhone(), readerUpdate.getEmail(), readerUpdate.getPassword());
            redirectAttributes.addAttribute("successMessage", "User '" + readerDTO.getFirstName() + ' ' + readerDTO.getLastName() + "'  has been updated successfully.");
            return "redirect:" + READER_HOME_URL + "about-reader?update_success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + READER_HOME_URL + "about-reader?update_error";
        }

    }









}

