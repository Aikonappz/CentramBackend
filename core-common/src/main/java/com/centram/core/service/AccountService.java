package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AccountRepository;
import com.centram.domain.Account;
import com.centram.domain.enumarator.AccountType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    @Value("${app.default.account.prefix}")
    public String appDefaultAccountPrefix;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrganisationService organisationService;

    /**
     * get account
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Account getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Account> optVendor = accountRepository.findById(id);
        if (!optVendor.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return optVendor.get();
    }

    /**
     * get vendor by name
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Account getByName(String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.getByName(name, loggedInUser.getOrganisationId());
    }

    @Transactional(readOnly = true)
    public Account getByNameAndType(AccountType accountType, String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.getByNameAndType(accountType, name, loggedInUser.getOrganisationId());
    }

    /**
     * get all Account
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Account> getAccounts(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<Account>(accountRepository.getByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save Account
     *
     * @param account
     * @return
     */
    @Transactional
    public Account save(Account account) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        account.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        Long total = accountRepository.getCount(loggedInUser.getOrganisationId()) + 1;
        String accountNo = appDefaultAccountPrefix + LocalDate.now().getYear() + StringUtils.leftPad(String.valueOf(total), 4, "0");
        account.setAccountNo(accountNo);
        return accountRepository.save(account);
    }
}