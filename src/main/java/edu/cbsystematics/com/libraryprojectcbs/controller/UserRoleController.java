package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(ADMIN_HOME_URL)
public class UserRoleController {

    private final UserRoleService userRoleService;

    private final UserService userService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService, UserService userService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/roles")
    public String showRolesPage(@RequestParam(name = "tab", defaultValue = "tab1") String tab, Model model) {
        model.addAttribute("tab", tab);
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        return "roles/role-list";
    }

    @GetMapping("/roles-{tab}")
    public String tab(@PathVariable String tab) {
        if (Arrays.asList("tab1", "tab2", "tab3")
                .contains(tab)) {
            return "roles/_roles-" + tab;
        }

        return "roles/empty";
    }

    @GetMapping("/roles-tab1")
    public String displayRoles(Model model) {

        // Create a new UserRole object
        UserRole createdUserRole = new UserRole();

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

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("createdUserRole", createdUserRole);
        model.addAttribute("roles", userRoles);
        model.addAttribute("userCountRole", userCountRole);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("usersWithoutRole", usersWithoutRole);
        return "roles/_roles-tab1";
    }

    @GetMapping("/roles-tab2")
    public String showCreateRoleForm(Model model) {
        model.addAttribute("tab", "tab2");
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("ROLE_ADMIN", ROLE_ADMIN);
        model.addAttribute("ROLE_LIBRARIAN", ROLE_LIBRARIAN);
        model.addAttribute("ROLE_READER", ROLE_READER);
        model.addAttribute("ROLE_WORKER", ROLE_WORKER);
        return "roles/_roles-tab2";
    }

    @PostMapping("/roles-tab2")
    public String validateAndSaveUserRole(@RequestParam String roleName, RedirectAttributes redirectAttributes) {
        // Check if a role is already in use
        if (userRoleService.existsByRoleName(roleName)) {
            redirectAttributes.addAttribute("errorMessage", "A role with that name already exists!");
            return "redirect:" + ADMIN_HOME_URL + "roles?error";
        }

        try {
            // Create role
            userRoleService.createRole(new UserRole(roleName, RoleUtilsForDescription.getRoleDescription(roleName)));
            redirectAttributes.addAttribute("successMessage", "Role '" + roleName + "' successfully created.");
            return "redirect:" + ADMIN_HOME_URL + "roles?success";
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + ADMIN_HOME_URL + "roles?error";
        }
    }

    @GetMapping("/roles/{id}/delete")
    public String deleteUserRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the role by ID.
            Optional<UserRole> existingRoleOptional = userRoleService.getRoleById(id);
            if (existingRoleOptional.isPresent()) {
                UserRole userRole = existingRoleOptional.get();
                String roleNameD = userRole.getRoleName();
                userRoleService.deleteRole(id);

                redirectAttributes.addAttribute("successMessage", "Role '" + roleNameD + "' successfully deleted.");
                return "redirect:" + ADMIN_HOME_URL + "roles?success";
            } else {
                redirectAttributes.addAttribute("errorMessage", "Error deleting user!");
                return "redirect:" + ADMIN_HOME_URL + "roles?error";
            }
        } catch (AdminDeletionException ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + ADMIN_HOME_URL + "roles?error";
        }
    }

    @GetMapping("/roles-tab3")
    public String showAssignRoleForm(Model model) {
        List<User> usersWithoutRole = userService.getUsersWithoutRoleId();
        int usersWithoutRoleCount = usersWithoutRole.size();
        List<UserRole> allRoles = userRoleService.getAllRolesWithoutAdmin();

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("tab", "tab3");
        model.addAttribute("usersWithoutRole", usersWithoutRole);
        model.addAttribute("usersWithoutRoleCount", usersWithoutRoleCount);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("selectedRoleId", null);
        return "roles/_roles-tab3";
    }

    @PostMapping(value = "roles/assign/all")
    public String assignRoleToAll(@RequestParam(name = "selectedRoleId", required = false) Long selectedRoleId, RedirectAttributes redirectAttributes) {
        Optional<UserRole> existingRoleOptional = userRoleService.getRoleById(selectedRoleId);
        if (existingRoleOptional.isPresent()) {
            UserRole selectedRole = existingRoleOptional.get();

            // Assign the selected role to all users
            userRoleService.assignRoleToAll(selectedRoleId);
            redirectAttributes.addAttribute("successMessage", "Users have been successfully assigned role '" + selectedRole.getRoleName() + "'");
            return "redirect:" + ADMIN_HOME_URL + "roles?success";
        } else {
            redirectAttributes.addAttribute("errorMessage", "Role not found");
            return "redirect:" + ADMIN_HOME_URL + "roles?error";
        }
    }

    @PostMapping(value = "/roles-tab3")
    public String assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Assign the role to the user
            userRoleService.assignRoleToUser(roleId, userId);
        } catch (UserRoleNotFoundException | UserNotFoundException ex) {
            redirectAttributes.addAttribute("errorMessage", ex.getMessage());
            return "redirect:" + ADMIN_HOME_URL + "roles?error";
        }
        // Redirect to the same page
        model.addAttribute("tab", "tab3");
        return "redirect:" + ADMIN_HOME_URL + "roles-tab3";
    }

    @GetMapping("/roles/{roleId}/users")
    public String viewUsersByRole(@PathVariable Long roleId, Model model) {
        // Retrieve the list of users associated with the specified role ID
        List<User> users = userService.getListUsersByRoleId(roleId);
        // Retrieve the total number of users for the specified role ID
        int totalUsers = userService.getTotalUsersByRoleId(roleId);
        // Retrieve the role name for the specified role ID
        String roleName = userRoleService.getRoleNameById(roleId);

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("roleId", roleId);
        model.addAttribute("roleName", roleName);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("users", users);

        // Return the logical view name for rendering the page displaying users for the given role
        return "roles/role-users";
    }

    @GetMapping("/roles/{roleId}/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, @PathVariable Long roleId, RedirectAttributes redirectAttributes) {

        // Retrieve the user by ID.
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String firstNameD = user.getFirstName();
            String lastNameD = user.getLastName();
            userService.deleteAdmin(id);

            redirectAttributes.addAttribute("successMessage", "User '" + firstNameD + ' ' + lastNameD + "' successfully deleted.");
            //return "redirect:" + ADMIN_HOME_URL + "roles?success";
            return "redirect:" + ADMIN_HOME_URL + "roles/" + roleId + "/users?success";
        } else {
            redirectAttributes.addAttribute("errorMessage", "Error deleting");
            return "redirect:" + ADMIN_HOME_URL + "roles/" + roleId + "/users?error";
        }

    }


}
