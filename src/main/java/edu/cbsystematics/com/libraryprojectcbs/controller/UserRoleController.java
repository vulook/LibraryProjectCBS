package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/library/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    private final UserService userService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService, UserService userService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/list")
    public String displayRoles(Model model) {
        // Retrieve a list of all roles
        List<UserRole> userRoles = userRoleService.getAllRoles();

        int usersWithoutRole = userService.getUsersWithoutRoleId().size();

        // Create a Map for user count
        Map<Long, Integer> userCountRole = new HashMap<>();
        // Total number of users
        int totalUsers = 0;

        // Calculate user count for each role
        for (UserRole role : userRoles) {
            int userCount = userService.getTotalUsersByRoleId(role.getId());
            userCountRole.put(role.getId(), userCount);
            totalUsers += userCount;
        }

        model.addAttribute("roles", userRoles);
        model.addAttribute("userCountRole", userCountRole);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("usersWithoutRole", usersWithoutRole);
        return "roles/role-list";
    }

    @GetMapping("/list/{roleId}/users")
    public String viewUsersByRole(@PathVariable Long roleId, Model model) {
        // Retrieve the list of users associated with the specified role ID
        List<User> users = userService.getListUsersByRoleId(roleId);
        // Retrieve the total number of users for the specified role ID
        int totalUsers = userService.getTotalUsersByRoleId(roleId);
        // Retrieve the role name for the specified role ID
        String roleName = userRoleService.getRoleNameById(roleId);

        model.addAttribute("roleName", roleName);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("users", users);

        // Return the logical view name for rendering the page displaying users for the given role
        return "roles/role-users";
    }

    @GetMapping("/create")
    public String showCreateUserRoleForm(Model model) {
        model.addAttribute("createdUserRole", new UserRole());
        return "roles/role-create";
    }

    @PostMapping("/create")
    public String validateUserRole(@Valid @ModelAttribute("createdUserRole") UserRole userRole, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "roles/role-create";
        }

        // Process and save the role
        return createUserRole(userRole, redirectAttributes);
    }

    public String createUserRole(@ModelAttribute UserRole userRole, RedirectAttributes redirectAttributes) {
        try {
            // Create role
            userRoleService.createRole(userRole);
            redirectAttributes.addFlashAttribute("successMessage", "Role '" + userRole.getRoleName() + "' successfully created.");
            return "redirect:/library/roles/success";
        } catch (UserRoleAlreadyExistsException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/roles/error";
        }
    }

    @GetMapping("/list/{id}/edit")
    public String showEditUserRoleForm(@PathVariable Long id, Model model) {
        UserRole userRole = userRoleService.getRoleById(id).orElse(null);
        model.addAttribute("updatedRole", userRole);
        return "roles/role-edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editUserRole(@PathVariable Long id, @Valid @ModelAttribute("updatedRole") UserRole updatedRole, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "roles/role-edit";
        }

        if (id != null) {
            try {
                // Update role details.
                userRoleService.updateRole(id, updatedRole);
                redirectAttributes.addAttribute("successMessage", "Role '" + updatedRole.getRoleName() + "' successfully updated.");
                return "redirect:/library/roles/success";
            } catch (UserRoleAlreadyExistsException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/roles/error";
            } catch (AdminDeletionException ex) {
                redirectAttributes.addAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/roles/error";
            }
        } else {
            throw new UserRoleNotFoundException("UserRole not found");
        }
    }

    @GetMapping("/list/{id}/delete")
    public String deleteUserRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the role by ID.
            Optional<UserRole> existingRoleOptional = userRoleService.getRoleById(id);
            if (existingRoleOptional.isPresent()) {
                UserRole userRole = existingRoleOptional.get();
                String roleNameD = userRole.getRoleName();
                userRoleService.deleteRole(id);

                redirectAttributes.addAttribute("successMessage", "Role '" + roleNameD + "' successfully deleted.");
                return "redirect:/library/roles/success";
            } else {
                // If the userRole is not found, throw UserRoleNotFoundException
                throw new UserRoleNotFoundException("UserRole not found");
            }
        } catch (AdminDeletionException ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/roles/error";
        }
    }

    @GetMapping("/assign")
    public String showAssignRoleForm(Model model) {
        List<User> usersWithoutRole = userService.getUsersWithoutRoleId();
        int usersWithoutRoleCount = usersWithoutRole.size();
        List<UserRole> allRoles = userRoleService.getAllRolesWithoutAdmin();

        model.addAttribute("usersWithoutRole", usersWithoutRole);
        model.addAttribute("usersWithoutRoleCount", usersWithoutRoleCount);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("selectedRoleId", null);
        return "roles/role-assign";
    }

    @PostMapping(value = "/assign/all")
    public String assignRoleToAll(@RequestParam(name = "selectedRoleId", required = false) Long selectedRoleId, Model model) {
        Optional<UserRole> existingRoleOptional = userRoleService.getRoleById(selectedRoleId);
        if (existingRoleOptional.isPresent()) {
            UserRole selectedRole = existingRoleOptional.get();

            // Assign the selected role to all users
            userRoleService.assignRoleToAll(selectedRoleId);
            model.addAttribute("successMessage", "Users have been successfully assigned role '" + selectedRole.getRoleName() + "'");
            return "roles/success-page";
        } else {
            model.addAttribute("errorMessage", "Role not found");
            return "roles/error-page";
        }
    }

    @PostMapping(value = "/assign")
    public String assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId, Model model) {
        try {
            // Assign the role to the user
            userRoleService.assignRoleToUser(roleId, userId);
        } catch (UserRoleNotFoundException | UserNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "roles/error-page";
        }
        // Redirect to the same page
        return "redirect:/library/roles/assign";
    }

    @GetMapping("list/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        // Retrieve the user and roles for the given ID.
        User editedUser = userService.getUserById(id).orElse(null);
        List<UserRole> roles = userRoleService.getAllRolesWithoutAdmin();

        model.addAttribute("editedUser", editedUser);
        model.addAttribute("roles", roles);
        return "roles/user-edit";
    }

    @PostMapping("list/users/{id}/edit")
    public String editUser(@PathVariable Long id, @Valid @ModelAttribute("editedUser") User editedUser,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "roles/user-edit";
        }

        if (id != null) {
            try {
                // Update user details.
                userService.updateUser(id, editedUser);
                redirectAttributes.addAttribute("successMessage", "User '" + editedUser.getFirstName() + ' ' + editedUser.getLastName() + "' successfully updated.");
                return "redirect:/library/roles/success";
            } catch (UserAlreadyExistsException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/roles/error";
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @GetMapping("/success")
    public String successPage(@ModelAttribute("successMessage") String successMessage, Model model) {
        model.addAttribute("message", successMessage);
        return "roles/success-page";
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public String handleUserRoleNotFoundException(UserRoleNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/library/users/error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/library/users/error";
    }

    @GetMapping("/error")
    public String errorPage(@ModelAttribute("errorMessage") String errorMessage, Model model) {
        if (errorMessage.isEmpty()) {
            errorMessage = "Oops! Something went wrong.";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "roles/error-page";
    }

}
