package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.models.Endpoint;
import edu.cbsystematics.com.libraryprojectcbs.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ADMIN_HOME_URL;


@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(ADMIN_HOME_URL)
public class EndpointController {

    private final EndpointService endpointService;

    @Autowired
    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @GetMapping("/endpoints")
    public String showEndpoints(Model model) {
        List<Endpoint> endpoints = endpointService.getAllEndpoints();
        model.addAttribute("endpoints", endpoints);
        return "logs/endpoints";
    }

}