package com.centram.core.service;


import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ActionRepository;
import com.centram.domain.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class ActionService {
    private static final Logger log = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private ActionRepository actionRepository;

    @Transactional(readOnly = true)
    public PaginatedList<Action> getActions(Pageable pageable) {
        return new PaginatedList<Action>(actionRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Action getById(BigInteger actionId) {
        return actionRepository.getById(actionId);
    }

}
