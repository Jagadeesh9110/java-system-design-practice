package shopping_cart.model;

public class Product {
    private final int id;
    private String productName;
    private String description;
    private double price;
    
    public Product(int id, String productName, String description, double price){
        if(productName==null || productName.isBlank()){
            throw new IllegalArgumentException("Please Enter the product name");
        }
        if(description==null || description.isBlank()){
            throw new IllegalArgumentException("Please Enter the product description");
        }
        if(price<0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.id=id;
        this.productName=productName;
        this.description=description;
        this.price=price;
    }

    public int getId(){
        return this.id;
    }

    public String getProductName(){
        return this.productName;
    }

    public String getDescription(){
        return this.description;
    }

    public double getPrice(){
        return this.price;
    }

    public void updatePrice(double newPrice){
        if(newPrice<0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price=newPrice;
    }

}
