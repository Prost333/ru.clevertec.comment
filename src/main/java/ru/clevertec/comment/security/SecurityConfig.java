package ru.clevertec.comment.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.clevertec.comment.multiFeign.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.crypto.SecretKey;
/**
 * SecurityConfig class configures the security settings for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String secretKey;
    @Autowired
    private ApplicationContext context;
    /**
     * Provides the SecretKey bean for JWT token generation and validation.
     *
     * @return The SecretKey bean.
     */
    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * Provides the JwtService bean for JWT token operations.
     *
     * @return The JwtService bean.
     */
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
    /**
     * Provides the UserClient bean for user-related operations.
     *
     * @return The UserClient bean.
     */
    @Bean
    public UserClient userClient() {
        return context.getBean(UserClient.class);
    }

    /**
     * Provides the JwtTokenFilter bean for JWT token authentication.
     *
     * @return The JwtTokenFilter bean.
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(secretKey,userClient());
    }
    /**
     * Configures HTTP security settings for the application.
     *
     * @param http The HttpSecurity instance to configure.
     * @return The SecurityFilterChain instance.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("Entering configure method");
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/comments/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/comments/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/comments/search").authenticated()
                        .requestMatchers(HttpMethod.GET, "/comments/username/{username}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/comments/news/{newsId}").permitAll());
        logger.debug("Exiting configure method");
        return http.build();
    }
}
