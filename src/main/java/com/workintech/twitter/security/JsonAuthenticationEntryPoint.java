package com.workintech.twitter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.twitter.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Token yok/gecersiz/suresi dolmus -> 401. Bu hata FILTRE seviyesinde olur,
// yani controller'a hic ulasmaz ve GlobalExceptionHandler onu GOREMEZ.
// Bu yuzden ayni JSON formatini burada kendimiz yaziyoruz.
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse body = new ErrorResponse(401, "Kimlik dogrulamasi gerekli ya da token gecersiz", request.getRequestURI());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
