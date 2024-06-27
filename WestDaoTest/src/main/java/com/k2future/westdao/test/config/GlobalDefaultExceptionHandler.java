package com.k2future.westdao.test.config;



import com.k2future.westdao.test.utils.resp.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<String> IllegalArgumentException(Exception e) {
        String message = e.getMessage();
        try {
            StackTraceElement[] stackTrace = e.getStackTrace();
            logger.error("{}: {}",stackTrace[1].toString(), message);
        } catch (Exception ignore) {
        }
        return Result.failedResult(message);
    }
}
