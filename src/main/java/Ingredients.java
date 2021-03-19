package main.java;

import main.java.exceptions.InvalidQuantityException;

public class Ingredients {

    private Long quantity;
    private final String name;

    public Ingredients(final Long quantity, final String name) {
        this.quantity = quantity;
        this.name = name;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public String getName() {
        return this.name;
    }

    public void updateQuantity(final int quantity) throws InvalidQuantityException {
        if (quantity < 0) {
            throw new InvalidQuantityException("Quantity must be positive: " + quantity);
        }
        this.quantity = new Long(quantity);
    }
}
