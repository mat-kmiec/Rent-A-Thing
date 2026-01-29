package pl.rentathing.item.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a category entity in the system. This class is designed to categorize
 * items and manage their associations.
 *
 * Each category is uniquely identifiable by its ID and name. It includes additional
 * properties such as a description and an icon class to represent the visual depiction
 * when required.
 *
 * The entity maintains a one-to-many relationship with the Item entity, where a single
 * category can manage and group multiple items.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    private String iconClass;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<Item> items;
}
