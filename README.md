# 도서 위탁 대여 API 개발 및 회고

이 프로젝트는 도서 위탁 대여 시스템의 API 서버를 구축하기 위한 것입니다.
이 API 서버를 통해 사용자는 도서를 등록하고 대여할 수 있으며, 도서 검색 기능을 제공합니다.


## 구성환경
- Spring Boot 3.2.2 
- jdk 17
- gradle 8.3
- Spring Security 6 
- Spring Data JPA
- H2 embedded DB (test)
- MySQL 8 (deploy)

## 빌드 및 배포
- gradle
- docker
- docker-compose

## 기능
- 회원 가입: 사용자의 정보로 회원가입을 합니다.
- 로그인: 이메일, 패스워드로 로그인을 합니다.
- 도서 위탁: 새로운 도서를 등록합니다.
- 도서 대여: 등록된 도서를 대여합니다.
- 도서 목록 조회: 특정 정렬 순서에 따라 정렬된 도서 목록을 조회합니다.
- 도서 반납: 대여 중인 도서를 반납합니다. (대여 후 10초 후 자동 반납)
- 사용자 도서 조회: 사용자의 위탁 도서 및 대여 도서 목록을 조회합니다.

## 실행
jdk 17, gradle 8, docker, docker-compose 가 설치된 환경에서 실행하세요.

- 해당 repo를 클론합니다.
- gradle 빌드를 합니다. `./gradlew build`
- docker-compose 를 통해 배포합니다. `docker-compose up -d --build`

## API SPEC 
서버 실행 후 `http://localhost:8080/swagger-ui/index.html` 를 확인하세요.
