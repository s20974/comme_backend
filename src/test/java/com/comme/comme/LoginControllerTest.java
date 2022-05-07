package com.comme.comme;

import com.comme.comme.error.ApiError;
import com.comme.comme.user.User;
import com.comme.comme.user.UserRepository;
import com.comme.comme.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.comme.comme.TestUtil.createValidUser;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {
    private static final String API_1_0_USERS = "/api/1.0/login";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Before
    public void cleanup(){
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorized(){
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withIncorrectCredentials_receiveUnauthorized(){
        authenticated();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiError(){
        ResponseEntity<ApiError> response = login(ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors(){
        ResponseEntity<String> response = login(String.class);
        assertThat(response.getBody().contains("validationErrors")).isFalse();
    }

    @Test
    public void postLogin_withIncorrectCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader(){
        authenticated();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    public void postLogin_withValidCredentials_receiveOk(){
        User user = createValidUser();
        userService.save(user);
        authenticated();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserId(){
        User inDB = userService.save(createValidUser());
        authenticated();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = response.getBody();
        Integer id = (Integer) body.get("id");
        assertThat(id).isEqualTo(inDB.getId());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserImage(){
        User inDB = userService.save(createValidUser());
        authenticated();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = response.getBody();
        String img = (String) body.get("image");
        assertThat(img).isEqualTo(inDB.getImage());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUserSurname(){
        User inDB = userService.save(createValidUser());
        authenticated();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = response.getBody();
        String surname = (String) body.get("surname");
        assertThat(surname).isEqualTo(inDB.getSurname());
    }

    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUsersPassword(){
        userService.save(createValidUser());
        authenticated();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });
        Map<String, Object> body = response.getBody();
        assertThat(body.containsKey("password")).isFalse();
    }

    private void authenticated() {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test-user", "P@ssw0rd"));
    }


    public <T> ResponseEntity<T> login(Class<T> responseType){
        return testRestTemplate.postForEntity(API_1_0_USERS, null, responseType);
    }

    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(API_1_0_USERS, HttpMethod.POST, null, responseType);
    }
}
