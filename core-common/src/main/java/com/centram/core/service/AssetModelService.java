package com.centram.core.service;


import com.centram.core.repository.AssetModelRepository;
import com.centram.domain.AssetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetModelService {
    private static final Logger log = LoggerFactory.getLogger(AssetModelService.class);

    @Autowired
    private AssetModelRepository assetModelRepository;

    @Transactional(readOnly = true)
    public List<AssetModel> getAssetModel(Pageable pageable) {
        return assetModelRepository.findAll();
    }
}
