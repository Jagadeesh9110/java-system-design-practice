package atm;

import java.util.Map;
import java.util.HashMap;


/*
 * ATM is the CONTEXT in the State Pattern.
 *
 * Responsibilities:
 * - Hold current ATMState
 * - Hold session data (currentCard)
 * - Hold system data (accounts, cashDispenser)
 * - DELEGATE user actions to the current state
 *
 * ATM itself MUST NOT:
 * - Decide which operations are allowed
 * - Check state conditions
 * - Enforce workflow rules
 *
 * All flow rules belong to ATMState implementations.
 */

public class ATM {
    private ATMState state;    // Current ATM state (Idle, CardInserted, Authenticated)
    private Card currentCard; // The card currently inserted in the ATM(session scope) 
    private Map<String ,Account> accounts; // Map of accountNumber to Account (in-memory for simplicity)
    // private double atmCashAvailable; // Total cash available in the ATM  
    private  CashDispenser cashDispenser;
    

    // this constructor is for before state pattern implementation
    // public ATM(double initialCash) {
    //     if(initialCash < 0){
    //         throw new IllegalArgumentException("Initial cash cannot be negative");
    //     }
    //     this.state = ATMState.IDLE;
    //     this.accounts = new HashMap<>();
    //     this.cashDispenser = new CashDispenser();
    // }

    public ATM() {
        this.state = new IdleState(); // initial state is IdleState
        this.accounts = new HashMap<>();
        this.cashDispenser = new CashDispenser();
    }

    // add account to the ATM system
    public void addAccount(Account account){
        if(account == null){
            throw new IllegalArgumentException("Account cannot be null");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    // state management methods
    public void loadCash(int denomination,int count){
        cashDispenser.loadCash(denomination,count);
    }

    // STATE DELEGATION METHODS - delegate to current state
    public void insertCard(Card card){
        state.insertCard(this, card);
    }
    public void enterPin(String pin){
        state.enterPin(this, pin);
    }
    public void checkBalance(){
        state.checkBalance(this);
    }
    public void withdrawCash(int amount){
        state.withdrawCash(this, amount);
    }
    public void ejectCard(){
        state.ejectCard(this);
    }

    // INTERNAL HELPER METHODS- used by state implementations ,these are not check by ATM users directly

    public Account getAuthenticatedAccount(){
        if(currentCard == null){
            throw new IllegalStateException("No card inserted");
        }
        String accountNumber = currentCard.getAccountNumber();
        Account account = accounts.get(accountNumber);
        if(account == null){
            throw new IllegalStateException("Account not found");
        }
        return account;
    }

    public double doCheckBalance() {
      Account account = getAuthenticatedAccount();
      return account.getBalance();
   }

    public void doWithdrawCash(int amount){
        if(!cashDispenser.canDispenseAmount(amount)){
            throw new IllegalStateException("ATM cannot dispense the requested amount with available cash");
        }
        Account account = getAuthenticatedAccount();
        account.debit(amount); // debit the account
        cashDispenser.dispenseCash(amount); // dispense cash
    }

    // getters and setters for state and currentCard
    public void setState(ATMState state){
        this.state=state;
    }

    public Card getCurrentCard(){
        return this.currentCard;
    }

    public void setCurrentCard(Card card){
        this.currentCard=card;
    }

    /*
     - the below methods are for b state pattern implementation
     

    //insert card into ATM
    public void insertCard(Card card){
        if(this.state != ATMState.IDLE){
            throw new IllegalStateException("ATM is not ready to accept a card");
        }
        if(card == null || !card.isActive()){
            throw new IllegalArgumentException("Invalid or inactive card");
        }
        this.currentCard = card;
        this.state = ATMState.CARD_INSERTED;
    }

    // enter pin and authenticate
    public void enterPin(String pin){
        if(this.state != ATMState.CARD_INSERTED){
            throw new IllegalStateException("No card inserted");
        }
        if(pin == null || pin.isBlank()){
            throw new IllegalArgumentException("PIN cannot be null or empty");
        }
        // here we need to iumplement the pin validation through the card.ValidatePin(pin) method
        if(!currentCard.validatePin(pin)){
            throw new IllegalStateException("Invalid PIN");
        }   

        this.state = ATMState.AUTHENTICATED;
    }

    private Account getAuthenticatedAccount(){
        if(this.state!=ATMState.AUTHENTICATED){
            throw new IllegalStateException("User not authenticated");
        }
        String accountNumber = currentCard.getAccountNumber();
        Account account = accounts.get(accountNumber);
        if(account == null){
            throw new IllegalStateException("Account not found");
        }
        return account;
    }

    // check balance
    public double checkBalance(){
       return getAuthenticatedAccount().getBalance();
    }

    // withdraw cash
    public void withdrawCash(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = getAuthenticatedAccount();

       if(!cashDispenser.canDispenseAmount(amount)){
            throw new IllegalStateException("ATM cannot dispense the requested amount with available cash");
       }

        account.debit(amount); // debit the account
        cashDispenser.dispenseCash(amount); // dispense cash
    }

    // eject card
    public void ejectCard(){
        if(this.state == ATMState.IDLE){
            throw new IllegalStateException("No card to eject");
        }
        this.currentCard = null;
        this.state = ATMState.IDLE;
    }
        */


}
