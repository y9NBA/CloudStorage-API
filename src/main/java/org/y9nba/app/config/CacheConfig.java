package org.y9nba.app.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.caffeine.maximumSize}")
    int maximumSize;

    @Value("${spring.cache.caffeine.expire}")
    long expire;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expire, TimeUnit.MINUTES);

        cacheManager.setCaffeine(caffeine);
        cacheManager.setAsyncCacheMode(true);

        return cacheManager;
    }
}
