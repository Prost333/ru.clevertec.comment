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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String secretKey;
    @Autowired
    private ApplicationContext context;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }


    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public UserClient userClient() {
        return context.getBean(UserClient.class);
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(secretKey,userClient());
    }

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
