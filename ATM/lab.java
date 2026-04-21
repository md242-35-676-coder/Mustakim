import java.util.Scanner;

interface Transaction {
    void deposit(double amount);
    void withdraw(double amount);
    void checkBalance();
}

abstract class Account implements Transaction {
    private String accountNumber;
    private String holderName;
    private int pin;
    protected double balance;
    protected String[] history = new String[100];
    protected int historyCount = 0;

    public Account(String accountNumber, String holderName, int pin, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = balance;
        addHistory("Account created with balance: " + balance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void addHistory(String message) {
        if (historyCount < history.length) {
            history[historyCount] = message;
            historyCount++;
        }
    }

    public void showMiniStatement() {
        System.out.println("\n----- MINI STATEMENT -----");
        if (historyCount == 0) {
            System.out.println("No transaction history found.");
        } else {
            for (int i = 0; i < historyCount; i++) {
                System.out.println((i + 1) + ". " + history[i]);
            }
        }
    }

    public void showAccountDetails() {
        System.out.println("\n----- ACCOUNT DETAILS -----");
        System.out.println("Account Holder: " + holderName);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Balance: " + balance);
    }

    public boolean transfer(Account receiver, double amount) {
        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return false;
        }

        if (amount <= 0) {
            System.out.println("Invalid transfer amount.");
            return false;
        }

        if (amount > balance) {
            System.out.println("Insufficient balance for transfer.");
            return false;
        }

        balance -= amount;
        receiver.balance += amount;

        addHistory("Transferred: " + amount + " to " + receiver.getAccountNumber());
        receiver.addHistory("Received: " + amount + " from " + this.getAccountNumber());

        System.out.println("Transfer successful.");
        return true;
    }

    public abstract void accountType();

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addHistory("Deposited: " + amount);
            System.out.println("Deposit successful. New balance: " + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    @Override
    public void checkBalance() {
        System.out.println("Current balance: " + balance);
    }
}

class SavingsAccount extends Account {
    private double interestRate = 0.05;

    public SavingsAccount(String accountNumber, String holderName, int pin, double balance) {
        super(accountNumber, holderName, pin, balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addHistory("Withdrawn from Savings: " + amount);
            System.out.println("Withdraw successful from Savings Account. New balance: " + balance);
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    public void addInterest() {
        double interest = balance * interestRate;
        balance += interest;
        addHistory("Interest added: " + interest);
        System.out.println("Interest added successfully: " + interest);
        System.out.println("New balance: " + balance);
    }

    @Override
    public void accountType() {
        System.out.println("Account Type: Savings Account");
    }
}

class CurrentAccount extends Account {
    private double overdraftLimit = 1000;

    public CurrentAccount(String accountNumber, String holderName, int pin, double balance) {
        super(accountNumber, holderName, pin, balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= (balance + overdraftLimit)) {
            balance -= amount;
            addHistory("Withdrawn from Current: " + amount);
            System.out.println("Withdraw successful from Current Account. New balance: " + balance);
        } else {
            System.out.println("Withdrawal exceeds overdraft limit or invalid amount.");
        }
    }

    public void showOverdraftLimit() {
        System.out.println("Overdraft Limit: " + overdraftLimit);
    }

    @Override
    public void accountType() {
        System.out.println("Account Type: Current Account");
    }
}

class ATM {
    private Account account;
    private Account[] allAccounts;

    public ATM(Account account, Account[] allAccounts) {
        this.account = account;
        this.allAccounts = allAccounts;
    }

    public boolean login(String accNo, int pin) {
        return account.getAccountNumber().equals(accNo) && account.getPin() == pin;
    }

    public Account findAccount(String accNo) {
        for (int i = 0; i < allAccounts.length; i++) {
            if (allAccounts[i] != null && allAccounts[i].getAccountNumber().equals(accNo)) {
                return allAccounts[i];
            }
        }
        return null;
    }

    public void changePin(Scanner sc) {
        System.out.print("Enter old PIN: ");
        int oldPin = sc.nextInt();

        if (oldPin == account.getPin()) {
            System.out.print("Enter new PIN: ");
            int newPin = sc.nextInt();
            account.setPin(newPin);
            account.addHistory("PIN changed successfully");
            System.out.println("PIN changed successfully.");
        } else {
            System.out.println("Incorrect old PIN.");
        }
    }

    public void transferMoney(Scanner sc) {
        System.out.print("Enter receiver account number: ");
        String receiverAccNo = sc.next();

        Account receiver = findAccount(receiverAccNo);

        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }

        if (receiver.getAccountNumber().equals(account.getAccountNumber())) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }

        System.out.print("Enter transfer amount: ");
        double amount = sc.nextDouble();

        account.transfer(receiver, amount);
    }

    public void showSpecialFeature() {
        if (account instanceof SavingsAccount) {
            SavingsAccount s = (SavingsAccount) account;
            s.addInterest();
        } else if (account instanceof CurrentAccount) {
            CurrentAccount c = (CurrentAccount) account;
            c.showOverdraftLimit();
        }
    }

    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n----- ATM MENU -----");
            System.out.println("1. Account Type");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Account Details");
            System.out.println("6. Change PIN");
            System.out.println("7. Mini Statement");
            System.out.println("8. Transfer Money");
            System.out.println("9. Special Feature");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    account.accountType();
                    break;

                case 2:
                    account.checkBalance();
                    break;

                case 3:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = sc.nextDouble();
                    account.deposit(depositAmount);
                    break;

                case 4:
                    System.out.print("Enter withdraw amount: ");
                    double withdrawAmount = sc.nextDouble();
                    account.withdraw(withdrawAmount);
                    break;

                case 5:
                    account.showAccountDetails();
                    break;

                case 6:
                    changePin(sc);
                    break;

                case 7:
                    account.showMiniStatement();
                    break;

                case 8:
                    transferMoney(sc);
                    break;

                case 9:
                    showSpecialFeature();
                    break;

                case 10:
                    System.out.println("Logged out successfully.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 10);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Account acc1 = new SavingsAccount("ATM101", "Mustakim", 1234, 5000);
        Account acc2 = new CurrentAccount("ATM102", "VECNA", 5678, 3000);
        Account acc3 = new SavingsAccount("ATM103", "Rahim", 4321, 7000);
        Account acc4 = new CurrentAccount("ATM104", "Karim", 8765, 2000);

        Account[] accounts = {acc1, acc2, acc3, acc4};

        System.out.println("Select Account:");
        System.out.println("1. Savings Account 1");
        System.out.println("2. Current Account 1");
        System.out.println("3. Savings Account 2");
        System.out.println("4. Current Account 2");
        System.out.print("Enter choice: ");
        int accountChoice = sc.nextInt();

        Account selectedAccount;

        if (accountChoice == 1) {
            selectedAccount = acc1;
        } else if (accountChoice == 2) {
            selectedAccount = acc2;
        } else if (accountChoice == 3) {
            selectedAccount = acc3;
        } else {
            selectedAccount = acc4;
        }

        ATM atm = new ATM(selectedAccount, accounts);

        boolean isLoggedIn = false;
        int attempts = 0;
        int maxAttempts = 3;

        while (!isLoggedIn && attempts < maxAttempts) {
            System.out.print("Enter Account Number: ");
            String accNo = sc.next();

            System.out.print("Enter PIN: ");
            int pin = sc.nextInt();

            if (atm.login(accNo, pin)) {
                System.out.println("Login successful. Welcome " + selectedAccount.getHolderName());
                selectedAccount.addHistory("Login successful");
                isLoggedIn = true;
                atm.showMenu();
            } else {
                attempts++;
                System.out.println("Invalid Account Number or PIN.");

                if (attempts < maxAttempts) {
                    System.out.println("Try again. Remaining attempts: " + (maxAttempts - attempts));
                } else {
                    System.out.println("Maximum login attempts reached. Account access denied.");
                }
            }
        }

        System.out.println("Thank you for using ATM Simulation System.");
    }
}