package com.centram.common.utility;


import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * App utility
 */
public class Utility {
    private final static String requestIdPrefix = "REQ";

    /*public static void main(String[] args) throws InterruptedException {
        List<String> incidents = new ArrayList<String>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    incidents.add(incidentNo("INC"));
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("\nFinished all threads");
        System.out.println(incidents.size());
        if (incidents.size() > 0) {
            for (String s : incidents) {
                if (Collections.frequency(incidents, s) > 1) {
                    System.out.println(s);
                }
            }
        }
    }*/

    public static String requestNo() {
        return uniqueId(requestIdPrefix);
    }

    public static String orderNo(String orderNoPrefix) {
        return orderNoPrefix.concat(generateUniqueID());
    }

    public static String assetNo(String assetNoPrefix) {
        return assetNoPrefix.concat(generateUniqueID());
    }

    public static String incidentNo(String incidentNoPrefix) {
        return incidentNoPrefix.concat(generateUniqueID());
    }

    public static String getCorrelationId(){
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    public static Long generateUniqueIDOld() {
        final String DATE_FORMATTER = "yyMMddHHmmss";
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String currentDateTime = localDateTime.format(dateTimeFormatter);
        Integer randomNumber = new SecureRandom().nextInt((9999999 - 1) + 1) + 1;
        String id = currentDateTime.concat(randomNumber.toString());
        return Long.parseLong(id);
    }

    public static String generateUniqueID() {
        Integer randomNumber = ThreadLocalRandom.current().nextInt((99999999 - 1) + 1) + 1;
        return String.format("%010d", randomNumber);
    }

    public static String uniqueId(final String prefix) {
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

    public static String encode(String data) {
        return new String(Base64.encodeBase64(data.getBytes()), StandardCharsets.UTF_8);
    }

    public static String decode(String encodedData) {
        return new String(Base64.decodeBase64(encodedData), StandardCharsets.UTF_8);
    }

    public static boolean isBase64(String s) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        return m.find();
    }

    /**
     * Convert seconds to string date like => 3600 = 1 hour
     * @param seconds
     * @return
     */
    public String convertSecondsToStringDate(Integer seconds) {
        String str = "";
        Integer day = seconds / (24 * 3600);
        seconds = seconds % (24 * 3600);
        Integer hour = seconds / 3600;
        seconds %= 3600;
        Integer minutes = seconds / 60;
        seconds %= 60;
        Integer sec = seconds;
        str += day > 1 ? day + " days " : day == 1 ? day + " day " : "";
        str += hour > 1 ? hour + " hours " : hour == 1 ? hour + " hour " : "";
        str += minutes > 1 ? minutes + " minutes " : minutes == 1 ? minutes + " minute " : "";
        return str;
    }
}
