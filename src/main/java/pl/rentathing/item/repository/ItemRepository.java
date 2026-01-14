package pl.rentathing.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rentathing.item.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
