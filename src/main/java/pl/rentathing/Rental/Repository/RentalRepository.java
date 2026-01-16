package pl.rentathing.Rental.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rentathing.Rental.Entity.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
}
