package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

    // Update User Information
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName, u.birthDate = :birthDate, u.phone = :phone, u.email = :email, u.password = :password, u.regDate = :regDate, u.userRole = :userRole WHERE u.id = :id")
    void updateUser(
            Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("birthDate") LocalDate birthDate,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("password") String password,
            @Param("regDate") LocalDate regDate,
            @Param("userRole") UserRole userRole
    );

    // Partial Update User Information
    @Modifying
    @Transactional
    @Query("UPDATE User up SET up.firstName = :firstName, up.lastName = :lastName, up.birthDate = :birthDate, up.phone = :phone, up.email = :email, up.password = :password WHERE up.id = :id")
    void partialUpdateUser(
            Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("birthDate") LocalDate birthDate,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("password") String password
    );

    // Search by FirstName Or/And LastName
    @Query("SELECT w FROM User w WHERE lower(w.firstName) LIKE lower(concat('%', :query, '%')) OR lower(w.lastName) LIKE lower(concat('%', :query, '%'))")
    List<User> searchUserByNameOrLastName(@Param("query") String query);

    // Search Users without Role
    @Query("SELECT u FROM User u WHERE u.userRole IS NULL")
    List<User> findUsersWithoutRole();

    // Custom method to count Users by Role ID
    int countAllByUserRoleId(Long roleId);

    // Custom method to get users by Role ID
    List<User> findAllByUserRoleId(Long userRoleId);

    // Check if a firstName with the given firstName exists. Returns true if a match is found
    boolean existsByFirstName(String firstName);

    // Check if a lastName with the given lastName exists. Returns true if a match is found
    boolean existsByLastName(String lastName);

    // Check if a birthDate with the given birthDate exists. Returns true if a match is found
    boolean existsByBirthDate(LocalDate birthDate);

    // Get Email
    User findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole = :role AND u.regDate >= :afterDate")
    Long countUsersByRoleAddedAfterDate(@Param("role") UserRole role, @Param("afterDate") LocalDate afterDate);

}
