package com.centram.common.service;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final String documentsPath = "/home/sumit/Documents/erp/documents";

    public String getHtmlFileContent(String type, String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(resolvePathLookUp(type, fileName)));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.FILE_READ_ISSUE);
        }
        return contentBuilder.toString();
    }

    private String resolvePathLookUp(String type, String fileName) {
        switch (type) {
            case "email":
                documentsPath.concat("/template/email/").concat(fileName);
                break;
            default:
                documentsPath.concat("/").concat(fileName);
                break;
        }
        return documentsPath;
    }

}
