package shopping_cart.model;

import java.util.Objects;

public class LineItem {
    private final int id;
    private Product product;
    private int quantity;

    public LineItem(int id, Product product, int quantity){
        if(product==null){
            throw new IllegalArgumentException("Product cannot be null");
        }
        if(quantity<=0){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.id=id;
        this.product=product;
        this.quantity=quantity;
    }

    public LineItem(Product product, int quantity){
        this(0, product, quantity); // id will be set by Cart when adding the line item
    }

    public int getId(){
        return this.id;
    }

    public Product getProduct(){
        return this.product;
    }
    public int getQuantity(){
        return this.quantity;
    }

    public double getSubTotal(){
        return this.product.getPrice()*this.quantity;
    }

    public void updateQuantity(int newQuantity){
        if(newQuantity<=0){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity=newQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineItem)) return false;
        LineItem other = (LineItem) o;
        return product.getId() == other.product.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getId());
    }
}
