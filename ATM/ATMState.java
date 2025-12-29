package atm;

// public enum ATMState {
//     IDLE,
//     CARD_INSERTED,
//     AUTHENTICATED
//     // TRANSACTION_IN_PROGRESS,
//     // EJECTING_CARD
// }

/*
 1) What State Pattern actually means (in simple words):-

* 🔑 Key idea

-> Instead of ATM checking the state,
-> the state object decides what is allowed.

* So we replace:

-> ATM checks state → does work


* with:

-> ATM delegates work → state decides

2) Why enum is NOT enough anymore:- 

* With enum, ATM checks state before every operation.
* This leads to lots of "if" conditions in ATM code.
*Enum only stores names: IDLE, CARD_INSERTED, AUTHENTICATED
* It does NOT define behavior for each state.
* With State Pattern, each state is a class implementing ATMState.
* Each state class defines allowed operations and transitions.
* This encapsulates state-specific behavior.

/*
 * ATMState represents the CURRENT STATE of the ATM.
 *
 * This is part of the STATE PATTERN.
 *
 * Each concrete implementation (IdleState, CardInsertedState, AuthenticatedState)
 * defines:
 *   - Which operations are ALLOWED
 *   - Which operations are ILLEGAL
 *   - How the ATM transitions to the next state
 *
 * IMPORTANT:
 * - ATM itself does NOT check state conditions
 * - ATM delegates all behavior to the current ATMState
 */
public interface ATMState {
    // Called when user inserts a card. Allowed only in IDLE state.
    void insertCard(ATM atm, Card card);
    // Called when user enters PIN. Allowed only after card insertion.
    void enterPin(ATM atm, String pin);
    // Called when user checks balance. Allowed only after successful authentication.
    void checkBalance(ATM atm);
    // Called when user requests cash withdrawal. Allowed only after authentication. Must ensure atomicity(account debit + cash dispense)
    void withdrawCash(ATM atm, int amount);
    // Called to eject the card. Allowed in multiple states. Allowed only after successful authentication.
    void ejectCard(ATM atm);
}
