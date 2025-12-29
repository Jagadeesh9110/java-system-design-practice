package atm;

/*
* Card is inside ATM, but user not authenticated
* Allowed operation (enterPin(...),ejectCard(...))
* Forbidden operations(insertCard(),checkBalance(), withdrawCash())
*/


public class CardInsertedState implements ATMState {

    @Override
    public void insertCard(ATM atm, Card card) {
        throw new IllegalStateException("Card already inserted");
    }
    @Override
    public void enterPin(ATM atm, String pin) {
        // 1. Validate PIN (non-null, non-blank)
        if(pin == null || pin.isBlank()){
            throw new IllegalArgumentException("PIN cannot be null or empty");
        }

        // 2. Validate PIN against card
        Card currentCard = atm.getCurrentCard();
        if(!currentCard.validatePin(pin)){
            throw new IllegalStateException("Invalid PIN");
        }

        // 3. Move ATM to AuthenticatedState
        atm.setState(new AuthenticatedState());
    }

    @Override
    public void checkBalance(ATM atm) {
        throw new IllegalStateException("User not authenticated");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        throw new IllegalStateException("User not authenticated");
    }
    
    @Override
    public void ejectCard(ATM atm) {
        // 1. Clear current card from ATM session
        atm.setCurrentCard(null);

        // 2. Move ATM to IdleState
        atm.setState(new IdleState());
    }
    
}
