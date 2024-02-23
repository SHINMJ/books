package com.example.books.integration;

import com.example.books.utils.DatabaseInitialize;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitialize databaseInitialize;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT){
            RestAssured.port = port;
            databaseInitialize.afterPropertiesSet();
        }

        databaseInitialize.execute();
    }
}
