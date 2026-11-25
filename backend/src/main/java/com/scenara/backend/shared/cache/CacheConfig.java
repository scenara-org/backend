package com.scenara.backend.shared.cache;

import com.scenara.backend.simulations.dto.SimulationResponse;
import com.scenara.backend.simulations.dto.SimulationSummaryResponse;
import com.scenara.backend.users.dto.CreateUserResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

/**
 * Cache das leituras no Redis. Cada cache tem um serializer JSON tipado para o seu DTO,
 * assim o JSON salvo não carrega nomes de classes e não depende de "default typing".
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, JsonMapper jsonMapper) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues();

        JavaType summaries = jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, SimulationSummaryResponse.class);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheNames.SIMULATION,
                        config(base, Duration.ofHours(1), jsonMapper, jsonMapper.constructType(SimulationResponse.class)))
                .withCacheConfiguration(CacheNames.SIMULATIONS_BY_PROJECT,
                        config(base, Duration.ofMinutes(10), jsonMapper, summaries))
                .withCacheConfiguration(CacheNames.SIMULATIONS_BY_SCENARIO,
                        config(base, Duration.ofMinutes(10), jsonMapper, summaries))
                .withCacheConfiguration(CacheNames.USER,
                        config(base, Duration.ofMinutes(10), jsonMapper, jsonMapper.constructType(CreateUserResponse.class)))
                .build();
    }

    private static RedisCacheConfiguration config(RedisCacheConfiguration base, Duration ttl,
                                                  JsonMapper jsonMapper, JavaType type) {
        return base.entryTtl(ttl)
                .serializeValuesWith(SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(jsonMapper, type)));
    }
}
