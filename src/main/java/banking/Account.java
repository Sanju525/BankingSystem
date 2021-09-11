package banking;

import java.util.Scanner;

public class Account {
    protected void menu() {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit\n");
    }
    protected int login(DataBase db, String number){
        // returns status of the customer:
        // {0: exit, 1: close account, 2: logout}
        Scanner sc = new Scanner(System.in);
        while (true) {
            menu();
            byte op;
            op = sc.nextByte();
            switch (op) {
                case 1:
                    System.out.printf("\nBalance: %d", db.getBalance(number));
                    break;
                case 2: // addIncome
                    int income;
                    income = sc.nextInt();
                    if (income > 0) {
                        int newBalance = income + db.getBalance(number);
                        db.updateBalance(newBalance, number);
                        System.out.println("Income was added!\n");
                    } else {
                        System.out.println("Income cannot be negative!\n");
                    }
                    break;
                case 3: // Transfer
                    String receiver;
                    System.out.println("Transfer\n" +
                            "Enter card number:");
                    receiver = sc.next();
                    int digit = Integer.parseInt(String.valueOf(receiver.charAt(15)));
                    int checkSum = Main.luhnAlgorithm(receiver.substring(0,15));
                    System.out.println("digit: " +digit);
                    System.out.println("checksum: " +checkSum);
                    if (receiver.length()==16 && digit == checkSum) {
                        if (db.cardExists(receiver)) {
                            int amount;
                            System.out.println("Enter how much money you want to transfer:");
                            amount = sc.nextInt();
                            if (amount <= db.getBalance(number)) {
                                db.updateBalance(db.getBalance(receiver)+amount, receiver);
                                db.updateBalance(db.getBalance(number)-amount, number);
                            } else {
                                System.out.println("Not enough money!\n");
                            }
                        } else {
                            System.out.println("Such a card does not exist.\n");
                        }
                    } else {
                        System.out.println("Probably you made a mistake in the card number. \n" +
                                "Please try again!\n");
                    }
                    break;
                case 4:
                    db.deleteRecord(number);
                    System.out.println("\nThe account has been closed!\n");
                    return 1; // close account
                case 5:
                    return 2; // logout
                case 0:
                    return 0; // exit
            }
        }
    }
}
