package pl.rentathing.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rentathing.user.entity.Role;
import pl.rentathing.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findAll(Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:search IS NULL OR u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> searchUsers(@Param("search") String search, @Param("role") Role role, Pageable pageable);
}
