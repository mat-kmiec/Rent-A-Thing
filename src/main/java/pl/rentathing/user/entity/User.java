package pl.rentathing.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The User class represents a user entity in the application.
 * It is annotated with JPA annotations to map its properties to the "users" database table.
 * This class implements the UserDetails interface to integrate with Spring Security.
 *
 * Key features include:
 * - Unique email identifier for each user.
 * - Attributes for user details such as first name, last name, email, password, and phone number.
 * - Enum-based role management for user access control.
 * - Boolean fields for account activation, lock status, and various notification preferences.
 * - Implements methods required by the UserDetails interface for security configuration.
 * - One-to-one relationship with the Address entity, representing the user's address.
 *
 * The builder pattern is supported for flexible instantiation.
 * Additionally, this class overrides equals and hashCode methods for uniqueness based on email.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean locked = false;

    @Column(length = 20)
    private String phoneNumber;

    @Builder.Default
    private boolean notifEmail = true;

    @Builder.Default
    private boolean notifSms = false;

    @Builder.Default
    private boolean newsletter = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user))
            return false;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
