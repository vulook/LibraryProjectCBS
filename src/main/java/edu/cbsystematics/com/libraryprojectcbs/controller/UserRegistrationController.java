package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.dto.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.MIN_AGE;


@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private final UserService userService;

    @Autowired
    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDTO userRegistrationDTO() {
        return new UserRegistrationDTO();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("age", MIN_AGE);
        return "logging/registration";
    }

    @PostMapping
    public String registerNewUser(@ModelAttribute("user") @Valid UserRegistrationDTO userRegistrationDTO,
                                  BindingResult result) {

        // Check if email is already in use
        if (emailExist(userRegistrationDTO.getEmail())) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        // If there are validation errors, return to the registration page
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "logging/registration";
        }

        // Save the user registration details if everything is valid
        userService.createUserRegistration(userRegistrationDTO);
        return "redirect:/registration?success";
    }





    private boolean emailExist(String email) {
        final User user = userService.findByEmail(email);
        return user != null;
    }


}