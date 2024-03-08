package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleService {

    // Create a new user role
    void createRoleDatabaseInit(UserRole userRole);

    // Create a new user role.
    void createRole(UserRole userRole);

    // Update an existing user role by its ID.
    void updateRole(Long id, UserRole updatedRole);

    // Delete a user role by its ID.
    void deleteRole(Long id);

    // Assign Role To All.
    void assignRoleToAll(Long roleId);

    // Assign Role To User
    void assignRoleToUser(Long roleId, Long userId);

    // Get a user role by its ID.
    Optional<UserRole> getRoleById(Long id);

    // Get a list of all user roles.
    List<UserRole> getAllRoles();

    // Get a list of all user roles without Admin
    List<UserRole> getAllRolesWithoutAdmin();

    // Find userRole by name
    UserRole findRoleByName(String name);

    boolean existsByRoleName(String roleName);

    // Find role name by ID
    String getRoleNameById(Long roleId);

    // Create a new admin
    void createAdmin(User user);
}