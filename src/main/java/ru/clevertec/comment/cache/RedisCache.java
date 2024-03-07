package ru.clevertec.comment.cache;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope("prototype")
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm", havingValue = "Redis")
public class RedisCache<K, V> implements Cache<K, V> {

    private final RedisTemplate<K, V> redisTemplate;

    public RedisCache(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(K key) {
        redisTemplate.delete(key);
    }
}
