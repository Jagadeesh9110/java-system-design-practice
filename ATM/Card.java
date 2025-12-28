package ATM;
  
public class Card {
    private final String cardNumber;
    private final String cardHolderName;
    private final String accountNumber;
    private final String pin;
    private final String expiryDate;
    private  Status status;

    public Card(String cardNumber, String cardHolderName, String accountNumber, String pin, String expiryDate) {
        if(cardNumber ==null || cardNumber.isBlank()){
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }

        if(pin ==null || pin.isBlank()){
            throw new IllegalArgumentException("PIN cannot be null or empty");
        }

         if(cardHolderName ==null || cardHolderName.isBlank()){
            throw new IllegalArgumentException("Card holder name cannot be null or empty");
        }

         if(accountNumber ==null || accountNumber.isBlank()){
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

         if(expiryDate ==null || expiryDate.isBlank()){
            throw new IllegalArgumentException("Expiry date cannot be null or empty");
        }
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.expiryDate = expiryDate;
        this.status = Status.ACTIVE; // card is active when created
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
    public String getCardHolderName() {
        return this.cardHolderName;
    }
    public String getAccountNumber() {
        return this.accountNumber;
    }
    public String getExpiryDate(){
        return this.expiryDate;
    }
   

     public void block() {
        this.status = Status.BLOCKED;
    }
    
    public boolean isActive(){
        return this.status == Status.ACTIVE;
    }

  // here we need to validate pin of hashing the pin and comparing hash values for security reasons
    public boolean validatePin(String inputPin) {
        if (!isActive()) {
            throw new IllegalStateException("Card is not active");
        }
        return this.pin.equals(inputPin);
    }

}
