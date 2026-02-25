package shopping_cart.model;

import java.util.List;

public class Order {
     private final int id;
     private final User user;
     private final List<OrderLineItem> lineItems;
     private final double totalAmount;

     public Order(int id, User user, List<OrderLineItem> lineItems){
         if(user==null){
             throw new IllegalArgumentException("User cannot be null");
         }
         if(lineItems==null || lineItems.size()==0){
             throw new IllegalArgumentException("Order must have at least one line item");
         }
         this.id=id;
         this.user=user;
         this.lineItems=lineItems;
         this.totalAmount=calculateTotalAmount();
     }

    private double calculateTotalAmount(){
        double total=0;
        for(OrderLineItem item: lineItems){
            total+=item.getSubTotal();
        }
        return total;
    }
    
     public int getId(){
         return this.id;
     }

     public User getUser(){
         return this.user;
     }

     public double getTotalAmount(){
         return this.totalAmount;
     }

}
