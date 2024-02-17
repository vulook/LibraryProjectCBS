package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ADMIN_HOME_URL;


@Controller
@RequestMapping(ADMIN_HOME_URL)
public class AdminController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    @Autowired
    public AdminController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/")
    public String showAdminDashboard(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        String fullName = user.getFirstName() + " " + user.getLastName();
        String role = user.getUserRole().getRoleName();
        LocalDateTime loginTime = LocalDateTime.now();
        System.out.println("Show AdminDashboard: " + fullName);

        model.addAttribute("role", role);
        model.addAttribute("fullName", fullName);
        model.addAttribute("loginTime", loginTime);

        return "admin/admin-dashboard";
    }

}

