package atm;

/*
 * User authenticated successfully
 * Allowed operations: checkBalance(), withdrawCash(), ejectCard()
 * Forbidden operations: insertCard(), enterPin()
 */

public class AuthenticatedState implements ATMState {

    @Override
    public void insertCard(ATM atm, Card card) {
        throw new IllegalStateException("User already authenticated");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new IllegalStateException("Already authenticated");
    }

    @Override
    public void checkBalance(ATM atm) {
        double balance = atm.doCheckBalance();
        System.out.println("Current balance: ₹" + balance);
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        atm.doWithdrawCash(amount);
        System.out.println("Please collect your cash: ₹" + amount);
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card ejected. Thank you.");
    }
}
