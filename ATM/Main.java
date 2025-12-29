package atm;

public class Main {

    public static void main(String[] args) {

        // 1️⃣ Create ATM (starts in IdleState)
        ATM atm = new ATM();

        // 2️⃣ Load cash into ATM
        atm.loadCash(2000, 5);  // ₹10,000
        atm.loadCash(500, 10);  // ₹5,000
        atm.loadCash(100, 20);  // ₹2,000

        // 3️⃣ Create Account
        Account account = new Account("ACC123", 8000);

        // 4️⃣ Add account to ATM system
        atm.addAccount(account);

        // 5️⃣ Create Card linked to account
        Card card = new Card(
                "CARD123",
                "Jagadeeswar",
                "ACC123",
                "1234",
                "12/29"
        );

        System.out.println("=== ATM SESSION START ===");

        // 6️⃣ Insert Card
        atm.insertCard(card);

        // 7️⃣ Enter PIN
        atm.enterPin("1234");

        // 8️⃣ Check Balance
        atm.checkBalance();   // Expected: ₹8000

        // 9️⃣ Withdraw Cash
        atm.withdrawCash(2600); // Should succeed (2000 + 500 + 100)

        // 🔟 Check Balance again
        atm.checkBalance();   // Expected: ₹5400

        // 1️⃣1️⃣ Eject Card
        atm.ejectCard();

        System.out.println("=== ATM SESSION END ===");
    }
}
