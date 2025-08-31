package com.pichincha.dm.bank.accounts.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class AccountNumberGenerator {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private AccountNumberGenerator() {}

    public static String generateAccountNumber() {
        String datePrefix = generateDatePrefix();
        String randomNumber = generateRandomSixDigits();
        String baseNumber = datePrefix + randomNumber;
        int checkDigit = calculateLuhnCheckDigit(baseNumber);

        return baseNumber + checkDigit;
    }

    public static Long generateAccountNumberAsLong() {
        return Long.valueOf(generateAccountNumber());
    }

    private static String generateDatePrefix() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(DATE_FORMATTER);
    }

    private static String generateRandomSixDigits() {
        int randomNum = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(randomNum);
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean doubleDigit = true;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit / 10 + digit % 10;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return (10 - (sum % 10)) % 10;
    }

    public static boolean validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 11) {
            return false;
        }

        String baseNumber = accountNumber.substring(0, 10);
        int providedCheckDigit = Character.getNumericValue(accountNumber.charAt(10));
        int calculatedCheckDigit = calculateLuhnCheckDigit(baseNumber);

        return providedCheckDigit == calculatedCheckDigit;
    }
}
