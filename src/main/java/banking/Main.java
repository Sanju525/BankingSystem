package banking;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static void mainMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit\n");
    }

    protected static int luhnAlgorithm(String number) { // 15 digit number. Returns 16th digit.
        int checkSum=0;
        int sum = 0;
        char[] arr = number.toCharArray();
        // Multiply odd places by two indexing from 1 = Even
        // and check if sum is > 9
        for (int i = 0; i < arr.length; i++) {
            if (i%2 == 0) {
                int res = Integer.parseInt(String.valueOf(arr[i])) * 2;
                if (res > 9) {
                    arr[i] = String.valueOf(res-9).charAt(0);
                } else {
                    arr[i] = String.valueOf(res).charAt(0);
                }
            }
        }
        // Add all values
        for (char c : arr) {
            sum += Integer.parseInt(String.valueOf(c));
        }
        while ((sum+checkSum)%10 != 0) {
            checkSum++;
        }
        return checkSum;
    }

    private static String getBin() { // Bank Identification Number. 6-digit number.
        return "400000";
    }

    private static String getCan() { // Customer Account Number(9-digit number). Returns a 9-digit String.
        Random rand = new Random();
        return getFourDigitNumber() + getFourDigitNumber() + rand.nextInt(10);
    }

    private static String getFourDigitNumber() { // returns a Four Digit Number.
        Random rand = new Random();
        return String.valueOf(rand.nextInt(10)) + rand.nextInt(10) +
                rand.nextInt(10) + rand.nextInt(10);
    }

    private  static String getCardNumber() { // returns a 16 digit card number
        String can = getCan();
        String bin = getBin();
        return bin + can + luhnAlgorithm(bin + can);
    }

    public static void main(String[] args) {
        File file = new File("card.s3db");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataBase db = new DataBase(file.getAbsolutePath());
        Scanner sc = new Scanner(System.in);
        while (true) {
            mainMenu();
            byte op;
            op = sc.nextByte();
            switch (op) {
                case 1: // Create Account by generating a card number and pin.
                    String cardNumber = getCardNumber();
                    String cardPin  = getFourDigitNumber();
                    if (!db.cardExists(cardNumber)) {
                        db.createRecord(cardNumber, cardPin);
                    }
                    System.out.printf("CardNumber: %s \nCardPin: %s%n", cardNumber, cardPin);
                    break;
                case 2:
                    String number;
                    System.out.println("Enter card number:");
                    number = sc.next();
                    String pin;
                    System.out.println("Enter card pin:");
                    pin = sc.next();
                    int digit = Integer.parseInt(String.valueOf(number.charAt(15)));
                    if (number.length()==16 && digit == luhnAlgorithm(number.substring(0, 15))) {
                        if (db.cardExists(number) && pin.equals(db.getPin(number))) {
                            // login
                            System.out.println("You are successfully logged in!");
                            Account account = new Account();
                            int status = account.login(db, number);
                            if (status == 0) {
                                return;
                            }
                        } else {
                            System.out.println("Wrong card number or pin!");
                        }
                    } else {
                        System.out.println("Wrong card number or pin!");
                    }
                    break;
                case 0:
                    return;
                case 3:
                    db.select();
                    return;
            }
        }
    }
}