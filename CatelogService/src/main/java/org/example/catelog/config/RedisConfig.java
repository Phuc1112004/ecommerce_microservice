package org.example.catelog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // ----------------- Connection Factory -----------------
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofMillis(60000));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration =
                JedisClientConfiguration.builder();
        jedisClientConfiguration.usePooling().poolConfig(poolConfig);
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(60000));
        jedisClientConfiguration.readTimeout(Duration.ofMillis(60000));

        return new JedisConnectionFactory(config, jedisClientConfiguration.build());
    }

    // ----------------- ObjectMapper cho Redis -----------------
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // xử lý LocalDateTime
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    // ----------------- RedisTemplate -----------------
    @Bean
    public RedisTemplate<String, Object> redisTemplate(ObjectMapper redisObjectMapper,
                                                       JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.setConnectionFactory(jedisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    // ----------------- CacheManager cho chi tiết sách -----------------
    @Bean
    public RedisCacheManager bookDetailCacheManager(JedisConnectionFactory jedisConnectionFactory,
                                                    ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(jedisConnectionFactory)
                .withCacheConfiguration("bookDetail", cacheConfig)
                .build();
    }

    // ----------------- CacheManager cho danh sách sách -----------------
    @Bean
    @Primary
    public RedisCacheManager booksListCacheManager(JedisConnectionFactory jedisConnectionFactory,
                                                   ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(jedisConnectionFactory)
                .withCacheConfiguration("books", cacheConfig)
                .build();
    }

}
