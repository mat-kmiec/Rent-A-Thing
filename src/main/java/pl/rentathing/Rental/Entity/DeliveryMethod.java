package pl.rentathing.Rental.Entity;

/**
 * Represents the delivery methods available for a rental transaction.
 *
 * This enumeration is used to define various delivery options that are
 * supported in the rental system. The available delivery methods include:
 *
 * - PICKUP: The customer retrieves the rented item from a specified location.
 * - DELIVERY: The rented item is delivered to the customer's provided address.
 *
 * Each method corresponds to a predefined delivery approach chosen during the rental
 * process.
 */
public enum DeliveryMethod {
    PICKUP, DELIVERY
}
