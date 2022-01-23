package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private OrganisationService organisationService;


    @Transactional(readOnly = true)
    public PaginatedList<Organisation> organisationReport(String name, Status status, LicenseType licenseType, Pageable pageable) {
        return organisationService.getOrganisations(name, status, licenseType, pageable);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadOrganisationReport(String name, Status status, LicenseType licenseType) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Organisation> page = organisationService.getOrganisations(name, status, licenseType, Pageable.unpaged());
        List<Organisation> organisations = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList(
                    "Company Name",
                    "Location",
                    "Addr1",
                    "Addr2",
                    "Key Person 1 Name",
                    "Key Person 1 Email",
                    "Key Person 1 Contact Number",
                    "Key Person 2 Name",
                    "Key Person 2 Email",
                    "Key Person 2 Contact Number",
                    "License Type",
                    "License Start Date",
                    "License End Date",
                    "Status"
            );
            csvPrinter.printRecord(data);
            for (Organisation organisation : organisations) {
                data = Arrays.asList(
                        organisation.getName(),
                        organisation.getCity(),
                        organisation.getAdd1(),
                        organisation.getAdd2(),
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getName() : "",
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getEmail() : "",
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getContactNo() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getName() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getEmail() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getContactNo() : "",
                        organisation.getLicenseType().name(),
                        organisation.getLicenseStart().format(DateTimeFormatter.ofPattern(dateFormat)),
                        organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)),
                        organisation.getStatus().name()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }
}
