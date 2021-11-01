package com.centram.common.utility;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

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
        if (!StringUtils.isEmpty(prefix)) {
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

    public static String getCorrelationId() {
        return getUniqueString(10);
    }

    public static Pageable getAllData() {
        return new Pageable() {
            @Override
            public int getPageNumber() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return Integer.MAX_VALUE;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };
    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }
}
