package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogsServiceImpl implements LogsService {

    private final LogsRepository logsRepository;

    @Autowired
    public LogsServiceImpl(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @Override
    public void saveLog(String fullName, String role, ActionType action, String method, String parameters, Long executionTime, User userCreator) {
        Logs log = new Logs(fullName, role, action, method, parameters, executionTime, userCreator);
        logsRepository.save(log);
    }

    @Override
    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    @Override
    public List<Logs> getLogsByUserCreator(User userCreator) {
        return logsRepository.findByUserCreator(userCreator);
    }

}