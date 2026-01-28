package pl.rentathing.Rental.Entity;

/**
 * Represents the payment methods available for a rental transaction.
 *
 * This enumeration is used to define the various methods of payment that are
 * supported in the rental system. The supported payment methods include:
 *
 * - CARD: Payment via a credit or debit card.
 * - BLIK: Payment using the BLIK mobile payment system.
 * - CASH: Payment made in cash.
 *
 * Each method corresponds to a predefined payment option used during the rental
 * process.
 */
public enum PaymentMethod {
    CARD, BLIK, CASH
}
