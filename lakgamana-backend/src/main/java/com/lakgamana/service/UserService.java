package com.lakgamana.service;

import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import com.lakgamana.repository.UserRepository;
import com.lakgamana.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateUserId());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = findById(id);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void updateLastLogin(String email) {
        User user = findByEmail(email);
        user.updateLastLogin();
        userRepository.save(user);
    }

    public void incrementBookingCount(Long userId) {
        User user = findById(userId);
        user.incrementBookingCount();
        userRepository.save(user);
    }

    public void toggleUserStatus(Long id) {
        User user = findById(id);
        UserStatus newStatus = user.getStatus() == UserStatus.ACTIVE 
                ? UserStatus.SUSPENDED 
                : UserStatus.ACTIVE;
        user.setStatus(newStatus);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersWithFilters(String name, String email, UserStatus status, 
                                          UserRole role, Pageable pageable) {
        return userRepository.findUsersWithFilters(name, email, status, role, pageable);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    @Transactional(readOnly = true)
    public List<User> findTopUsersByBookings(Pageable pageable) {
        return userRepository.findTopUsersByBookings(pageable);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private String generateUserId() {
        String userId;
        do {
            userId = "U" + String.format("%03d", System.currentTimeMillis() % 1000);
        } while (userRepository.existsByUserId(userId));
        return userId;
    }
}
