package shopping_cart.model;

import java.util.List;
import java.util.ArrayList;

public class User {
    private final int id;
    private String name;
    private String email;
    private String password;

    // realtionship with shopping cart
    private Cart cart;
    
    private List<Order> orders;

    public User(int id, String name,String email,String password){
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("Please Enter your name");
        }
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("Please Enter your email");
        }
        if(password==null || password.isBlank()){
            throw new IllegalArgumentException("Please Enter your password");
        }
        if(password.length()<6){
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        this.id=id;
        this.name=name;
        this.email=email;
        this.password=password;

        this.cart=null; // user can have only one cart at a time
        this.orders=new ArrayList<>();
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    
    public void updatePassword(String newPassword){
        if(newPassword==null){
            throw new IllegalArgumentException("Please Enter your password");
        }
        if(newPassword.length()<6){
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        this.password=newPassword;
    }

    public void assignCart(Cart cart) {
       this.cart = cart;
    }

    public Cart getCart() {
        return this.cart;
    }

}
