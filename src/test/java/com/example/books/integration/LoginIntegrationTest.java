package com.example.books.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.example.books.integration.SignupIntegrationTest.SIGNUP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("로그인 통합 테스트")
public class LoginIntegrationTest extends IntegrationTest{

    @Test
    void 로그인_성공() {
        //given
        SIGNUP("{ " +
                        "\"email\":\"test@email.com\"," +
                        "\"password\":\"test111\"," +
                        "\"name\":\"testUser\"," +
                        "\"phone\":\"01099912222\"" +
                        "}");

        String login = "{" +
                        "\"email\":\"test@email.com\"," +
                        "\"password\":\"test111\"" +
                        "}";

        //when
        ExtractableResponse<Response> response = 로그인(login);

        //then
        assertEquals(response.statusCode(), HttpStatus.OK.value());
        assertNotNull(response.body().jsonPath().getString("token"));
    }

    @Test
    void 로그인_실패_사용자없음() {
        String login = "{" +
                "\"email\":\"test@email.com\"," +
                "\"password\":\"test111\"" +
                "}";

        //when
        ExtractableResponse<Response> response = 로그인(login);

        assertEquals(response.statusCode(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 로그인_실패_비밀번호틀림() {
        String login = "{" +
                "\"email\":\"test@email.com\"," +
                "\"password\":\"tessdt111\"" +
                "}";

        //when
        ExtractableResponse<Response> response = 로그인(login);

        assertEquals(response.statusCode(), HttpStatus.UNAUTHORIZED.value());
    }

    private static ExtractableResponse<Response> 로그인(String body){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/auth/login")
                .then().log().all()
                .extract();
    }

    public static String LOGGED_IN(String body){
        ExtractableResponse<Response> response = 로그인(body);
        return response.body().jsonPath().getString("token");
    }
}
