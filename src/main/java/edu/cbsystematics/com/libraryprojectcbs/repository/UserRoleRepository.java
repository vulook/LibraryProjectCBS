package edu.cbsystematics.com.libraryprojectcbs.repository;

import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // Check if a UserRole with the given roleName exists. Returns true if a match is found
    boolean existsByRoleName(String roleName);

    // Find a role by its name in the Role entity.
    UserRole findByRoleName(String roleName);

}