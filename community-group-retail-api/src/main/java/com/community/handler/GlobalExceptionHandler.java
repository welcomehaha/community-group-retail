package com.community.handler;

import com.community.constant.MessageConstant;
import com.community.exception.BaseException;
import com.community.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(IllegalArgumentException ex){
        log.error("参数异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().hasFieldErrors()
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "请求参数不合法";
        log.error("表单校验异常：{}", message);
        return Result.error(message);
    }

    /**
     * 处理表单绑定校验异常。
     *
     * @param ex 绑定异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result exceptionHandler(BindException ex){
        String message = ex.getBindingResult().hasFieldErrors()
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "请求参数不合法";
        log.error("参数绑定校验异常：{}", message);
        return Result.error(message);
    }

    /**
     * 处理方法级参数校验异常。
     *
     * @param ex 约束异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result exceptionHandler(ConstraintViolationException ex){
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .distinct()
                .collect(Collectors.joining("；"));
        if (message.isBlank()) {
            message = "请求参数不合法";
        }
        log.error("方法参数校验异常：{}", message);
        return Result.error(message);
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'staff.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
