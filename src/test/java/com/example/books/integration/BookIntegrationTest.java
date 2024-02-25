package com.example.books.integration;

import com.example.books.domain.book.Book;
import com.example.books.exception.dto.ErrorResponse;
import com.example.books.usecase.book.dto.BookResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.example.books.integration.LoginIntegrationTest.LOGGED_IN;
import static com.example.books.integration.SignupIntegrationTest.SIGNUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도서 위탁 대여 통합 테스트")
public class BookIntegrationTest extends IntegrationTest{
    private static final String END_POINT = "/books";

    @Test
    void consignment_success() {
        String token = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");
        ExtractableResponse<Response> response =
                CONSIGNMENTS(token, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");

        assertEquals(response.statusCode(), HttpStatus.CREATED.value());
    }

    @Test
    void consignment_failed_invalidArguments() {
        String token = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");

        String request = "{ \"isbn\":\" \", \"price\":-100}";

        ExtractableResponse<Response> response = CONSIGNMENTS(token, request);

        assertEquals(response.statusCode(), HttpStatus.BAD_REQUEST.value());

        List<ErrorResponse.ValidationFieldError> errors =
                response.body().jsonPath().getList("fieldErrors", ErrorResponse.ValidationFieldError.class);

        assertEquals(errors.size(), 3);
        assertThat(errors).extracting("field").contains("title", "isbn", "price");
    }

    @Test
    void getList_orderLowerPrice_success() {
        String token = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");

        CONSIGNMENTS(token, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");
        CONSIGNMENTS(token, "{\"title\": \"세이노의 가르침\", \"isbn\":\"9791168473690\", \"price\":3000}");
        CONSIGNMENTS(token, "{\"title\": \"Clean Code(클린 코드)\", \"isbn\":\"9788966260959\", \"price\":2000}");
        CONSIGNMENTS(token, "{\"title\": \"클린 아키텍처: 소프트웨어 구조와 설계의 원칙\", \"isbn\":\"9788966262472\", \"price\":2000}");

        ExtractableResponse<Response> response = GET_LIST(token, "price", "ASC");

        assertEquals(response.statusCode(), HttpStatus.OK.value());
        List<BookResponse> content = response.body().jsonPath().getList("content", BookResponse.class);

        assertAll(
                () -> assertEquals(content.size(), 4),
                () -> assertEquals(content.get(0).title(), "첫 1년 움직임의 비밀"),
                () -> assertEquals(content.get(3).title(), "세이노의 가르침")
        );
    }

    @Test
    void getList_orderRecent_success() {
        String token = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");

        CONSIGNMENTS(token, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");
        CONSIGNMENTS(token, "{\"title\": \"세이노의 가르침\", \"isbn\":\"9791168473690\", \"price\":3000}");
        CONSIGNMENTS(token, "{\"title\": \"Clean Code(클린 코드)\", \"isbn\":\"9788966260959\", \"price\":2000}");
        CONSIGNMENTS(token, "{\"title\": \"클린 아키텍처: 소프트웨어 구조와 설계의 원칙\", \"isbn\":\"9788966262472\", \"price\":2000}");

        ExtractableResponse<Response> response = GET_LIST(token, "createdDate", "DESC");

        assertEquals(response.statusCode(), HttpStatus.OK.value());
        List<BookResponse> content = response.body().jsonPath().getList("content", BookResponse.class);

        assertAll(
                () -> assertEquals(content.size(), 4),
                () -> assertEquals(content.get(3).title(), "첫 1년 움직임의 비밀"),
                () -> assertEquals(content.get(2).title(), "세이노의 가르침")
        );
    }

    @Test
    void borrowed_success() {
        //given
        String token = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");

        CONSIGNMENTS(token, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");
        CONSIGNMENTS(token, "{\"title\": \"세이노의 가르침\", \"isbn\":\"9791168473690\", \"price\":3000}");
        CONSIGNMENTS(token, "{\"title\": \"Clean Code(클린 코드)\", \"isbn\":\"9788966260959\", \"price\":2000}");
        CONSIGNMENTS(token, "{\"title\": \"클린 아키텍처: 소프트웨어 구조와 설계의 원칙\", \"isbn\":\"9788966262472\", \"price\":2000}");

        String borrowed = signupAndLoggedIn("borrowed@email.com", "test111", "borrowedUser", "01099912222");

        ExtractableResponse<Response> getList = GET_LIST(borrowed, "createdDate", "DESC");

        List<BookResponse> content = getList.body().jsonPath().getList("content", BookResponse.class);

        List<Long> ids = new ArrayList<>();
        for (BookResponse book: content) {
            if (book.title().equals("첫 1년 움직임의 비밀") || book.title().equals("클린 아키텍처: 소프트웨어 구조와 설계의 원칙")){
                ids.add(book.id());
            }
        }

        assertEquals(ids.size(), 2);

        //when
        ExtractableResponse<Response> response = BORROWED(borrowed, ids);

        assertEquals(response.statusCode(), HttpStatus.CREATED.value());

        getList = GET_LIST(borrowed, "createdDate", "DESC");

        content = getList.body().jsonPath().getList("content", BookResponse.class);
        assertThat(content).filteredOn(BookResponse::isBorrowed)
                .extracting("title")
                .contains("첫 1년 움직임의 비밀", "클린 아키텍처: 소프트웨어 구조와 설계의 원칙");

        //10 초 후 반납 확인
        await()
                .atMost(Duration.ofSeconds(11L))
                .untilAsserted(() -> {
                    ExtractableResponse<Response> list = GET_LIST(borrowed, "numberOfBorrowed", "DESC");
                    List<BookResponse> bookResponses = list.body().jsonPath().getList("content", BookResponse.class);

                    assertThat(bookResponses).filteredOn(BookResponse::isBorrowed)
                            .isEmpty();
                });

    }

    private String signupAndLoggedIn(String email, String password, String name, String phone){
        SIGNUP("{ " +
                "\"email\":\""+email+"\"," +
                "\"password\":\""+password+"\"," +
                "\"name\":\""+name+"\"," +
                "\"phone\":\""+phone+"\"" +
                "}");

        return LOGGED_IN("{" +
                "\"email\":\""+email+"\"," +
                "\"password\":\""+password+"\"" +
                "}");
    }

    static ExtractableResponse<Response> CONSIGNMENTS(String token, String body){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .body(body)
                .when()
                .post(END_POINT)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> GET_LIST(String token, String order, String direction){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .queryParam("order", order)
                .queryParam("direction", direction)
                .queryParam("page", 0)
                .when()
                .get(END_POINT)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> BORROWED(String token, List<Long> ids){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .body("{\"ids\": "+ids+"}")
                .when()
                .post(END_POINT + "/rent")
                .then().log().all()
                .extract();
    }
}
