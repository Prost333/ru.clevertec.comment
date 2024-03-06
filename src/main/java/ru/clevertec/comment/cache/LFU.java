package ru.clevertec.comment.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Component
@Scope("prototype")
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm", havingValue = "LFUCache")
public class LFU<K, V> implements Cache<K, V> {

    private final int maxSize;
    private final Map<K, V> cacheMap;
    private final Map<K, Integer> keyFrequency;
    private final Map<Integer, Set<K>> frequencyKeys;
    private int minFrequency;

    public LFU(@Value("${spring.cache.capacity}") int maxSize) {
        this.maxSize = maxSize;
        this.cacheMap = new HashMap<>();
        this.keyFrequency = new HashMap<>();
        this.frequencyKeys = new HashMap<>();
        this.minFrequency = 0;
        this.frequencyKeys.put(1, new LinkedHashSet<>());
    }


    @Override
    public void put(K key, V value) {
        if (maxSize > 0) {
            if (cacheMap.containsKey(key)) {
                cacheMap.put(key, value);
            } else {
                if (cacheMap.size() >= maxSize) {
                    evict();
                }
                cacheMap.put(key, value);
                keyFrequency.put(key, 1);
                frequencyKeys.get(1).add(key);
                minFrequency = 1;
            }
        }
    }


    @Override
    public V get(K key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }
        int frequency = keyFrequency.get(key);
        keyFrequency.put(key, frequency + 1);
        frequencyKeys.get(frequency).remove(key);
        if (frequency == minFrequency && frequencyKeys.get(frequency).isEmpty()) {
            minFrequency++;
        }
        if (!frequencyKeys.containsKey(frequency + 1)) {
            frequencyKeys.put(frequency + 1, new LinkedHashSet<>());
        }
        frequencyKeys.get(frequency + 1).add(key);
        return cacheMap.get(key);
    }


    @Override
    public void remove(K key) {
        if (cacheMap.containsKey(key)) {
            int frequency = keyFrequency.get(key);
            keyFrequency.remove(key);
            frequencyKeys.get(frequency).remove(key);
            cacheMap.remove(key);
            if (frequency == minFrequency && frequencyKeys.get(frequency).isEmpty()) {
                minFrequency++;
            }
        }
    }


    private void evict() {
        K keyToRemove = frequencyKeys.get(minFrequency).iterator().next();
        frequencyKeys.get(minFrequency).remove(keyToRemove);
        cacheMap.remove(keyToRemove);
        keyFrequency.remove(keyToRemove);
    }
}