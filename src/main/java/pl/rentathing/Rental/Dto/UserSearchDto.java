package pl.rentathing.Rental.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSearchDto {
    private Long id;
    private String label;
    private String email;
}