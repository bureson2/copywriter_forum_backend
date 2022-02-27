package cz.cvut.kbss.ear.copyto.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * {@link RestTemplate} can be used to communicate with web services of another application - see for example <a
     * href="http://www.baeldung.com/rest-template">http://www.baeldung.com/rest-template</a>.
     */

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * {@link MultipartResolver} is used to support file upload.
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /**
     * Overrides the default {@link ObjectMapper} bean created by Spring Boot to allow further customization.
     * <p>
     * Basic configuration could be also done in {@code application.properties}, see <a
     * href="https://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper">Spring
     * Boot reference</a>.
     *
     * @return {@code ObjectMapper} bean
     */
    @Bean
    @Primary    // Override the default instance created by Spring Boot
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        // Support for Java 8 Date/Time API
        objectMapper.registerModule(new JavaTimeModule());
        // Only non-null properties are serialized
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // This is necessary for our way of working with Java 8 Date/Time API. If it were not configured, the
        // datetime object in JSON would consist of several attributes, each for the respective date/time part, i.e. year, day etc.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Ignore unknown properties in JSON
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").exposedHeaders(HttpHeaders.LOCATION);
            }
        };
    }
}
