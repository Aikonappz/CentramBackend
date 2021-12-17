package com.centram.core.service;


import com.centram.core.repository.ActionRepository;
import com.centram.domain.Action;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentCommunicationService {
    private static final Logger log = LoggerFactory.getLogger(IncidentCommunicationService.class);

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<Action> getActions(Pageable pageable) {
        return actionRepository.findAll(pageable);
    }

}
