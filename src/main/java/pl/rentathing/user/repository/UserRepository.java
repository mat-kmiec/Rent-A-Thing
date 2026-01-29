package pl.rentathing.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rentathing.user.entity.Role;
import pl.rentathing.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository for standard CRUD operations and adds custom queries
 * for managing and retrieving User entities based on various attributes.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
       Optional<User> findByEmail(String email);

       Page<User> findAll(Pageable pageable);

       Page<User> findByRole(Role role, Pageable pageable);

       Page<User> findByLocked(boolean locked, Pageable pageable);

       Page<User> findByEnabled(boolean enabled, Pageable pageable);

       @Query("SELECT u FROM User u WHERE " +
                     "(:role IS NULL OR u.role = :role) AND " +
                     "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
       Page<User> searchUsers(@Param("search") String search, @Param("role") Role role, Pageable pageable);

       @Query("SELECT u FROM User u WHERE " +
                     "(:role IS NULL OR u.role = :role) AND " +
                     "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND "
                     +
                     "(:statusLocked IS NULL OR u.locked = :statusLocked) AND " +
                     "(:statusDisabled IS NULL OR u.enabled = :statusDisabled)")
       Page<User> searchUsersWithStatus(@Param("search") String search, @Param("role") Role role,
                     @Param("statusLocked") Boolean statusLocked,
                     @Param("statusDisabled") Boolean statusDisabled,
                     Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsersForAdmin(@Param("query") String query, Pageable pageable);
}
