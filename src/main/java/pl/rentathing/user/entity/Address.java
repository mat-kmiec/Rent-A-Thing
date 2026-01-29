package pl.rentathing.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * The Address class represents an address entity associated with a user.
 * This class is annotated with JPA annotations to map its fields to a database table.
 * It provides relevant fields such as street, house number, apartment number,
 * city, and zip code to store the details of an address.
 *
 * The Address entity has a one-to-one relationship with the User entity,
 * indicating that each address is associated with a single user.
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String houseNumber;

    private String apartmentNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
