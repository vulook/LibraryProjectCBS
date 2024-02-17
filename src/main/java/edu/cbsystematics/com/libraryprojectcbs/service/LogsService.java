package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;

import java.util.List;

public interface LogsService {

    void saveLog(String fullName, String role, ActionType action, String method, String parameters, Long executionTime, User userCreator);

    List<Logs> getAllLogs();

    List<Logs> getLogsByUserCreator(User userCreator);

}