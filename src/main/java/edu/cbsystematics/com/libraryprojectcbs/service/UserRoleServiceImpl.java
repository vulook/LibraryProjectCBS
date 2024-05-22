package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRepository;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ROLE_ADMIN;


@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void createRoleDatabaseInit(UserRole userRole) {
        // Check for Role name
        if (checkRoleNameExists(userRole.getRoleName())) {
            return;
        }
        userRoleRepository.save(userRole);
    }

    @Loggable(value = ActionType.CREATE)
    @Override
    public void createRole(UserRole userRole) {
        // Check for Role name
        if (checkRoleNameExists(userRole.getRoleName())) {
            throw new UserRoleAlreadyExistsException("UserRole with name " + userRole.getRoleName() + " already exists");
        }

        userRoleRepository.save(userRole);
    }


    private boolean checkRoleNameExists(String roleName) {
        return userRoleRepository.existsByRoleName(roleName);
    }


    @Loggable(value = ActionType.DELETE)
    @Override
    public void deleteRole(Long id) {
        // Check if the role is ROLE_ADMIN
        UserRole role = userRoleRepository.findById(id).orElse(null);
        if (role != null && role.getRoleName().equals(ROLE_ADMIN)) {
            throw new AdminDeletionException("Cannot delete administrator role");
        }

        // Get all users with the role
        List<User> usersWithRole = userRepository.findAllByUserRoleId(id);

        // Set the user role to null for each user
        for (User user : usersWithRole) {
            user.setUserRole(null);
        }

        userRepository.saveAll(usersWithRole);
        userRoleRepository.deleteById(id);
    }

    @Loggable(value = ActionType.CREATE)
    @Override
    public void assignRoleToAll(Long roleId) {
        // Get all users without the specified role
        List<User> usersWithoutRole = userRepository.findUsersWithoutRole();
        Optional<UserRole> roleOptional = getRoleById(roleId);

        // Get the role to assign
        if (roleOptional.isPresent()) {
            UserRole roleToAssign = roleOptional.get();
            for (User user : usersWithoutRole) {
                user.setUserRole(roleToAssign);
            }
            userRepository.saveAll(usersWithoutRole);
        }
    }

    @Loggable(value = ActionType.CREATE)
    @Override
    public void assignRoleToUser(Long roleId, Long userId) {
        // Get the user by ID
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

        // Get the role to assign
        UserRole roleToAssign = getRoleById(roleId)
                .orElseThrow(() -> new UserRoleNotFoundException("UserRole not found for ID: " + roleId));

        // Assign the role to the user
        user.setUserRole(roleToAssign);

        // Save the user
        userRepository.save(user);
    }

    @Override
    public Optional<UserRole> getRoleById(Long id) {
        return userRoleRepository.findById(id);
    }

    @Override
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public List<UserRole> getAllRolesWithoutAdmin() {
        List<UserRole> allRoles = userRoleRepository.findAll();

        // Remove the ROLE_ADMIN role from the list
        allRoles.removeIf(role -> role.getRoleName().equals(ROLE_ADMIN));

        return allRoles;
    }

    @Override
    public UserRole findRoleByName(String roleName) {
        Optional<UserRole> optionalUserRole = Optional.ofNullable(userRoleRepository.findByRoleName(roleName));
        return optionalUserRole.orElse(null);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return userRoleRepository.existsByRoleName(roleName);
    }

    @Override
    public String getRoleNameById(Long roleId) {
        // Validate the role ID
        if (roleId == null) {
            return "ROLE_NONE";
        }

        // Get the role name by ID
        UserRole userRole = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new UserRoleNotFoundException("UserRole not found for ID: " + roleId));

        // Return the role name
        return userRole.getRoleName();
    }



}