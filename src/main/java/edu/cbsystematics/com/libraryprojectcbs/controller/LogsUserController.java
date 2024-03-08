package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class LogsUserController {

    private final LogsService logsService;

    private final UserService userService;

    private final UserRoleService userRoleService;

    @Autowired
    public LogsUserController(LogsService logsService, UserService userService, UserRoleService userRoleService) {
        this.logsService = logsService;
        this.userService = userService;
        this.userRoleService = userRoleService;
    }


    @GetMapping("/logs")
    public String logsPage(Model model) {
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        return "logs/main-logs";
    }

    @GetMapping("/logs-{tab}")
    public String tab(@PathVariable String tab) {
        if (Arrays.asList("tab1", "tab2", "tab3", "tab4")
                .contains(tab)) {
            return "logs/_logs-" + tab;
        }

        return "logs/empty";
    }

    @GetMapping("/logs-tab1")
    public String showActionCounts(Model model) {
        EnumMap<ActionType, List<Long>> actionCounts = logsService.getActionCounts();
        List<String> monthNames = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            monthNames.add(PeriodUtils.getStartMonthNameOfMonthsAgo(i));
        }

    /*
        // Logging the result
        System.out.println("Action count differences:");
        actionCounts.forEach((actionType, counts) -> {
            System.out.println("ActionType: " + actionType);
            System.out.println("Counts: " + counts);
        });
    */

        model.addAttribute("monthNames", monthNames);
        model.addAttribute("actionCounts", actionCounts);
        return "logs/_logs-tab1";
    }


    @PostMapping("/logs-tab2")
    public String filterLogsActionType(Model model, @RequestParam ActionType actionType) {
        model.addAttribute("actionTypes", Arrays.asList(ActionType.values()));
        model.addAttribute("logs", logsService.getLogsByActionType(actionType));
        return "logs/_logs-tab2";
    }

    @GetMapping("/logs-tab3")
    public String showUsersByRoleAdmin(Model model) {
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        return "logs/_logs-tab3";
    }

    @PostMapping("/logs-tab3")
    public String showLogsForUserByRoleAdmin(@RequestParam("selectedId") Long selectedId, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        return "logs/_logs-tab3";
    }

    @GetMapping("/logs-tab4")
    public String showUsersByRoleLibrarianAndFullName(@PathVariable String fullName, Model model) {
        List<Logs> librarians = logsService.getUsersByRoleAndFullName(ROLE_LIBRARIAN, fullName);

        model.addAttribute("librarians", librarians);
        return "logs/_logs-tab4";
    }


    @GetMapping("/logs/admin/log")
    public String showFormAdminLogs(Model model) {
        UserRole adminRole = userRoleService.findRoleByName(ROLE_ADMIN);
        List<User> admins = userService.getListUsersByRoleId(adminRole.getId());

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("admins", admins);
        model.addAttribute("selectedId", null); // default value
        return "logs/admin-logs";
    }

    @PostMapping("/logs/admin/log")
    public String getLogsForAdmin(@RequestParam("selectedId") Long selectedId, RedirectAttributes redirectAttributes, Model model) {
        if (selectedId == null) {
            redirectAttributes.addAttribute("errorMessage", "Please select an admin");
            return "redirect:" + ADMIN_HOME_URL + "logs?error";
        }

        Optional<User> adminOptional = userService.getUserById(selectedId);
        if (adminOptional.isPresent()) {
            List<Logs> adminLogs = logsService.getLogsByUserCreator(adminOptional.get());
            adminLogs.sort(Comparator.comparing(Logs::getUserNumber).reversed());
            model.addAttribute("adminLogs", adminLogs);
        } else {
            redirectAttributes.addAttribute("errorMessage", "Error selected AdminId");
            return "redirect:" + ADMIN_HOME_URL + "logs?error";
        }
        return "logs/admin-logs";
    }

    @GetMapping("/logs/librarian/log")
    public String showFormLibrarianLogs(Model model) {
        UserRole librarianRole = userRoleService.findRoleByName(ROLE_LIBRARIAN);
        List<User> librarians = userService.getListUsersByRoleId(librarianRole.getId());

        model.addAttribute("ADMIN_HOME_URL", ADMIN_HOME_URL);
        model.addAttribute("librarians", librarians);
        model.addAttribute("selectedId", null); // default value
        return "logs/librarian-logs";
    }

    @PostMapping("/logs/librarian/log")
    public String getLogsForLibrarian(@RequestParam("selectedId") Long selectedId, RedirectAttributes redirectAttributes, Model model) {
        if (selectedId == null) {
            redirectAttributes.addAttribute("errorMessage", "Please select an librarian");
            return "redirect:" + ADMIN_HOME_URL + "logs?error";
        }

        Optional<User> librarianOptional = userService.getUserById(selectedId);
        if (librarianOptional.isPresent()) {
            List<Logs> librarianLogs = logsService.getLogsByUserCreator(librarianOptional.get());
            librarianLogs.sort(Comparator.comparing(Logs::getUserNumber).reversed());
            model.addAttribute("librarianLogs", librarianLogs);
        } else {
            redirectAttributes.addAttribute("errorMessage", "Error selected LibrarianId");
            return "redirect:" + ADMIN_HOME_URL + "logs?error";
        }
        return "logs/librarian-logs";
    }

    @GetMapping("/logs/action")
    public String showLogByActionTypePage(Model model) {
        model.addAttribute("actionTypes", ActionType.values());
        return "logs/logs-action";
    }

    @PostMapping("/logs/action")
    public String filterLogByActionType(@RequestParam(required = false) ActionType actionType,
                                         @RequestParam String sortField,
                                         @RequestParam String sortOrder,
                                         Model model) {
        Sort sort = Sort.by(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        List<Logs> logs = logsService.getLogSortByActionType(actionType, sort);
        model.addAttribute("logs", logs);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "logs/logs-action";
    }


}