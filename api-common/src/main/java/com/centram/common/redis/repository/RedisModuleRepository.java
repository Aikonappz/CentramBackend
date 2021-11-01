package com.centram.common.redis.repository;

import com.centram.domain.Module;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisModuleRepository {

    private final HashOperations hashOperations;
    private final RedisTemplate redisTemplate;

    public RedisModuleRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public List<Module> saveAll(List<Module> modules) {
        hashOperations.put("all_modules", "all_modules", modules);
        return modules;
    }

    public List<Module> findAll() {
        return hashOperations.values("all_modules");
    }

    public void deleteAll(String id) {
        hashOperations.delete("all_module", "all_module");
    }

    public Module save(Module module) {
        hashOperations.put("module", module.getId().toString(), module);
        return module;
    }

    public Module findById(String id) {
        return (Module) hashOperations.get("module", id);
    }

    public Module update(Module module) {
        return save(module);
    }

    public void delete(String id) {
        hashOperations.delete("module", id);
    }
}
