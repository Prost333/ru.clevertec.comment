package ru.clevertec.comment.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Component
@Scope("prototype")
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm", havingValue = "LRUCache")
public class LRU<K, V> implements Cache<K, V> {

    private final Map<K, V> cacheMap;
    private final int capacity;

    public LRU(@Value("${spring.cache.capacity}") int capacity) {
        this.capacity = capacity;
        this.cacheMap = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }


    @Override
    public void put(K key, V value) {
        cacheMap.put(key, value);
    }


    @Override
    public V get(K key) {
        return cacheMap.get(key);
    }


    @Override
    public void remove(K key) {
        cacheMap.remove(key);
    }
}
