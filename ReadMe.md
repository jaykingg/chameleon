# Cafe 관리 시스템

## Features

- Account(계정), Product(상품) 을 기반으로한 프로젝트입니다.
- JWT (JSON Web Token) 를 기반으로 인증합니다.

## Requirements & Additional content

- 요구사항 분석과 요구사항에 대한 처리방식 코멘트내용입니다.
- Framework & language : SpringBoot & kotlin(java11+)
- Spec : java17, Spring MVC, Spring Security, Spring JPA
- Database : Mysql@5.7
- 회원가입
    - 휴대폰 번호와 비밀번호로 가입.
        - 비밀번호는 암호화 처리.
    - 휴대폰 번호가 제대로 입력되었는지 확인.
        - 정규식 이용하여 처리.
- 로그인 / 로그아웃
    - 가입된 휴대폰 번호와 비밀번호로 로그인 이후 Jwt Token 발행.
    - 로그아웃은 구현하지 않음.
        - 로그아웃 기능은 Stateless 기반으로 일반적으로 JWT 토큰을 삭제하여 클라이언트 측에서 처리함.
        - 만약 필요하다면 Redis 와 같은 미들레벨 Cache 저장소를 활용하여 해결하는 방안도 있습니다.
- 로그인 이후 아래와 같은 액션을 취할 수 있음
    - 여러 사장님이 존재할 경우, 각각의 특수성과 환경, 지점에 따라 조회되어야할 삼품리소스가 다름.
        - 기능 요구사항에 사장님의 호칭이 "단수"인 점, 도메인 요구사항에 작성되지 않음에 따라 배제하였으나, <br> 추후 수정되어야한다면 회원가입 시 사장님 혹은 매장마다 고유식발자와 권한을 부여하여
          처리가능합니다.
    - 상품 등록
    - 상품 단일 조회
        - 상품 조회 시 정상 접근이지만 데이터가 없을 때, ApiResponse 의 data 부분에 null 을 담아 응답합니다.
        - ApiResponse 를 확장할 수 있다면, 총 갯수를 포함하여 리턴하는 방식을 채택하고 클라이언트에 내용을 가공하여 랜더링하는 방식으로 개선할 수 있습니다.
    - 상품 다수 조회
        - cursor 기반 pagination 을 적용, 클라이언트에서 전달받은 cursor 값을 기준으로하며 10개씩 출력되게끔 처리.
    - 상품 부분 수정
        - "부분" 수정이 가능할 것이 요구사항이었으므로 임의로 변동이 가장 많아보이는 가격과, 설명을 기본으로 함.
        - Put 대신 Patch 를 사용하여 Payload Contents 가 Entity 를 Wrapping 하지 못하여도 Nullable 이슈가 발생하지 않도록 처리함.
    - 상품 검색
        - 상품은 Like 검색과 초성검색기 가능하게 구현.
- 모든 응답은 ApiResponse(Custom) 를 이용하여 요구된 Json 형태에 맞게 응답되게 구현.
- Test 는 Integration Test 로 진행하였으며 Kotest 기반의 DescribeSpec 을 사용
- MySql 초기 설정 및 테이블 생성 DDL 을 작성하여 /resource 내 첨부.
    - 서비스는 JPA 를 이용하여 개발자의 수동 DDL 없이 자동생성되게 처리.

## Before testing and operating

- Mysql@5.7 버전의 경우 Old Version 으로 Docker 이미지를 제공하지 않기 때문에 반드시 local 에서 application.yaml 에 작성된 내용과 동일하게 세팅해야합니다.

application.yaml

~~~yaml
 spring:
   datasource:
     url: jdbc:mysql://localhost:3306/cafe
     username: cafe
     password: password
     driver-class-name: com.mysql.cj.jdbc.Driver
~~~

/resource/Mysql.sql

~~~sql
create
database cafe;
create
user 'cafe' identified by 'password';
grant all privileges on cafe.* TO
'cafe';
flush
privileges;
~~~

# Project Structure

프로젝트 구조와 설명입니다

- `src`
    - `main`
        - `kotlin`
            - `com.chameleon`: 임의로 정한 프로젝트 명
                - `account`:
                    - `Account.kt`: 계정 도메인
                    - `AccountController.kt`: 계정 핸들링 REST Controller
                    - `AccountPayload.kt`: 계정 등록을 위한 Payload(DTO)
                    - `AccountRepository.kt`: JPA repository for 계정 Entity
                    - `AuthController.kt`: 인증 핸들링 REST controller
                    - `LoginPayload.kt`: 로그인 진행을 위한 Payload(DTO)
                - `config`:
                    - `ApiResponse.kt`: API Endpoint 에 대한 표준응답 설정
                    - `ExceptionHandler.kt`: 전역 Exception handler
                - `product`:
                    - `Product.kt`: 상품 도메인
                    - `ProductController.kt`: 상품 핸들링 REST controller
                    - `ProductPayload.kt`: 상품 등록을 위한 Payload(DTO)
                    - `ProductRepository.kt`: JPA repository for 상품 Entity
                    - `ProductService.kt`: 상품 관련 비즈니스 로직을 우한 Service layer
                    - `ProductSize.kt`: 상품 크기 정의
                - `security`:
                    - `CustomUserDetailsService.kt`: Spring Security 에 대한 사용자별 데이터를 로드하는 Service
                    - `JwtProperties.kt`: JWT 세팅을 위한 Configuration properties
                    - `JwtRequestFilter.kt`: 각 요청에 대해 JWT를 처리할 Filter
                    - `JwtUtil.kt`: JWT 생성 및 유효성 검사 Util
                    - `SecurityConfig.kt`: Spring Security configuration
                - `ChameleonApplication.kt`: Main class
        - `resources`:
            - `application.yaml`: Application configuration
            - `Mysql.sql`:Initial database schema.
- `gradle`
- `Test`
    - `com.chameleon`
        - `AccountControllerTests`: 계정 관련 Integration Test
        - `AuthControllerTests`: 인증 관련 Integration Test
        - `ProductControllerTests`: 상품 관련 Integration Test 

