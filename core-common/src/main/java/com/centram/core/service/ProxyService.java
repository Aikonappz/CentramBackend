package com.centram.core.service;


import com.centram.core.repository.AssetRepository;
import com.centram.domain.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProxyService {
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private AssetRepository assetRepository;

    @Transactional(readOnly = false)
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }
}
