package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Long> {

    // Count the number of user actions of a specific type
    @Query("SELECT COUNT(lo) FROM Logs lo WHERE lo.action = :action AND lo.userCreator = :userCreator")
    Long countUserActions(@Param("action") ActionType action, @Param("userCreator") User userCreator);

    // Count the number of user actions of a specific type within a given month
    @Query("SELECT COUNT(lo) FROM Logs lo WHERE lo.action = :action AND lo.createdAt >= :startOfMonth AND lo.createdAt <= :endOfMonth")
    Long countUserActions(@Param("action") ActionType action, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // Count the number of actions for each action type by a specific user
    @Query(value = "SELECT lo.action, COUNT(lo) FROM Logs lo WHERE lo.userCreator = :userCreator GROUP BY lo.action")
    List<Object[]> countByActionTypeForUser(@Param("userCreator") User userCreator);

    // Find all logs for a user by their role and full name
    @Query("SELECT lo FROM Logs lo WHERE lo.role = :role AND lo.fullName = :fullName")
    List<Logs> findAllByRoleAndFullName(@Param("role") String role, @Param("fullName") String fullName);

    // Delete ANONYMOUS
    @Transactional
    @Modifying
    @Query("DELETE FROM Logs l WHERE l.fullName = 'ANONYMOUS'")
    void deleteAllAnonymousLogs();

    // Find all logs for a specific user
    List<Logs> findByUserCreator(User userCreator);

    // Find all logs for a specific action type
    List<Logs> findByAction(ActionType action);

    // Find all logs for a specific action type with sorting
    List<Logs> findByAction(ActionType actionType, Sort sort);

    // Find all logs for a specific full name
    List<Logs> findAllByFullName(String fullName);

}
