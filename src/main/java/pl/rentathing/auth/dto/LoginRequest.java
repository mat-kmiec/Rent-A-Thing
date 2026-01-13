package pl.rentathing.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a login request containing the necessary credentials for user authentication.
 *
 * This class extends {@code AuthRequest}, inheriting the validation constraints
 * for {@code email} and {@code password} fields. It serves as a specialized
 * implementation of the authentication base class for login-specific functionality.
 *
 * The {@code LoginRequest} uses validation annotations to ensure that provided
 * credentials adhere to defined formatting and security rules, including:
 * - A valid email address pattern.
 * - A password with minimum length, at least one uppercase letter, and one digit.
 *
 * This class leverages Lombok annotations to generate getters, setters, a no-arguments
 * constructor, and support for a builder pattern.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class LoginRequest extends AuthRequest{}
