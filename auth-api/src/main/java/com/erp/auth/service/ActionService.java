package com.erp.auth.service;


import com.erp.auth.repository.ActionRepository;
import com.erp.domain.Action;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ActionService {
    private static final Logger log = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ModelMapper modelMapper;

    //@Cacheable(cacheNames = "all_actions", key = "'list'")
    public Page<Action> getActions(Pageable pageable) {
        //return modelMapper.map(actionRepository.findAll(pageable).getContent(), new TypeToken<List<ActionVO>>() {}.getType());
        return actionRepository.findAll(pageable);
    }


}
