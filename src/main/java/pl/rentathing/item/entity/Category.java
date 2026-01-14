package pl.rentathing.item.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
