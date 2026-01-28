package pl.rentathing.Rental.Service;

import org.springframework.stereotype.Component;
import pl.rentathing.item.entity.Item;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * RentalPriceCalculator is a utility class responsible for calculating various rental-related
 * values such as rental days, discounted prices, shipping costs, and deposit amounts for an item.
 * This class is annotated with @Component, making it a Spring-managed bean.
 */
@Component
public class RentalPriceCalculator {

    /**
     * Calculates the number of rental days between the specified start and end dates.
     * If the calculated duration is zero or negative, the result will default to 1 day to ensure
     * a minimum rental period.
     *
     * @param start the start date of the rental period, must not be null
     * @param end the end date of the rental period, must not be null
     * @return the number of rental days, with a minimum value of 1
     */
    public long calculateRentalDays(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return days <= 0 ? 1 : days;
    }

    /**
     * Calculates the discounted price for an item based on its price per day and discount percentage.
     * If no discount is applied or the discount percentage is null or zero, the original price per day is returned.
     *
     * @param item the item for which the discounted price is calculated, must not be null
     * @return the discounted price as a BigDecimal, rounded to two decimal places
     */
    public BigDecimal calculateDiscountedPrice(Item item) {
        BigDecimal price = item.getPricePerDay();
        if (item.getDiscountedPercent() != null && item.getDiscountedPercent() > 0) {
            BigDecimal multiplier = BigDecimal.valueOf(100 - item.getDiscountedPercent());
            return price.multiply(multiplier)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return price;
    }

    /**
     * Calculates the shipping cost for the specified item based on the provided shipping method.
     * If the method is not "DELIVERY", the shipping cost will be zero.
     * If the item has a defined shipping price, that value is used; otherwise, a default value of 25 is returned.
     *
     * @param item the item for which to calculate the shipping cost
     * @param method the shipping method, e.g., "DELIVERY"
     * @return the calculated shipping cost, or BigDecimal.ZERO if the shipping method is not "DELIVERY"
     */
    public BigDecimal calculateShippingCost(Item item, String method) {
        if (!"DELIVERY".equals(method)) {
            return BigDecimal.ZERO;
        }
        return item.getShippingPrice() != null ? item.getShippingPrice() : BigDecimal.valueOf(25);
    }

    /**
     * Calculates the deposit amount for the given item based on its deposit status and deposit price.
     *
     * @param item the item for which the deposit amount is calculated
     * @return the deposit amount if the item requires a deposit and has a valid deposit price,
     *         or BigDecimal.ZERO otherwise
     */
    public BigDecimal calculateDeposit(Item item) {
        return (item.isDeposit() && item.getDepositPrice() != null)
                ? item.getDepositPrice()
                : BigDecimal.ZERO;
    }
}