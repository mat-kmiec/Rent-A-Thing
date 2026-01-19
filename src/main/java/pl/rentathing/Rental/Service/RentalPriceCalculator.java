package pl.rentathing.Rental.Service;

import org.springframework.stereotype.Component;
import pl.rentathing.item.entity.Item;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RentalPriceCalculator {

    public long calculateRentalDays(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return days <= 0 ? 1 : days;
    }

    public BigDecimal calculateDiscountedPrice(Item item) {
        BigDecimal price = item.getPricePerDay();
        if (item.getDiscountedPercent() != null && item.getDiscountedPercent() > 0) {
            BigDecimal multiplier = BigDecimal.valueOf(100 - item.getDiscountedPercent());
            return price.multiply(multiplier)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return price;
    }

    public BigDecimal calculateShippingCost(Item item, String method) {
        if (!"DELIVERY".equals(method)) {
            return BigDecimal.ZERO;
        }
        return item.getShippingPrice() != null ? item.getShippingPrice() : BigDecimal.valueOf(25);
    }

    public BigDecimal calculateDeposit(Item item) {
        return (item.isDeposit() && item.getDepositPrice() != null)
                ? item.getDepositPrice()
                : BigDecimal.ZERO;
    }
}