package com.handling;

import com.dto.ErrorDto;
import com.enums.StatusCode;
import com.dto.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@ControllerAdvice(basePackages = "com.e_rental.user.controllers")
public class GlobalExceptionHandling extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(ErrorDto.class)
    private ResponseEntity<? extends Response> processErrorDto(ErrorDto err) {
        Response res = new Response();
        res.setCode(StatusCode.BAD_REQUEST.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<? extends Response> processErrorDto(ResourceNotFoundException err) {
        Response res = new Response();
        res.setCode(StatusCode.NOT_FOUND.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InternationalErrorException.class)
    private ResponseEntity<? extends Response> processErrorDto(InternationalErrorException err) {
        Response res = new Response();
        res.setCode(StatusCode.INTERNATIONAL_ERROR.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.internalServerError().body(res);
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<? extends Response> processErrorDto(BadRequestException err) {
        Response res = new Response();
        res.setCode(StatusCode.BAD_REQUEST.getCode());
        res.setMessage(err.getMessage());
        return ResponseEntity.badRequest().body(res);
    }
}
