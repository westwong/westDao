package cn.k2future.westdao.test.config;



import cn.k2future.westdao.test.utils.resp.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
