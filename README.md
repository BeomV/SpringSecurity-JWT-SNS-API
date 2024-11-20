## REST API SpringSecurity-Jwt

## 기술 스택
+ Java17
+ Docker
+ PostgreSQL
+ SpringSecurity + JWT

## 디렉토리 구조
```
├── src/                       
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── board/
│   │   │               ├── config/         
│   │   │               ├── controller/     
│   │   │               ├── exception/      
│   │   │               ├── model/          
│   │   │               ├── repository/    
│   │   │               ├── service/        
│   │   │               ├── util/           
│   │   │               └── BoardApplication.java 
│   │   └── resources/
│   │       ├── application.yaml
```

## REST API 명세서
| Index | 기능        | Method       | Path               |
|-------|-----------|--------------|--------------------|
| USER  | 회원가입      | POST         | /api/v1/users      |
|       | 로그인       | POST         | /api/v1/users/authenticate |
|       | 로그아웃      | POST         | /api/v1/users/logout     
|       | 유저 전체 조회  | GET          |/api/v1/users
|       | 유저 조회     | GET          |/api/v1/users/{username}
|       | 유저 정보 수정  | PATCH        |/api/v1/users/{username}
|       | 유저 팔로우    | POST         |/api/v1/users/{username}/follows
|       | 유저 팔로우 취소 | Delete       |/api/v1/users/{username}/follows
|       | 팔로우 유저 조회 | GET          |/api/v1/users/{username}/followers
|       | 팔로잉 유저 조회 | GET          |/api/v1/users/{username}/followings
| POST  | 게시글 전체 조회 | GET          |/api/v1/posts
|       | 게시글 조회    | GET          |/api/v1/posts/{postId}
|       | 게시글 작성    | POST         |/api/v1/posts
|       | 게시글 수정    | PatchMapping |/api/v1/posts/{postId}
|       | 게시글 삭제    | POST         |/api/v1/posts/{postId}
|       | 게시글 좋아요   | POST         |/api/v1/posts/{postId}/likes
|    Reply   | 게시글 댓글 조회 | POST         |/api/v1/posts/{postId}/replies
|       | 댓글 작성     | POST         |/api/v1/posts/{postId}/replies
|       | 댓글 수정     | PATCH        |/api/v1/posts/{postId}/replies/{replyId}
|       | 댓글 삭제     | Delete       |/api/v1/posts/{postId}/replies/{replyId}


## Spring Security에서 Stateless 인증 설정
``
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
``

STATELESS로 설정하여 세션하여 서버는 불필요한 클라이언트의 상태 정보를 저장하지 않음
+ 서버는 세션 데이터를 저장하지 않으니 추가적인 저장 공간 사용이나 메로리 사용이 필요 없음
+ 세션을 탈취하는 공격에 대한 노출이 줄어듬
+ JWT와 같은 서명된 토큰을 사용하고 검증하므로 데이터 변조를 방지하고 신뢰성을 유지함

Gradle 의존성
```
dependencies {
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.projectlombok:lombok'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    runtimeOnly 'org.postgresql:postgresql'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
}
```
## Webconfiguration 설정
```java
@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/index", "/js/**", "/css/**", "/images/**", "/favicon.ico", "/user/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/users/authenticate", "/","/api/v1/users/refresh","/api/v1/users/logout")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
```
회원가입, 로그인, 재발급, 로그아웃 API는 토큰 검증 없이도 접근을 허용하여 인증이 필요 없는 엔드포인트로 설정

```
.requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/users/authenticate", "/","/api/v1/users/refresh","/api/v1/users/logout")
```
재발급, 로그아웃 경우 유효기간 확인 및 AccessToken 검증을 위해 부분적 허용

## AccessToken, RefreshToken 생성 시나리오
+ 유저는 로그인 성공 시 AccessToken과 RefreshToken을 생성
+ AccessToken은 클라이언트에게 응답하고 RefreshToken은 서버 Cookie에 저장
+ AccessToken은 유효기간을 짧게, RefreshToken은 유효기간은 길게 설정한다
+ AccessTokne 유효기간이 만료가 되면 ExpiredJwtException을 보내 재발급 요청을 보낸다.
+ RefreshTokne의 정보를 확인 후 AccessToken의 사용자와 일치하면 재발급 후 클라이언트에게 응답

## AccessToken, RefreshToken 검증 시나리오
1. RefreshToken이 존재하고 AccessToken의 기간이 만료된 경우
+ 서버 측 쿠키에 refreshToken이 있고 AccessToken이 기간이 만료가 되면 ExpiredJwtException 예외가 발생함
+ POST /api/v1/users/refresh를 통해 클라이언트에게 AccessToken을 재발급


2. RefreshToken이 존재하지만 AccessToken이 없는 경우
+ 이 상황은 CSRF 공격을 통해 서버에 접근 했을 가능성을 대비해 JwtTokenNotFoundException 예외가 발생함


3. AccessToken은 존재하지만 RefreshToken이 없는 경우
+ XSS 공격에 대한 가능성을 대비해 JwtTokenNotFoundException 예외가 발생함

### JwtAuthentiacationFilter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String BEARER_PREFIX = "Bearer ";
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();
        var cookies = request.getCookies();

        try {
            if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null) {
                var accessToken = authorization.substring(BEARER_PREFIX.length());
                setAuthenticationInSecurityContext(accessToken, request);
            }
            else if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith(BEARER_PREFIX) && cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        var refreshToken = cookie.getValue();
                        setAuthenticationInSecurityContext(refreshToken, request);
                        break;
                    }
                }
            }

        }catch (ExpiredJwtException e){
            throw new JwtTokenExpiredException();
        }


        filterChain.doFilter(request, response);
    }

    private void setAuthenticationInSecurityContext(String token, HttpServletRequest request) {
        var username = jwtService.getUsername(token); // 토큰에서 사용자 이름 추출
        var userDetails = userService.loadUserByUsername(username); // 사용자 정보 로드

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        var securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }
```


Rest API를 개발하면서 클라이언트와 통신을 구현할 때, 처음에는 localStorage에 AccessToken을 저장하고 이를 

Header에 포함시켜 요청하는 방식으로 개발했습니다.

그러나 브라우저의 F12 개발자 도구에서 토큰 값이 쉽게 노출된다는 문제를 확인하고 보안을 강화하기 위해  접근 방식을 변경했습니다.

AccessToken과 RefreshToken을 HTTP-only 쿠키에 저장하도록 수정했습니다. 

HTTP-only 쿠키는 클라이언트 측에서 JavaScript를 통해 접근할 수 없으므로, 

XSS(크로스 사이트 스크립팅) 공격으로부터 보호할 수 있습니다. 또한, AccessToken은 

짧은 유효 기간을 가지도록 설정하고,  

만료 시 RefreshToken을 통해 재발급받는 구조로 구현하여 보안과 사용자 편의성을 고려해 개발하게 되었습니다.





