package atm;

/*
* ATM is idle, waiting for card insertion
* Allowed operation (insertCard(...))
* Forbidden operations(enterPin(), checkBalance(), withdrawCash())
*/
public class IdleState implements ATMState {
     @Override
     public void insertCard(ATM atm, Card card){
        // 1. Validate card (non-null, active)
        if(card==null || !card.isActive()){
            throw new IllegalArgumentException("Invalid or inactive card");
        }

        // 2. Set card into ATM session
        atm.setCurrentCard(card);

        // 3. Move ATM to CardInsertedState
        atm.setState(new CardInsertedState());

     }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new IllegalStateException("No card inserted");
    }

    @Override
    public void checkBalance(ATM atm) {
        throw new IllegalStateException("No card inserted");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        throw new IllegalStateException("No card inserted");
    }

    @Override
    public void ejectCard(ATM atm) {
        throw new IllegalStateException("No card to eject");
    }
}
