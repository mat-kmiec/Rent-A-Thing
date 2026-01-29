package pl.rentathing.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rentathing.item.entity.Item;

import java.util.List;

/**
 * Repository interface for managing {@link Item} entities.
 * Provides CRUD operations and custom queries for interacting with Item data.
 * Extends {@link JpaRepository} for basic CRUD operations and {@link JpaSpecificationExecutor}
 * for supporting complex query specifications.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    @Override
    @EntityGraph(attributePaths = {"category"})
    Page<Item> findAll(Specification<Item> spec, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Item> searchAllItemsForAdmin(@Param("query") String query, Pageable pageable);
}
