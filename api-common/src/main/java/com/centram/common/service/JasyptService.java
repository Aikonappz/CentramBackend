package com.centram.common.service;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JasyptService {
    private static final Logger log = LoggerFactory.getLogger(JasyptService.class);

    @Autowired
    private StandardPBEStringEncryptor standardPBEStringEncryptor;

    /**
     * @param plainData
     * @return
     */
    public String encrypt(String plainData) {
        return standardPBEStringEncryptor.encrypt(plainData);
    }

    /**
     * @param encryptedData
     * @return
     */
    public String decrypt(String encryptedData) {
        return standardPBEStringEncryptor.decrypt(encryptedData);
    }

    /*public static void main(String[] args) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        standardPBEStringEncryptor.setPassword("UIbhssh^^51771771@!!0m0!!n==!hasg@3#==");

        //appCentramWsUser
        //appCentramWsUser@#9087
        System.out.println(standardPBEStringEncryptor.encrypt("appCentramWsUser"));
        System.out.println(standardPBEStringEncryptor.encrypt("appCentramWsUser@#9087"));
    }*/

}
