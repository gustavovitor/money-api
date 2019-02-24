package com.gustavomiranda.money.exceptionshandler;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class MoneyExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String mensagem = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
        List<ErrorHandler> erros = Arrays.asList(new ErrorHandler(mensagem, ex.getCause() != null ? ex.getCause().toString() : ex.toString()));
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorHandler> erros = createErrorList(ex.getBindingResult());
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> dataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){
        String mensagem = messageSource.getMessage("recurso.integridade_dados", null, LocaleContextHolder.getLocale());
        List<ErrorHandler> erros = Arrays.asList(new ErrorHandler(mensagem, ExceptionUtils.getRootCauseMessage(ex)));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<Object> entityNotFoundException(RuntimeException ex, WebRequest request){
        String mensagem = messageSource.getMessage("recurso.not_found", null, LocaleContextHolder.getLocale());
        List<ErrorHandler> erros = Arrays.asList(new ErrorHandler(mensagem, ex.toString()));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    private List<ErrorHandler> createErrorList(BindingResult bindingResult){
        List<ErrorHandler> errs = new ArrayList<>();
        for(FieldError fieldError : bindingResult.getFieldErrors()){
            errs.add(new ErrorHandler(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()), fieldError.toString()));
        }
        return errs;
    }

    @Getter
    @Setter
    public static class ErrorHandler{

        private String message;
        private String error;

        public ErrorHandler(String message, String error) {
            this.message = message;
            this.error = error;
        }
    }
}
