package pl.rentathing.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rentathing.item.entity.Category;

import java.util.List;

/**
 * Repository interface for managing {@link Category} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations for the Category entity.
 * Includes additional query methods for customized data retrieval.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);
}
