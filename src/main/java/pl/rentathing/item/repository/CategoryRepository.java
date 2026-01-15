package pl.rentathing.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rentathing.item.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
