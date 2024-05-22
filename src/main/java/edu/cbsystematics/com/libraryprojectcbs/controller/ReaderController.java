package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.user.UserDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.user.UserMapper;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.*;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountTimePeriod;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForStatus;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.MembershipDuration;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
@Secured(ROLE_READER)
@RequestMapping(READER_HOME_URL)
public class ReaderController {

    private final UserService userService;

    private final LogsService logsService;

    private final CardService cardService;

    private final FormService formService;

    private final UserRoleService userRoleService;

    private final UserAuthenticationUtils userAuthenticationUtils;


    @Autowired
    public ReaderController(UserService userService, LogsService logsService, CardService cardService, FormService formService,
                            UserAuthenticationUtils userAuthenticationUtils, UserRoleService userRoleService) {
        this.userService = userService;
        this.logsService = logsService;
        this.cardService = cardService;
        this.formService = formService;
        this.userAuthenticationUtils = userAuthenticationUtils;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/")
    public String showReaderDashboard(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Extract user information
        User reader = userService.findByEmail(username).orElse(null);
        String fullName = (reader != null) ? reader.getFirstName() + " " + reader.getLastName() : "ANONYMOUS";
        String status = (roles != null) ? RoleUtilsForStatus.getRoleLabel(roles) : "anonymous";
        System.out.println("Show ReaderDashboard: " + fullName);

        UserRole librarian = userRoleService.findRoleByName(ROLE_LIBRARIAN);
        List <User> librarianList = userService.getListUsersByRoleId(librarian.getId());
        Long countLogin = logsService.countUserActions(ActionType.LOGIN, reader);
        String membershipDuration = MembershipDuration.calculateTotalDuration(reader);
        Integer countBook = formService.getFormsByUser(reader).size();
        List<CountTimePeriod> countUserBooksRead = cardService.countUserBooksReadAfterDate(reader);

        model.addAttribute("READER_HOME_URL", READER_HOME_URL);
        model.addAttribute("fullName", fullName);
        model.addAttribute("status", status);
        model.addAttribute("countLogin", countLogin);
        model.addAttribute("membershipDuration", membershipDuration);
        model.addAttribute("countBook", countBook);
        model.addAttribute("librarianList", librarianList);
        model.addAttribute("countUserBooksRead",countUserBooksRead);

        return "reader/reader-dashboard";
    }

    @GetMapping("/about-reader")
    public String showAboutReader(Authentication authentication, Model model) {
        String emailReader = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User reader = userService.findByEmail(emailReader)
                .orElseGet(User::new);
        // Get user Logs
        List<Logs> readerLogs = logsService.getLogsByUserCreator(reader);

        model.addAttribute("READER_HOME_URL", READER_HOME_URL);
        model.addAttribute("reader", reader);
        model.addAttribute("readerLogs", readerLogs);
        return "reader/reader-about-user";
    }

    @GetMapping("/{id}/edit")
    public String showEditReaderForm(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        // Retrieve the user and roles for the given ID.
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            UserDTO readerDTO = UserMapper.toDTO(userOptional.get());

            model.addAttribute("READER_HOME_URL", READER_HOME_URL);
            model.addAttribute("reader", readerDTO);
            return "reader/reader-edit";
        } else {
            redirectAttributes.addFlashAttribute("update_error", "Error updating user!");
            return "redirect:" + READER_HOME_URL + "reader-about-user";
        }
    }

    @PostMapping("/{id}/edit")
    public String editReader(@PathVariable Long id, @Valid @ModelAttribute("reader") UserDTO readerDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {

        // Check if email is already in use
        User emailExisting = userService.findByEmail(readerDTO.getEmail()).orElse(null);
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

