package com.workintech.twitter.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Tum controller'lardan firlayan exception'lar buraya duser.
// Spring, exception tipine en uygun @ExceptionHandler metodunu secer.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1) Bizim bilerek firlattigimiz hatalar: status'u exception'in kendisi tasir.
    @ExceptionHandler(TwitterException.class)
    public ResponseEntity<ErrorResponse> handleTwitterException(TwitterException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(ex.getStatus().value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // 2) @Valid basarisiz: hangi alan, neden hatali -> fieldErrors map'i.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Gecersiz istek", request.getRequestURI(),
                LocalDateTime.now(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // 3) Bozuk JSON (eksik parantez, yanlis tip vb.)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Istek govdesi okunamadi (JSON formatini kontrol edin)", request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    // 3b) Yanlis tipte parametre: /tweet/findById?id=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "'" + ex.getName() + "' parametresi gecersiz: " + ex.getValue();
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI()));
    }

    // 4) Veritabani kisiti ihlali (or. iki istek AYNI ANDA ayni username ile geldi;
    //    servisteki exists kontrolunu ikisi de gecti ama DB'deki unique kisit yakaladi).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Veritabani kisiti ihlali: {}", ex.getMostSpecificCause().getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT.value(), "Kayit veritabani kurallariyla cakisiyor", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 5) Beklenmeyen her sey. Detayi LOG'a yaz, client'a ic detay sizdirma.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        // Spring'in kendi HTTP hatalari (404 olmayan adres, 405 yanlis metod vb.)
        // kendi status kodunu bilir; bunlari 500'e cevirmeyelim.
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            int status = springError.getStatusCode().value();
            ErrorResponse body = new ErrorResponse(status, ex.getMessage(), request.getRequestURI());
            return ResponseEntity.status(status).body(body);
        }
        log.error("Beklenmeyen hata: {} {}", request.getMethod(), request.getRequestURI(), ex);
        ErrorResponse body = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Beklenmeyen bir hata olustu", request.getRequestURI());
        return ResponseEntity.internalServerError().body(body);
    }
}
