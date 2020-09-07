package me.fede1132.plasmaprisoncore.internal.util;

public class NumberFormat {
    public static String format(long money) {
        if (money<0) return "-" + format(-money);
        if (money<1000) {
           return String.valueOf(money);
        } else if (money<=999999) {
            return Math.round(money / 100D) / 10 + "k";
        } else if (money>=1e6&&money<=9.99999999e8) {
            return Math.round(money / 1e4D) / 100 + "M";
        } else if (money>=1e9&&money<=9.99999999999e11) {
            return Math.round(money / 1e6D) / 1000 + "B";
        } else if (money>=1e12&&money<=9.99999999999999e14) {
            return Math.round(money / 1e8D) / 10000 + "T";
        } else if (money>=1e15&&money<=9.99999999999999999e17) {
            return Math.round(money / 1e10D) / 100000 + "Qa";
        } else if (money>=1e18&&money<=9.99999999999999999999e20) {
            return Math.round(money / 1e12D) / 1000000 + "Qi";
        } else if (money>=1e21&&money<=9.99999999999999999999999e23) {
            return Math.round(money / 1e14D) / 10000000 + "Sx";
        } else if (money>=1e24) {
            return Math.round(money / 1e16D) / 100000000 + "Sp";
        }
        return "";
    }
}
