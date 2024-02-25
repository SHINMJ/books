package com.example.books.integration;

import com.example.books.usecase.book.dto.BookResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.example.books.integration.BookIntegrationTest.BORROWED;
import static com.example.books.integration.BookIntegrationTest.CONSIGNMENTS;
import static com.example.books.integration.LoginIntegrationTest.LOGGED_IN;
import static com.example.books.integration.SignupIntegrationTest.SIGNUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("사용자의 위탁 정보 및 대여 정보 관리 통합 테스트")
public class MemberIntegrationTest extends IntegrationTest{
    private static final String ENDPOINT = "/members";

    @Test
    void 나의위탁정보_조회_success() {
        //given
        String owner = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");
        String borrower = signupAndLoggedIn("test2@email.com", "test111", "testUser2", "01099912222");
        CONSIGNMENTS(owner, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");
        CONSIGNMENTS(owner, "{\"title\": \"세이노의 가르침\", \"isbn\":\"9791168473690\", \"price\":3000}");

        //when
        ExtractableResponse<Response> ownerResponse = myBookList(owner);
        ExtractableResponse<Response> borrowerResponse = myBookList(borrower);

        //then
        assertEquals(ownerResponse.statusCode(), HttpStatus.OK.value());
        assertEquals(borrowerResponse.statusCode(), HttpStatus.OK.value());
        List<BookResponse> books = ownerResponse.body().jsonPath().getList("content", BookResponse.class);
        List<BookResponse> books2 = borrowerResponse.body().jsonPath().getList("content", BookResponse.class);

        assertEquals(books.size(), 2);
        assertThat(books).extracting("title")
                .contains("첫 1년 움직임의 비밀", "세이노의 가르침");

        assertEquals(books2.size(), 0);
    }

    @Test
    void 나의대여목록_조회_success() {
        //given
        String owner = signupAndLoggedIn("test@email.com", "test111", "testUser", "01099912222");
        String borrower = signupAndLoggedIn("test2@email.com", "test111", "testUser2", "01099912222");
        ExtractableResponse<Response> consignments = CONSIGNMENTS(owner, "{\"title\": \"첫 1년 움직임의 비밀\", \"isbn\":\"9791186202753\", \"price\":1000}");
        CONSIGNMENTS(owner, "{\"title\": \"세이노의 가르침\", \"isbn\":\"9791168473690\", \"price\":3000}");

        String id = consignments.header("Location").replace("/books/", "");

        BORROWED(borrower, List.of(Long.valueOf(id)));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(borrower)
                .when()
                .get("/members/books/borrowed")
                .then().log().all()
                .extract();

        //then
        assertEquals(response.statusCode(), HttpStatus.OK.value());

    }

    private ExtractableResponse<Response> myBookList(String token){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .queryParam("page", 0)
                .when()
                .get(ENDPOINT+"/books")
                .then().log().all()
                .extract();
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
}
