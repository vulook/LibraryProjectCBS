package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Sort;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public interface LogsService {

    // Save a log entry
    void saveLog(String fullName, String role, ActionType action, String method, String parameters, Long executionTime, User userCreator);

    // Get all logs
    List<Logs> getAllLogs();

    // Get logs for a specific user creator
    List<Logs> getLogsByUserCreator(User userCreator);

    // Get logs for a specific action type
    List<Logs> getLogsByActionType(ActionType actionType);

    // Count user actions for a specific action and user creator
    Long countUserActions(ActionType action, User userCreator);

    // Count the number of actions for each action type for a specific user creator
    Map<String, Long> countByActionTypeForUser(User userCreator);

    // Get action counts for all users
    EnumMap<ActionType, List<Long>> getActionCounts();

    // Get users by role and full name
    List<Logs> getUsersByRoleAndFullName(String roleName, String fullName);

    // Get logs for a specific action type and sort them
    List<Logs> getLogSortByActionType(ActionType actionType, Sort sort);

    // Find all logs for a specific full name
    List<Logs> findAllLogsByFullName(String fullName);

    // Delete Anonymous
    void deleteAllAnonymousLogs();
}