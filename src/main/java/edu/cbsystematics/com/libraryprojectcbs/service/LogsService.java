package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Sort;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public interface LogsService {

    void saveLog(String fullName, String role, ActionType action, String method, String parameters, Long executionTime, User userCreator);

    List<Logs> getAllLogs();

    List<Logs> getLogsByUserCreator(User userCreator);

    List<Logs> getLogsByActionType(ActionType actionType);

    Long countUserActions(ActionType action, User userCreator);

    Map<String, Long> countByActionTypeForUser(User userCreator);

    EnumMap<ActionType, List<Long>> getActionCounts();

    List<Logs> getUsersByRoleAndFullName(String roleName, String fullName);

    List<Logs> getLogSortByActionType(ActionType actionType, Sort sort);
}