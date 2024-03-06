package ru.clevertec.comment.cache;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm")
public abstract class CacheFactory {

    @Lookup
    public abstract Cache<Object, Object> getCacheAlgorithm();
}