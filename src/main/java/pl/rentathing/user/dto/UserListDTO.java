package pl.rentathing.user.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

@Data
@Builder
public class UserListDTO {
    private Long id;
    
    @NotBlank(message = "Imię nie może być puste")
    private String firstName;
    
    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;
    
    @Email(message = "Nieprawidłowy format emaila")
    @NotBlank(message = "Email nie może być pusty")
    private String email;
    
    private String role;
    
    private Boolean enabled;
    
    private LocalDateTime createdAt;
}
