package org.example;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static Boolean equalTo(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    public static Boolean notEqualTo(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) != 0;
    }

    public static Boolean greaterThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static Boolean greaterThanEqualTo(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    public static Boolean lessThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public static Boolean lessThanEqualTo(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

    public static String formattedAmount(BigDecimal a) {
        return String.format("%,.2f", a.setScale(2, RoundingMode.DOWN));
    }

    public static Boolean isInclusiveBetween(BigDecimal a, BigDecimal floor, BigDecimal ceiling) {
        return greaterThanEqualTo(a, floor) && lessThanEqualTo(a, ceiling);
    }

    public static BigDecimal toDecimal(BigDecimal percentValue) {
        return percentValue.movePointLeft(2);
    }

    public static BigDecimal toPercent(BigDecimal decimalValue) {
        return decimalValue.movePointRight(2);
    }
}

