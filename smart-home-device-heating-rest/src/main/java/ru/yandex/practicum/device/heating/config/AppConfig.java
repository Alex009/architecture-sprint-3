package ru.yandex.practicum.device.heating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Используем StringRedisSerializer для ключей
        template.setKeySerializer(new StringRedisSerializer());

        // Используем GenericJackson2JsonRedisSerializer для значений
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Используем StringRedisSerializer для хэшей (ключи хэша)
        template.setHashKeySerializer(new StringRedisSerializer());

        // Используем GenericJackson2JsonRedisSerializer для хэшей (значения хэша)
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
