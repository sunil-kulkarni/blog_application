package com.example.blog.service;

import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for user-related operations such as registration, login,
 * logout placeholder handling, and fetching users by ID.
 */
@Service
public class UserService {

    // Repository used to access user data from the database.
    private final UserRepository userRepository;

    // Constructor-based dependency injection of UserRepository.
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user after checking for duplicate email.
     *
     * @param name     user's display name
     * @param email    user's unique email
     * @param password user's raw password (should be hashed in production)
     * @return saved User entity
     * @throws RuntimeException if email already exists
     */
    public User register(String name, String email, String password) {
        // Prevent duplicate accounts by email.
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create and populate new user entity.
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // TODO: Hash password before storing.

        // Persist the new user record.
        return userRepository.save(user);
    }

    /**
     * Validates user login credentials.
     *
     * @param email    user's email
     * @param password user's raw password
     * @return matching User entity if credentials are valid
     * @throws RuntimeException if credentials are invalid
     */
    public User login(String email, String password) {
        // Fetch user by email.
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Check user existence and password match.
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional.get();
        }

        // Authentication failed.
        throw new RuntimeException("Invalid credentials");
    }

    /**
     * Placeholder for logout logic.
     * In stateless APIs, logout is typically handled client-side by discarding
     * tokens.
     *
     * @param userId ID of the user logging out
     */
    public void logout(Integer userId) {
        // Handle logout, e.g., invalidate token. Stateless REST typically clears on
        // client,
        // but logic can be added here if using server-side sessions or token
        // blacklisting.
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id user ID
     * @return found User entity
     * @throws RuntimeException if user does not exist
     */
    public User findById(Integer id) {
        // Return user if found; otherwise throw an error.
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
