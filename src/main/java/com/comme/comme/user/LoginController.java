package com.comme.comme.user;

import com.comme.comme.error.ApiError;
import com.comme.comme.shared.CurrentUser;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;

@RestController
public class LoginController {

    @PostMapping("/api/1.0/login")
    @JsonView(Views.Base.class)
    private User handleLogin(@CurrentUser User user){
        return user;
    }

}
