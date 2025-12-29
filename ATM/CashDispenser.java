package atm;

import java.util.Map;
import java.util.HashMap;

public class CashDispenser {
    Map<Integer,Integer> cashInventory; // denomination to count mapping
    private final int[] denominations = {2000,500,200,100, 50, 20, 10}; // supported denominations

    public CashDispenser() {
        cashInventory = new HashMap<>();
        for(int denom : denominations){
            cashInventory.put(denom, 0); // initialize all denominations with zero count
        }
    }

    // Load cash into the dispenser
    public void loadCash(int denomination,int count){
        if(!cashInventory.containsKey(denomination)){
            throw new IllegalArgumentException("Unsupported denomination: " + denomination);
        }
        if(count <= 0){
            throw new IllegalArgumentException("Count must be positive");
        }
        int currentCount = cashInventory.get(denomination);
        cashInventory.put(denomination, currentCount + count);
    }

    public boolean canDispenseAmount(int amount){
        int remainingAmount = amount;
        for(int denom : denominations){
            int availableNotes = cashInventory.get(denom);
            int neededNotes = (int)(remainingAmount / denom);
            int notesToUse = Math.min(availableNotes, neededNotes);
            remainingAmount -= notesToUse * denom;
        }
        return remainingAmount == 0;
    }

    // Dispense cash and update inventory
    public void dispenseCash(int amount){
        if(!canDispenseAmount(amount)){
            throw new IllegalArgumentException("Cannot dispense the requested amount with available cash");
        }
        Map<Integer,Integer> dispensedCash = new HashMap<>();
        int remaining = amount;
        for(int denom:denominations){
            int availableNotes = cashInventory.get(denom);
            int neededNotes=remaining/denom;
            int notesToDispense = Math.min(availableNotes, neededNotes);
            
            if(notesToDispense >0){
                dispensedCash.put(denom, notesToDispense);
                cashInventory.put(denom, availableNotes - notesToDispense);
                remaining -= notesToDispense * denom;
            }
        }
    }

}
