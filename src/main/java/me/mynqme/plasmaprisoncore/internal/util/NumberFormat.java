package me.mynqme.plasmaprisoncore.internal.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberFormat {

    public static String format(long money) {
        return format(BigDecimal.valueOf(money));
    }

    public static String format(BigDecimal money) {
        if (money.compareTo(BigDecimal.valueOf(0L))<0) return "-" + format(money.negate());
        if (money.compareTo(BigDecimal.valueOf(1000L))<=0) {
           return money.toString();
        } else if (smaller(money, 999999)) {
            return money.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(10L), 0, RoundingMode.HALF_UP).toString() + "k";
        } else if (bigger(money, 1e6)&&smaller(money, 9.99999999e8)) {
            return money.divide(BigDecimal.valueOf(1e4D), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100L), 0, RoundingMode.HALF_UP).toString() + "M";
        } else if (bigger(money, 1e9)&&smaller(money, 9.99999999999e11)) {
            return money.divide(BigDecimal.valueOf(1e6), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(1000L), 0, RoundingMode.HALF_UP).toString() + "B";
        } else if (bigger(money, 1e12)&&smaller(money, 9.99999999999999e14)) {
            return money.divide(BigDecimal.valueOf(1e8), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(10000L), 0, RoundingMode.HALF_UP).toString() + "T";
        } else if (bigger(money, 1e15)&&smaller(money, 9.99999999999999999e17)) {
            return money.divide(BigDecimal.valueOf(1e10), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100000L), 0, RoundingMode.HALF_UP).toString() + "Q";
        } else if (bigger(money, 1e18)&&smaller(money, 9.99999999999999999999e20)) {
            return money.divide(BigDecimal.valueOf(1e12), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(1000000L), 0, RoundingMode.HALF_UP).toString() + "P";
        } else if (bigger(money, 1e21)) {
            return money.divide(BigDecimal.valueOf(1e14), 0, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(10000000L), 0, RoundingMode.HALF_UP).toString() + "E";
        }
        return "";
    }

    private static boolean bigger(BigDecimal bigger, double than) {
        return bigger.compareTo(BigDecimal.valueOf(than)) >= 0;
    }

    private static boolean smaller(BigDecimal smaller, double than) {
        return smaller.compareTo(BigDecimal.valueOf(than)) <= 0;
    }
}
