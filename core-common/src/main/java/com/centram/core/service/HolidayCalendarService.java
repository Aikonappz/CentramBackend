package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.HolidayCalendarRepository;
import com.centram.domain.Holiday;
import com.centram.domain.HolidayCalendar;
import org.apache.commons.csv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
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
public class HolidayCalendarService {

    private static final Logger log = LoggerFactory.getLogger(HolidayCalendarService.class);

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private LocationService locationService;

    /**
     * get HolidayCalendar
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public HolidayCalendar getById(BigInteger id) {
        Optional<HolidayCalendar> holidayCalender = holidayCalendarRepository.findById(id);
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
    public PaginatedList<HolidayCalendar> getHolidayCalendars(BigInteger organisationId, Pageable pageable) {
        return new PaginatedList<HolidayCalendar>(holidayCalendarRepository.getHolidayCalendars(organisationId, pageable));
    }

    /**
     * save location
     *
     * @param holidayCalendar
     * @return
     */
    @Transactional
    public HolidayCalendar save(BigInteger organisationId, HolidayCalendar holidayCalendar) {
        holidayCalendar.setOrganisation(organisationService.getOrganisationById(organisationId));
        return holidayCalendarRepository.save(holidayCalendar);
    }

    /**
     * @param multipartFile
     * @param holidayCalendar
     * @return
     * @throws IOException
     */
    @Transactional
    public HolidayCalendar uploadHolidayCalendarData(BigInteger organisationId, MultipartFile multipartFile, HolidayCalendar holidayCalendar) {
        Map<String, String> value = new HashMap<String, String>();
        List<Holiday> holidays = new ArrayList<Holiday>();
        List<String> commonHeaders = Arrays.asList("DATE", "DESCRIPTION");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            if (multipartFile.getBytes().length == 0) {
                throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
            }
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                value = csvRecord.toMap().entrySet().stream().filter(i -> commonHeaders.contains(i.getKey())).collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
                holidays.add(new Holiday(LocalDate.parse(value.get("DATE"), DateTimeFormatter.ofPattern(dateFormat)), value.get("DESCRIPTION")));
            }
            holidayCalendar.setLocation(locationService.getById(holidayCalendar.getLocation().getId()));
            holidayCalendar.setHolidays(holidays);
            holidayCalendar.setOrganisation(organisationService.getOrganisationById(organisationId));
            return proxyService.saveHolidayCalender(holidayCalendar);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "Holiday Calendar");
            }});
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
    public ByteArrayInputStream downloadHolidayCalendar(BigInteger id) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("DATE", "DESCRIPTION");
            csvPrinter.printRecord(data);
            HolidayCalendar holidayCalendar = this.getById(id);
            for (Holiday holiday : holidayCalendar.getHolidays()) {
                data = Arrays.asList(holiday.getDate().format(DateTimeFormatter.ofPattern(dateFormat)), holiday.getDescription());
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    /**
     * @param accountId
     * @param locationId
     * @param organisationId
     * @param year
     * @return
     */
    @Transactional(readOnly = true)
    public List<Holiday> getHolidays(BigInteger accountId, BigInteger locationId, BigInteger organisationId, String year) {
        HolidayCalendar holidayCalendar = holidayCalendarRepository.getHolidays(accountId, year, locationId, organisationId);
        if (holidayCalendar == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return holidayCalendar.getHolidays();
    }
}