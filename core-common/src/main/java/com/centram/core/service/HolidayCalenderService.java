package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.HolidayCalenderRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.Holiday;
import com.centram.domain.HolidayCalender;
import com.centram.domain.enumarator.ActivityType;
import org.apache.commons.csv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HolidayCalenderService {

    private static final Logger log = LoggerFactory.getLogger(HolidayCalenderService.class);

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private HolidayCalenderRepository holidayCalenderRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * get location
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public HolidayCalender getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<HolidayCalender> holidayCalender = holidayCalenderRepository.findById(id);
        if (!holidayCalender.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return holidayCalender.get();
    }

    /**
     * get all locations
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<HolidayCalender> getHolidayCalenders(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<HolidayCalender>(holidayCalenderRepository.getHolidayCalenderByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save location
     *
     * @param holidayCalender
     * @return
     */
    @Transactional
    public HolidayCalender save(HolidayCalender holidayCalender) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        holidayCalender.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, holidayCalender.getId() != null ? ActivityType.ADD_CALENDER : ActivityType.UPDATE_CALENDER));
        return holidayCalenderRepository.save(holidayCalender);
    }

    /**
     * @param multipartFile
     * @param holidayCalender
     * @return
     * @throws IOException
     */
    @Transactional
    public HolidayCalender uploadHolidayCalenderData(MultipartFile multipartFile, HolidayCalender holidayCalender) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, String> value = new HashMap<String, String>();
        List<Holiday> holidays = new ArrayList<Holiday>();
        List<String> commonHeaders = Arrays.asList("DATE", "DESCRIPTION");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            if (multipartFile.getBytes().length == 0) {
                throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
            }
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                value = csvRecord.toMap()
                        .entrySet().stream()
                        .filter(i -> commonHeaders.contains(i.getKey()))
                        .collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
                holidays.add(new Holiday(LocalDate.parse(value.get("DATE"), DateTimeFormatter.ofPattern(dateFormat)), value.get("DESCRIPTION")));
            }
            holidayCalender.setHolidays(holidays);
            holidayCalender.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
            activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, holidayCalender.getId() != null ? ActivityType.ADD_CALENDER : ActivityType.UPDATE_CALENDER));
            return holidayCalenderRepository.save(holidayCalender);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_PROCESSING_ISSUE);
        }
    }

    /**
     * download calender
     *
     * @return
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadHolidayCalender(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList(
                    "DATE",
                    "DESCRIPTION"
            );
            csvPrinter.printRecord(data);
            HolidayCalender holidayCalender = this.getById(id);
            for (Holiday holiday : holidayCalender.getHolidays()) {
                data = Arrays.asList(
                        holiday.getDate().format(DateTimeFormatter.ofPattern(dateFormat)),
                        holiday.getDescription()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysByYear(String year) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HolidayCalender holidayCalender = holidayCalenderRepository.getHolidayCalenderByYear(loggedInUser.getOrganisationId(), year, loggedInUser.getLocationId());
        if (holidayCalender == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return holidayCalender.getHolidays();
    }
}