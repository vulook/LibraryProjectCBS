package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserFullDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserFullMapper;
import edu.cbsystematics.com.libraryprojectcbs.dto.UserMapper;
import edu.cbsystematics.com.libraryprojectcbs.exception.AdminDeletionException;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.*;
import edu.cbsystematics.com.libraryprojectcbs.service.FormService;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.PaginationConstants;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForStatus;
import edu.cbsystematics.com.libraryprojectcbs.utils.TabAdminConstants;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.CountUsersFromTimePeriod;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.MembershipDuration;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(ADMIN_HOME_URL)
public class AdminController {

    private final UserService userService;

    private final LogsService logsService;

    private final FormService formService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    private final UserRoleService userRoleService;

    @Autowired
    public AdminController(UserService userService, LogsService logsService, FormService formService, UserAuthenticationUtils userAuthenticationUtils, UserRoleService userRoleService) {
        this.userService = userService;
        this.logsService = logsService;
        this.formService = formService;
        this.userAuthenticationUtils = userAuthenticationUtils;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/")
    public String showAdminDashboard(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Extract user information
        User admin = userService.findByEmail(username);
        String fullName = (admin != null) ? admin.getFirstName() + " " + admin.getLastName() : "ANONYMOUS";
        String status = RoleUtilsForStatus.getRoleLabel(roles);
        System.out.println("Show AdminDashboard: " + fullName);

        Long countLogin = logsService.countUserActions(ActionType.LOGIN, admin);
        String membershipDuration = MembershipDuration.calculateTotalDuration(admin);

        Integer countBook = formService.getFormsByUser(admin).size();

        UserRole roleReader = userRoleService.findRoleByName(ROLE_READER);
        UserRole roleLibrarian = userRoleService.findRoleByName(ROLE_LIBRARIAN);
        UserRole roleAdmin = userRoleService.findRoleByName(ROLE_ADMIN);
        List<CountUsersFromTimePeriod> countReaders = userService.getUserRegistrationsByRole(roleReader);
        List<CountUsersFromTimePeriod> countLibrarians = userService.getUserRegistrationsByRole(roleLibrarian);
        List<CountUsersFromTimePeriod> countAdmins = userService.getUserRegistrationsByRole(roleAdmin);

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("fullName", fullName);
        model.addAttribute("status", status);
        model.addAttribute("countLogin", countLogin);
        model.addAttribute("membershipDuration", membershipDuration);
        model.addAttribute("countBook", countBook);
        model.addAttribute("countReaders", countReaders);
        model.addAttribute("countLibrarians", countLibrarians);
        model.addAttribute("countAdmins", countAdmins);
        return "admin/admin-dashboard";
    }

    @GetMapping("/about-admin")
    public String showAboutAdmin(Authentication authentication, Model model) {
        String emailAdmin = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User admin = userService.findByEmail(emailAdmin);
        // Get user Logs
        List<Logs> adminLogs = logsService.getLogsByUserCreator(admin);

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("admin", admin);
        model.addAttribute("adminLogs", adminLogs);
        return "admin/admin-about-user";
    }

    @GetMapping("/{id}/edit")
    public String showEditAdminForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            UserDTO adminDTO = UserMapper.toDTO(userOptional.get());

            model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
            model.addAttribute("admin", adminDTO);
            return "admin/admin-edit";
        } else {
            model.addAttribute("update_error", "Error updating user!");
            return "redirect:/admin-about-user";
        }
    }

    @PostMapping("/{id}/edit")
    public String editAdmin(@PathVariable Long id, @Valid @ModelAttribute("admin") UserDTO adminDTO,
                            BindingResult result, RedirectAttributes redirectAttributes) {

        // Check if email is already in use
        if (emailExist(adminDTO.getEmail(), id)) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            adminDTO.setId(id);
            return "admin/admin-edit";
        }

        User adminUpdate = UserMapper.toEntity(adminDTO);

        try {
            // Update user details.
            userService.partialUpdateUser(id, adminUpdate.getFirstName(), adminUpdate.getLastName(), adminUpdate.getBirthDate(), adminUpdate.getPhone(), adminUpdate.getEmail(), adminUpdate.getPassword());
            redirectAttributes.addAttribute("successMessage", "User '" + adminDTO.getFirstName() + ' ' + adminDTO.getLastName() + "'  has been updated successfully.");
            return "redirect:" + ADMIN_HOME_URL + "about-admin?success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + ADMIN_HOME_URL + "about-admin?error";
        }

    }

    // display list of users
    @GetMapping("/user-management")
    public String viewPaginationPage(@RequestParam(name = "tab", required = false, defaultValue = "admin-management-users") String tab, Model model) {
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);

        String selectedTab = getSelectedTab(tab);

        model.addAttribute("tab", selectedTab);
        model.addAttribute("USERS", TabAdminConstants.USERS);
        model.addAttribute("USER_DETAILS", TabAdminConstants.USER_DETAILS);
        model.addAttribute("ADD_USER", TabAdminConstants.ADD_USER);
        model.addAttribute("UPDATE_USER", TabAdminConstants.UPDATE_USER);

        return pagination(PaginationConstants.DEFAULT_PAGE_Number, PaginationConstants.DEFAULT_FIELD, PaginationConstants.DEFAULT_SORT_DIRECTION, model);
    }

    private String getSelectedTab(String tab) {
        return switch (tab) {
            case TabAdminConstants.USERS, TabAdminConstants.USER_DETAILS, TabAdminConstants.ADD_USER, TabAdminConstants.UPDATE_USER -> tab;
            default -> TabAdminConstants.USERS;
        };
    }

    @GetMapping("/user-management/page/{pageNumber}")
    public String pagination(@PathVariable(value = "pageNumber") Integer pageNumber,
                             @RequestParam(value = "sortField", required = false) String sortField,
                             @RequestParam(value = "sortDirection", required = false) String sortDirection,
                             Model model) {

        Integer pageSize = PaginationConstants.DEFAULT_PAGE_SIZE;

        Page<User> page = userService.paginationUsers(pageNumber, pageSize, sortField, sortDirection);
        List<User> listUsers = page.getContent();

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("tab", "admin-management-users");
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("ASC") ? "DESC" : "ASC");

        model.addAttribute("listUsers", listUsers);
        return "admin/admin-management-users";
    }

    @GetMapping("/user-management/add-user")
    public String showAddUserForm(Model model) {
        List<UserRole> roles = userRoleService.getAllRoles();
        UserFullDTO userForCreation = new UserFullDTO();
        userForCreation.setRegDate(LocalDate.now());

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("roles", roles);
        model.addAttribute("userForCreation", userForCreation);
        model.addAttribute("tab", "admin-management-add-user");
        return "admin/admin-management-users";
    }

    @PostMapping("/user-management/add-user")
    public String addUser(@Valid @ModelAttribute("userForCreation") UserFullDTO userFullDTO,
                          BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        // Check if email is already in use
        if (emailExist(userFullDTO.getEmail())) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            List<UserRole> roles = userRoleService.getAllRoles();
            model.addAttribute("tab", "admin-management-add-user");
            model.addAttribute("roles", roles);
            redirectAttributes.addAttribute("org.springframework.validation.BindingResult.userForCreation", result);
            return "admin/admin-management-users";
        }

        try {
            userService.createUser(UserFullMapper.toEntity(userFullDTO));
            redirectAttributes.addAttribute("successMessage", "User '" + userFullDTO.getFirstName() + ' ' + userFullDTO.getLastName() + "' has been added successfully.");
            return "redirect:/library/admin/home/user-management?success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/admin/home/user-management?error";
        }
    }

    @GetMapping("/user-management/{id}/edit")
    public String showUpdateUserForm(@PathVariable("id") Long id, Model model) {
        List<UserRole> roles = userRoleService.getAllRoles();
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            UserFullDTO userForUpdate = UserFullMapper.toDTO(userOptional.get());

            model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
            model.addAttribute("userForUpdate", userForUpdate);
            model.addAttribute("roles", roles);
            model.addAttribute("tab", "admin-management-update-user");
            return "admin/admin-management-users";

        } else {
            model.addAttribute("errorMessage", "Error updating user!");
            return "redirect:/library/admin/home/user-management?error";
        }
    }

    @PostMapping("/user-management/{id}/edit")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("userForUpdate") UserFullDTO userFullDTO,
                             BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        // Check if email is already in use
        if (emailExist(userFullDTO.getEmail(), id)) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            userFullDTO.setId(id);
            List<UserRole> roles = userRoleService.getAllRoles();

            model.addAttribute("roles", roles);
            model.addAttribute("tab", "admin-management-update-user");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForCreation", result);
            redirectAttributes.addFlashAttribute("userForUpdate", userFullDTO);
            return "admin/admin-management-users";
        }

        try {
            // Update user details.
            userService.updateUser(id, UserFullMapper.toEntity(userFullDTO));
            redirectAttributes.addAttribute("successMessage", "User '" + userFullDTO.getFirstName() + ' ' + userFullDTO.getLastName() + "'  has been updated successfully.");
            return "redirect:/library/admin/home/user-management?success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/admin/home/user-management?error";
        }

    }

    @GetMapping("/user-management/{id}/delete")
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
                return "redirect:/library/admin/home/user-management?success";
            } else {
                redirectAttributes.addAttribute("errorMessage", "Error deleting");
                return "redirect:/library/admin/home/user-management?error";
            }
        } catch (AdminDeletionException ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/admin/home/user-management?error";
        }
    }

    @GetMapping("/user-management/user-details/{id}")
    public String showUserDetails(@PathVariable Long id, Model model) {
        // Retrieve the user details by ID
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Map<String, Long> actionCountMap = logsService.countByActionTypeForUser(user);
            Map<String, Long> sortedActionCountMap = new TreeMap<>(actionCountMap);

            model.addAttribute("actionCountMap", sortedActionCountMap);
            model.addAttribute("user", user);
            model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
            model.addAttribute("tab", "admin-management-user-details");
            return "admin/admin-management-users";
        } else {
            model.addAttribute("errorMessage", "User details not found!");
            return "redirect:/library/admin/home/user-management?error";
        }
    }

    private boolean emailExist(String email) {
        final User user = userService.findByEmail(email);
        return user != null;
    }

    private boolean emailExist(String email, Long id) {
        final User user = userService.findByEmail(email);
        return user != null && !Objects.equals(user.getId(), id);
    }

}

