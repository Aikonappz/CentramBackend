package com.centram.common.utility;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

/**
 * App utility
 */
public class Utility {

    public static String prepareUniqueId(final String prefix) {
        final String DATE_FORMATTER = "yyMMddHHmmss";
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String currentDateTime = localDateTime.format(dateTimeFormatter);
        Integer randomNumber = new SecureRandom().nextInt((9999999 - 1) + 1) + 1;
        String id = currentDateTime.concat(randomNumber.toString());
        Long longId = Long.parseLong(id);
        String encodedString = Long.toString(longId, 36).toUpperCase();
        return prefix.concat(encodedString);
    }

    public static BigInteger getUniqueInteger(Integer length) {
        Integer m = (int) Math.pow(10, length - 1);
        Integer n = m + length + new Random().nextInt(9 * m);
        return BigInteger.valueOf(n.intValue());
    }

    public static String generateCode(String prefix) {
        StringBuilder code = new StringBuilder();
        if (prefix != null && !prefix.equals("")) {
            code.append(prefix);
        }
        Random random = new Random();
        code.append(new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
        code.append(random.nextInt(10));
        return code.toString();
    }

    public static String getUniqueString(Integer length) {
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            Integer index = (int) (candidateChars.length() * Math.random());
            sb.append(candidateChars.charAt(index));
        }
        return sb.toString();
    }


}
