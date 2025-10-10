package com.lakgamana.repository;

import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByUserId(String userId);

    List<User> findByRole(UserRole role);

    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> findUsersWithFilters(
            @Param("name") String name,
            @Param("email") String email,
            @Param("status") UserStatus status,
            @Param("role") UserRole role,
            Pageable pageable
    );

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER' AND u.status = 'ACTIVE'")
    long countActiveUsers();

    @Query("SELECT u FROM User u WHERE u.role = 'USER' ORDER BY u.totalBookings DESC")
    List<User> findTopUsersByBookings(Pageable pageable);
}
