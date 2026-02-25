package shopping_cart.model;

public class OrderLineItem {
     private final int productId;
     private final String productName;
     private final double priceAtPurchase;
     private final int quantity;

     public OrderLineItem(int productId, String productName, double priceAtPurchase, int quantity){
         if(productName==null || productName.isBlank()){
             throw new IllegalArgumentException("Please Enter the product name");
         }
         if(priceAtPurchase<0){
             throw new IllegalArgumentException("Price cannot be negative");
         }
         if(quantity<=0){
             throw new IllegalArgumentException("Quantity must be greater than zero");
         }
         this.productId=productId;
         this.productName=productName;
         this.priceAtPurchase=priceAtPurchase;
         this.quantity=quantity;
     }

     public double getSubTotal() {
        return priceAtPurchase * quantity;
    }
}
