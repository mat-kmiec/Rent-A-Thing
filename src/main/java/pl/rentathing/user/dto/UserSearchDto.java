package pl.rentathing.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Data Transfer Object (DTO) for searching and retrieving user details.
 *
 * This class encapsulates essential information about a user, typically used
 * for user listings, search results, or other read-only operations involving user data.
 *
 * Key Fields:
 * - id: Unique identifier of the user.
 * - label: A descriptive label or name associated with the user.
 * - email: The email address of the user.
 */
@Getter
@Setter
@AllArgsConstructor
public class UserSearchDto {
    private Long id;
    private String label;
    private String email;
}