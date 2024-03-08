package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class LibraryController {

    private final UserService userService;


    @Autowired
    public LibraryController(UserService userService) {
        this.userService = userService;
    }


    // This method maps the GET request to the "/login" endpoint
    @Loggable(ActionType.LOGIN)
    @GetMapping("/login")
    public String showLoginPage() {
        return "logging/login";
    }

}