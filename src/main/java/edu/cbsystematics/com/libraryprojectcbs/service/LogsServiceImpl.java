package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.LogsRepository;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


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

        List<Logs> userLogs = logsRepository.findByUserCreator(userCreator);

        // Calculation of serial number for the user
        log.calculateUserSequenceNumber(userLogs);

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

    @Override
    public List<Logs> getLogsByActionType(ActionType actionType) {
        return logsRepository.findByAction(actionType);
    }

    @Override
    public Long countUserActions(ActionType action, User userCreator) {
        return logsRepository.countUserActions(action, userCreator);
    }

    @Override
    public Map<String, Long> countByActionTypeForUser(User userCreator) {
        List<Object[]> results = logsRepository.countByActionTypeForUser(userCreator);
        Map<String, Long> actionCountMap = new HashMap<>();

        for (Object[] result : results) {
            ActionType actionType = (ActionType) result[0];
            String action = actionType.name();
            Long count = (Long) result[1];
            actionCountMap.put(action, count);
        }

        return actionCountMap;
    }

    @Override
    public EnumMap<ActionType, List<Long>> getActionCounts() {
        EnumMap<ActionType, List<Long>> actionCounts = new EnumMap<>(ActionType.class);

        for (ActionType actionType : ActionType.values()) {
            List<Long> counts = new ArrayList<>();
            for (int i = 0; i <= 6; i++) {
                LocalDateTime startOfMonth = PeriodUtils.getStartOfMonthsAgo(i);
                LocalDateTime endOfMonth = PeriodUtils.getEndOfMonthsAgo(i);

                Long count = logsRepository.countUserActions(actionType, startOfMonth, endOfMonth);
                counts.add(count);
            }
            actionCounts.put(actionType, counts);
        }

        return actionCounts;
    }

    @Override
    public List<Logs> getUsersByRoleAndFullName(String roleName, String fullName) {
        return logsRepository.findAllByRoleAndFullName(roleName, fullName);
    }

    @Override
    public List<Logs> getLogSortByActionType(ActionType actionType, Sort sort) {
        if (actionType != null) {
            return logsRepository.findByAction(actionType, sort);
        } else {
            return logsRepository.findAll(sort);
        }
    }

    @Override
    public List<Logs> findAllLogsByFullName(String fullName) {
        return logsRepository.findAllByFullName(fullName);
    }

    @Override
    public void deleteAllAnonymousLogs() {
        logsRepository.deleteAllAnonymousLogs();
    }


    /**********************************************
    @Override
    public List<String> getLogsFullNames(String roleName) {
        List<Logs> users = logsRepository.findAllByRole(roleName);
        return users.stream()
                .map(Logs::getFullName)
                .distinct()
                .toList();
    }
    *************************************************/

}