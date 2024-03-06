package ru.clevertec.comment.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.comment.exeption.DefaultExceptionHandler;
import ru.clevertec.comment.exeption.ExceptionHandler;


@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "exception.handler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExceptionHandlerAutoConfiguration {

    /**
     * Returns a DefaultExceptionHandler bean if it is not already present in the Spring application context.
     *
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandler exceptionHandler() {
        return new DefaultExceptionHandler();
    }
    /**
     * Initializes class and send the initialization message.
     */
    @PostConstruct
    void init() {
        log.info("LoggingAutoConfiguration init");
    }
}
