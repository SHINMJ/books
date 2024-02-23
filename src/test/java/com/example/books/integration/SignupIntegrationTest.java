package com.example.books.integration;

import com.example.books.exception.dto.ErrorResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("회원가입 통합 테스트")
public class SignupIntegrationTest extends IntegrationTest{

    @Test
    void 회원가입_성공() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                        "\"email\":\"test@email.com\"," +
                        "\"password\":\"test111\"," +
                        "\"name\":\"testUser\"," +
                        "\"phone\":\"01099912222\"" +
                        "}"
        );

        assertEquals(response.statusCode(), HttpStatus.CREATED.value());
    }

    @Test
    void 회원가입_실패_비밀번호형식() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                "\"email\":\"test@email.com\"," +
                "\"password\":\"2223111\"," +
                "\"name\":\"testUser\"," +
                "\"phone\":\"01099912222\"" +
                "}"
        );

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());
        assertThat(getFieldMessage(response))
                .contains("비밀번호");

    }

    @Test
    void 회원가입_실패_이름필수() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                "\"email\":\"test@email.com\"," +
                "\"password\":\"222df3111\"," +
                "\"name\":\"\"," +
                "\"phone\":\"01099912222\"" +
                "}"
        );

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());
        assertThat(getFieldMessage(response))
                .contains("이름", "필수");
    }

    @Test
    void 회원가입_실패_이메일형식() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                "\"email\":\"test.com\"," +
                "\"password\":\"222df3111\"," +
                "\"name\":\"testUser\"," +
                "\"phone\":\"01099912222\"" +
                "}"
        );

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());
        assertThat(getFieldMessage(response))
                .contains("이메일");
    }

    @Test
    void 회원가입_실패_휴대폰번호형식() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                "\"email\":\"test@email.com\"," +
                "\"password\":\"222df3111\"," +
                "\"name\":\"testUser\"," +
                "\"phone\":\"010999sdfd12222\"" +
                "}"
        );

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());
        assertThat(getFieldMessage(response))
                .contains("휴대폰번호");
    }

    @Test
    void 회원가입_실패_이메일_비밀번호_이름_휴대폰번호_notValid() {
        ExtractableResponse<Response> response = SIGNUP( "{ " +
                "\"email\":\"testom\"," +
                "\"password\":\"23111\"," +
                "\"name\":\"\"," +
                "\"phone\":\"010999sdfd12222\"" +
                "}"
        );

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());
        assertEquals(response.body().jsonPath().getList("fieldErrors", ErrorResponse.ValidationFieldError.class).size(), 4);
    }


    public static ExtractableResponse<Response> SIGNUP(String body){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/auth/signup")
                .then().log().all()
                .extract();
    }

    private String getFieldMessage(ExtractableResponse<Response> response){
        return response.body()
                .jsonPath().getList("fieldErrors", ErrorResponse.ValidationFieldError.class).get(0)
                .message();
    }
}
