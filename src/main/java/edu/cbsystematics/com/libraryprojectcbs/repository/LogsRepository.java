package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.Logs;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Long> {

    @Query("SELECT COUNT(lo) FROM Logs lo WHERE lo.action = :action AND lo.userCreator = :userCreator")
    Long countUserActions(@Param("action") ActionType action, @Param("userCreator") User userCreator);

    @Query("SELECT COUNT(lo) FROM Logs lo WHERE lo.action = :action AND lo.createdAt >= :startOfMonth AND lo.createdAt <= :endOfMonth")
    Long countUserActions(@Param("action") ActionType action, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query(value = "SELECT lo.action, COUNT(lo) FROM Logs lo WHERE lo.userCreator = :userCreator GROUP BY lo.action")
    List<Object[]> countByActionTypeForUser(@Param("userCreator") User userCreator);

    @Query("SELECT lo FROM Logs lo WHERE lo.role = :role AND lo.fullName = :fullName")
    List<Logs> findAllByRoleAndFullName(@Param("role") String role, @Param("fullName") String fullName);


    List<Logs> findByUserCreator(User userCreator);


    List<Logs> findByAction(ActionType action);

    List<Logs> findByAction(ActionType actionType, Sort sort);

    List<Logs> findAllByFullName(String fullName);



}
