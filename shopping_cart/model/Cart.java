package shopping_cart.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final int id;
    private final List<LineItem> lineItems;
    private CartStatus status;

    public Cart(int id) {
        this.id = id;
        this.lineItems = new ArrayList<>();
        this.status = CartStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public double getTotalAmount() {
        double total = 0;
        for (LineItem item : lineItems) {
            total += item.getSubTotal();
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (LineItem item : lineItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public void addProduct(Product product, int quantity) {
        ensureActive();

        if (product == null)
            throw new IllegalArgumentException("Product cannot be null");

        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        for (LineItem item : lineItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.updateQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        lineItems.add(new LineItem(product, quantity));
    }

    public void removeProduct(int productId) {
        ensureActive();

        boolean removed = lineItems.removeIf(
            item -> item.getProduct().getId() == productId
        );

        if (!removed) {
            throw new IllegalArgumentException("Product not found in cart");
        }
    }

    public void updateQuantity(int productId, int newQuantity) {
        ensureActive();

        if (newQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        for (LineItem item : lineItems) {
            if (item.getProduct().getId() == productId) {
                item.updateQuantity(newQuantity);
                return;
            }
        }

        throw new IllegalArgumentException("Product not found in cart");
    }

    public void checkout() {
        ensureActive();

        if (lineItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        status = CartStatus.CHECKED_OUT;
    }

    private void ensureActive() {
        if (status != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cart is not active");
        }
    }
}