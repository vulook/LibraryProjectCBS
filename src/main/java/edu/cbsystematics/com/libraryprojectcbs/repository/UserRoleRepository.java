package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // Update UserRole Information
    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.roleName = :roleName, ur.description = :description WHERE ur.id = :id")
    void updateUserRole(
            @Param("id") Long id,
            @Param("roleName") String roleName,
            @Param("description") String description);

    // Custom method to find a role by name
    @Query("SELECT r FROM UserRole r WHERE lower(r.roleName) LIKE lower(concat('%', :name, '%'))")
    Optional<UserRole> findRoleByName(@Param("name") String name);

    // Check if a UserRole with the given roleName exists. Returns true if a match is found
    boolean existsByRoleName(String roleName);

}