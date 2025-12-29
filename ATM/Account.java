package atm;

public class Account {
     private final String accountNumber;
     private double balance;
     private AccountStatus accountStatus;

     public Account(String accountNumber, double initialBalance) {
         if(accountNumber == null || accountNumber.isBlank()){
             throw new IllegalArgumentException("Account number cannot be null or empty");
         }
         if(initialBalance < 0){
             throw new IllegalArgumentException("Initial balance cannot be negative");
         }
         this.accountNumber = accountNumber;
         this.balance = initialBalance;
         this.accountStatus = AccountStatus.ACTIVE; // account is active when created
     }

     public String getAccountNumber() {
        return accountNumber;
    }

     public double getBalance() {
         return this.balance;
     }

     public void debit(double amount) {
         if (accountStatus!=AccountStatus.ACTIVE) {
             throw new IllegalStateException("Account is not active");
         }
         if(amount <= 0){
             throw new IllegalArgumentException("Debit amount must be positive");
         }
         if (balance < amount){
            throw new IllegalArgumentException("Insufficient balance");
         }
         this.balance -= amount;
     }

     
    // for futture phases(deposit/refund)
     private void credit(double amount) {
         if(amount <= 0){
             throw new IllegalArgumentException("Credit amount must be positive");
         }
         this.balance += amount;
     }
}
