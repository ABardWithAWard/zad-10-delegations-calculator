package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm VV");

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) {
        if (name == null || start == null || end == null || dailyRate == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }

        ZonedDateTime startTime;
        ZonedDateTime endTime;
        try {
            startTime = ZonedDateTime.parse(start, FORMATTER);
            endTime = ZonedDateTime.parse(end, FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }

        if (!endTime.isAfter(startTime)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        Duration duration = Duration.between(startTime, endTime);
        long totalMinutes = duration.toMinutes();

        long fullDays = totalMinutes / (24 * 60);
        long remainingMinutes = totalMinutes % (24 * 60);

        BigDecimal totalAmount = dailyRate.multiply(BigDecimal.valueOf(fullDays));

        if (remainingMinutes > 0) {
            if (remainingMinutes <= 8 * 60) {
                BigDecimal part = dailyRate.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
                totalAmount = totalAmount.add(part);
            } else if (remainingMinutes <= 12 * 60) {
                BigDecimal part = dailyRate.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                totalAmount = totalAmount.add(part);
            } else {
                totalAmount = totalAmount.add(dailyRate);
            }
        }

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }
}