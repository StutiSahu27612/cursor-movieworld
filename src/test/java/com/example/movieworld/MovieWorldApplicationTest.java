package com.example.movieworld;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never",
    "spring.jpa.defer-datasource-initialization=false"
})
class MovieWorldApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // which exercises the main class and Spring Boot auto-configuration
        // The @SpringBootTest annotation loads the full application context including MovieWorldApplication
    }
}
