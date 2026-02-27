package org.dbs.sbgb.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TestConfiguration
@EnableCaching
public class CacheTestConfig {

    @Bean
    public CachedNoiseGridAdapter cachedNoiseGridAdapter() {
        return new CachedNoiseGridAdapter();
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCache noiseGridCache = new CaffeineCache("noiseGrid",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(50)
                        .build());
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(noiseGridCache));
        return manager;
    }
}
