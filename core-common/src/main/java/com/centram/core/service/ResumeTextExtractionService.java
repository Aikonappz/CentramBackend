package com.centram.core.service;

import com.centram.common.exception.ResumeException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ResumeTextExtractionService {

    public String extract(MultipartFile file) {

        try (InputStream stream = file.getInputStream()) {

            Tika tika = new Tika();
            return tika.parseToString(stream);

        } catch (Exception e) {
            throw new ResumeException("Unable to extract resume content", e);
        }

    }
}
